package coapproxy.forward.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ForwardServiceConfigLoader {

	private static final String configFilename = "coapproxy/forward-service-config.json";
	private static final Map<String, Map<String, Object>> configMap = new HashMap<>();

	static {
		ObjectMapper mapper = new ObjectMapper();

		try {
			InputStream is = ForwardServiceConfigLoader.class.getClassLoader().getResourceAsStream(configFilename);

			TypeReference<Map<String, Map<String, Object>>> typeRef = new TypeReference<>() {
			};

			configMap.putAll(mapper.readValue(is, typeRef));

			is.close();
		} catch (IOException e) {
			throw new RuntimeException("Failed to load " + configFilename, e);
		}
	}

	public static Map<String, Map<String, Object>> getConfigMap() {
		return configMap;
	}
}
