package com.crajas.gcpsamples.pubsub.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.integration.outbound.PubSubMessageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import com.crajas.gcpsamples.springpubsub.Listener;

/**
 * REST endpoint that exposes a service to push messages to a topic. The
 * application expects the following configuration in application.yml <code>
gcp:
  project-id: ${PROJECT_ID}   				# name of the project
  pubsub:
    subscriber:
      queue-depth: 100
      subscription: ${SUBSCRIPTION_ID}		# name of subscription against topic 
      private-key-file: ${PRIVATE_KEY_FILE}
    topic: ${TOPIC_NAME}					# name of topic
 </code>
 * 
 * @author rcmohan
 *
 */
@Path("/messages")
@Component
public class MessageService {

	public static final String OUTPUT_CHANNEL = "pubsubOutputChannel";

	@Value("${gcp.project-id}")
	private String projectName;

	@Value("${gcp.pubsub.subscriber.subscription}")
	private String subscriptionName;

	@Value("${gcp.pubsub.topic}")
	private String topicName;

	@Autowired
	private PubsubOutboundGateway messagingGateway;	

	@Autowired
	private Listener listener;

	private ExecutorService executor = Executors.newFixedThreadPool(1);

	 @MessagingGateway(defaultRequestChannel = OUTPUT_CHANNEL)
     public interface PubsubOutboundGateway {

             void sendToPubsub(String text);
     }

	@Bean(name = "PubsubOutboundGateway")
	public MessageChannel appTypeUpgradeInput() {
	     return new DirectChannel();
	}
	
	@Bean
	@ServiceActivator(inputChannel = OUTPUT_CHANNEL)
    public MessageHandler messageSender(PubSubTemplate pubsubTemplate) {
            return new PubSubMessageHandler(pubsubTemplate, topicName);
    }

	@PostConstruct
	public void init() throws IOException, GeneralSecurityException {
		this.executor.submit(this.listener);
	}

	@POST
	public Response publishMessage(@RequestParam("message") String message) {
		this.messagingGateway.sendToPubsub(message);
		return Response.ok().build();
	}
}