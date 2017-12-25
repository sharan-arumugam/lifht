package com.lti.lifht.repository;

import java.util.List;

import com.lti.lifht.model.EmployeeBean;
import com.lti.lifht.model.EntryDateBean;
import com.lti.lifht.model.request.RangeSinglePs;

public interface EmployeeRepositoryCustom {

    void saveOrUpdateHeadCount(List<EmployeeBean> employeeList);

    void saveOrUpdateProjectAllocation(List<EmployeeBean> employeeList);
    
}
