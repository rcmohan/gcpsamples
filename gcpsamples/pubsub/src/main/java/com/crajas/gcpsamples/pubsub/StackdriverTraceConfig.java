package com.crajas.gcpsamples.pubsub;

import java.io.IOException;

import org.springframework.stereotype.Component;

import io.opencensus.common.Scope;
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceConfiguration;
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceExporter;
import io.opencensus.trace.Span;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.config.TraceConfig;
import io.opencensus.trace.config.TraceParams;
import io.opencensus.trace.export.ExportComponent;
import io.opencensus.trace.samplers.Samplers;
import lombok.extern.slf4j.Slf4j;

/**
 * Manages configuration of stackdriver config
 * 
 * @author B002471
 */
@Component
@Slf4j
public class StackdriverTraceConfig {
	
	/*
	 * OpenCensus Tracer
	 */
	private static final Tracer tracer = Tracing.getTracer();

	public StackdriverTraceConfig() {
		try {
			createAndRegister();
			log.info("Registered StackdriverTraceConfiguration");
		} catch (Throwable e) {
			log.error("", e);
			e.printStackTrace();
		}
	}
	
	public Scope startSpan(String spanName) {
		return tracer.spanBuilder(spanName).setSampler(Samplers.alwaysSample()).startScopedSpan();
	}

	private void createAndRegister() throws IOException {
		StackdriverTraceConfiguration config = StackdriverTraceConfiguration.builder().build();
		System.out.println(config);
		StackdriverTraceExporter.createAndRegister(config);
		log.info("Stackdriver tracer " + Tracing.getExportComponent().getSpanExporter());
		ExportComponent exportComponent = Tracing.getExportComponent();
		TraceConfig traceConfig = Tracing.getTraceConfig();
		TraceParams activeTraceParams = traceConfig.getActiveTraceParams();
		traceConfig.updateActiveTraceParams(activeTraceParams.toBuilder().setSampler(Samplers.alwaysSample()).build());
		log.info(exportComponent.getRunningSpanStore().getSummary().toString());
	}


	public Span createSpan(String spanName) {
		return tracer.spanBuilder(spanName).startSpan();
	}

}
