package com.lti.lifht.model;

import java.lang.reflect.Field;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Employee {

	private static final Logger logger = LoggerFactory.getLogger(Employee.class);

	private String psNumber;
	private String psName;
	private String businessUnit;
	private String manager;
	private Boolean isAdmin;
	private String email;

	public Employee() {
		super();
	}

	public Employee(String psNumber, String psName) {
		super();
		this.psNumber = psNumber;
		this.psName = psName;
	}

	public Employee(String number, String name, String bu, String eml) {
		super();
		psNumber = number;
		psName = name;
		businessUnit = bu;
		email = eml;
	}

	public Employee(Map<String, String> columnMap, Map<String, String> headerMap) {
		super();
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field field : fields) {
			try {
				field.set(this, columnMap.get(headerMap.get(field.getName())));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				logger.error(e.getMessage());
			}
		}
	}

	public Employee(String psNumber) {
		this.psNumber = psNumber;
	}

	public String getPsNumber() {
		return psNumber;
	}

	public void setPsNumber(String psNumber) {
		this.psNumber = psNumber;
	}

	public String getPsName() {
		return psName;
	}

	public void setPsName(String psName) {
		this.psName = psName;
	}

	public String getBusinessUnit() {
		return businessUnit;
	}

	public void setBusinessUnit(String businessUnit) {
		this.businessUnit = businessUnit;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

}
