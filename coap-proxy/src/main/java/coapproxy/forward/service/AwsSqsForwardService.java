package coapproxy.forward.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coapproxy.forward.service.config.AwsSqsForwardServiceConfig;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

public class AwsSqsForwardService implements ForwardService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AwsSqsForwardService.class);

	public static final String FORWARD_SERVICE_CONFIG_CLASS = "coapproxy.forward.service.config.AwsSqsForwardServiceConfig";

	private final SqsClient sqsClient = SqsClient.builder().build();
	private final String id;
	private final AwsSqsForwardServiceConfig config;

	private String queueUrl;

	public AwsSqsForwardService(String id, AwsSqsForwardServiceConfig config) {
		this.id = id;
		this.config = config;
	}

	@Override
	public void forward(String payload) {
		getQueueUrl();

		SendMessageRequest sendMessageRequest = SendMessageRequest.builder().queueUrl(queueUrl).messageBody(payload)
				.delaySeconds(0).build();

		SendMessageResponse sendMessageResponse = sqsClient.sendMessage(sendMessageRequest);

		LOGGER.info("[{}] Message ID: {}", id, sendMessageResponse.messageId());
	}

	private void getQueueUrl() {
		if (queueUrl == null) {
			GetQueueUrlResponse getQueueUrlResponse = sqsClient
					.getQueueUrl(GetQueueUrlRequest.builder().queueName(config.getQueueName()).build());

			queueUrl = getQueueUrlResponse.queueUrl();
		}
	}
}
