package com.crajas.gcpsamples.pubsub.adapter;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectTopicName;

@Component
public class PubsubAdapter {



	public void pushWithGCPLibs(String gcpProjectId, String topicName, String message) {

		ProjectTopicName topic = ProjectTopicName.of(gcpProjectId, topicName);
		Publisher publisher = null;
		try {
			publisher = Publisher.newBuilder(topic).build();
			ByteString data = ByteString.copyFromUtf8(message);
			com.google.pubsub.v1.PubsubMessage pubsubMessage = com.google.pubsub.v1.PubsubMessage.newBuilder().setData(data).build();
			ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
			messageIdFuture.get(1, TimeUnit.MINUTES);
		} catch (Exception e) {
			System.err.format("Failed to create publisher %s ", e);
		} finally {
			if (publisher != null) {
				try {
					publisher.shutdown();
					publisher.awaitTermination(1, TimeUnit.MINUTES);
				} catch (Exception e) {
					System.err.format("Failed to close publisher %s ", e);
				}
			}
		}
	}



}
