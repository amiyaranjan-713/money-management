package com.qsp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MoneymanagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoneymanagementApplication.class, args);
	}

}
