package com.lti.lifht.service;

import static com.lti.lifht.constant.MessageConstatnt.MSG_PS_NOT_FOUND;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.lti.lifht.entity.Employee;
import com.lti.lifht.model.EmployeeDetails;
import com.lti.lifht.repository.EmployeeRepository;

@Service
public class EmployeeDetailsService implements UserDetailsService {

	@Autowired
	private EmployeeRepository employeeRepository;

	@Override
	public UserDetails loadUserByUsername(String psNumber) throws UsernameNotFoundException {

		Optional<Employee> employee = employeeRepository.findByPsNumber(psNumber);

		employee.orElseThrow(() -> new UsernameNotFoundException(MSG_PS_NOT_FOUND));

		return employee
				.map(EmployeeDetails::new)
				.get();
	}

}
