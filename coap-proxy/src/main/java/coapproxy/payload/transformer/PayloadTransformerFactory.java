package coapproxy.payload.transformer;

public class PayloadTransformerFactory {

	static {
		PayloadTransformerRegistry.register(PayloadTransformerType.DEFAULT_TRANSFORMER,
				DefaultPayloadTransformer.INSTANCE);
	}

	private PayloadTransformerFactory() {
	}

	public static PayloadTransformer get(PayloadTransformerType type) {
		return PayloadTransformerRegistry.get(type);
	}
}
