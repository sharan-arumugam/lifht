package com.lti.lifht.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lti.lifht.model.Employee;

public class AdminDao extends BaseDao {

	private static final Logger logger = LoggerFactory.getLogger(AdminDao.class);
	private static final StringBuilder sql = new StringBuilder();

	public List<Employee> getAllEmployees() {

		List<Employee> employeePsList = null;
		try {
			sql.setLength(0);
			sql.append("select ps_number, ps_name from employee");

			PreparedStatement preparedStatement = prepareStatement(sql);
			logger.info(preparedStatement.toString());
			ResultSet rs = preparedStatement.executeQuery();

			employeePsList = new ArrayList<>();
			while (rs.next()) {
				employeePsList.add(new Employee(rs.getString("ps_number"), rs.getString("ps_name")));
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return employeePsList;
	}

	public void saveOrUpdateHeadCount(List<Employee> employeeList) {

		sql.setLength(0);

		employeeList.forEach(employee -> {
			try {

				sql.setLength(0);

				sql.append("INSERT INTO employee(ps_number, ps_name, manager, email)")
						.append(" VALUES (?,?,?,?)")
						.append(" ON DUPLICATE KEY UPDATE")
						.append(" ps_name = VALUES (ps_name),")
						.append(" manager = VALUES (manager),")
						.append(" email = VALUES (email)");

				PreparedStatement preparedStatement = prepareStatement(sql);

				preparedStatement.setString(1, employee.getPsNumber());
				preparedStatement.setString(2, employee.getPsName());
				preparedStatement.setString(3, employee.getManager());
				preparedStatement.setString(4, employee.getEmail());
				logger.info(preparedStatement.toString());
				preparedStatement.addBatch();
				preparedStatement.executeLargeBatch();

			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		});
	}

	public void saveOrUpdateProjectAllocation(List<Employee> employeeList) {

		sql.setLength(0);

		employeeList.forEach(employee -> {
			try {

				sql.setLength(0);

				sql.append("INSERT INTO employee(ps_number, business_unit)")
						.append(" VALUES (?,?)")
						.append(" ON DUPLICATE KEY UPDATE")
						.append(" business_unit = VALUES (business_unit)");

				PreparedStatement preparedStatement = prepareStatement(sql);
				preparedStatement.setString(1, employee.getPsNumber());
				preparedStatement.setString(2, employee.getBusinessUnit());
				logger.info(preparedStatement.toString());
				preparedStatement.addBatch();
				preparedStatement.executeLargeBatch();

			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		});
	}
}
