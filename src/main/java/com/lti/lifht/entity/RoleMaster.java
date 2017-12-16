package com.lti.lifht.entity;

import static javax.persistence.GenerationType.AUTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import static com.lti.lifht.constant.EntityConstant.ROLE_MASTER;
import static com.lti.lifht.constant.EntityConstant.ROLE_ID;
import static com.lti.lifht.constant.EntityConstant.ROLE;

@Entity
@Table(name = ROLE_MASTER)
public class RoleMaster {

	@Id
	@GeneratedValue(strategy = AUTO)
	@Column(name = ROLE_ID)
	private int roleId;

	@Column(name = ROLE)
	private String role;

	public RoleMaster() {
		super();
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}
