package coapproxy.forward.service;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AwsKinesisForwardServiceConfig implements ForwardServiceConfig {

	@JsonProperty(value = "stream-name", required = true)
	private String streamName;

	@JsonProperty(value = "partition-key", required = true)
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
