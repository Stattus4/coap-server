package coapproxy.server.resources;

import java.util.List;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.network.Endpoint;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ResourceInfo extends CoapResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceInfo.class);
	private final CoapServer server;

	public ResourceInfo(String name, CoapServer server) {
		super(name);
		this.server = server;

		getAttributes().setTitle(this.getClass().getSimpleName());

		LOGGER.info("CoapResource added");
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			ObjectNode jsonResponse = objectMapper.createObjectNode();

			ArrayNode endpointsArray = objectMapper.createArrayNode();
			List<Endpoint> endpoints = server.getEndpoints();

			for (Endpoint endpoint : endpoints) {
				ObjectNode endpointJson = objectMapper.createObjectNode();
				endpointJson.put("hostName", endpoint.getAddress().getHostName());
				endpointJson.put("uri", endpoint.getUri().toString());
				endpointsArray.add(endpointJson);
			}

			jsonResponse.set("endpoints", endpointsArray);

			ArrayNode resourcesArray = objectMapper.createArrayNode();

			server.getRoot().getChildren().forEach(resource -> {
				ObjectNode resourceJson = objectMapper.createObjectNode();
				resourceJson.put("name", resource.getName());
				resourceJson.put("path", resource.getPath());
				resourceJson.put("title", resource.getAttributes().getTitle());
				resourceJson.put("uri", resource.getURI());
				resourcesArray.add(resourceJson);
			});

			jsonResponse.set("resources", resourcesArray);

			LOGGER.info("Success - SourceContext: {} RequestCode: {} RequestOptions: {} RequestPayloadSize: {}",
					exchange.getSourceContext().toString(), exchange.getRequestCode(),
					exchange.getRequestOptions().toString(), exchange.getRequestPayloadSize());

			exchange.respond(ResponseCode.CONTENT, objectMapper.writeValueAsString(jsonResponse),
					MediaTypeRegistry.APPLICATION_JSON);
		} catch (Exception e) {
			LOGGER.info("Error - SourceContext: {} RequestCode: {} RequestOptions: {} ExceptionMessage: {}",
					exchange.getSourceContext().toString(), exchange.getRequestCode(),
					exchange.getRequestOptions().toString(), e.getMessage());

			exchange.respond(ResponseCode.INTERNAL_SERVER_ERROR, "Internal server error.");
		}
	}
}
