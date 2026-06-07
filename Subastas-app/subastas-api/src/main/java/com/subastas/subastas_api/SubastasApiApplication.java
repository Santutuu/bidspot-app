package com.subastas.subastas_api;




import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;
@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})

public class SubastasApiApplication {

	public static void main(String[] args) {

		TimeZone.setDefault(
				TimeZone.getTimeZone("America/Argentina/Buenos_Aires")
		);

		SpringApplication.run(SubastasApiApplication.class, args);
	}
}