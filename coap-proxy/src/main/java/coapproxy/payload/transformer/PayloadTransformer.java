package coapproxy.payload.transformer;

import java.util.Map;

public interface PayloadTransformer {

	public String transform(String payload, Map<String, Object> dictionary) throws InvalidFormatException;
}
