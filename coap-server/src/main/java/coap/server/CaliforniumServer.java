package coap.server;

import java.io.File;
import java.net.InetSocketAddress;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.server.resources.MyIpResource;
import org.eclipse.californium.cose.AlgorithmID;
import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.californium.elements.config.Configuration.DefinitionsProvider;
import org.eclipse.californium.elements.config.UdpConfig;
import org.eclipse.californium.elements.util.StringUtil;
import org.eclipse.californium.oscore.HashMapCtxDB;
import org.eclipse.californium.oscore.OSCoreCoapStackFactory;
import org.eclipse.californium.oscore.OSCoreCtx;
import org.eclipse.californium.oscore.OSException;
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

			HashMapCtxDB oscoreCtxDb = new HashMapCtxDB();;			
			OSCoreCoapStackFactory.useAsDefault(oscoreCtxDb);
			
			byte[] oscoreServerRid = initOscore(configuration, oscoreCtxDb);

			CoapEndpoint.Builder builder = new CoapEndpoint.Builder();

			builder.setConfiguration(configuration);
			builder.setInetSocketAddress(new InetSocketAddress(coapPort));

			// builder.setCustomCoapStackArgument(oscoreCtxDb);
			// builder.setCoapStackFactory(new OSCoreCoapStackFactory());

			CaliforniumServer server = new CaliforniumServer();

			server.addResources(oscoreCtxDb, oscoreServerRid);
			server.addEndpoint(builder.build());
			server.start();

			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				server.stop();
			}));

			LOGGER.info("Started CaliforniumServer");
		} catch (Exception e) {
			LOGGER.error("Failed to start CaliforniumServer: {}", e.getMessage(), e);
		}
	}

	private void addResources(HashMapCtxDB oscoreCtxDb, byte[] oscoreServerRid) {
		add(new MyIpResource(MyIpResource.RESOURCE_NAME, true));
		add(new ResourceHello("hello"));
		add(new ResourceInfo("info", this));
		add(new ResourceReadings("readings"));
		add(new SecureResourceHello("secure-hello", oscoreCtxDb, oscoreServerRid));
	}

	public static byte[] initOscore(Configuration configuration, HashMapCtxDB oscoreCtxDb) {
		AlgorithmID alg = AlgorithmID.AES_CCM_16_64_128;
		AlgorithmID kdf = AlgorithmID.HKDF_HMAC_SHA_256;

		byte[] master_secret = StringUtil.hex2ByteArray("0102030405060708090a0b0c0d0e0f10");
		byte[] master_salt = StringUtil.hex2ByteArray("9e7ca92223786340");
		byte[] sid = StringUtil.hex2ByteArray("01");
		byte[] rid = StringUtil.hex2ByteArray("02");
		byte[] id_context = StringUtil.hex2ByteArray("37cbf3210017a2d3");

		int MAX_UNFRAGMENTED_SIZE = configuration.get(CoapConfig.MAX_RESOURCE_BODY_SIZE);

		try {
			OSCoreCtx oscoreCtx = new OSCoreCtx(master_secret, false, alg, sid, rid, kdf, 32, master_salt, id_context,
					MAX_UNFRAGMENTED_SIZE);
		
			oscoreCtx.setContextRederivationEnabled(true);
			oscoreCtxDb.addContext(oscoreCtx);

		} catch (OSException e) {
			LOGGER.error("Failed to derive OSCORE context");
			e.printStackTrace();
		}

		return rid;
	}
}
