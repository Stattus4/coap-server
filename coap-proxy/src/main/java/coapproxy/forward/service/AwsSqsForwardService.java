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
	private final String queueName;

	public AwsSqsForwardService(Map<String, Object> configMap) {
		queueName = (String) configMap.get("queue-name");
	}

	@Override
	public void forward(String payload) {
		String queueUrl = getQueueUrl();

		SendMessageRequest sendMessageRequest = SendMessageRequest.builder().queueUrl(queueUrl).messageBody(payload)
				.delaySeconds(0).build();

		SendMessageResponse sendMessageResponse = sqsClient.sendMessage(sendMessageRequest);

		LOGGER.info("Message ID:{}", sendMessageResponse.messageId());
	}

	private String getQueueUrl() {
		GetQueueUrlResponse getQueueUrlResponse = sqsClient
				.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build());

		return getQueueUrlResponse.queueUrl();
	}
}
