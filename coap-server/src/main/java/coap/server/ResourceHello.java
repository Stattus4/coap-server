package coap.server;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceHello extends CoapResource {

	private static final Logger logger = LoggerFactory.getLogger(ResourceHello.class);

	public ResourceHello(String name) {
		super(name);

		getAttributes().setTitle("Hello Resource");

		logger.info("CoapResource added");
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		logger.info("GET request: {}", exchange.getSourceAddress());

		exchange.respond(ResponseCode.CONTENT, "Hello!");
	}
}