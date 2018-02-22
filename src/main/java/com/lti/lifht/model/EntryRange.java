package com.lti.lifht.model;

import static com.lti.lifht.util.CommonUtil.formatDuration;

import java.time.Duration;
import java.time.LocalDate;
import java.util.StringJoiner;

public class EntryRange {

	private LocalDate fromDate;
	private LocalDate toDate;
	private Duration duration;
	private Duration compliance;
	private String swipeDoor;
	private String psNumber;
	private EmployeeBean employee;
	private Duration filo;
	private int daysPresent;
	private LocalDate validSince;
	private Duration average;

	public Duration getFilo() {
		return filo;
	}

	public void setFilo(Duration filo) {
		this.filo = filo;
	}

	public String getFiloString() {
		return formatDuration(filo);
	}

	public EntryRange(LocalDate fromDate, LocalDate toDate, Duration duration, Duration compliance, String psNumber) {
		super();
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.duration = duration;
		this.compliance = compliance;
		this.psNumber = psNumber;
	}

	public EntryRange(LocalDate from, LocalDate to, LocalDate validSince, int daysPresent, Duration durationSum,
			Duration complianceSum, Duration filoSum, Duration average, String door, String psNumber, EmployeeBean employee) {
		super();
		this.fromDate = from;
		this.toDate = to;
		this.validSince = validSince;
		this.daysPresent = daysPresent;
		this.duration = durationSum;
		this.compliance = complianceSum;
		this.swipeDoor = door;
		this.psNumber = psNumber;
		this.employee = employee;
		this.filo = filoSum;
		this.average = average;
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
		return null != duration ? formatDuration(duration) : "";
	}

	public String getComplianceString() {
		return null != compliance ? formatDuration(compliance) : "";
	}

	public int getDaysPresent() {
		return daysPresent;
	}

	public void setDaysPresent(int daysPresent) {
		this.daysPresent = daysPresent;
	}

	public LocalDate getValidSince() {
		return validSince;
	}

	public void setValidSince(LocalDate validSince) {
		this.validSince = validSince;
	}

	public String toCsvString() {
		StringJoiner joiner = new StringJoiner(",");
		return joiner
				.add(null != employee && null != employee.getBusinessUnit()
						&& !"null".equals(employee.getBusinessUnit()) ? employee.getBusinessUnit() : "")
				.add(null != employee && null != employee.getDsId() ? employee.getDsId() : "")
				.add(null != psNumber ? psNumber : "")
				.add(null != employee && null != employee.getPsName() ? employee.getPsName() : "")
				.add(null != validSince ? validSince + "" : "")
				.add(daysPresent + "")
				.add(getFiloString())
				.add(getDurationString())
				.add(getComplianceString())
				.toString();
	}

	@Override
	public String toString() {
		return toCsvString();
	}

	public static final StringJoiner fetchReportHeaders() {
		return new StringJoiner(",").add("Business Unit")
				.add("DS ID")
				.add("PS Number")
				.add("PS Name")
				.add("Valid Since")
				.add("Days")
				.add("FILO Hours")
				.add("Floor Hours")
				.add("Compliance");
	}

    public Duration getAverage() {
        return average;
    }

    public void setAverage(Duration average) {
        this.average = average;
    }

    public String getAverageString() {
        return null != average ? formatDuration(average) : "";
    }
}
