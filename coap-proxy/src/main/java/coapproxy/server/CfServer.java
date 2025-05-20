package coapproxy.server;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.function.Supplier;

import org.eclipse.californium.core.CoapResource;
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

import coapproxy.server.resources.OscoreResourceHello;
import coapproxy.server.resources.OscoreResourceInfo;
import coapproxy.server.resources.ResourceHello;
import coapproxy.server.resources.ResourceInfo;
import coapproxy.server.resources.ResourceOscoreContext;
import coapproxy.server.resources.ResourceReadings;

public class CfServer extends CoapServer {

	private static final Logger LOGGER = LoggerFactory.getLogger(CfServer.class);

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
			LOGGER.info("Starting CfServer ...");

			Configuration configuration = Configuration.createWithFile(CONFIG_FILE, CONFIG_HEADER, DEFAULTS);
			Configuration.setStandard(configuration);

			int coapPort = configuration.get(CoapConfig.COAP_PORT);

			CoapEndpoint.Builder builder = new CoapEndpoint.Builder();

			builder.setConfiguration(configuration);
			builder.setInetSocketAddress(new InetSocketAddress(coapPort));

			CfServer server = new CfServer();

			HashMapCtxDB oscoreCtxDb = new HashMapCtxDB();
			OSCoreCoapStackFactory.useAsDefault(oscoreCtxDb);

			server.addResources(oscoreCtxDb);
			server.addEndpoint(builder.build());
			server.start();

			Runtime.getRuntime().addShutdownHook(new Thread(server::stop));

			LOGGER.info("Started CfServer");
		} catch (

		Exception e) {
			LOGGER.error("Failed to start CfServer: {}", e.getMessage(), e);
		}
	}

	private void addResources(HashMapCtxDB oscoreCtxDb) {
		tryAddResource(() -> new MyIpResource(MyIpResource.RESOURCE_NAME, true), MyIpResource.class.getName());
		tryAddResource(() -> new ResourceHello("hello"), ResourceHello.class.getName());
		tryAddResource(() -> new ResourceInfo("info", this), ResourceInfo.class.getName());
		tryAddResource(() -> new ResourceOscoreContext("oscore-context", oscoreCtxDb),
				ResourceOscoreContext.class.getName());
		tryAddResource(() -> new ResourceReadings("readings"), ResourceReadings.class.getName());
		tryAddResource(() -> new OscoreResourceHello("oscore-hello", true), OscoreResourceHello.class.getName());
		tryAddResource(() -> new OscoreResourceInfo("oscore-info", oscoreCtxDb), OscoreResourceInfo.class.getName());
	}

	private void tryAddResource(Supplier<CoapResource> resourceSupplier, String resourceName) {
		try {
			add(resourceSupplier.get());
		} catch (Exception e) {
			LOGGER.error("Failed to add CoapResource [{}] ExceptionMessage: {}", resourceName, e.getMessage());
		}
	}
}
