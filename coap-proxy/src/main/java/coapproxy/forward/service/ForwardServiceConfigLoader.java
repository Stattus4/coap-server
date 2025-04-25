package coapproxy.forward.service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ForwardServiceConfigLoader {

	private static final String configFilename = "coapproxy/forward-service-config.json";

	private static final Map<String, Object> configMap = new HashMap<>();
	private static final ObjectMapper mapper = new ObjectMapper();

	public static Map<String, Object> getConfigMap() {
		if (configMap.isEmpty()) {
			try (InputStream is = ForwardServiceConfigLoader.class.getClassLoader()
					.getResourceAsStream(configFilename)) {
				TypeReference<Map<String, Map<String, Object>>> typeRef = new TypeReference<>() {
				};

				configMap.putAll(mapper.readValue(is, typeRef));
			} catch (Exception e) {
				throw new RuntimeException("Failed to load " + configFilename, e);
			}
		}

		return configMap;
	}
}
