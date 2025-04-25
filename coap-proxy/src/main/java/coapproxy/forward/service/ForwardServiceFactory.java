package coapproxy.forward.service;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class ForwardServiceFactory {

	private static final ObjectMapper objectMapper = new ObjectMapper();;
	private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	static {
		objectMapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, true);
		objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	private ForwardServiceFactory() {
	}

	public static ForwardService get(String id) {
		ForwardService forwardService = ForwardServiceRegistry.get(id);

		if (forwardService != null) {
			return forwardService;
		}

		try {
			forwardService = forwardServiceNewInstance(id);

			ForwardServiceRegistry.register(id, forwardService);

			return forwardService;
		} catch (Exception e) {
			throw new RuntimeException("[" + ForwardServiceFactory.class.getName() + "] Failed to register [" + id
					+ "] ExceptionMessage: " + e.getMessage());
		}
	}

	private static ForwardService forwardServiceNewInstance(String id) {
		try {
			Map<String, Object> configMap = ForwardServiceConfigLoader.getConfigMap();

			Map<String, Object> serviceId = (Map<String, Object>) configMap.get(id);
			String forwardServiceClass = (String) serviceId.get("forward-service-class");
			Class<?> clazz = Class.forName(forwardServiceClass);

			String forwardServiceConfigClass = (String) clazz.getDeclaredField("FORWARD_SERVICE_CONFIG_CLASS")
					.get(String.class);
			Class<?> configClazz = Class.forName(forwardServiceConfigClass);

			ForwardServiceConfig forwardServiceConfig = (ForwardServiceConfig) objectMapper
					.convertValue(configMap.get(id), configClazz);

			validateForwardServiceConfig(forwardServiceConfig);

			ForwardService forwardService = (ForwardService) clazz
					.getConstructor(String.class, forwardServiceConfig.getClass())
					.newInstance(id, forwardServiceConfig);

			return forwardService;
		} catch (Exception e) {
			throw new RuntimeException("Failed to instantiate [" + id + "] ExceptionMessage: " + e.getMessage());
		}
	}

	private static void validateForwardServiceConfig(ForwardServiceConfig forwardServiceConfig) {
		Set<ConstraintViolation<ForwardServiceConfig>> violations = validator.validate(forwardServiceConfig);

		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(violations);
		}
	}
}
