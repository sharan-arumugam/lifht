package com.lti.lifht.util;

import static com.lti.lifht.constant.CommonConstant.DATE;
import static com.lti.lifht.constant.CommonConstant.DATE_FORMAT;
import static com.lti.lifht.constant.CommonConstant.DEFAULT_DATE;
import static com.lti.lifht.constant.CommonConstant.DEFAULT_NUMBER;
import static com.lti.lifht.constant.CommonConstant.DEFAULT_TEXT;
import static com.lti.lifht.constant.CommonConstant.DEFAULT_TIME;
import static com.lti.lifht.constant.CommonConstant.NUMBER;
import static com.lti.lifht.constant.CommonConstant.TEXT;
import static com.lti.lifht.constant.CommonConstant.TIME;
import static com.lti.lifht.constant.CommonConstant.TIME_FORMAT;
import static com.lti.lifht.constant.ExcelConstant.SWP_MAP;
import static java.time.Instant.ofEpochMilli;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.stream.StreamSupport.stream;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.lti.lifht.model.EntryRaw;
import com.lti.lifht.model.EntryPairBean;

public class CommonUtil {

	private static final Logger logger = LoggerFactory.getLogger(CommonUtil.class);

	private static final Gson gson = new Gson();

	// Date formats
	// https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html
	private static DateTimeFormatter MDY = ofPattern("M/d/yyyy");
	public static final Function<String, LocalDate> parseMDY = source -> {
		if (source.endsWith("/17")) {
			source = source.substring(0, 5) + "20" + source.substring(6, 7);
		}
		return LocalDate.parse(source, MDY);
	};

	private static DateTimeFormatter DMYDash = ofPattern("dd-MM-yyyy");
	public static final Function<String, LocalDate> parseDMYDash = source -> LocalDate.parse(source, DMYDash);

	public static final Function<String, Date> parseDMYDashToDate = source -> Date
			.from(LocalDate.parse(source, DMYDash).atStartOfDay(ZoneId.systemDefault()).toInstant());

	private static DateTimeFormatter DMY = ofPattern("dd/MM/yyyy");
	public static final Function<String, LocalDate> parseDMY = source -> LocalDate.parse(source, DMY);

	private static DateTimeFormatter HMM = ofPattern("H:mm:ss");
	public static final Function<String, LocalTime> parseTime = source -> {
		source = source.replaceAll("1900-01-01 ", "");
		source = source.replaceAll(".000", "");
		return StringUtils.isNotBlank(source)
				? LocalTime.parse(source, HMM)
				: null;
	};

	public static final BiFunction<List<EntryRaw>, Integer, EntryRaw> getNext = (list, index) -> list.get(index + 1);
	public static final BiFunction<List<EntryRaw>, Integer, EntryRaw> getPrev = (list, index) -> list.get(index - 1);

	public static final BiFunction<Map<String, String>, Map<String, String>, Map<String, String>> getLatest = (current,
			next) -> {

		if (current.get(SWP_MAP.get("swipeDoor")).contains(" Entry") &&
				next.get(SWP_MAP.get("swipeDoor")).contains(" Exit")) {
			return current;
		}

		else if (current.get(SWP_MAP.get("swipeDoor")).contains(" Exit") &&
				next.get(SWP_MAP.get("swipeDoor")).contains(" Entry")) {
			return current;
		}

		return null;
	};

	public static final Function<String, SimpleDateFormat> format = format -> new SimpleDateFormat(format);

	public static final BiPredicate<List<String>, Integer> isValid = (row, column) -> row.size() > column
			&& isNotEmpty(row.get(column));

	public static final Map<String, String> defaults = new HashMap<>();
	static {
		defaults.put(DATE, DEFAULT_DATE);
		defaults.put(TIME, DEFAULT_TIME);
		defaults.put(NUMBER, DEFAULT_NUMBER);
		defaults.put(TEXT, DEFAULT_TEXT);
	}

	public static String getModerated(List<String> row, Integer column, String type) {
		return isValid.test(row, column) ? row.get(column) : defaults.get(type);
	}

	public static Date toDate(String dateString) {
		Date output = null;
		try {
			output = format.apply(DATE_FORMAT).parse(dateString);
		} catch (ParseException e) {
			logger.error(e.getMessage());
		}
		return output;
	}

	public static long toTime(String timeString) {
		long output = 0;
		try {
			output = format.apply(TIME_FORMAT).parse(timeString).getTime();
		} catch (ParseException e) {
			logger.error(e.getMessage());
		}
		return output;
	}

	/**
	 * Get in hh:mm String format
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static String between(long start, long end, long complianceHours) {
		if (end == 0 || start > end) {
			return DEFAULT_TIME;
		}

		Duration difference = Duration.between(ofEpochMilli(start), ofEpochMilli(end));
		long millis = difference.minusHours(complianceHours).toMillis();

		String formatted = formatTime(millis);
		formatted = complianceHours == 0 ? formatted.substring(1, formatted.length()) : formatted;

		return formatted;
	}

	public static String formatTime(long millis) {

		int hours = modHours(millis);
		int minutes = modMinutes(millis);

		String formatted = "-00:00";

		if (hours >= 0 && minutes >= 0) {
			formatted = "+" + String.format("%02d:%02d", hours, minutes);

		} else if (hours > 0 && minutes < 0) {
			minutes *= -1;
			formatted = "+" + String.format("%02d:%02d", hours, minutes);

		} else if (hours < 0 && minutes >= 0) {
			formatted = String.format("%03d:%02d", hours, minutes);

		} else if (hours < 0 && minutes < 0) {
			minutes *= -1;
			formatted = String.format("%03d:%02d", hours, minutes);

		} else if (hours == 0 && minutes < 0) {
			minutes *= -1;
			formatted = "-" + String.format("%02d:%02d", hours, minutes);

		} else if (hours == 0 && minutes >= 0) {
			formatted = "+" + String.format("%02d:%02d", hours, minutes);
		}

		return formatted;
	}

	public static String formatToTime(long millis) {

		int hours = modHours(millis);
		int minutes = modMinutes(millis);

		String formatted = "";

		if (hours >= 0 && minutes >= 0) {
			formatted = String.format("%d:%d", hours, minutes);

		} else if (hours > 0 && minutes < 0) {
			minutes *= -1;
			formatted = String.format("%d:%d", hours, minutes);

		} else if (hours < 0 && minutes >= 0) {
			formatted = String.format("%d:%d", hours, minutes);

		} else if (hours < 0 && minutes < 0) {
			minutes *= -1;
			formatted = String.format("%d:%d", hours, minutes);

		} else if (hours == 0 && minutes < 0) {
			minutes *= -1;
			formatted = String.format("%d:%d", hours, minutes);

		} else if (hours == 0 && minutes >= 0) {
			formatted = String.format("%d:%d", hours, minutes);
		}

		return formatted;
	}

	/**
	 * Get in minutes long format
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static long between(EntryPairBean entry) {
		if (entry == null) {
			return 0;
		}
		return 0;// return Duration.between(ofEpochMilli(entry.getSwipeIn()),
					// ofEpochMilli(entry.getSwipeOut())).toMillis();
	}

	public static int modHours(long millis) {
		return (int) ((millis / (1000 * 60 * 60)) % 24);
	}

	public static int modMinutes(long millis) {
		return (int) ((millis / (1000 * 60)) % 60);
	}

	public static final <T> Stream<T> toStream(Iterator<T> iterator) {
		Iterable<T> iterable = () -> iterator;
		return stream(iterable.spliterator(), false);
	}

	public static String formatDuration(Duration duration) {
		long seconds = Math.abs(duration.getSeconds());
		String unsigned = String.format(
				"%d:%02d:%02d",
				seconds / 3600,
				(seconds % 3600) / 60,
				seconds % 60);
		return duration.getSeconds() < 0 ? "-" + unsigned : unsigned;
	}

	public static final String toJson(Object object) {
		return gson.toJson(object);
	}

	public static final <T> T fromJson(String json, Class<T> classOfT) {
		return classOfT.cast(gson.fromJson(json, classOfT));
	}
}
