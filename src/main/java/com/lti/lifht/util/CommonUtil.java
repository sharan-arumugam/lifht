package com.lti.lifht.util;

import static com.lti.lifht.constant.ExcelConstant.SWP_MAP;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.collections4.map.UnmodifiableMap.unmodifiableMap;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap.SimpleEntry;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CommonUtil {

    static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    static Function<String, SimpleEntry<String, String>> crypt = base -> new SimpleEntry<>(base, encoder.encode(base));

    public static final DateTimeFormatter reportDateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public static final <T> Stream<T> toStream(Iterator<T> iterator) {
        Iterable<T> iterable = () -> iterator;
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    public static final Function<String, LocalDate> parseMDY = source -> {
        String[] splitDate = source.split("/");
        if (splitDate[2].length() < 4) {
            splitDate[2] = "20" + splitDate[2];
        }
        return LocalDate.parse(Stream.of(splitDate)
                .collect(Collectors.joining("/")), ofPattern("M/d/yyyy"));
    };

    private static DateTimeFormatter DMYDash = ofPattern("dd-MM-yyyy");
    public static final Function<String, LocalDate> parseDMYDash = source -> LocalDate.parse(source, DMYDash);
    public static final Function<String, Date> parseDMYDashToDate = source -> Date.from(parseDMYDash.apply(source)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant());

    private static DateTimeFormatter HMM = ofPattern("H:mm:ss");
    public static final Function<String, LocalTime> parseTime = source -> {
        return StringUtils.isNotBlank(source
                .replaceAll("1900-01-01 ", "")
                .replaceAll(".000", ""))
                        ? LocalTime.parse(source, HMM)
                        : null;
    };

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

    public static String formatDuration(Duration duration) {
        if (null == duration) {
            return null;
        }

        long seconds = Math.abs(duration.getSeconds());
        String unsigned = String.format(
                "%d:%02d:%02d",
                seconds / 3600,
                (seconds % 3600) / 60,
                seconds % 60);
        return duration.getSeconds() < 0 ? "-" + unsigned : unsigned;
    }

    public static Map<String, String> cryptTupleMap(List<String> baseList) {
        return unmodifiableMap(baseList.stream()
                .map(crypt)
                .collect(toMap(SimpleEntry::getKey,
                        SimpleEntry::getValue)));
    }

}
