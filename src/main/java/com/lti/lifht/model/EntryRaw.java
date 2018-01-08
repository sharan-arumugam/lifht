package com.lti.lifht.model;

import static com.lti.lifht.constant.ExcelConstant.SWP_MAP;
import static com.lti.lifht.util.CommonUtil.parseMDY;
import static com.lti.lifht.util.CommonUtil.parseTime;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntryRaw {

	Logger logger = LoggerFactory.getLogger(EntryRaw.class);

	private LocalDate swipeDate;
	private LocalTime swipeTime;
	private String swipeDoor;
	private String psNumber;

	public EntryRaw() {
		super();
	}

	public EntryRaw(Map<String, String> rawEntry) {
		swipeDate = parseMDY.apply(rawEntry.get(SWP_MAP.get("swipeDate")));
		swipeTime = parseTime.apply(rawEntry.get(SWP_MAP.get("swipeTime")));
		swipeDoor = rawEntry.get(SWP_MAP.get("swipeDoor"));
		psNumber = rawEntry.get(SWP_MAP.get("psNumber"));
	}

	public EntryRaw(LocalDate swipeDate, LocalTime swipeTime, String swipeDoor, String psNumber) {
		this.swipeDate = swipeDate;
		this.swipeTime = swipeTime;
		this.swipeDoor = swipeDoor;
		this.psNumber = psNumber;
	}

	public LocalDate getSwipeDate() {
		return swipeDate;
	}

	public void setSwipeDate(String swipeDate) {
		this.swipeDate = parseMDY.apply(swipeDate);
	}

	public LocalTime getSwipeTime() {
		return swipeTime;
	}

	public void setSwipeTime(String swipeTime) {
		this.swipeTime = parseTime.apply(swipeTime);
	}

	public String getSwipeDoor() {
		return swipeDoor;
	}

	public void setSwipeDoor(String swipeDoor) {
		this.swipeDoor = swipeDoor;
	}

	public String getPsNumber() {
		return psNumber;
	}

	public void setPsNumber(String psNumber) {
		this.psNumber = psNumber;
	}
}
