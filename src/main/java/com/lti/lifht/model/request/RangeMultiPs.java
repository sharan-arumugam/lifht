package com.lti.lifht.model.request;

import static com.lti.lifht.util.CommonUtil.parseDMYDashToDate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RangeMultiPs {

	private List<String> psNumberList;
	private Date fromDate;
	private Date toDate;

	public RangeMultiPs() {
	}

	public RangeMultiPs(String fromDate, String toDate, String[] psNumberList) {
		this.fromDate = parseDMYDashToDate.apply(fromDate);
		this.toDate = parseDMYDashToDate.apply(toDate);
		this.psNumberList = null != psNumberList ? Arrays.asList(psNumberList) : new ArrayList<>();
	}

	public List<String> getPsNumberList() {
		return psNumberList;
	}

	public void setPsNumberList(List<String> psNumberList) {
		this.psNumberList = psNumberList;
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

}
