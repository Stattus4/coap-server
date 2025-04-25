package coapproxy.payload.transformer;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DefaultPayloadTransformer implements PayloadTransformer {

	public static final PayloadTransformer INSTANCE = new DefaultPayloadTransformer();

	private final ObjectMapper mapper = new ObjectMapper();

	private DefaultPayloadTransformer() {
	}

	@Override
	public String transform(String payload, Map<String, Object> dictionary) throws InvalidFormatException {
		if (dictionary == null || !dictionary.containsKey("device")) {
			throw new InvalidFormatException(
					"[" + DefaultPayloadTransformer.class.getName() + "] Dictionary must contain 'device'");
		}

		ObjectNode result = mapper.createObjectNode();
		result.put("device", (String) dictionary.get("device"));
		result.put("payload", payload.trim());
		result.put("rule_key", "nbiot");
		result.put("timestamp", System.currentTimeMillis());

		try {
			return mapper.writeValueAsString(result);
		} catch (Exception e) {
			throw new RuntimeException("[" + DefaultPayloadTransformer.class.getName() + "] Failed to serialize");
		}
	}
}
