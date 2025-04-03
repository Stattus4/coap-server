package coap.server;

import java.util.List;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.network.Endpoint;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceInfo extends CoapResource {

	private static final Logger logger = LoggerFactory.getLogger(ResourceInfo.class);
	private final CoapServer server;

	public ResourceInfo(String name, CoapServer server) {
		super(name);
		this.server = server;

		getAttributes().setTitle("Resource Info");

		logger.info("CoapResource added");
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		try {
			JSONObject jsonResponse = new JSONObject();

			JSONArray endpointsArray = new JSONArray();
			List<Endpoint> endpoints = server.getEndpoints();
			for (Endpoint endpoint : endpoints) {
				JSONObject endpointJson = new JSONObject();
				endpointJson.put("hostName", endpoint.getAddress().getHostName());
				endpointJson.put("uri", endpoint.getUri().toString());
				endpointsArray.put(endpointJson);
			}
			jsonResponse.put("endpoints", endpointsArray);

			JSONArray resourcesArray = new JSONArray();
			server.getRoot().getChildren().forEach(resource -> {
				JSONObject resourceJson = new JSONObject();
				resourceJson.put("name", resource.getName());
				resourceJson.put("path", resource.getPath());
				resourceJson.put("title", resource.getAttributes().getTitle());
				resourceJson.put("uri", resource.getURI());
				resourcesArray.put(resourceJson);
			});
			jsonResponse.put("resources", resourcesArray);

			logger.info("Success - SourceContext:{} RequestCode:{} RequestOptions:{}",
					exchange.getSourceContext().toString(), exchange.getRequestCode(),
					exchange.getRequestOptions().toString());

			exchange.respond(ResponseCode.CONTENT, jsonResponse.toString(), MediaTypeRegistry.APPLICATION_JSON);
		} catch (Exception e) {
			logger.info("Error - SourceContext:{} RequestCode:{} RequestOptions:{} - Message:{}",
					exchange.getSourceContext().toString(), exchange.getRequestCode(),
					exchange.getRequestOptions().toString(), e.getMessage());

			exchange.respond(ResponseCode.INTERNAL_SERVER_ERROR, "Internal server error.");
		}
	}
}
