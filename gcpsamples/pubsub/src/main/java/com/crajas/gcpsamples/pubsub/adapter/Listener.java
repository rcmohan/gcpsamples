package com.crajas.gcpsamples.pubsub.adapter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;

public class Listener implements Runnable {

	private Subscriber subscriber;
	private MessageReceiver receiver;
	private ProjectSubscriptionName subscription;
	private static final BlockingQueue<PubsubMessage> messages = new LinkedBlockingDeque<>();

	public Listener(String projectName, String subscriptionName) {
		subscription = ProjectSubscriptionName.of(projectName, subscriptionName);
	}

	public Listener subscription(ProjectSubscriptionName subscription) {
		this.subscription = subscription;
		return this;
	}
	
	public void run() {
		System.out.format("Starting listener against %s\n", this.subscription);
		receiver = new MessageReceiver() {
			@Override
			public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
				System.out.println("got message: " + message.getData().toStringUtf8());
				messages.offer(message);
				consumer.ack();
			}
		};

		try {
			subscriber = Subscriber.newBuilder(subscription, receiver).build();
			subscriber.startAsync().awaitRunning();
			System.out.format("Subscriber %s started against %s using listener %s \n", subscriber, subscription, receiver);
			while (true) {
				try {
					PubsubMessage message = messages.take();
					System.out.println("Message Id: " + message.getMessageId());
					System.out.println("Data: " + message.getData().toStringUtf8());
				} catch (InterruptedException ie) {
					System.err.println("Encountered error while taking message: " + ie);
				}
			}
		} finally {
			if (subscriber != null) {
				subscriber.stopAsync();
			}
		}
	}
}