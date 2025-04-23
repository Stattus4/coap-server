package coapproxy.forward.service;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AwsSqsForwardServiceConfig implements ForwardServiceConfig {

	@JsonProperty(value = "queue-name", required = true)
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
