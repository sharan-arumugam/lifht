package com.lti.lifht.resource;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static com.lti.lifht.constant.PathConstant.HELLO_BASE;
import static com.lti.lifht.constant.PathConstant.HELLO_ALL;
import static com.lti.lifht.constant.PathConstant.HELLO_SECURE;
import static com.lti.lifht.constant.MessageConstatnt.MSG_ALL;
import static com.lti.lifht.constant.MessageConstatnt.MSG_SECURED;
import static com.lti.lifht.constant.PatternConstant.HAS_ANY_ROLE_ADMIN;

@RestController
@RequestMapping(HELLO_BASE)
public class HelloResource {

	@GetMapping(HELLO_ALL)
	public String getAll() {
		return MSG_ALL;
	}

	@PreAuthorize(HAS_ANY_ROLE_ADMIN)
	@GetMapping(HELLO_SECURE)
	public String getSecured() {
		return MSG_SECURED;
	}
}
