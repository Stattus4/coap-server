package coap.server;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.server.resources.MyIpResource;
import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.californium.elements.config.UdpConfig;
import org.eclipse.californium.elements.util.NetworkInterfacesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CaliforniumServer extends CoapServer {

	private static final Logger logger = LoggerFactory.getLogger(CaliforniumServer.class);

	static {
		CoapConfig.register();
		UdpConfig.register();
	}

	public CaliforniumServer() throws SocketException {
		add(new MyIpResource(MyIpResource.RESOURCE_NAME, true));
		add(new ResourceHello("hello"));
	}

	public static void main(String[] args) {
		try {
			int port = Configuration.getStandard().get(CoapConfig.COAP_PORT);

			logger.info("Starting CaliforniumServer ...");

			CaliforniumServer server = new CaliforniumServer();

			server.addEndpoints(true, false, port);
			server.start();

			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				server.stop();
			}));

			logger.info("Started CaliforniumServer");
		} catch (SocketException e) {
			logger.error("Failed to start: {}", e.getMessage(), e);
		}
	}

	private void addEndpoints(boolean udp, boolean tcp, int port) {
		Configuration config = Configuration.getStandard();

		for (InetAddress addr : NetworkInterfacesUtil.getNetworkInterfaces()) {
			InetSocketAddress bindToAddress = new InetSocketAddress(addr, port);

			if (udp) {
				CoapEndpoint.Builder builder = new CoapEndpoint.Builder();

				builder.setInetSocketAddress(bindToAddress);
				builder.setConfiguration(config);

				addEndpoint(builder.build());
			}
		}
	}
}
