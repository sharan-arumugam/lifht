package com.lti.lifht.repository;

import java.util.List;

import com.lti.lifht.model.EmployeeBean;

public interface EmployeeRepositoryCustom {

	Integer saveOrUpdateHeadCount(List<EmployeeBean> employeeList);

	Integer saveOrUpdateProjectAllocation(List<EmployeeBean> employeeList);

	void reset();
}
