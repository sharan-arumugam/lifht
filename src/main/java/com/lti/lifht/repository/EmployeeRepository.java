package com.lti.lifht.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lti.lifht.entity.Employee;

@SuppressWarnings("unchecked")
public interface EmployeeRepository extends JpaRepository<Employee, String>, EmployeeRepositoryCustom {

	Optional<Employee> findByPsNumber(String psNumber);

	@Query("from Employee where resetToken = ?")
	Optional<Employee> findByResetToken(String resetToken);

	List<Employee> findAll();

	Employee saveAndFlush(Employee employee);

	@Query("select psNumber from Employee where active <> 'N'")
	Set<String> findAllpsNumber();
}
