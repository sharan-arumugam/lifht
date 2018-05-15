package com.lti.lifht.util;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Stream.iterate;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ONE;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class LocalDateStream implements Iterable<LocalDate> {

	private final LocalDate start;
	private final LocalDate end;
	private Boolean weekend = FALSE;

	/**
	 * Weekend excluded/false by default
	 * 
	 * @param startDate
	 * @param endDate
	 */
	public LocalDateStream(LocalDate startDate, LocalDate endDate) {
		requireNonNull(startDate, "start");
		requireNonNull(endDate, "end");
		start = startDate;
		end = endDate;
	}

	public LocalDateStream includeWeekend() {
		weekend = TRUE;
		return this;
	}

	public LocalDateStream excludeWeekend() {
		weekend = FALSE;
		return this;
	}

	public Stream<LocalDate> stream() {
		Stream<LocalDate> dateStream = iterate(start, next -> next.plusDays(INTEGER_ONE))
				.limit(DAYS.between(start, end) + INTEGER_ONE)
				.filter(date -> date.getDayOfWeek() != SATURDAY)
				.filter(date -> date.getDayOfWeek() != SUNDAY);

		return weekend ? dateStream.filter(date -> date.getDayOfWeek() != SATURDAY)
				.filter(date -> date.getDayOfWeek() != SUNDAY)
				: dateStream;
	}

	@Override
	public Iterator<LocalDate> iterator() {
		return stream()
				.iterator();
	}

	public List<LocalDate> list() {
		return stream()
				.collect(toCollection(ArrayList::new));
	}

	public Set<LocalDate> set() {
		return stream()
				.collect(toCollection(HashSet::new));
	}

	public Map<String, LocalDate> map() {
		return stream()
				.collect(toMap(LocalDate::toString, identity()));
	}

}