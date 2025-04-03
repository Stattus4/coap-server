package coap.server;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceReadings extends CoapResource {

	private static final Logger logger = LoggerFactory.getLogger(ResourceHello.class);

	public ResourceReadings(String name) {
		super(name);

		getAttributes().setTitle("Resource Readings");

		logger.info("CoapResource added");
	}

	@Override
	public void handlePOST(CoapExchange exchange) {
		try {
			String payload = exchange.getRequestText();
			JSONObject jsonRequest = new JSONObject(payload);
			String deviceId = jsonRequest.getString("deviceId");

			logger.info("Success - SourceContext:{} RequestCode:{} RequestOptions:{}",
					exchange.getSourceContext().toString(), exchange.getRequestCode(),
					exchange.getRequestOptions().toString());

			exchange.respond(ResponseCode.CREATED);
		} catch (org.json.JSONException e) {
			logger.info("Error - SourceContext:{} RequestCode:{} RequestOptions:{} - Message:{}",
					exchange.getSourceContext().toString(), exchange.getRequestCode(),
					exchange.getRequestOptions().toString(), e.getMessage());

			exchange.respond(ResponseCode.BAD_REQUEST, "Invalid JSON format.");
		} catch (Exception e) {
			logger.info("Error - SourceContext:{} RequestCode:{} RequestOptions:{} - Message:{}",
					exchange.getSourceContext().toString(), exchange.getRequestCode(),
					exchange.getRequestOptions().toString(), e.getMessage());

			exchange.respond(ResponseCode.INTERNAL_SERVER_ERROR, "Internal server error.");
		}
	}
}
