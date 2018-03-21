package com.lti.lifht.model;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.lti.lifht.entity.Employee;
import com.lti.lifht.entity.RoleMaster;

public class EmployeeBean {

    private String psNumber;
    private String psName;
    private String businessUnit;
    private String manager;
    private String email;
    private List<String> roles;
    private String dsId;
    private String billable;

    public EmployeeBean() {
        super();
    }

    public EmployeeBean(Employee entity) {
        psNumber = entity.getPsNumber();
        psName = entity.getPsName();
        businessUnit = entity.getBusinessUnit();
        email = entity.getLtiMail();
        roles = entity.getRoles()
                .stream()
                .map(RoleMaster::getRole)
                .collect(Collectors.toList());
        dsId = entity.getDsId();
        billable = entity.getBillable();
    }

    public EmployeeBean(String number, String name, String bu, String eml, String ds, String billable) {
        super();
        psNumber = number;
        psName = name;
        businessUnit = bu;
        email = eml;
        dsId = ds;
        this.billable = billable;
    }

    public EmployeeBean(Map<String, String> columnMap, Map<String, String> headerMap) {
        super();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.set(this, columnMap.get(headerMap.get(field.getName())));
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public EmployeeBean(String psNumber) {
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

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getDsId() {
        return dsId;
    }

    public void setDsId(String dsId) {
        this.dsId = dsId;
    }

    public String getBillable() {
        return billable;
    }

    public void setBillable(String billable) {
        this.billable = billable;
    }

}
