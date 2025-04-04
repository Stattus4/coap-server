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

		getAttributes().setTitle("Resource Hello");

		logger.info("CoapResource added");
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		try {
			logger.info("Success - SourceContext:{} RequestCode:{} RequestOptions:{}",
					exchange.getSourceContext().toString(), exchange.getRequestCode(),
					exchange.getRequestOptions().toString());

			exchange.respond(ResponseCode.CONTENT, "Hello!");
		} catch (Exception e) {
			logger.info("Error - SourceContext:{} RequestCode:{} RequestOptions:{} - Message:{}",
					exchange.getSourceContext().toString(), exchange.getRequestCode(),
					exchange.getRequestOptions().toString(), e.getMessage());

			exchange.respond(ResponseCode.INTERNAL_SERVER_ERROR, "Internal server error.");
		}
	}
}
