package com.lti.lifht.model.request;

import static java.sql.Date.valueOf;
import static java.time.ZoneId.systemDefault;
import static java.util.Arrays.asList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateMultiPs {

	private Date date;
	private List<String> psNumberList;

	public DateMultiPs() {
	}

	public DateMultiPs(Date date, List<String> psNumberList) {
		this.psNumberList = psNumberList;
		this.date = date;
	}

	public DateMultiPs(LocalDate localDate, String[] psNumbers) {
		psNumberList = null != psNumbers ? asList(psNumbers) : new ArrayList<>();
		date = Date.from(localDate.atStartOfDay(systemDefault()).toInstant());
	}

	public List<String> getPsNumberList() {
		return psNumberList;
	}

	public DateMultiPs setPsNumberList(List<String> psNumberList) {
		this.psNumberList = psNumberList;
		return this;
	}

	public LocalDate getDate() {
		return date.toInstant()
				.atZone(systemDefault())
				.toLocalDate();
	}

	public DateMultiPs setDate(LocalDate localDate) {
		date = valueOf(localDate);
		return this;
	}

	public DateMultiPs setDate(Date date) {
		this.date = date;
		return this;
	}

}
