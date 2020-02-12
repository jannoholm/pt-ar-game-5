package com.playtech.ptargame5.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		// TODO Add HTTP authentication for private APIs
		SpringApplication.run(Application.class, args);
	}
}
