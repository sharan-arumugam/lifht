package com.lti.lifht.controller;

import static com.lti.lifht.constant.PatternConstant.HAS_ANY_ROLE_ADMIN;
import static com.lti.lifht.constant.PatternConstant.HAS_ANY_ROLE_EMPLOYEE;
import static com.lti.lifht.constant.PatternConstant.HAS_ROLE_SUPER;
import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static java.util.Collections.disjoint;

import java.util.List;

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

	@GetMapping("/")
	public String defaultIndex(HttpSession session, HttpServletResponse response) {

		String psNumber = valueOf(session.getAttribute("psNumber"));
		String psName = valueOf(session.getAttribute("psName"));
		String authorities = valueOf(session.getAttribute("authorities")).replace("[", "").replace("]", "").trim();

		List<String> authoritiesList = asList(authorities.split(","));

		response.addHeader("psNumber", psNumber);
		response.addHeader("psName", psName);
		response.addHeader("authorities", authorities);

		List<String> adminAuthorities = asList("ROLE_ADMIN", "ROLE_SUPER");

		return !disjoint(adminAuthorities, authoritiesList) ? admin() : user();
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
