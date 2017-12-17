package com.lti.lifht.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lti.lifht.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, String> {

	Optional<Employee> findByPsNumber(String psNumber);

	List<Employee> findAll();

}
