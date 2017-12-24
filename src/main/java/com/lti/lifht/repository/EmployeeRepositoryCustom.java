package com.lti.lifht.repository;

import java.util.List;

import com.lti.lifht.model.EmployeeBean;

public interface EmployeeRepositoryCustom {

    void saveOrUpdateHeadCount(List<EmployeeBean> employeeList);

    void saveOrUpdateProjectAllocation(List<EmployeeBean> employeeList);
}
