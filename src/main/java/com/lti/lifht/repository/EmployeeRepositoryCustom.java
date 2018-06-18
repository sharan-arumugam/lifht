package com.lti.lifht.repository;

import java.util.List;

import com.lti.lifht.model.EmployeeBean;
import com.lti.lifht.model.ResourceDetails;

public interface EmployeeRepositoryCustom {

    Integer saveOrUpdateHeadCount(List<EmployeeBean> employeeList);

    Integer saveOrUpdateProjectAllocation(List<EmployeeBean> employeeList);
    
    List<ResourceDetails> getResourceDetailsList();

}
