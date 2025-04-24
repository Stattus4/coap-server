package coapproxy.server.resources;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.UriQueryParameter;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coapproxy.forward.service.ForwardService;
import coapproxy.forward.service.ForwardServiceFactory;
import coapproxy.payload.transformer.PayloadTransformer;
import coapproxy.payload.transformer.PayloadTransformerFactory;
import coapproxy.payload.transformer.PayloadTransformerType;

public class ResourceReadings extends CoapResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceReadings.class);

	public ResourceReadings(String name) {
		super(name);

		getAttributes().setTitle(this.getClass().getSimpleName());

		LOGGER.info("CoapResource added");
	}

	@Override
	public void handlePOST(CoapExchange exchange) {
		try {
			UriQueryParameter uriQueryParameter = exchange.getRequestOptions().getUriQueryParameter();
			String device = uriQueryParameter.getArgument("device");

			Map<String, Object> dictionary = new HashMap<>();
			dictionary.put("device", device);

			String requestPayload = exchange.getRequestText();

			PayloadTransformer payloadTransformer = PayloadTransformerFactory
					.get(PayloadTransformerType.DEFAULT_TRANSFORMER);
			String forwardPayload = payloadTransformer.transform(requestPayload, dictionary);

			ForwardService forwardService = ForwardServiceFactory.get("aws-sqs-staging");
			forwardService.forward(forwardPayload);

			LOGGER.info("Success - SourceContext: {} RequestCode: {} RequestOptions: {} RequestPayloadSize: {}",
					exchange.getSourceContext().toString(), exchange.getRequestCode(),
					exchange.getRequestOptions().toString(), exchange.getRequestPayloadSize());

			exchange.respond(ResponseCode.CREATED);
		} catch (Exception e) {
			LOGGER.info("Error - SourceContext: {} RequestCode: {} RequestOptions: {} ExceptionMessage: {}",
					exchange.getSourceContext().toString(), exchange.getRequestCode(),
					exchange.getRequestOptions().toString(), e.getMessage());

			exchange.respond(ResponseCode.INTERNAL_SERVER_ERROR, "Internal server error.");
		}
	}
}
