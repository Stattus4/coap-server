package coapproxy.forward.service;

import java.util.Map;

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
			Class<?> clazz = Class.forName(forwardServiceClass);

			forwardService = (ForwardService) clazz.getConstructor(String.class, Map.class).newInstance(id,
					configMap.get(id));

			ForwardServiceRegistry.register(id, forwardService);

			return forwardService;
		} catch (Exception e) {
			throw new RuntimeException(ForwardServiceFactory.class.getSimpleName() + ": Failed to instantiate for ID: "
					+ id + " Message: " + e.getCause().getMessage());
		}
	}
}
