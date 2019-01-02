package com.crajas.gcpsamples.springpubsub;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;


@Configuration
public class Listener extends Thread {

	@Value("${gcp.pubsub.subscriber.subscription}")
	private String subscriptionName;
	

	public static final String INPUT_CHANNEL =  "pubsubInputChannel";

	@Bean
	public MessageChannel pubsubInputChannel() {
		return new DirectChannel();
	}
	
	private static final BlockingQueue<String> messages = new LinkedBlockingDeque<>();


	@Bean
	public PubSubInboundChannelAdapter messageChannelAdapter(@Qualifier(INPUT_CHANNEL) MessageChannel inputChannel, PubSubTemplate pubSubTemplate) {
		PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate, subscriptionName);
		adapter.setOutputChannel(inputChannel);
		return adapter;
	}
	
    @ServiceActivator(inputChannel = INPUT_CHANNEL)
    public void messageReceiver(String payload) {
		messages.offer(payload);
    }
    
    @Override
    public void run() {
    	while(true) {
			try {
				String payload = messages.take();
	    		System.out.format("Received Message [%s]", payload);
			} catch (InterruptedException e) {
				System.err.println("Interrupted while taking message: " + e.getMessage());
			}
    	}
    }
}
