package com.lti.lifht.model;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class DateMultiPs {

	private List<String> psNumberList;
	private Date date;

	public List<String> getPsNumberList() {
		return psNumberList;
	}

	public void setPsNumberList(List<String> psNumberList) {
		this.psNumberList = psNumberList;
	}

	public LocalDate getDate() {
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
