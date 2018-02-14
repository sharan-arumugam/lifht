package com.lti.lifht.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.MessageBody;

@Service
public class MailService {

	private ExchangeService exchangeService;

	public MailService(@Value("${exchange.url}") String exchangeUrl,
			@Value("${exchange.username}") String username,
			@Value("${exchange.password}") String password) throws URISyntaxException {

		exchangeService = new ExchangeService();
		exchangeService.setUrl(new URI(exchangeUrl));
		exchangeService.setCredentials(new WebCredentials(username, password));
	}

	@Async
	public Future<String> sendMail(String psNumber, String subject, String messageBody) {

		String emailAddress = psNumber + "@lntinfotech.com";

		try {
			EmailMessage emailMessage = new EmailMessage(exchangeService);

			emailMessage.setSubject(subject);
			emailMessage.setBody(new MessageBody(messageBody));
			emailMessage.getToRecipients().add(emailAddress);
			emailMessage.sendAndSaveCopy();

			return new AsyncResult<String>("email sent: " + emailAddress);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return new AsyncResult<String>("failed to email: " + emailAddress);
	}
}
