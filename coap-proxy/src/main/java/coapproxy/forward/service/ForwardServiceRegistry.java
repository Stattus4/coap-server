package coapproxy.forward.service;

import java.util.HashMap;
import java.util.Map;

public class ForwardServiceRegistry {

	private static final Map<String, ForwardService> registry = new HashMap<>();

	private ForwardServiceRegistry() {
	}

	public static void register(String serviceId, ForwardService forwardService) {
		registry.put(serviceId, forwardService);
	}

	public static ForwardService get(String serviceId) {
		ForwardService forwardService = registry.get(serviceId);

		if (forwardService == null) {
			throw new IllegalArgumentException("No forward service registered under id: " + serviceId);
		}

		return forwardService;
	}
}
