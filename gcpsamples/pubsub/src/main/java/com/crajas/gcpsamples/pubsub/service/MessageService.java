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
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import com.crajas.gcpsamples.pubsub.StackdriverTraceConfig;
import com.crajas.gcpsamples.pubsub.adapter.Listener;
import com.crajas.gcpsamples.pubsub.adapter.PubsubAdapter;

import io.opencensus.common.Scope;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.Tracing;
/**
 * REST endpoint that exposes a service to push messages to a topic.
 * The application expects the following configuration in application.yml 
<code>
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

	/*
	 * OpenCensus Tracer
	 */
	private static final Tracer tracer = Tracing.getTracer();

	@Autowired
	private PubsubAdapter messagingGateway;

	@Autowired
	private StackdriverTraceConfig sdtConfig;
	
	@Value("${gcp.project-id}")
	private String projectName;

	@Value("${gcp.pubsub.subscriber.subscription}")
	private String subscriptionName;

	@Value("${gcp.pubsub.topic}")
	private String topicName;

	private Listener listener;

	private ExecutorService executor = Executors.newFixedThreadPool(1);
	
	@PostConstruct
	public void init() throws IOException, GeneralSecurityException {
		this.listener = new Listener(projectName, subscriptionName);
		this.executor.submit(this.listener);
		tracer.getCurrentSpan().addAnnotation("Construction done");
	}

	@POST
	public Response publishMessage(@RequestParam("message") String message) {
		String spanName = this.getClass().getSimpleName();
		try (Scope ss = sdtConfig.startSpan(spanName)) {

			messagingGateway.pushWithGCPLibs(projectName, topicName, message);
			
			tracer.getCurrentSpan().addAnnotation("Finished posting messages");
		}
		return Response.ok().build();
	}

}