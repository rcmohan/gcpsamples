package com.crajas.gcpsamples.trace;


import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.wadl.internal.WadlResource;
import org.springframework.stereotype.Component;

import com.crajas.gcpsamples.trace.resource.BookResource;

@Component
public class JerseyConfig extends ResourceConfig {
	public JerseyConfig() {
		registerEndpoints();
	}

	private void registerEndpoints() {
		register(WadlResource.class);
		register(BookResource.class);
	}
}
