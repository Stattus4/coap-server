package coapproxy.forward.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ForwardServiceRegistry {

	private static final Map<String, ForwardService> registry = new ConcurrentHashMap<>();

	private ForwardServiceRegistry() {
	}

	public static void register(String id, ForwardService forwardService) {
		registry.put(id, forwardService);
	}

	public static ForwardService get(String id) {
		return registry.get(id);
	}
}
