package com.lti.lifht.service;

import static com.lti.lifht.constant.CommonConstant.ENTRY;
import static com.lti.lifht.constant.CommonConstant.EXIT;
import static com.lti.lifht.constant.ExcelConstant.ALC_MAP;
import static com.lti.lifht.constant.ExcelConstant.HC_MAP;
import static com.lti.lifht.constant.ExcelConstant.SWP_MAP;
import static com.lti.lifht.constant.SwipeConstant.ANTIPASS;
import static com.lti.lifht.constant.SwipeConstant.DOOR_DT;
import static com.lti.lifht.constant.SwipeConstant.DOOR_T2;
import static com.lti.lifht.constant.SwipeConstant.DOOR_TR;
import static com.lti.lifht.constant.SwipeConstant.DOOR_TS;
import static com.lti.lifht.constant.SwipeConstant.GRANTED1;
import static com.lti.lifht.constant.SwipeConstant.TIMEOUT;
import static com.lti.lifht.util.CommonUtil.parseMDY;
import static com.lti.lifht.util.CommonUtil.reportDateFormatter;
import static java.time.LocalDate.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.util.Comparator.comparing;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summarizingDouble;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.math.NumberUtils.isCreatable;
import static org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER;
import static org.apache.poi.ss.usermodel.HorizontalAlignment.GENERAL;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lti.lifht.entity.Allocation;
import com.lti.lifht.entity.EntryDate;
import com.lti.lifht.entity.EntryPair;
import com.lti.lifht.entity.Exclusion;
import com.lti.lifht.entity.HeadCount;
import com.lti.lifht.model.AllocationRaw;
import com.lti.lifht.model.EmployeeBean;
import com.lti.lifht.model.EntryDateBean;
import com.lti.lifht.model.EntryPairBean;
import com.lti.lifht.model.EntryRange;
import com.lti.lifht.model.EntryRaw;
import com.lti.lifht.model.ExclusionRaw;
import com.lti.lifht.model.HeadCountRaw;
import com.lti.lifht.model.request.RangeMultiPs;
import com.lti.lifht.repository.AllocationRepository;
import com.lti.lifht.repository.EmployeeRepository;
import com.lti.lifht.repository.EntryDateRepository;
import com.lti.lifht.repository.EntryPairRepository;
import com.lti.lifht.repository.ExclusionRepository;
import com.lti.lifht.repository.HeadCountRepository;
import com.lti.lifht.repository.RoleMasterRepository;
import com.lti.lifht.util.CommonUtil;

import one.util.streamex.StreamEx;

@Service
public class IOService {

    @Autowired
    private AdminService adminService;

    @Autowired
    private MailService emailService;

    @Autowired
    private EmployeeRepository employeeRepo;

    @Autowired
    private RoleMasterRepository roleMasterRepo;

    @Autowired
    private EntryPairRepository entryPairRepo;

    @Autowired
    private EntryDateRepository entryDateRepo;

    @Autowired
    private HeadCountRepository headCountRepo;

    @Autowired
    private AllocationRepository allocationRepo;

    @Autowired
    private ExclusionRepository exclusionRepo;

    @Autowired
    private ObjectMapper mapper;

    public List<EntryPairBean> pairEntry(List<Map<String, String>> entries, String doorName) {

        Comparator<EntryRaw> byPsNumDateTime = comparing(EntryRaw::getPsNumber)
                .thenComparing(EntryRaw::getSwipeDate)
                .thenComparing(EntryRaw::getSwipeTime);

        Predicate<Map<String, String>> validEvent = entry -> {
            String eventNumber = entry.get(SWP_MAP.get("eventNumber"));

            eventNumber = eventNumber != null ? eventNumber : GRANTED1;
            return !(eventNumber.equals(TIMEOUT)
                    || eventNumber.equals(ANTIPASS)
                    || eventNumber.startsWith("---"));
        };

        boolean isTurnstile = doorName.equals(DOOR_TS);

        Function<Map<String, String>, String> getDoor = entry -> entry.get(SWP_MAP.get("swipeDoor"));

        Predicate<Map<String, String>> turnstileEntry = entry -> getDoor.apply(entry).contains(DOOR_TS);

        Predicate<Map<String, String>> otherDoorEntry = entry -> getDoor.apply(entry).contains(doorName);

        UnaryOperator<EntryRaw> adaptToTurnstile = entry -> {

            EntryRaw mutated = entry;
            String door = entry.getSwipeDoor();
            String suffix = door.endsWith(ENTRY) ? ENTRY : EXIT;

            // TODO: Turnstile - 2 bug entry exit flipped
            if (door.contains(DOOR_T2)) {
                suffix = door.endsWith(ENTRY) ? EXIT : ENTRY;
            }

            mutated.setSwipeDoor(DOOR_TS + suffix);
            return mutated;
        };

        // parse row to EntryRaw
        List<EntryRaw> entryList = entries.stream()
                .filter(Objects::nonNull)
                .filter(validEvent)
                .filter(isTurnstile ? turnstileEntry : otherDoorEntry)
                .map(EntryRaw::new)
                .map(isTurnstile ? adaptToTurnstile : identity())
                .sorted(byPsNumDateTime)
                .collect(toList());

        int entrySize = entryList.size();

        List<EntryRaw> filteredList = new ArrayList<>();

        Predicate<Integer> doorNotNull = index -> null != entryList.get(index)
                && null != entryList.get(index).getSwipeDoor();

        BiPredicate<EntryRaw, EntryRaw> validRow = (current, adjacent) -> {
            return current.getPsNumber().equals(adjacent.getPsNumber())
                    && current.getSwipeDate().equals(adjacent.getSwipeDate());
        };

        BiPredicate<EntryRaw, EntryRaw> doorPair = (current, next) -> {
            return current.getSwipeDoor().endsWith(ENTRY) &&
                    next.getSwipeDoor().endsWith(EXIT);
        };

        BiPredicate<EntryRaw, EntryRaw> sameDoor = (current, adjacent) -> {
            return current.getSwipeDoor().equals(adjacent.getSwipeDoor());
        };

        BiPredicate<EntryRaw, EntryRaw> timeNotNull = (current, adjacent) -> {
            return null != current
                    && null != current.getSwipeTime()
                    && null != adjacent
                    && null != adjacent.getSwipeTime();
        };

        // filter repeating doors
        Predicate<Integer> duplicateEntry = index -> {
            EntryRaw current = entryList.get(index);

            if (index > 0) { // filter duplicate door entries
                EntryRaw previous = entryList.get(index - 1);
                return validRow.test(current, previous)
                        ? !sameDoor.test(current, previous)
                        : true;

            } else if (index == 0 && index + 1 < entrySize) { // validate first pair
                EntryRaw next = entryList.get(index + 1);

                return validRow.test(current, next)
                        ? doorPair.test(current, next)
                        : false;
            }
            return true;
        };

        Consumer<Integer> addToFilteredList = index -> filteredList.add(entryList.get(index));

        // parse EntryRaw to EntryPair
        BiFunction<EntryRaw, EntryRaw, EntryPairBean> toEntryPair = (current, next) -> {
            if (timeNotNull.test(current, next)
                    && validRow.test(current, next)
                    && doorPair.test(current, next)) {

                EntryPairBean pair = new EntryPairBean(current);
                pair.setSwipeIn(current.getSwipeTime());
                pair.setSwipeOut(next.getSwipeTime());
                return pair;
            }
            return null;
        };

        // filter duplicates
        IntStream.range(0, entrySize)
                .boxed()
                .filter(doorNotNull)
                .filter(duplicateEntry)
                .forEach(addToFilteredList);

        // map to pairs
        List<EntryPairBean> pairList = StreamEx.of(filteredList)
                .sorted(byPsNumDateTime)
                .nonNull()
                .pairMap(toEntryPair)
                .nonNull()
                .collect(toList());

        return pairList;
    }

    public List<EntryDateBean> dateEntry(List<EntryPairBean> pairList) {

        List<EntryDateBean> dateList = new ArrayList<>();

        pairList.stream()
                .collect(groupingBy(EntryPairBean::getSwipeDate))
                .forEach((date, psList) -> {

                    psList.stream()
                            .filter(entry -> null != entry.getPsNumber())
                            .collect(groupingBy(EntryPairBean::getPsNumber))
                            .forEach((psNumber, groupedList) -> {

                                LocalTime firstIn = groupedList.stream()
                                        .findFirst()
                                        .get()
                                        .getSwipeIn();

                                LocalTime lastOut = groupedList.stream()
                                        .reduce((current, next) -> next)
                                        .get()
                                        .getSwipeOut();

                                String door = groupedList.stream()
                                        .map(EntryPairBean::getSwipeDoor)
                                        .findAny()
                                        .orElse("Invalid");

                                Duration durationSum = groupedList.stream()
                                        .map(EntryPairBean::getDuration)
                                        .reduce(Duration::plus)
                                        .orElse(null);

                                dateList.add(new EntryDateBean(psNumber, date, door, durationSum, firstIn, lastOut));
                            });
                });

        return dateList;
    }

    public Integer saveOrUpdateEntry(List<Map<String, String>> entries) {

        Map<String, EntryDateBean> dateMapTurnstile = dateEntry(pairEntry(entries, DOOR_TS)).stream()
                .collect(toMap(EntryDateBean::getPsNumber, identity()));

        Map<String, EntryDateBean> dateMapTraining = dateEntry(pairEntry(entries, DOOR_TR)).stream()
                .collect(toMap(EntryDateBean::getPsNumber, identity()));

        Map<String, EntryDateBean> dateMapDorm = dateEntry(pairEntry(entries, DOOR_DT)).stream()
                .collect(toMap(EntryDateBean::getPsNumber, identity()));

        entryPairRepo.saveOrUpdatePair(pairEntry(entries, DOOR_TS)
                .stream()
                .map(EntryPair::new)
                .filter(pair -> isCreatable(pair.getPsNumber()))
                .collect(toList()));

        dateMapTurnstile.forEach((psNumber, turnstileEntryDate) -> {

            EntryDateBean trainingSum = null != dateMapTraining.get(psNumber)
                    ? dateMapTraining.get(psNumber)
                    : new EntryDateBean();

            EntryDateBean dormSum = null != dateMapDorm.get(psNumber)
                    ? dateMapDorm.get(psNumber)
                    : new EntryDateBean();

            Duration duration = null != turnstileEntryDate.getDuration()
                    ? turnstileEntryDate.getDuration()
                    : Duration.ZERO;

            if (null != trainingSum.getDuration()) {
                duration = duration.minus(trainingSum.getDuration());
            }

            if (null != dormSum.getDuration()) {
                duration = duration.minus(dormSum.getDuration());
            }

            turnstileEntryDate.setDuration(duration);
        });

        return entryDateRepo.saveOrUpdateDate(dateMapTurnstile.values()
                .stream()
                .map(EntryDate::new)
                .collect(toList()));
    }

    public Integer saveOrUpdateHeadCount(List<Map<String, String>> rows) {

        List<EmployeeBean> offshoreList = rows
                .stream()
                .filter(row -> row.get(HC_MAP.get("offshore")).equalsIgnoreCase("Yes"))
                .filter(row -> isNotBlank(row.get(HC_MAP.get("psNumber"))))
                .map(row -> new EmployeeBean(row, HC_MAP))
                .collect(toList());

        int updateCount = employeeRepo.saveOrUpdateHeadCount(offshoreList);

        List<String> psNumberList = offshoreList.stream()
                .map(EmployeeBean::getPsNumber)
                .collect(toList());

        employeeRepo.resetAccess(psNumberList, roleMasterRepo.findByRole("ROLE_EMPLOYEE"));

        return updateCount;
    }

    public Integer saveOrUpdateProjectAllocation(List<Map<String, String>> rows) {

        Set<String> psNumbers = employeeRepo.findAllpsNumber();

        rows = rows
                .stream()
                .filter(row -> null != row.get(ALC_MAP.get("customer"))
                        && row.get(ALC_MAP.get("customer")).equalsIgnoreCase("Apple"))
                .filter(row -> psNumbers.contains(row.get(ALC_MAP.get("psNumber"))))
                .collect(toList());

        List<EmployeeBean> employeeList = rows
                .stream()
                .map(row -> new EmployeeBean(row, ALC_MAP))
                .collect(toList());

        return employeeRepo.saveOrUpdateProjectAllocation(employeeList);
    }

    public Workbook generateRangeMultiDatedReport(XSSFWorkbook workbook, StringJoiner cumulativeHeaders,
            Set<LocalDate> datedHeaders, Object[] reportData, Map<String, StringJoiner> averageMap) {

        LocalDate from = datedHeaders.stream()
                .sorted(Comparator.comparing(LocalDate::atStartOfDay))
                .findFirst()
                .orElse(null);

        LocalDate to = datedHeaders.stream()
                .sorted(Comparator.comparing(LocalDate::atStartOfDay))
                .reduce((d1, d2) -> d2)
                .orElse(null);

        String cumulativeDateRange = null != from && null != to
                ? "Cumulative"
                        + " from " + from.format(reportDateFormatter)
                        + " to " + to.format(reportDateFormatter)
                : "Invalid dates";

        XSSFSheet sheet0 = (XSSFSheet) workbook.createSheet("report");
        XSSFSheet sheet1 = (XSSFSheet) workbook.createSheet("average");

        Font font = workbook.createFont();
        font.setBold(true);
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(CENTER);
        headerStyle.setFont(font);

        return createDatedTable(sheet0, sheet1, headerStyle, cumulativeDateRange, cumulativeHeaders,
                datedHeaders, reportData, averageMap);

    }

    private Workbook createDatedTable(XSSFSheet sheet0, XSSFSheet sheet1, CellStyle headerStyle,
            String cumulativeDateRange, StringJoiner cumulativeHeaders, Set<LocalDate> datedHeaders,
            Object[] reportData, Map<String, StringJoiner> averageMap) {

        int rowLength = reportData.length;

        List<String> dateHeaderList = new ArrayList<>();
        dateHeaderList.add(""); // bu
        dateHeaderList.add(""); // dsId
        dateHeaderList.add(""); // psNumber
        dateHeaderList.add(""); // psName
        dateHeaderList.add(""); // validSince
        dateHeaderList.add(""); // daysPresent
        dateHeaderList.add(""); // filo
        dateHeaderList.add(""); // floor
        dateHeaderList.add(""); // compliance

        datedHeaders.stream()
                .sorted(Comparator.comparing(LocalDate::atStartOfDay))
                .forEach(e -> {
                    dateHeaderList.add(e.format(reportDateFormatter));
                    dateHeaderList.add(""); // colspan for filo-floor
                });

        List<String> headersList2 = new ArrayList<>();
        headersList2.addAll(Arrays.asList(cumulativeHeaders.toString().split(",")));

        datedHeaders.stream().forEach(e -> {
            headersList2.add("FILO");
            headersList2.add("Floor");
        });

        Object[] dateHeader = dateHeaderList.toArray();
        Object[] reportHeaders2 = headersList2.toArray();

        // set headers
        int colLength = dateHeader.length;
        XSSFRow headerRow = sheet0.createRow(0); // first row as column names
        IntStream.range(0, colLength).forEach(colIndex -> {
            XSSFCell cell = headerRow.createCell(colIndex);
            cell.setCellValue(String.valueOf(dateHeader[colIndex]));
        });

        Cell cumulativeTitleCell = sheet0.getRow(0).getCell(4);
        cumulativeTitleCell.setCellValue(cumulativeDateRange);
        cumulativeTitleCell.setCellStyle(headerStyle);
        sheet0.addMergedRegion(new CellRangeAddress(0, 0, 4, 8));

        int cellCount = 8;

        List<Cell> dateHederStreamList = CommonUtil.toStream(sheet0.getRow(0).cellIterator())
                .collect(Collectors.toList());

        for (Cell cell : dateHederStreamList) {
            if (cellCount < dateHederStreamList.size()) {
                sheet0.addMergedRegion(new CellRangeAddress(
                        0,
                        0,
                        ++cellCount,
                        ++cellCount));
            }
            cell.setCellStyle(headerStyle);
        }

        // set headers
        XSSFRow headerRow2 = sheet0.createRow(1); // first row as column names
        IntStream.range(0, colLength).forEach(colIndex -> {
            XSSFCell cell = headerRow2.createCell(colIndex);
            cell.setCellValue(String.valueOf(reportHeaders2[colIndex]));
            cell.setCellStyle(headerStyle);
        });

        // set row, column values
        IntStream.range(0, rowLength).forEach(rowIndex -> {
            String[] colArr = reportData[rowIndex].toString().split(",");
            XSSFRow row = sheet0.createRow(rowIndex + 2); // +1 as first row for headers

            IntStream.range(0, colLength).forEach(colIndex -> {
                XSSFCell cell = row.createCell(colIndex);
                if (NumberUtils.isCreatable(colArr[colIndex])) {
                    cell.setCellValue(Double.valueOf(colArr[colIndex]));
                } else {
                    cell.setCellValue(null != colArr[colIndex]
                            && !"null".equals(colArr[colIndex])
                                    ? colArr[colIndex]
                                    : "");
                }
                sheet0.autoSizeColumn(colIndex);
            });
        });

        // Sheet 2: Average
        Row averageTitle = sheet1.createRow(0);
        Cell titleCell = averageTitle.createCell(0);
        titleCell.setCellValue("Billable Statistics: " + now().format(ofPattern("MMM, yyyy")));
        titleCell.setCellStyle(headerStyle);

        sheet1.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));

        Row averageHeader = sheet1.createRow(1);
        Cell dateCell = averageHeader.createCell(0);
        dateCell.setCellValue("Date");

        Cell countCell = averageHeader.createCell(1);
        countCell.setCellValue("Count");

        Cell averageCell = averageHeader.createCell(2);
        averageCell.setCellValue("Average");

        dateCell.setCellStyle(headerStyle);
        countCell.setCellStyle(headerStyle);
        averageCell.setCellStyle(headerStyle);

        AtomicInteger averageRowCount = new AtomicInteger(2);

        averageMap.entrySet()
                .stream()
                .sorted(Entry.comparingByKey())
                .map(Entry::getValue)
                .forEach(joiner -> {
                    Row averageRow = sheet1.createRow(averageRowCount.getAndIncrement());
                    String[] values = joiner.toString().split(",");

                    averageRow.createCell(0).setCellValue(values[0]);
                    averageRow.createCell(1).setCellValue(Double.valueOf(values[1]));
                    averageRow.createCell(2).setCellValue(Double.valueOf(values[2]));
                });

        sheet1.createRow(averageRowCount.getAndIncrement()); // separator

        Function<Integer, DoubleSummaryStatistics> statify = index -> averageMap
                .values()
                .stream()
                .filter(Objects::nonNull)
                .map(joiner -> joiner.toString().split(",")[index])
                .filter(StringUtils::isNotEmpty)
                .collect(summarizingDouble(Double::valueOf));

        DoubleSummaryStatistics headCountStatistics = statify.apply(1);
        DoubleSummaryStatistics averaeStatistics = statify.apply(2);
        //DoubleSummaryStatistics sumStatistics = statify.apply(3);

        long days = averaeStatistics.getCount();
        double countAverage = headCountStatistics.getAverage();
        double grandAverage = averaeStatistics.getAverage();

        double grandAverageRound = (double) Math.round(grandAverage * 100.0) / 100.0;

        CellStyle footerStyle = sheet1.getWorkbook().createCellStyle();
        Font boldFont = sheet1.getWorkbook().createFont();
        boldFont.setBold(true);
        footerStyle.setAlignment(GENERAL);
        footerStyle.setFont(boldFont);

        Row averageFooter = sheet1.createRow(averageRowCount.getAndIncrement());
        Cell footerLabel = averageFooter.createCell(0);
        footerLabel.setCellValue("Average(" + days + ")");
        footerLabel.setCellStyle(footerStyle);

        Cell footerCount = averageFooter.createCell(1);
        footerCount.setCellValue(Double.valueOf(Math.round(countAverage)));
        footerCount.setCellStyle(footerStyle);

        Cell footerAverage = averageFooter.createCell(2);
        footerAverage.setCellValue(Double.valueOf(grandAverageRound));
        footerAverage.setCellStyle(footerStyle);

        sheet1.autoSizeColumn(0);
        sheet1.autoSizeColumn(1);
        sheet1.autoSizeColumn(2);

        return sheet0.getWorkbook();
    }

    public void saveHeadCountForReconciliation(List<Map<String, String>> rows) {

        List<HeadCount> currentList = headCountRepo.findAll();

        List<HeadCount> newList = rows
                .stream()
                .skip(1)
                .map(HeadCountRaw::new)
                .map(HeadCount::new)
                .collect(toList());

        Predicate<HeadCount> newContains = newList::contains;

        List<HeadCount> toDelete = currentList.stream()
                .filter(newContains.negate())
                .collect(toList());

        headCountRepo.save(newList);
        headCountRepo.delete(toDelete);
    }

    public void saveAllocationForReconciliation(List<Map<String, String>> rows) {

        List<Allocation> currentList = allocationRepo.findAll();

        List<Allocation> newList = rows
                .stream()
                .skip(1)
                .filter(row -> null != row.get(ALC_MAP.get("customer"))
                        && row.get(ALC_MAP.get("customer")).equalsIgnoreCase("Apple"))
                .map(AllocationRaw::new)
                .map(Allocation::new)
                .collect(toList());

        Predicate<Allocation> newContains = newList::contains;

        List<Allocation> toDelete = currentList.stream()
                .filter(newContains.negate())
                .collect(toList());

        allocationRepo.save(newList);
        allocationRepo.delete(toDelete);
    }

    public JsonNode reconcileHeadCount() {
        List<String> hcPsList = headCountRepo.findAll()
                .stream()
                .map(HeadCount::getPsNumber)
                .collect(toList());

        List<Allocation> allocationRecon = allocationRepo.psNumberNotIn(hcPsList);
        List<String> entryDateRecon = entryDateRepo.psNumberNotIn(hcPsList);

        Map<String, List<?>> reconcileMap = new HashMap<>();
        reconcileMap.put("allocationRecon", allocationRecon);
        reconcileMap.put("entryDateRecon", entryDateRecon);
        return mapper.valueToTree(reconcileMap);
    }

    public JsonNode reconcileAllocation() {
        List<String> allocPsList = allocationRepo.findAll()
                .stream()
                .map(Allocation::getPsNumber)
                .collect(toList());

        List<HeadCount> headCountRecon = headCountRepo.psNumberNotIn(allocPsList);
        List<String> entryDateRecon = entryDateRepo.psNumberNotIn(allocPsList);

        Map<String, List<?>> reconcileMap = new HashMap<>();
        reconcileMap.put("headCountRecon", headCountRecon);
        reconcileMap.put("entryDateRecon", entryDateRecon);

        return mapper.valueToTree(reconcileMap);
    }

    public JsonNode reconcileSwipe() {
        List<String> swipePsList = entryDateRepo.findAll()
                .stream()
                .map(EntryDate::getPsNumber)
                .distinct()
                .collect(toList());

        List<HeadCount> headCountRecon = headCountRepo.psNumberNotIn(swipePsList);
        List<Allocation> allocationRecon = allocationRepo.psNumberNotIn(swipePsList);

        Map<String, List<?>> reconcileMap = new HashMap<>();
        reconcileMap.put("headCountRecon", headCountRecon);
        reconcileMap.put("allocationRecon", allocationRecon);

        return mapper.valueToTree(reconcileMap);
    }

    public JsonNode reconcileAll() {
        Map<String, JsonNode> reconcileMap = new HashMap<>();
        reconcileMap.put("headCount", reconcileHeadCount());
        reconcileMap.put("allocation", reconcileAllocation());
        reconcileMap.put("swipe", reconcileSwipe());

        return mapper.valueToTree(reconcileMap);
    }

    public void notifyNonCompliant(String swipeDate, String appUrl) {

        LocalDate toDate = parseMDY.apply(swipeDate);
        LocalDate fromDate = toDate.with(firstDayOfMonth());

        RangeMultiPs request = new RangeMultiPs(fromDate.format(ofPattern("dd-MM-yyyy")),
                toDate.format(ofPattern("dd-MM-yyyy")),
                null);

        List<EntryRange> cumulative = adminService.getRangeMulti(request, true);

        // List<EntryRange> billable = cumulative.stream()
        // .filter(range -> null != range.getEmployee().getBillable()
        // ? range.getEmployee().getBillable().equalsIgnoreCase("yes")
        // : false)
        // .collect(Collectors.toList());

        List<EntryRange> withDsId = cumulative.stream()
                .filter(range -> null != range.getEmployee().getDsId())
                .collect(Collectors.toList());

        List<EntryRange> nonCompliant = withDsId.stream()
                .filter(entryRange -> null != entryRange.getEmployee().getPsName())
                .filter(entryRange -> entryRange
                        .getCompliance()
                        .isNegative())
                .collect(toList());

        List<Map<String, String>> nonCompliantRows = nonCompliant.stream().map(entryRange -> {
            Map<String, String> dataMap = new LinkedHashMap<>();
            dataMap.put("Name", entryRange.getEmployee().getPsName());
            dataMap.put("PS Number", entryRange.getPsNumber());
            dataMap.put("From", entryRange.getFromDate().toString());
            dataMap.put("To", entryRange.getToDate().toString());
            dataMap.put("Days", String.valueOf(entryRange.getDaysPresent()));
            dataMap.put("Floor", entryRange.getDurationString());
            dataMap.put("Compliance", entryRange.getComplianceString());
            return dataMap;
        }).collect(toList());

        nonCompliantRows.stream().forEach(row -> {

            StringBuilder htmlBody = new StringBuilder();

            htmlBody.append("Hello " + row.remove("Name") + ", <br>");

            htmlBody.append("Please maintain compliance, current compliance is ")
                    .append("<font color='red'> <b>")
                    .append(row.get("Compliance"))
                    .append("<b> </font>");

            htmlBody.append("<br>");
            htmlBody.append("<br>");

            htmlBody.append("<table border='1px' cellpadding='4'>");

            htmlBody.append("<thead><tr>");

            row.forEach((heading, value) -> {
                htmlBody.append("<th>").append(heading).append("</th>");
            });

            htmlBody.append("</tr></thead>");

            htmlBody.append("<tbody><tr>");

            row.forEach((heading, value) -> {
                htmlBody.append("<td align='center'>")
                        .append("<font color='red'>")
                        .append(value)
                        .append("</font>")
                        .append("</td>");
            });

            htmlBody.append("</tr></tbody>");

            htmlBody.append("</table>");

            htmlBody.append("<br>");
            htmlBody.append("<br>");

            String appLink = "<a href=" + appUrl + ">" + "<b>" + "LTI Floor Hour Tracker [Lifht]" + "</b></a>";

            htmlBody.append("For more details, please check your compliance @ ").append(appLink);
            htmlBody.append("<br>");
            htmlBody.append("PS: Kindly disconnect VPN before accessing the portal");
            htmlBody.append("<br>");

            emailService.sendMail(row.get("PS Number"), "Compliance " + row.get("Compliance"), htmlBody.toString());
        });

    }

    public void saveOrUpdateExclusion(List<Map<String, String>> rows) {
        List<Exclusion> exclusionList = rows.stream()
                .skip(1)
                .map(ExclusionRaw::new)
                .map(Exclusion::new)
                .collect(Collectors.toList());

        exclusionRepo.save(exclusionList);
    }

}
