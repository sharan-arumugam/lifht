package com.lti.lifht.entity;

import static com.lti.lifht.constant.EntityConstant.ACCESS;
import static com.lti.lifht.constant.EntityConstant.PS_NUMBER;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

@Entity
public class Employee {

    @Id
    private String psNumber;
    private String psName;
    private String dsId;
    private String password;
    private String appleMail;
    private String ltiMail;
    private String appleManager;
    private String businessUnit;
    private char active;
    private String resetToken;
    @OneToMany(cascade = ALL, fetch = EAGER)
    @JoinTable(name = ACCESS, joinColumns = @JoinColumn(name = PS_NUMBER))
    private Set<RoleMaster> roles;

    public Employee() {
        super();
    }

    public Employee(Employee employee) {
        super();
        psNumber = employee.psNumber;
        psName = employee.psName;
        dsId = employee.dsId;
        password = employee.password;
        appleMail = employee.appleMail;
        ltiMail = employee.ltiMail;
        appleManager = employee.appleManager;
        businessUnit = employee.businessUnit;
        active = employee.active;
        roles = employee.roles;
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

    public String getDsId() {
        return dsId;
    }

    public void setDsId(String dsId) {
        this.dsId = dsId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAppleMail() {
        return appleMail;
    }

    public void setAppleMail(String appleMail) {
        this.appleMail = appleMail;
    }

    public String getLtiMail() {
        return ltiMail;
    }

    public void setLtiMail(String ltiMail) {
        this.ltiMail = ltiMail;
    }

    public String getAppleManager() {
        return appleManager;
    }

    public void setAppleManager(String appleManager) {
        this.appleManager = appleManager;
    }

    public String getBusinessUnit() {
        return businessUnit;
    }

    public void setBusinessUnit(String businessUnit) {
        this.businessUnit = businessUnit;
    }

    public boolean isActive() {
        return 'Y' == active;
    }

    public void setActive(char active) {
        this.active = active;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public Set<RoleMaster> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleMaster> roles) {
        this.roles = roles;
    }
}
