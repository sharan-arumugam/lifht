package com.lti.lifht.model;

import java.sql.Date;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.StringJoiner;

import com.lti.lifht.entity.EntryDate;
import com.lti.lifht.util.CommonUtil;

public class EntryDateBean {

    private LocalDate swipeDate;
    private String swipeDoor;
    private Duration duration;
    private Duration compliance;
    private String psNumber;
    private EmployeeBean employee;
    private LocalTime firstIn;
    private LocalTime lastOut;
    private Duration filo;

    public EntryDateBean() {

    }

    public EntryDateBean(Object[] rawResult) {

        employee = new EmployeeBean(
                String.valueOf(rawResult[0]),
                String.valueOf(rawResult[1]),
                String.valueOf(rawResult[2]),
                String.valueOf(rawResult[3]),
                String.valueOf(rawResult[4]));

        psNumber = String.valueOf(rawResult[0]);
        swipeDate = null != rawResult[5] ? LocalDate.parse(String.valueOf(rawResult[5])) : null;
        duration = null != rawResult[6] ? Duration.ofMillis(Long.valueOf(String.valueOf(rawResult[6]))) : null;
        filo = null != rawResult[7] ? Duration.ofMillis(Long.valueOf(String.valueOf(rawResult[7]))) : null;
        compliance = null != rawResult[8] ? Duration.ofMillis(Long.valueOf(String.valueOf(rawResult[8]))) : null;
        swipeDoor = String.valueOf(rawResult[9]);
    }

    public EntryDateBean(Object[] rawResult, Object fi, Object lo) {

        employee = new EmployeeBean(
                String.valueOf(rawResult[0]),
                String.valueOf(rawResult[1]),
                String.valueOf(rawResult[2]),
                String.valueOf(rawResult[3]),
                String.valueOf(rawResult[4]));

        psNumber = String.valueOf(rawResult[0]);
        swipeDate = null != rawResult[5]
                ? LocalDate.parse(String.valueOf(rawResult[5]))
                : null;
        duration = null != rawResult[6]
                ? Duration.ofMillis(Long.valueOf(String.valueOf(rawResult[6])))
                : null;
        filo = null != rawResult[7]
                ? Duration.ofMillis(Long.valueOf(String.valueOf(rawResult[7])))
                : null;
        compliance = null != rawResult[8]
                ? Duration.ofMillis(Long.valueOf(String.valueOf(rawResult[8])))
                : null;
        swipeDoor = String.valueOf(rawResult[9]);
        firstIn = null != fi ? LocalTime.parse(String.valueOf(fi)) : null;
        lastOut = null != lo ? LocalTime.parse(String.valueOf(lo)) : null;
    }

    public EntryDateBean(EntryDate entity) {
        super();
        psNumber = entity.getPsNumber();
        swipeDate = entity.getSwipeDate();
        swipeDoor = entity.getSwipeDoor();
        duration = Duration.ofMillis(entity.getDuration());
        compliance = Duration.ofMillis(entity.getCompliance());
        firstIn = entity.getFirstIn();
        lastOut = entity.getLastOut();
        filo = Duration.ofMillis(entity.getFilo());
    }

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

    public EntryDateBean(String psNumber, LocalDate date, String door, Duration durationSum, LocalTime firstIn,
            LocalTime lastOut) {
        super();
        this.swipeDate = date;
        this.swipeDoor = door;
        this.duration = durationSum;
        this.psNumber = psNumber;
        this.firstIn = firstIn;
        this.lastOut = lastOut;
    }

    public EntryDateBean(LocalDate date, String duration, String compliance, String filo, String door, String psNumber,
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

    public EntryDateBean(Date date, String duration, String compliance, String filo, String door, String psNumber,
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

    public EntryDateBean(String psNumber, String door, Duration durationSum, Duration complianceSum) {
        super();
        this.swipeDoor = door;
        this.duration = durationSum;
        this.compliance = complianceSum;
        this.psNumber = psNumber;
    }

    public EntryDateBean(String psNumber, Object[] rs) {

    }

    public EntryDateBean(
            String psNumber,
            LocalDate swipeDate,
            Duration duration,
            Duration filo,
            Duration compliance,
            String door,
            EmployeeBean employee) {
        super();
        this.psNumber = psNumber;
        this.swipeDate = swipeDate;
        this.duration = duration;
        this.compliance = compliance;
        this.filo = filo;
        this.swipeDoor = door;
        this.employee = employee;
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
        StringJoiner joiner = new StringJoiner(",");
        return joiner
                .add(swipeDate + "")
                .add(psNumber)
                .add(getFiloString())
                .add(getComplianceString())
                .toString();
    }

    public static final String[] fetchReportHeaders() {
        String[] reportHeaders = { "Date", "PS Number", "Floor Hours", "Compliance" };
        return reportHeaders;
    }

}
