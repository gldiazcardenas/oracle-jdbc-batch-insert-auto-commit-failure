package com.gldc.oraclejdbcbatchautocommit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

	@Autowired
	private Scenario1 scenario1;

	@Autowired
	private Scenario2 scenario2;

	@Autowired
	private Scenario3 scenario3;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) {
		scenario3.run();
	}

}
