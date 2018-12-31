package com.crajas.gcpsamples.pubsub;


import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import com.crajas.gcpsamples.pubsub.service.MessageService;

@Component
public class JerseyConfig extends ResourceConfig {
	public JerseyConfig() {
		registerEndpoints();
	}

	private void registerEndpoints() {
		register(MessageService.class);
	}
}
