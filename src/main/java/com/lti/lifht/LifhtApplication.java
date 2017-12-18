package com.lti.lifht;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan(basePackageClasses = { LifhtApplication.class })
@SpringBootApplication
public class LifhtApplication {

	public static void main(String[] args) {
		SpringApplication.run(LifhtApplication.class, args);
	}
}
