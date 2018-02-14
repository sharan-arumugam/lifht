package com.lti.lifht;

import static com.lti.lifht.constant.PathConstant.PATH_LOGIN;
import static com.lti.lifht.constant.PathConstant.PATH_NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.Base64;
import java.util.Base64.Decoder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;

@EntityScan(basePackageClasses = { Application.class })
@PropertySource("file:${user.home}/odc_apps/lifht/application.properties")
@SpringBootApplication
public class Application extends WebMvcConfigurerAdapter {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public RedirectStrategy redirectStrategy() {
		return new DefaultRedirectStrategy();
	}

	@Bean
	public Decoder b64Decoder() {
		return Base64.getDecoder();
	}

	@Bean
	public ObjectMapper mapper() {
		return new ObjectMapper();
	}

	@Bean
	public EmbeddedServletContainerCustomizer containerCustomizer() {
		return container -> container.addErrorPages(new ErrorPage(NOT_FOUND, PATH_NOT_FOUND));
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController(PATH_LOGIN);

		registry.addViewController(PATH_NOT_FOUND)
				.setViewName("login");
	}

}
