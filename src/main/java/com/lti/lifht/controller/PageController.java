package com.lti.lifht.controller;

import static com.lti.lifht.constant.PatternConstant.HAS_ANY_ROLE_ADMIN;
import static com.lti.lifht.constant.PatternConstant.HAS_ANY_ROLE_EMPLOYEE;
import static com.lti.lifht.constant.PatternConstant.HAS_ROLE_SUPER;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@PreAuthorize(HAS_ANY_ROLE_EMPLOYEE)
public class PageController {

	@GetMapping
	public String defaultIndex(HttpSession session, HttpServletResponse response) {

		String psNumber = String.valueOf(session.getAttribute("psNumber"));
		String psName = String.valueOf(session.getAttribute("psName"));
		String authorities = String.valueOf(session.getAttribute("authorities"));

		response.addHeader("psNumber", psNumber);
		response.addHeader("psName", psName);
		response.addHeader("authorities", authorities);

		return "ROLE_ADMIN, ROLE_SUPER".contains(authorities) ? admin() : user();
	}

	@GetMapping("/staff")
	public String user() {
		return "staffindex";
	}

	@GetMapping("/admin")
	@PreAuthorize(HAS_ANY_ROLE_ADMIN)
	public String admin() {
		return "adminindex";
	}

	@GetMapping("/upload")
	@PreAuthorize(HAS_ROLE_SUPER)
	public String upload() {
		return "upload";
	}

}
