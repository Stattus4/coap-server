package coapproxy.server.resources.config;

public class ResourceReadingsConfig {

	private static final String COAPPROXY_DEFAULT_FORWARD_SERVICE_ENV = "COAPPROXY_DEFAULT_FORWARD_SERVICE";

	private static final String defaultForwardService;

	static {
		defaultForwardService = System.getenv(COAPPROXY_DEFAULT_FORWARD_SERVICE_ENV);
	}

	public static String getDefaultForwardService() {
		return validateOrThrow(defaultForwardService, COAPPROXY_DEFAULT_FORWARD_SERVICE_ENV);
	}

	private static String validateOrThrow(String value, String env) {
		if (value == null || value.trim().isBlank()) {
			throw new IllegalStateException(
					"[" + ResourceReadingsConfig.class.getName() + "] Missing or empty environment variable: " + env);
		}

		return value;
	}
}
