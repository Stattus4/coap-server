package coapproxy.forward.service;

import java.util.Map;

public class ForwardServiceFactory {

	static {
		Map<String, Map<String, Object>> configMap = ForwardServiceConfigLoader.getConfigMap();

		for (String serviceId : configMap.keySet()) {
			String forwardServiceClass = (String) configMap.get(serviceId).get("forward-service-class");

			try {
				Class<?> clazz = Class.forName(forwardServiceClass);

				ForwardService forwardService = (ForwardService) clazz.getConstructor(Map.class)
						.newInstance(configMap.get(serviceId));

				ForwardServiceRegistry.register(serviceId, forwardService);
			} catch (Exception e) {
				throw new RuntimeException("Failed to instantiate ForwardService for: " + serviceId, e);
			}
		}

	}

	public static ForwardService get(String serviceId) {
		return ForwardServiceRegistry.get(serviceId);
	}
}
