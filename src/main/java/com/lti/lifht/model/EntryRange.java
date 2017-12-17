package com.lti.lifht.model;

import java.time.Duration;
import java.time.LocalDate;

import com.lti.lifht.util.CommonUtil;

public class EntryRange {

	private static StringBuilder builder = new StringBuilder();

	private LocalDate fromDate;
	private LocalDate toDate;
	private Duration duration;
	private Duration compliance;
	private String swipeDoor;
	private String psNumber;
	private EmployeeBean employee;
	private Duration filo;

	public Duration getFilo() {
		return filo;
	}

	public void setFilo(Duration filo) {
		this.filo = filo;
	}

	public String getFiloString() {
		return CommonUtil.formatDuration(filo);
	}

	public EntryRange(LocalDate fromDate, LocalDate toDate, Duration duration, Duration compliance, String psNumber) {
		super();
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.duration = duration;
		this.compliance = compliance;
		this.psNumber = psNumber;
	}

	public EntryRange(LocalDate from, LocalDate to, Duration durationSum, Duration complianceSum, Duration filoSum,
			String door,
			String psNumber,
			EmployeeBean employee) {
		super();
		this.fromDate = from;
		this.toDate = to;
		this.duration = durationSum;
		this.compliance = complianceSum;
		this.swipeDoor = door;
		this.psNumber = psNumber;
		this.employee = employee;
		this.filo = filoSum;
	}

	public LocalDate getFromDate() {
		return fromDate;
	}

	public void setFromDate(LocalDate fromDate) {
		this.fromDate = fromDate;
	}

	public LocalDate getToDate() {
		return toDate;
	}

	public void setToDate(LocalDate toDate) {
		this.toDate = toDate;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public Duration getCompliance() {
		return compliance;
	}

	public void setCompliance(Duration compliance) {
		this.compliance = compliance;
	}

	public String getDateRange() {
		return fromDate + " to " + toDate;
	}

	public String getPsNumber() {
		return psNumber;
	}

	public void setPsNumber(String psNumber) {
		this.psNumber = psNumber;
	}

	public EmployeeBean getEmployee() {
		return employee;
	}

	public void setEmployee(EmployeeBean employee) {
		this.employee = employee;
	}

	public String getSwipeDoor() {
		return swipeDoor;
	}

	public void setSwipeDoor(String swipeDoor) {
		this.swipeDoor = swipeDoor;
	}

	public String getDurationString() {
		return CommonUtil.formatDuration(null != duration ? duration : Duration.ofMillis(0));
	}

	public String getComplianceString() {
		return CommonUtil.formatDuration(null != compliance ? compliance : Duration.ofMillis(0));
	}

	public String toCsvString() {
		builder.setLength(0);
		return builder
				.append(fromDate).append(",")
				.append(toDate).append(",")
				.append(psNumber).append(",")
				.append(null != employee ? employee.getPsName() : "").append(",")
				.append(getFiloString()).append(",")
				.append(getDurationString()).append(",")
				.append(getComplianceString())
				.toString();
	}

	@Override
	public String toString() {
		return toCsvString();
	}

	public static final String[] fetchReportHeaders() {
		String[] reportHeaders = { "From", "To", "PS Number", "PS Name", "FILO Hours", "Floor Hours", "Compliance" };
		return reportHeaders;
	}
}
