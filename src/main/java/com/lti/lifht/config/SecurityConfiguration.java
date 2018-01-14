package com.lti.lifht.config;

import static com.lti.lifht.constant.PathConstant.PARAM_PASSWORD;
import static com.lti.lifht.constant.PathConstant.PARAM_PS_NUMBER;
import static com.lti.lifht.constant.PathConstant.PATH_ASSET_ANY;
import static com.lti.lifht.constant.PathConstant.PATH_LIB_ANY;
import static com.lti.lifht.constant.PathConstant.PATH_LOGIN;
import static com.lti.lifht.constant.PathConstant.PATH_LOGIN_ERROR;
import static com.lti.lifht.constant.PathConstant.PATH_LOGOUT;
import static com.lti.lifht.constant.PathConstant.PATH_PASSWORD_ANY;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsService employeeDetailsService;

    @Autowired
    SuccessHandler successHandler;

    @Autowired
    CorsFilter corsFilter;

    @Autowired
    BCryptPasswordEncoder encoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().authenticated()
                .antMatchers(PATH_PASSWORD_ANY).permitAll()
                .and().formLogin().loginPage(PATH_LOGIN).permitAll()
                .failureUrl(PATH_LOGIN_ERROR)
                .usernameParameter(PARAM_PS_NUMBER)
                .passwordParameter(PARAM_PASSWORD)
                .successHandler(successHandler)
                .and().logout().logoutUrl(PATH_LOGOUT)
                .logoutSuccessUrl(PATH_LOGIN)
                .and().csrf().disable();
    }

    public void configure(WebSecurity webSecurity) throws Exception {
        webSecurity.ignoring().antMatchers(PATH_LIB_ANY, PATH_ASSET_ANY);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder authMgrBuilder) throws Exception {
        authMgrBuilder.userDetailsService(employeeDetailsService).passwordEncoder(encoder);
    }
}
