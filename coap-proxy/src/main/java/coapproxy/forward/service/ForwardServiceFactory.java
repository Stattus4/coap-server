package coapproxy.forward.service;

import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ForwardServiceFactory {

	private ForwardServiceFactory() {
	}

	public static ForwardService get(String id) {
		ForwardService forwardService = ForwardServiceRegistry.get(id);

		if (forwardService != null) {
			return forwardService;
		}

		Map<String, Map<String, Object>> configMap = ForwardServiceConfigLoader.getConfigMap();

		String forwardServiceClass = (String) configMap.get(id).get("forward-service-class");

		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, true);
			mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			Class<?> clazz = Class.forName(forwardServiceClass);

			String forwardServiceConfigClass = (String) clazz.getDeclaredField("FORWARD_SERVICE_CONFIG_CLASS")
					.get(String.class);

			Class<?> configClazz = Class.forName(forwardServiceConfigClass);

			ForwardServiceConfig forwardServiceConfig = (ForwardServiceConfig) mapper.convertValue(configMap.get(id),
					configClazz);

			forwardService = (ForwardService) clazz.getConstructor(String.class, forwardServiceConfig.getClass())
					.newInstance(id, forwardServiceConfig);

			ForwardServiceRegistry.register(id, forwardService);

			return forwardService;
		} catch (Exception e) {
			throw new RuntimeException(ForwardServiceFactory.class.getSimpleName() + ": Failed to instantiate for ID: "
					+ id + " Message: " + e.getMessage());
		}
	}
}
