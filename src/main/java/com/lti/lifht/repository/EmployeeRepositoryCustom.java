package com.lti.lifht.repository;

import java.util.List;

import com.lti.lifht.entity.Employee;

public interface EmployeeRepositoryCustom {
	List<Employee> getFirstNamesLike(String name);
}
