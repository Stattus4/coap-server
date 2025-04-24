package coapproxy.server.resources;

import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.elements.util.StringUtil;
import org.eclipse.californium.oscore.OSCoreResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OscoreResourceHello extends OSCoreResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(OscoreResourceHello.class);

	private static final byte[] rid = StringUtil.hex2ByteArray("02");

	public OscoreResourceHello(String name, boolean isProtected) {
		super(name, isProtected);

		getAttributes().setTitle(this.getClass().getSimpleName());

		LOGGER.info("CoapResource added");
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		LOGGER.info("Success - SourceContext: {} RequestCode: {} RequestOptions: {} RequestPayloadSize: {}",
				exchange.getSourceContext().toString(), exchange.getRequestCode(),
				exchange.getRequestOptions().toString(), exchange.getRequestPayloadSize());

		exchange.setMaxAge(30);
		exchange.respond(ResponseCode.CONTENT, "Hello!", MediaTypeRegistry.TEXT_PLAIN);
	}
}
