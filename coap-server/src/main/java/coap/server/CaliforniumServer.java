package coap.server;

import java.io.File;
import java.net.InetSocketAddress;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.server.resources.MyIpResource;
import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.californium.elements.config.Configuration.DefinitionsProvider;
import org.eclipse.californium.elements.config.UdpConfig;
import org.eclipse.californium.oscore.HashMapCtxDB;
import org.eclipse.californium.oscore.OSCoreCoapStackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CaliforniumServer extends CoapServer {

	private static final Logger LOGGER = LoggerFactory.getLogger(CaliforniumServer.class);

	private static final File CONFIG_FILE = new File("Californium3.properties");
	private static final String CONFIG_HEADER = "Californium CoAP Properties";

	private static DefinitionsProvider DEFAULTS = new DefinitionsProvider() {

		@Override
		public void applyDefinitions(Configuration config) {
		}
	};

	static {
		CoapConfig.register();
		UdpConfig.register();
	}

	public static void main(String[] args) {
		try {
			LOGGER.info("Starting CaliforniumServer ...");

			Configuration configuration = Configuration.createWithFile(CONFIG_FILE, CONFIG_HEADER, DEFAULTS);
			Configuration.setStandard(configuration);

			int coapPort = configuration.get(CoapConfig.COAP_PORT);

			CoapEndpoint.Builder builder = new CoapEndpoint.Builder();

			builder.setConfiguration(configuration);
			builder.setInetSocketAddress(new InetSocketAddress(coapPort));

			CaliforniumServer server = new CaliforniumServer();

			HashMapCtxDB oscoreCtxDb = new HashMapCtxDB();
			OSCoreCoapStackFactory.useAsDefault(oscoreCtxDb);

			server.addResources(oscoreCtxDb);
			server.addEndpoint(builder.build());
			server.start();

			Runtime.getRuntime().addShutdownHook(new Thread(server::stop));

			LOGGER.info("Started CaliforniumServer");
		} catch (

		Exception e) {
			LOGGER.error("Failed to start CaliforniumServer: {}", e.getMessage(), e);
		}
	}

	private void addResources(HashMapCtxDB oscoreCtxDb) {
		add(new MyIpResource(MyIpResource.RESOURCE_NAME, true));
		add(new ResourceHello("hello"));
		add(new ResourceInfo("info", this));
		add(new ResourceOscoreContext("oscore-context", oscoreCtxDb));
		add(new ResourceReadings("readings"));
		add(new SecureResourceHello("secure-hello", oscoreCtxDb));
	}
}
