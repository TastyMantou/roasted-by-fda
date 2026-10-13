package com.roastedbyfda.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Arrays;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {

		System.out.println(
				"OS ENV SPRING_PROFILES_ACTIVE = " +
						System.getenv("SPRING_PROFILES_ACTIVE")
		);

		System.out.println(
				"JVM spring.profiles.active = " +
						System.getProperty("spring.profiles.active")
		);

		ConfigurableApplicationContext context =
				SpringApplication.run(BackendApplication.class, args);

		ConfigurableEnvironment env = context.getEnvironment();

		System.out.println(
				"ACTUAL ACTIVE PROFILES = " +
						Arrays.toString(env.getActiveProfiles())
		);
	}
}