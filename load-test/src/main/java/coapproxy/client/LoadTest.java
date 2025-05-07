package coapproxy.client;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.MessageObserverAdapter;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.californium.elements.config.UdpConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

public class LoadTest {

	// private static final File CONFIG_FILE = new File("Californium3.properties");
	// private static final String CONFIG_HEADER = "Californium CoAP Properties";

	// private static DefinitionsProvider DEFAULTS = new DefinitionsProvider() {

	// @Override
	// public void applyDefinitions(Configuration config) {
	// }
	// };

	private static AtomicInteger clientUniqueId = new AtomicInteger(0);
	private static Map<String, Integer> onRetransmissionHashMap = new ConcurrentHashMap<>();

	static {
		ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(Logger.ROOT_LOGGER_NAME);

		logger.setLevel(Level.ERROR);

		CoapConfig.register();
		UdpConfig.register();
	}

	public static void main(String[] args) throws InterruptedException, IOException {
		Map<String, String> argsMap = parseArgs(args);

		if (argsMap.size() == 0) {
			usage();
			System.exit(0);
		}

		String uri = argsMap.get("uri");
		String method = argsMap.get("method");

		int numRequests = Integer.parseInt(argsMap.getOrDefault("num-requests", "1"));
		int threadPoolSize = Integer.parseInt(argsMap.getOrDefault("thread-pool-size", "1"));

		if (method == null || uri == null) {
			usage();
			System.exit(1);
		}

		method = method.toUpperCase();

		if (!method.equals("GET") && !method.equals("POST")) {
			System.err.println("Invalid method. Use GET or POST.");
			System.exit(1);
		}

		String payload = "";

		if (method.equals("POST") && argsMap.containsKey("payload-file")) {
			payload = Files.readString(new File(argsMap.get("payload-file")).toPath());
		}

		String finalMethod = method;
		String finalPayload = payload;

		// Configuration configuration = Configuration.createWithFile(CONFIG_FILE,
		// CONFIG_HEADER, DEFAULTS);
		Configuration configuration = Configuration.getStandard();

		if (argsMap.get("ack-timeout") != null) {
			configuration.set(CoapConfig.ACK_TIMEOUT, Integer.parseInt(argsMap.get("ack-timeout")),
					TimeUnit.MILLISECONDS);
		}

		if (argsMap.get("max-retransmit") != null) {
			configuration.set(CoapConfig.MAX_RETRANSMIT, Integer.parseInt(argsMap.get("max-retransmit")));
		}

		ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
		List<Long> responseTimes = Collections.synchronizedList(new ArrayList<>());

		AtomicInteger successCount = new AtomicInteger(0);
		AtomicInteger failureCount = new AtomicInteger(0);
		AtomicInteger onCancelCount = new AtomicInteger(0);
		AtomicInteger onRejectCount = new AtomicInteger(0);
		AtomicInteger onRetransmissionCount = new AtomicInteger(0);
		AtomicInteger onTimeoutCount = new AtomicInteger(0);

		CountDownLatch latch = new CountDownLatch(numRequests);

		for (int i = 0; i < numRequests; i++) {
			String clientId = String.valueOf(clientUniqueId.incrementAndGet());

			executor.submit(() -> {
				try {
					CoapClient client = new CoapClient();

					client.setEndpoint(new CoapEndpoint.Builder().setConfiguration(configuration).build());

					// client.setTimeout(5000L);

					Request request = finalMethod.equals("GET") ? Request.newGet() : Request.newPost();
					request.setURI(uri);

					if (finalMethod.equals("POST")) {
						request.setPayload(finalPayload);
						request.getOptions().setContentFormat(MediaTypeRegistry.TEXT_PLAIN);
					}

					request.addMessageObserver(new MessageObserverAdapter() {
						@Override
						public void onCancel() {
							onCancelCount.incrementAndGet();
						}

						@Override
						public void onReject() {
							onRejectCount.incrementAndGet();
						}

						@Override
						public void onRetransmission() {
							onRetransmissionCount.incrementAndGet();
							onRetransmissionHashMap.merge(clientId, 1, Integer::sum);
						}

						@Override
						public void onTimeout() {
							onTimeoutCount.incrementAndGet();
						}
					});

					long startTime = System.nanoTime();

					CoapResponse response = client.advanced(request);

					long duration = (System.nanoTime() - startTime) / 1_000_000;

					// if (response != null && response.getCode() == ResponseCode.CONTENT) {
					if (response != null && response.getCode().isSuccess()) {
						responseTimes.add(duration);
						successCount.incrementAndGet();
					} else {
						failureCount.incrementAndGet();
					}

					client.shutdown();
				} catch (Exception e) {
					failureCount.incrementAndGet();

					// e.printStackTrace();
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();
		executor.shutdown();

		if (responseTimes.isEmpty()) {
			System.out.println("No successful requests.");
			System.exit(1);
		}

		long min = Collections.min(responseTimes);
		long max = Collections.max(responseTimes);
		double avg = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0);

		System.out.println("= Settings =============================\n");
		System.out.println(String.format("%-21s %s", "URI:", uri));
		System.out.println(String.format("%-21s %s", "Method:", method));
		System.out.println(String.format("%-21s %s", "ACK Timeout:", configuration.getAsText(CoapConfig.ACK_TIMEOUT)));
		System.out.println(
				String.format("%-21s %s", "Max Retransmit:", configuration.getAsText(CoapConfig.MAX_RETRANSMIT)));
		System.out.println(String.format("%-21s %d", "Num Requests:", numRequests));
		System.out.println(String.format("%-21s %d", "Thread Pool Size:", threadPoolSize));
		System.out.println();

		System.out.println("= Request Results ======================\n");
		System.out.println(String.format("%-21s %d", "Success:", successCount.get()));
		System.out.println(String.format("%-21s %d", "Failed:", failureCount.get()));
		System.out.println();
		System.out.println(String.format("%-21s %d ms", "Min Time:", min));
		System.out.println(String.format("%-21s %.2f ms", "Avg Time:", avg));
		System.out.println(String.format("%-21s %d ms", "Max Time:", max));
		System.out.println();

		System.out.println("= Request Details ======================\n");
		System.out.println(String.format("%-21s %d", "Cancel:", onCancelCount.get()));
		System.out.println(String.format("%-21s %d", "Reject:", onRejectCount.get()));
		System.out.println(String.format("%-21s %d", "Retransmission:", onRetransmissionCount.get()));
		System.out.println(String.format("%-21s %d", "Timeout:", onTimeoutCount.get()));
		System.out.println();

		System.out.println("= Message Retransmission ===============\n");

		int retransmissionTotal = onRetransmissionHashMap.size();

		if (retransmissionTotal == 0) {
			System.out.println("No retransmissions.");
		}

		for (int i = 1; i <= Integer.parseInt(configuration.getAsText(CoapConfig.MAX_RETRANSMIT)); i++) {
			int retransmissionCount = 0;

			for (int retransmission : onRetransmissionHashMap.values()) {
				if (retransmission == i) {
					retransmissionCount++;
				}
			}

			double percent = retransmissionTotal > 0 ? (retransmissionCount * 100.0) / retransmissionTotal : 0;

			if (retransmissionCount > 0) {
				System.out.println(String.format("%-21s %d (%.2f%%) requests", String.format("Retransmitted %dx:", i),
						retransmissionCount, percent));
			}
		}
	}

	private static Map<String, String> parseArgs(String[] args) {
		Map<String, String> map = new HashMap<>();

		for (int i = 0; i < args.length - 1; i++) {
			if (args[i].startsWith("--")) {
				map.put(args[i].substring(2), args[i + 1]);
				i++; // skip next as it's a value
			}
		}

		return map;
	}

	private static void usage() {
		System.out.println("Usage: java LoadTest --uri <coap://...> --method <GET|POST> [options]\n\n"
				+ "    --ack-timeout <N>              ACK timeout in milliseconds\n"
				+ "    --max-retransmit <N>           Maximum number of retransmissions per request\n"
				+ "    --num-requests <N>             Total number of requests to send (default: 1)\n"
				+ "    --thread-pool-size <N>         Thread pool size to use (default: 1)\n"
				+ "    --payload-file <path>          Path to file containing request payload\n");
	}
}
