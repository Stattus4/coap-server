package coap.server;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.elements.util.StringUtil;
import org.eclipse.californium.oscore.HashMapCtxDB;
import org.eclipse.californium.oscore.OSCoreCtx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SecureResourceHello extends CoapResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(SecureResourceHello.class);

	private final HashMapCtxDB oscoreCtxDb;
	private final byte[] rid = StringUtil.hex2ByteArray("02");

	public SecureResourceHello(String name, HashMapCtxDB oscoreCtxDb) {
		super(name, true);

		getAttributes().setTitle("Secure Resource Hello");

		this.oscoreCtxDb = oscoreCtxDb;

		LOGGER.info("CoapResource added");
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		if (!exchange.getRequestOptions().hasOscore()) {
			LOGGER.info("Error - SourceContext:{} RequestCode:{} RequestOptions:{} - Message:{}",
					exchange.getSourceContext().toString(), exchange.getRequestCode(),
					exchange.getRequestOptions().toString(), ResponseCode.UNAUTHORIZED);

			exchange.respond(ResponseCode.UNAUTHORIZED);

			return;
		}

		OSCoreCtx oscoreCtx = oscoreCtxDb.getContext(rid);
		int offset = oscoreCtx.getRecipientReplaySize()
				- Integer.numberOfLeadingZeros(oscoreCtx.getRecipientReplayWindow());

		StringBuilder payload = new StringBuilder();
		payload.append("oscoreCtx.getIdContext(): " + Utils.toHexString(oscoreCtx.getIdContext()));
		payload.append("\noscoreCtx.getLowestRecipientSeq(): " + String.valueOf(oscoreCtx.getLowestRecipientSeq()));
		payload.append("\noscoreCtx.getRecipientReplaySize(): " + String.valueOf(oscoreCtx.getRecipientReplaySize()));
		payload.append(
				"\noscoreCtx.getRecipientReplayWindow(): " + String.valueOf(oscoreCtx.getRecipientReplayWindow()));
		payload.append("\noffset = oscoreCtx.getRecipientReplaySize() - oscoreCtx.getRecipientReplayWindow(): "
				+ String.valueOf(offset));

		LOGGER.info("Success - SourceContext:{} RequestCode:{} RequestOptions:{}",
				exchange.getSourceContext().toString(), exchange.getRequestCode(),
				exchange.getRequestOptions().toString());

		exchange.setMaxAge(30);
		exchange.respond(ResponseCode.CONTENT, payload.toString(), MediaTypeRegistry.TEXT_PLAIN);
	}
}
