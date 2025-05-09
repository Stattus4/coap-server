package coapproxy.forward.service.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotEmpty;

public class AwsSqsForwardServiceConfig implements ForwardServiceConfig {

	@JsonProperty(value = "queue-name", required = true)
	@NotEmpty
	private String queueName;

	public AwsSqsForwardServiceConfig() {
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
}
