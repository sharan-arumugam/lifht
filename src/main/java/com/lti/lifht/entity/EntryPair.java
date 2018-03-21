package com.lti.lifht.entity;

import static javax.persistence.GenerationType.AUTO;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import com.lti.lifht.model.EntryPairBean;

@Entity
@Table(name = "entry_pair", uniqueConstraints = @UniqueConstraint(columnNames = {
        "psNumber",
        "swipeDate",
        "swipeIn",
        "swipeOut",
        "swipeDoor" }))

public class EntryPair {

    @Id
    @GeneratedValue(strategy = AUTO)
    private int id;
    private String psNumber;
    private LocalDate swipeDate;
    private LocalTime swipeIn;
    private LocalTime swipeOut;
    private String swipeDoor;
    private long duration;
    @Transient
    private Employee employee;

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public EntryPair(EntryPairBean bean) {
        psNumber = bean.getPsNumber();
        swipeDate = bean.getSwipeDate();
        swipeIn = bean.getSwipeIn();
        swipeOut = bean.getSwipeOut();
        swipeDoor = bean.getSwipeDoor();
        duration = bean.getDuration().toMillis();
    }

    public EntryPair() {
        super();
    }

    public EntryPair(int id, String psNumber, LocalDate swipeDate, LocalTime swipeIn, LocalTime swipeOut,
            String swipeDoor, long duration) {
        super();
        this.id = id;
        this.psNumber = psNumber;
        this.swipeDate = swipeDate;
        this.swipeIn = swipeIn;
        this.swipeOut = swipeOut;
        this.swipeDoor = swipeDoor;
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPsNumber() {
        return psNumber;
    }

    public void setPsNumber(String psNumber) {
        this.psNumber = psNumber;
    }

    public LocalDate getSwipeDate() {
        return swipeDate;
    }

    public void setSwipeDate(LocalDate swipeDate) {
        this.swipeDate = swipeDate;
    }

    public LocalTime getSwipeIn() {
        return swipeIn;
    }

    public void setSwipeIn(LocalTime swipeIn) {
        this.swipeIn = swipeIn;
    }

    public LocalTime getSwipeOut() {
        return swipeOut;
    }

    public void setSwipeOut(LocalTime swipeOut) {
        this.swipeOut = swipeOut;
    }

    public String getSwipeDoor() {
        return swipeDoor;
    }

    public void setSwipeDoor(String swipeDoor) {
        this.swipeDoor = swipeDoor;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

}
