package com.lti.lifht.util;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Stream.iterate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class LocalDateStream implements Iterable<LocalDate> {

	private final Integer ONE = 1;
	private final LocalDate start;
	private final LocalDate end;

	public LocalDateStream(LocalDate startDate, LocalDate endDate) {
		Objects.requireNonNull(startDate, "start");
		Objects.requireNonNull(endDate, "end");
		start = startDate;
		end = endDate;
	}

	public Stream<LocalDate> stream() {
		return iterate(start, next -> next.plusDays(ONE))
				.limit(DAYS.between(start, end) + ONE);
	}

	@Override
	public Iterator<LocalDate> iterator() {
		return stream()
				.iterator();
	}

	public List<LocalDate> toList() {
		return stream()
				.collect(toCollection(ArrayList::new));
	}

}