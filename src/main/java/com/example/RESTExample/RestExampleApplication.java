package com.example.RESTExample;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RestExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestExampleApplication.class, args);
	}

	@Bean
	public ObjectMapper getObjectMapper() {
		return new ObjectMapper();
	}
}
