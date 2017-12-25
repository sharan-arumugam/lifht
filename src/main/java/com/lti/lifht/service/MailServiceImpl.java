package com.lti.lifht.service;

import java.util.concurrent.Future;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender sender;

    @Async
    @Override
    public Future<String> sendMail(String appUrl, String psNumber, String resetToken) throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setTo(psNumber + "@lntinfotech.com");
        helper.setSubject("LTI ODC - reset password");
        helper.setText("reset link: " + appUrl + ":8080/password/reset?token=" + resetToken);

        sender.send(message);

        return new AsyncResult<String>("Email send successfully");
    }
}
