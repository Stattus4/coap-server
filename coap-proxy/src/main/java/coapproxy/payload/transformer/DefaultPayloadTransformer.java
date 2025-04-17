package coapproxy.payload.transformer;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DefaultPayloadTransformer implements PayloadTransformer {

	public static final DefaultPayloadTransformer INSTANCE = new DefaultPayloadTransformer();

	private final ObjectMapper mapper = new ObjectMapper();

	private DefaultPayloadTransformer() {
	}

	@Override
	public String transform(String payload, Map<String, Object> dictionary) throws InvalidFormatException {
		if (dictionary == null || !dictionary.containsKey("device")) {
			throw new InvalidFormatException("Dictionary must contain a 'device' key.");
		}

		ObjectNode result = mapper.createObjectNode();

		result.put("device", String.valueOf(dictionary.get("device")));
		result.put("payload", payload);
		result.put("timestamp", System.currentTimeMillis() / 1000);

		try {
			String serializedResult = mapper.writeValueAsString(result);

			return serializedResult;
		} catch (Exception e) {
			throw new InvalidFormatException("Failed to serialize result to JSON.");
		}
	}
}
