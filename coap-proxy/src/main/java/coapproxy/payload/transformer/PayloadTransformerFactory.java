package coapproxy.payload.transformer;

public class PayloadTransformerFactory {

	public static PayloadTransformer getPayloadTransformer(PayloadTransformerType payloadTransformerType) {
		switch (payloadTransformerType) {
		case DEFAULT_TRANSFORMER: {
			return DefaultPayloadTransformer.INSTANCE;
		}
		default:
			throw new IllegalArgumentException("Unsupported payload transformer type.");
		}
	}
}
