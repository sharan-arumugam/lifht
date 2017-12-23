package com.lti.lifht.controller;

import static com.lti.lifht.constant.PatternConstant.HAS_ANY_ROLE_ADMIN;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class PageController {

    @GetMapping("/upload")
    public String upload() {
        return "upload";
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
}
