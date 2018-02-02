package com.lti.lifht.repository;

import static com.lti.lifht.util.CommonUtil.cryptTupleMap;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lti.lifht.entity.Employee;
import com.lti.lifht.entity.RoleMaster;

@SuppressWarnings("unchecked")
public interface EmployeeRepository extends JpaRepository<Employee, String>, EmployeeRepositoryCustom {

	Optional<Employee> findByPsNumber(String psNumber);

	@Query("from Employee where resetToken = ?")
	Optional<Employee> findByResetToken(String resetToken);

	@Query("from Employee where active <> 'N' and active <> 'A'")
	List<Employee> findAll();

	Employee saveAndFlush(Employee employee);

	@Query("select psNumber from Employee where active <> 'N' and active <> 'A'")
	Set<String> findAllpsNumber();

	List<Employee> findByPsNumberNotIn(List<String> psNumberList);

	default void resetAccess(List<String> psNumberList, RoleMaster employeeRole) {
		save(findByPsNumberNotIn(psNumberList)
				.stream()
				.map(employee -> 'A' == employee.getActive() ? employee : employee.setActive('N')) // ignore super sets
				.collect(toSet()));

		setNewAccess(employeeRole);
	}

	/**
	 * to be called only after resetAccess
	 * 
	 * @see EmployeeRepository#resetAccess
	 * @param employeeRole
	 */
	default void setNewAccess(RoleMaster employeeRole) {
		Supplier<Stream<Employee>> newEmployeeStream = () -> findAll()
				.stream()
				.filter(emp -> null == emp.getPassword());

		Map<String, String> cryptMap = cryptTupleMap(newEmployeeStream
				.get()
				.map(Employee::getPsNumber)
				.collect(toList()));

		Set<String> cryptSet = cryptMap.keySet();

		Set<RoleMaster> roles = new HashSet<>(asList(employeeRole));

		save(newEmployeeStream
				.get()
				.filter(emp -> 'A' != emp.getActive())
				.filter(emp -> cryptSet.contains(emp.getPsNumber()))
				.map(employee -> {
					return employee
							.setPassword(cryptMap.get(employee.getPsNumber()))
							.setRoles(roles);
				})
				.collect(toSet()));
	}
}
