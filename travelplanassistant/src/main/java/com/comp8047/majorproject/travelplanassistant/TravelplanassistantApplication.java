package com.comp8047.majorproject.travelplanassistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TravelplanassistantApplication {

	public static void main(String[] args) {
		SpringApplication.run(TravelplanassistantApplication.class, args);
	}

}
