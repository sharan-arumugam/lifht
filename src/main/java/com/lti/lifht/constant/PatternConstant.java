package com.lti.lifht.constant;

public class PatternConstant {

	public static final String HAS_ROLE_SUPER = "hasRole('ROLE_SUPER')";
	public static final String HAS_ANY_ROLE_ADMIN = "hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER')";
	public static final String HAS_ANY_ROLE_EMPLOYEE = "hasAnyRole('ROLE_EMPLOYEE', 'ROLE_ADMIN', 'ROLE_SUPER')";

}
