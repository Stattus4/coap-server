package coapproxy.forward.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.PutRecordRequest;
import software.amazon.awssdk.services.kinesis.model.PutRecordResponse;

public class AwsKinesisForwardService implements ForwardService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AwsKinesisForwardService.class);

	public static final String FORWARD_SERVICE_CONFIG_CLASS = "coapproxy.forward.service.AwsKinesisForwardServiceConfig";

	private final KinesisClient kinesisClient = KinesisClient.builder().build();
	private final String id;
	private final String streamName;
	private final String partitionKey;

	public AwsKinesisForwardService(String id, AwsKinesisForwardServiceConfig config) {
		this.id = id;

		streamName = (String) config.getStreamName();
		partitionKey = (String) config.getPartitionKey();
	}

	@Override
	public void forward(String payload) {
		PutRecordRequest putRecordRequest = PutRecordRequest.builder().partitionKey(partitionKey).streamName(streamName)
				.data(SdkBytes.fromUtf8String(payload)).build();

		PutRecordResponse putRecordResponse = kinesisClient.putRecord(putRecordRequest);

		LOGGER.info("ID: {} Sequence Number: {}", id, putRecordResponse.sequenceNumber());
	}
}
