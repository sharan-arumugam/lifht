package com.lti.lifht.controller;

import static com.lti.lifht.constant.PatternConstant.HAS_ANY_ROLE_EMPLOYEE;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.lti.lifht.entity.Employee;
import com.lti.lifht.service.AdminService;
import com.lti.lifht.service.MailService;

@Controller
public class PasswordController {

	@Autowired
	private AdminService userService;

	@Autowired
	private MailService emailService;

	@Autowired
	private BCryptPasswordEncoder encoder;

	@GetMapping("/forgotpassword")
	public String forgotPassword() {
		return "forgotPassword";
	}

	@PostMapping("/forgotpassword")
	public ModelAndView forgotPassword(ModelAndView modelAndView, @RequestParam("psNumber") String psNumber,
			HttpServletRequest request) {

		Optional<Employee> optional = userService.findByPsNumber(psNumber);

		if (!optional.isPresent()) {
			modelAndView.addObject("errorMessage", psNumber + " not found");

		} else {
			Employee user = optional.get();
			user.setResetToken(UUID.randomUUID().toString());
			userService.save(user);

			String appUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

			String subject = "Password reset";
			String messageBody = "Looks like you have requested for a password reset,  <a href=" + appUrl + "/resetpassword?token=" + user.getResetToken()+"><b>click here</b></a> to reset"
					+ "<br><br> Please ignore if you have not requested" ;

			emailService.sendMail(psNumber, subject, messageBody);

			modelAndView.addObject("successMessage", "Reset link sent to mail associated with: " + psNumber);
		}

		modelAndView.setViewName("forgotPassword");
		return modelAndView;
	}

	@GetMapping("/resetpassword")
	public ModelAndView resetPassword(ModelAndView modelAndView, @RequestParam("token") String token) {

		Optional<Employee> user = userService.findByResetToken(token);

		if (user.isPresent()) {
			modelAndView.addObject("resetToken", token);
		} else {
			modelAndView.addObject("errorMessage", "invalid");
		}

		modelAndView.setViewName("resetPassword");
		return modelAndView;
	}

	@PostMapping("/resetpassword")
	public ModelAndView resetPassword(ModelAndView modelAndView, @RequestParam Map<String, String> requestParams,
			HttpServletRequest request) {

		Optional<Employee> user = userService.findByResetToken(requestParams.get("token"));

		String appUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

		if (user.isPresent()) {
			Employee resetUser = user.get();

			resetUser.setPassword(encoder.encode(requestParams.get("password")));
			resetUser.setResetToken(null);

			userService.save(resetUser);

			modelAndView.addObject("successMessage", "@{" + appUrl + "}");
			modelAndView.setViewName("resetPassword");
			return modelAndView;

		} else {
			modelAndView.addObject("errorMessage", "Invalid PS Number");
			modelAndView.setViewName("resetPassword");
		}

		return modelAndView;
	}

	@PreAuthorize(HAS_ANY_ROLE_EMPLOYEE)
	@GetMapping("/changepassword")
	public ModelAndView changePassowrd(ModelAndView modelAndView) {
		modelAndView.setViewName("changePassword");
		return modelAndView;
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ModelAndView handleMissingParams(MissingServletRequestParameterException ex) {
		return new ModelAndView("redirect:login");
	}
}
