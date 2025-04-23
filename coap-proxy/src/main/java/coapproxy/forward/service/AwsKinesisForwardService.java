package coapproxy.forward.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.PutRecordRequest;
import software.amazon.awssdk.services.kinesis.model.PutRecordResponse;

public class AwsKinesisForwardService implements ForwardService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AwsKinesisForwardService.class);

	private final KinesisClient kinesisClient = KinesisClient.builder().build();
	private final String streamName;
	private final String partitionKey;

	public AwsKinesisForwardService(Map<String, Object> configMap) {
		streamName = (String) configMap.get("stream-name");
		partitionKey = (String) configMap.get("partition-key");
	}

	@Override
	public void forward(String payload) {
		PutRecordRequest putRecordRequest = PutRecordRequest.builder().partitionKey(partitionKey).streamName(streamName)
				.data(SdkBytes.fromUtf8String(payload)).build();

		PutRecordResponse putRecordResponse = kinesisClient.putRecord(putRecordRequest);

		LOGGER.info("Sequence Number:{}", putRecordResponse.sequenceNumber());
	}
}
