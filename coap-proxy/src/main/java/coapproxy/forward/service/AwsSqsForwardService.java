package coapproxy.forward.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

public class AwsSqsForwardService implements ForwardService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AwsSqsForwardService.class);

	private final SqsClient sqsClient = SqsClient.builder().build();
	private final String id;
	private final String queueName;

	private String queueUrl;

	public AwsSqsForwardService(String id, Map<String, Object> configMap) {
		this.id = id;

		queueName = (String) configMap.get("queue-name");

		if (queueName == null) {
			throw new IllegalArgumentException(this.getClass().getSimpleName() + ": Missing queue-name");
		}
	}

	@Override
	public void forward(String payload) {
		getQueueUrl();

		SendMessageRequest sendMessageRequest = SendMessageRequest.builder().queueUrl(queueUrl).messageBody(payload)
				.delaySeconds(0).build();

		SendMessageResponse sendMessageResponse = sqsClient.sendMessage(sendMessageRequest);

		LOGGER.info("ID: {} Message ID: {}", id, sendMessageResponse.messageId());
	}

	private void getQueueUrl() {
		if (queueUrl == null) {
			GetQueueUrlResponse getQueueUrlResponse = sqsClient
					.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build());

			queueUrl = getQueueUrlResponse.queueUrl();
		}
	}
}
