package com.lti.lifht.model.request;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DateSinglePs {

    private String psNumber;
    private Date date;

    public String getPsNumber() {
        return psNumber;
    }

    public void setPsNumber(String psNumber) {
        this.psNumber = psNumber;
    }

    public LocalDate getDate() {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
