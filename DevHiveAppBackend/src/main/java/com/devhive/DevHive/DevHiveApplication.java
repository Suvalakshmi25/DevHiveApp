package com.devhive.DevHive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DevHiveApplication {

	public static void main(String[] args) {
		System.setProperty("server.port", "8080");
		SpringApplication.run(DevHiveApplication.class, args);
	}

}
