package com.lti.lifht;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Main {

	public static void main(String[] args) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(4);
		System.out.println(encoder.encode("inactive"));

	}

}
