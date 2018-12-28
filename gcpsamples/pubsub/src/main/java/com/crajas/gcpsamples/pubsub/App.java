package com.crajas.gcpsamples.pubsub;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
public class App {

	
	public static void main(String[] args) throws IOException {
		SpringApplication.run(App.class, args);
	}
}
