package coap.server;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.cose.AlgorithmID;
import org.eclipse.californium.elements.util.StringUtil;
import org.eclipse.californium.oscore.HashMapCtxDB;
import org.eclipse.californium.oscore.OSCoreCtx;
import org.eclipse.californium.oscore.OSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceOscoreContext extends CoapResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceOscoreContext.class);

	private final HashMapCtxDB oscoreCtxDb;

	public ResourceOscoreContext(String name, HashMapCtxDB oscoreCtxDb) {
		super(name, true);

		getAttributes().setTitle("Resource OSCORE Context");

		this.oscoreCtxDb = oscoreCtxDb;

		LOGGER.info("CoapResource added");
	}

	@Override
	public void handlePOST(CoapExchange exchange) {
		try {
			// String payload = exchange.getRequestText();
			// JSONObject jsonRequest = new JSONObject(payload);

			oscoreContext(exchange, oscoreCtxDb);

			LOGGER.info("Success - SourceContext:{} RequestCode:{} RequestOptions:{}",
					exchange.getSourceContext().toString(), exchange.getRequestCode(),
					exchange.getRequestOptions().toString());

			exchange.respond(ResponseCode.CREATED);
		} catch (org.json.JSONException e) {
			LOGGER.info("Error - SourceContext:{} RequestCode:{} RequestOptions:{} - Message:{}",
					exchange.getSourceContext().toString(), exchange.getRequestCode(),
					exchange.getRequestOptions().toString(), e.getMessage());

			exchange.respond(ResponseCode.BAD_REQUEST, "Invalid JSON format.");
		} catch (Exception e) {
			LOGGER.info("Error - SourceContext:{} RequestCode:{} RequestOptions:{} - Message:{}",
					exchange.getSourceContext().toString(), exchange.getRequestCode(),
					exchange.getRequestOptions().toString(), e.getMessage());

			exchange.respond(ResponseCode.INTERNAL_SERVER_ERROR, "Internal server error.");
		}
	}

	public static void oscoreContext(CoapExchange exchange, HashMapCtxDB oscoreCtxDb) {
		AlgorithmID alg = AlgorithmID.AES_CCM_16_64_128;
		AlgorithmID kdf = AlgorithmID.HKDF_HMAC_SHA_256;

		byte[] master_secret = StringUtil.hex2ByteArray("0102030405060708090a0b0c0d0e0f10");
		byte[] master_salt = StringUtil.hex2ByteArray("9e7ca92223786340");
		byte[] sid = StringUtil.hex2ByteArray("01");
		byte[] rid = StringUtil.hex2ByteArray("02");
		byte[] id_context = StringUtil.hex2ByteArray("37cbf3210017a2d3");

		int MAX_UNFRAGMENTED_SIZE = exchange.advanced().getEndpoint().getConfig()
				.get(CoapConfig.MAX_RESOURCE_BODY_SIZE);

		try {
			OSCoreCtx oscoreCtx = new OSCoreCtx(master_secret, false, alg, sid, rid, kdf, 32, master_salt, id_context,
					MAX_UNFRAGMENTED_SIZE);

			oscoreCtx.setContextRederivationEnabled(true);
			oscoreCtxDb.addContext(oscoreCtx);
		} catch (OSException e) {
			LOGGER.error("Failed to derive OSCORE context");
			e.printStackTrace();
		}
	}
}
