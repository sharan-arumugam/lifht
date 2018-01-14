package com.lti.lifht.controller;

import static com.lti.lifht.constant.PatternConstant.HAS_ANY_ROLE_EMPLOYEE_ADMIN;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lti.lifht.entity.Employee;
import com.lti.lifht.service.AdminService;
import com.lti.lifht.service.MailService;

@Controller
@RequestMapping("/password")
public class PasswordController {

    @Autowired
    private AdminService userService;

    @Autowired
    private MailService emailService;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @GetMapping("/forgot")
    public String forgotPassword() {
        return "forgotPassword";
    }

    @PostMapping("/forgot")
    public ModelAndView forgotPassword(ModelAndView modelAndView, @RequestParam("psNumber") String psNumber,
            HttpServletRequest request) throws MessagingException {

        Optional<Employee> optional = userService.findByPsNumber(psNumber);

        if (!optional.isPresent()) {
            modelAndView.addObject("errorMessage", psNumber + " not found");
        } else {
            Employee user = optional.get();
            user.setResetToken(UUID.randomUUID().toString());
            userService.save(user);

            String appUrl = request.getScheme() + "://" + request.getServerName();

            emailService.sendMail(appUrl, psNumber, user.getResetToken());
            modelAndView.addObject("successMessage", "reset link sent to mail associated with :: " + psNumber);
        }

        modelAndView.setViewName("forgotPassword");
        return modelAndView;
    }

    @GetMapping("/reset")
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

    @PostMapping("/reset")
    public ModelAndView resetPassword(ModelAndView modelAndView, @RequestParam Map<String, String> requestParams,
            RedirectAttributes redir) {

        Optional<Employee> user = userService.findByResetToken(requestParams.get("token"));

        if (user.isPresent()) {
            Employee resetUser = user.get();

            resetUser.setPassword(encoder.encode(requestParams.get("password")));
            resetUser.setResetToken(null);

            userService.save(resetUser);

            redir.addFlashAttribute("successMessage", "password reset");
            modelAndView.setViewName("resetPassword");
            return modelAndView;

        } else {
            modelAndView.addObject("errorMessage", "invalid");
            modelAndView.setViewName("resetPassword");
        }

        return modelAndView;
    }

    @PreAuthorize(HAS_ANY_ROLE_EMPLOYEE_ADMIN)
    @GetMapping("/change")
    public ModelAndView changePassowrd(ModelAndView modelAndView) {
        return modelAndView;
    }

    @PostMapping("/change")
    public ModelAndView changePassowrd(ModelAndView modelAndView, @RequestParam Map<String, String> requestParams,
            RedirectAttributes redir) {
        return modelAndView;
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ModelAndView handleMissingParams(MissingServletRequestParameterException ex) {
        return new ModelAndView("redirect:login");
    }
}