package coapproxy.forward.service.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotEmpty;

public class AwsKinesisForwardServiceConfig implements ForwardServiceConfig {

	@JsonProperty(value = "stream-name", required = true)
	@NotEmpty
	private String streamName;

	@JsonProperty(value = "partition-key", required = true)
	@NotEmpty
	private String partitionKey;

	public AwsKinesisForwardServiceConfig() {
	}

	public String getStreamName() {
		return streamName;
	}

	public void setStreamName(String streamName) {
		this.streamName = streamName;
	}

	public String getPartitionKey() {
		return partitionKey;
	}

	public void setPartitionKey(String partitionKey) {
		this.partitionKey = partitionKey;
	}
}
