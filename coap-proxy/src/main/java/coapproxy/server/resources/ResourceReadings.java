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
import coapproxy.server.resources.config.ResourceReadingsConfig;

public class ResourceReadings extends CoapResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceReadings.class);

	private final ForwardService forwardService;
	private final PayloadTransformer payloadTransformer;

	public ResourceReadings(String name) {
		super(name);

		forwardService = ForwardServiceFactory.get(ResourceReadingsConfig.getDefaultForwardService());
		payloadTransformer = PayloadTransformerFactory.get(PayloadTransformerType.DEFAULT_TRANSFORMER);

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
			String forwardPayload = payloadTransformer.transform(requestPayload, dictionary);

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
