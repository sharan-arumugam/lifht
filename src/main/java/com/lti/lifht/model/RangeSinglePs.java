package com.lti.lifht.model;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class RangeSinglePs {

	private String psNumber;
	private Date fromDate;
	private Date toDate;

	public String getPsNumber() {
		return psNumber;
	}

	public void setPsNumber(String psNumber) {
		this.psNumber = psNumber;
	}

	public LocalDate getFromDate() {
		return fromDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public LocalDate getToDate() {
		return toDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	@Override
	public String toString() {
		return "RangeSinglePs [psNumber=" + psNumber + ", fromDate=" + fromDate + ", toDate=" + toDate + "]";
	}

}
