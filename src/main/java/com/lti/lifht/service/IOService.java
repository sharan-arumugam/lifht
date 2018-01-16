package com.lti.lifht.service;

import static com.lti.lifht.constant.ExcelConstant.ALC_MAP;
import static com.lti.lifht.constant.ExcelConstant.HC_MAP;
import static com.lti.lifht.constant.ExcelConstant.SWP_MAP;
import static com.lti.lifht.util.CommonUtil.reportDateFormatter;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lti.lifht.constant.CommonConstant;
import com.lti.lifht.entity.EntryDate;
import com.lti.lifht.entity.EntryPair;
import com.lti.lifht.model.EmployeeBean;
import com.lti.lifht.model.EntryDateBean;
import com.lti.lifht.model.EntryPairBean;
import com.lti.lifht.model.EntryRaw;
import com.lti.lifht.repository.EmployeeRepository;
import com.lti.lifht.repository.EntryDateRepository;
import com.lti.lifht.repository.EntryPairRepository;
import com.lti.lifht.repository.RoleMasterRepository;
import com.lti.lifht.util.CommonUtil;

import one.util.streamex.StreamEx;

@Service
public class IOService {

	@Autowired
	EmployeeRepository employeeRepo;

	@Autowired
	RoleMasterRepository roleMasterRepo;

	@Autowired
	EntryPairRepository entryPairRepo;

	@Autowired
	EntryDateRepository entryDateRepo;

	Logger LOG = LoggerFactory.getLogger(IOService.class);

	public Integer saveOrUpdateRawEntry(List<Map<String, String>> entries) {

		Comparator<EntryRaw> byPsNumDateTime = Comparator.comparing(EntryRaw::getPsNumber)
				.thenComparing(EntryRaw::getSwipeDate)
				.thenComparing(EntryRaw::getSwipeTime);

		Predicate<Map<String, String>> validEvent = entry -> {
			String eventNumber = entry.get(SWP_MAP.get("eventNumber"));
			eventNumber = eventNumber != null ? eventNumber : "201";
			return !(eventNumber.equals("200") || eventNumber.equals("215") || eventNumber.startsWith("---"));
		};

		// parse row to EntryRaw
		List<EntryRaw> entryList = entries.stream()
				.filter(Objects::nonNull)
				.filter(validEvent)
				.filter(entry -> entry.get(SWP_MAP.get("swipeDoor"))
						.contains("Apple Main Door"))
				.map(EntryRaw::new)
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
			return current.getSwipeDoor().endsWith(CommonConstant.ENTRY) &&
					next.getSwipeDoor().endsWith(CommonConstant.EXIT);
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

		entryPairRepo.saveOrUpdatePair(pairList
				.stream()
				.map(EntryPair::new)
				.filter(pair -> NumberUtils.isCreatable(pair.getPsNumber()))
				.collect(toList()));

		LocalDate minDate = pairList.stream()
				.map(EntryPairBean::getSwipeDate)
				.findFirst()
				.orElse(null);

		LocalDate maxDate = pairList.stream()
				.map(EntryPairBean::getSwipeDate)
				.reduce((current, next) -> next)
				.orElse(null);

		return saveOrUpdateEntryDate(minDate, maxDate);
	}

	public Integer saveOrUpdateEntryDate(LocalDate minDate, LocalDate maxDate) {

		List<EntryPair> entityList = entryPairRepo.findBetween(minDate, maxDate);

		List<EntryPairBean> pairList = entityList
				.stream()
				.map(EntryPairBean::new)
				.collect(toList());

		List<EntryDateBean> entryDateList = new ArrayList<>();

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

								entryDateList
										.add(new EntryDateBean(psNumber, date, door, durationSum, firstIn, lastOut));
							});
				});

		return entryDateRepo.saveOrUpdateDate(entryDateList
				.stream()
				.map(EntryDate::new)
				.collect(toList()));
	}

	public Integer saveOrUpdateHeadCount(List<Map<String, String>> rows) {

		List<EmployeeBean> offshoreList = rows
				.stream()
				.filter(row -> row.get(HC_MAP.get("offshore")).equalsIgnoreCase("Yes"))
				.filter(row -> StringUtils.isNotBlank(row.get(HC_MAP.get("psNumber"))))
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
			Set<LocalDate> datedHeaders, Object[] reportData) {

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

		XSSFSheet sheet = (XSSFSheet) workbook.createSheet();

		Font font = workbook.createFont();
		font.setBold(true);
		CellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		headerStyle.setFont(font);

		return createDatedTable(sheet, headerStyle, cumulativeDateRange, cumulativeHeaders,
				datedHeaders, reportData);

	}

	private Workbook createDatedTable(XSSFSheet sheet, CellStyle headerStyle, String cumulativeDateRange,
			StringJoiner cumulativeHeaders, Set<LocalDate> datedHeaders, Object[] reportData) {

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
		XSSFRow headerRow = sheet.createRow(0); // first row as column names
		IntStream.range(0, colLength).forEach(colIndex -> {
			XSSFCell cell = headerRow.createCell(colIndex);
			cell.setCellValue(String.valueOf(dateHeader[colIndex]));
		});

		Cell cumulativeTitleCell = sheet.getRow(0).getCell(4);
		cumulativeTitleCell.setCellValue(cumulativeDateRange);
		cumulativeTitleCell.setCellStyle(headerStyle);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 4, 8));

		int cellCount = 8;

		List<Cell> dateHederStreamList = CommonUtil.toStream(sheet.getRow(0).cellIterator())
				.collect(Collectors.toList());

		for (Cell cell : dateHederStreamList) {
			if (cellCount < dateHederStreamList.size()) {
				sheet.addMergedRegion(new CellRangeAddress(
						0,
						0,
						++cellCount,
						++cellCount));
			}
			cell.setCellStyle(headerStyle);
		}

		// set headers
		XSSFRow headerRow2 = sheet.createRow(1); // first row as column names
		IntStream.range(0, colLength).forEach(colIndex -> {
			XSSFCell cell = headerRow2.createCell(colIndex);
			cell.setCellValue(String.valueOf(reportHeaders2[colIndex]));
			cell.setCellStyle(headerStyle);
		});

		// set row, column values
		IntStream.range(0, rowLength).forEach(rowIndex -> {
			String[] colArr = reportData[rowIndex].toString().split(",");
			XSSFRow row = sheet.createRow(rowIndex + 2); // +1 as first row for headers

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
				sheet.autoSizeColumn(colIndex);
			});
		});
		return sheet.getWorkbook();
	}

}
