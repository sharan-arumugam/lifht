package com.lti.lifht.entity;

import static javax.persistence.CascadeType.ALL;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.AUTO;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

import static com.lti.lifht.constant.EntityConstant.EMPLOYEE;
import static com.lti.lifht.constant.EntityConstant.PS_NUMBER;
import static com.lti.lifht.constant.EntityConstant.PS_NAME;
import static com.lti.lifht.constant.EntityConstant.DS_ID;
import static com.lti.lifht.constant.EntityConstant.PASSWORD;
import static com.lti.lifht.constant.EntityConstant.APPLE_MAIL;
import static com.lti.lifht.constant.EntityConstant.LTI_MAIL;
import static com.lti.lifht.constant.EntityConstant.APPLE_MANAGER;
import static com.lti.lifht.constant.EntityConstant.BUSINESS_UNIT;
import static com.lti.lifht.constant.EntityConstant.IS_ACTIVE;
import static com.lti.lifht.constant.EntityConstant.RESET_TOKEN;
import static com.lti.lifht.constant.EntityConstant.ACCESS;

@Entity
@Table(name = EMPLOYEE)
public class Employee {

    @Id
    @GeneratedValue(strategy = AUTO)
    @Column(name = PS_NUMBER)
    private String psNumber;

    @Column(name = PS_NAME)
    private String psName;

    @Column(name = DS_ID)
    private String dsId;

    @Column(name = PASSWORD)
    private String password;

    @Column(name = APPLE_MAIL)
    private String appleMail;

    @Column(name = LTI_MAIL)
    private String ltiMail;

    @Column(name = APPLE_MANAGER)
    private String appleManager;

    @Column(name = BUSINESS_UNIT)
    private String businessUnit;

    @Column(name = IS_ACTIVE)
    private boolean isActive;

    @Column(name = RESET_TOKEN)
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
        isActive = employee.isActive;
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
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
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
