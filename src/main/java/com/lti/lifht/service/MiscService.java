package com.lti.lifht.service;

import static org.springframework.security.crypto.bcrypt.BCrypt.checkpw;

import java.util.Base64.Decoder;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.lti.lifht.entity.Employee;
import com.lti.lifht.model.EmployeeBean;
import com.lti.lifht.repository.EmployeeRepository;

@Service
public class MiscService {

	Logger logger = LoggerFactory.getLogger(EmployeeBean.class);

	@Autowired
	EmployeeRepository empRepo;

	@Autowired
	Decoder decoder;

	@Autowired
	BCryptPasswordEncoder encoder;

	public Employee changePassword(String currentPassword, String newPassword, String psNumber) {

		Objects.requireNonNull(currentPassword, "CURRENT PASSWORD");
		Objects.requireNonNull(newPassword, "NEW PASSWORD");
		Objects.requireNonNull(psNumber, "SESSION PS_NUMBER");

		String currentPass = baseToPlain(currentPassword);
		String newPass = baseToPlain(newPassword);

		Employee currentUser = empRepo.findByPsNumber(psNumber).get();

		if (checkpw(currentPass.trim(), currentUser.getPassword().trim())) {
			return empRepo.save(currentUser.setPassword(encoder.encode(newPass)));
		} else {
			logger.error("invlaid-password-attempt::" + psNumber);
			return null;
		}
	}

	private String baseToPlain(String thriceCoded) {
		return new String(decoder.decode(decoder.decode(decoder.decode(thriceCoded.getBytes()))));
	}

}
