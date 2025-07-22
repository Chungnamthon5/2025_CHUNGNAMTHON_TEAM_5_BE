package com.chungnamthon.cheonon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CheononApplication {

	public static void main(String[] args) {
		SpringApplication.run(CheononApplication.class, args);
	}

}
