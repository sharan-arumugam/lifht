package com.lti.lifht.service;

import java.util.concurrent.Future;

import javax.mail.MessagingException;

public interface MailService {

    public Future<String> sendMail(String appUrl, String ltiMail, String resetToken) throws MessagingException;
}
