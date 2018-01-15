package com.lti.lifht.model;

import java.util.Collection;
import static java.util.stream.Collectors.toList;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.lti.lifht.entity.Employee;

public class EmployeeDetails extends Employee implements UserDetails {

	private static final long serialVersionUID = 4503091765407803891L;

	public EmployeeDetails(final Employee employee) {
		super(employee);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return getRoles()
				.stream()
				.map(role -> new SimpleGrantedAuthority(role.getRole()))
				.collect(toList());
	}

	@Override
	public String getPsName() {
		return super.getPsName();
	}

	@Override
	public String getPassword() {
		return super.getPassword();
	}

	@Override
	public String getUsername() {
		return super.getPsNumber();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return super.getActive() != 'N';
	}

}
