package coapproxy.payload.transformer;

import java.util.HashMap;
import java.util.Map;

public class PayloadTransformerRegistry {

	private static final Map<PayloadTransformerType, PayloadTransformer> registry = new HashMap<>();

	private PayloadTransformerRegistry() {
	}

	public static void register(PayloadTransformerType type, PayloadTransformer payloadTransformer) {
		registry.put(type, payloadTransformer);
	}

	public static PayloadTransformer get(PayloadTransformerType type) {
		PayloadTransformer payloadTransformer = registry.get(type);

		if (payloadTransformer == null) {
			throw new IllegalArgumentException("No payload transformer registered under type: " + type.name());
		}

		return payloadTransformer;
	}
}
