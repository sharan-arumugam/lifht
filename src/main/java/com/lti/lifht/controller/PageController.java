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
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
@PreAuthorize(HAS_ANY_ROLE_EMPLOYEE)
public class PageController {

	@GetMapping("/")
	public String defaultIndex(HttpSession session, HttpServletResponse response) {

		String authorities = String.valueOf(session.getAttribute("authorities"));
		return "ROLE_ADMIN, ROLE_SUPER".contains(authorities) ? user(session, response) : admin(session, response);
	}

	@GetMapping("/staff")
	public String user(HttpSession session, HttpServletResponse response) {

		response.addHeader("username", String.valueOf(session.getAttribute("username")));
		response.addHeader("authorities", String.valueOf(session.getAttribute("authorities")));

		return "staffindex";
	}

	@GetMapping("/admin")
	@PreAuthorize(HAS_ANY_ROLE_ADMIN)
	public String admin(HttpSession session, HttpServletResponse response) {

		response.addHeader("username", String.valueOf(session.getAttribute("username")));
		response.addHeader("authorities", String.valueOf(session.getAttribute("authorities")));

		return "adminindex";
	}

	@GetMapping("/upload")
	@PreAuthorize(HAS_ROLE_SUPER)
	public ModelAndView upload(ModelAndView mv) {

		mv.addObject("messageSwipe", "");
		mv.addObject("messageHc", "");
		mv.addObject("messagePa", "");
		mv.setViewName("upload");

		return mv;
	}

}
