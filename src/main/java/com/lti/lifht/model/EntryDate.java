package com.lti.lifht.model;

import java.sql.Date;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import com.lti.lifht.util.CommonUtil;

public class EntryDate {

	private static StringBuilder builder = new StringBuilder();

	private LocalDate swipeDate;
	private String swipeDoor;
	private Duration duration;
	private Duration compliance;
	private String psNumber;
	private EmployeeBean employee;
	private LocalTime firstIn;
	private LocalTime lastOut;
	private Duration filo;

	public LocalTime getFirstIn() {
		return firstIn;
	}

	public void setFirstIn(LocalTime firstIn) {
		this.firstIn = firstIn;
	}

	public LocalTime getLastOut() {
		return lastOut;
	}

	public void setLastOut(LocalTime lastOut) {
		this.lastOut = lastOut;
	}

	public Duration getFilo() {
		if (filo != null) {
			return filo;
		}
		return null != firstIn && null != lastOut ? Duration.between(firstIn, lastOut) : Duration.ofMillis(0);
	}

	public void setFilo(Duration filo) {
		this.filo = filo;
	}

	public String getFiloString() {
		return CommonUtil.formatDuration(filo);
	}

	public void setCompliance(Duration compliance) {
		this.compliance = compliance;
	}

	public EntryDate(String psNumber, LocalDate date, String door, Duration durationSum, LocalTime firstIn,
			LocalTime lastOut) {
		super();
		this.swipeDate = date;
		this.swipeDoor = door;
		this.duration = durationSum;
		this.psNumber = psNumber;
		this.firstIn = firstIn;
		this.lastOut = lastOut;
	}

	public EntryDate(LocalDate date, String duration, String compliance, String filo, String door, String psNumber,
			EmployeeBean employee) {
		super();
		this.swipeDate = date;
		this.swipeDoor = door;
		this.duration = Duration.parse(duration);
		this.compliance = Duration.parse(compliance);
		this.filo = Duration.parse(filo);
		this.psNumber = psNumber;
		this.employee = employee;
	}

	public EntryDate(Date date, String duration, String compliance, String filo, String door, String psNumber,
			LocalTime firstIn, LocalTime lastOut, EmployeeBean employee) {
		super();
		this.swipeDate = date.toLocalDate();
		this.swipeDoor = door;
		this.duration = Duration.parse(duration);
		this.compliance = Duration.parse(compliance);
		this.filo = Duration.parse(filo);
		this.psNumber = psNumber;
		this.firstIn = firstIn;
		this.lastOut = lastOut;
		this.employee = employee;
	}

	public EntryDate(String psNumber, String door, Duration durationSum, Duration complianceSum) {
		super();
		this.swipeDoor = door;
		this.duration = durationSum;
		this.compliance = complianceSum;
		this.psNumber = psNumber;
	}

	public LocalDate getSwipeDate() {
		return swipeDate;
	}

	public void setSwipeDate(LocalDate swipeDate) {
		this.swipeDate = swipeDate;
	}

	public String getSwipeDoor() {
		return swipeDoor;
	}

	public void setSwipeDoor(String swipeDoor) {
		this.swipeDoor = swipeDoor;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	/**
	 * if not set, returns duration.minus(8 hours)
	 * 
	 * @return
	 */
	public Duration getCompliance() {
		if (compliance != null) {
			return compliance;
		}
		return null != duration ? duration.minus(8, ChronoUnit.HOURS) : Duration.ofMillis(0);
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

	public String getDurationString() {
		return CommonUtil.formatDuration(duration);
	}

	public String getComplianceString() {
		return CommonUtil.formatDuration(compliance);
	}

	@Override
	public String toString() {
		return toCsvString();
	}

	public String toCsvString() {
		builder.setLength(0);
		return builder
				.append(swipeDate).append(",")
				.append(psNumber).append(",")
				// .append(employee.getPsName()).append(",")
				// .append(getFiloString()).append(",")
				.append(getDurationString()).append(",")
				.append(getComplianceString())
				.toString();
	}

	public static final String[] fetchReportHeaders() {
		String[] reportHeaders = { "Date", "PS Number", "Floor Hours", "Compliance" };
		return reportHeaders;
	}

}
