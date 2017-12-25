package com.lti.lifht.config;

import java.io.IOException;
import java.util.function.Consumer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class SuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(SuccessHandler.class);

    private static final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response, Authentication authentication)
            throws IOException {

        Consumer<String> redirect = targetUrl -> {
            try {
                request.getSession().setAttribute("username", authentication.getName());
                request.getSession().setAttribute("authorities", authentication.getAuthorities());

                redirectStrategy.sendRedirect(request, response, targetUrl);

            } catch (IOException e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
        };

        authentication.getAuthorities().forEach(grantedAuthority -> {

            switch (grantedAuthority.getAuthority()) {

            case "ROLE_EMPLOYEE":
                redirect.accept("/staff");
                logger.info("staff login");
                break;

            case "ROLE_ADMIN":
                redirect.accept("/admin");
                logger.info("admin login");
                break;

            default:
                throw new IllegalStateException();
            }
        });

        if (response.isCommitted()) {
            logger.debug("response committed");
            return;
        }

        HttpSession session = request.getSession(false);
        if (null == session) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }
}
