package com.lti.lifht.service;

import static com.lti.lifht.constant.ExcelConstant.ALC_MAP;
import static com.lti.lifht.constant.ExcelConstant.HC_MAP;
import static com.lti.lifht.constant.ExcelConstant.SWP_MAP;
import static com.lti.lifht.util.CommonUtil.getNext;
import static com.lti.lifht.util.CommonUtil.getPrev;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lti.lifht.constant.CommonConstant;
import com.lti.lifht.repository.AdminDao;
import com.lti.lifht.repository.EntryDao;
import com.lti.lifht.model.EntryRaw;
import com.lti.lifht.model.Employee;
import com.lti.lifht.model.EntryPair;
import com.lti.lifht.model.EntryRange;
import com.lti.lifht.util.ExcelUtil;

import one.util.streamex.StreamEx;

public class IOService {

	private static final Logger logger = LoggerFactory.getLogger(IOService.class);
	private static final EntryDao entryDao = new EntryDao();
	private static final AdminDao adminDao = new AdminDao();
	private static final AdminService adminService = new AdminService();

	public void saveOrUpdateRawEntry(List<Map<String, String>> entries) {

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
						.startsWith("BLR  LTI  Embassy Apple Main Door Door"))
				.map(EntryRaw::new)
				.sorted(byPsNumDateTime)
				.collect(Collectors.toList());

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
				EntryRaw previous = getPrev.apply(entryList, index);
				return validRow.test(current, previous)
						? !sameDoor.test(current, previous)
						: true;

			} else if (index == 0 && index + 1 < entrySize) { // validate first pair
				EntryRaw next = getNext.apply(entryList, index);

				return validRow.test(current, next)
						? doorPair.test(current, next)
						: false;
			}
			return true;
		};

		Consumer<Integer> addToFilteredList = index -> filteredList.add(entryList.get(index));

		// parse EntryRaw to EntryPair
		BiFunction<EntryRaw, EntryRaw, EntryPair> toEntryPair = (current, next) -> {
			if (timeNotNull.test(current, next)
					&& validRow.test(current, next)
					&& doorPair.test(current, next)) {

				EntryPair pair = new EntryPair(current);
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
		List<EntryPair> pairList = StreamEx.of(filteredList)
				.sorted(byPsNumDateTime)
				.nonNull()
				.pairMap(toEntryPair)
				.nonNull()
				.collect(Collectors.toList());

		entryDao.saveOrUpdatePair(pairList);
		adminService.saveOrUpdateEntryDate();
	}

	public void saveOrUpdateHeadCount(List<Map<String, String>> rows) {

		rows = rows.stream()
				// .filter(row -> row.get(HC_MAP.get("offshore")).equalsIgnoreCase("Yes"))
				.collect(Collectors.toList());

		List<Employee> employeeList = rows
				.stream()
				.map(row -> new Employee(row, HC_MAP))
				.collect(Collectors.toList());

		adminDao.saveOrUpdateHeadCount(employeeList);
	}

	public void saveOrUpdateProjectAllocation(List<Map<String, String>> rows) {

		List<String> psNumberList = adminService.getAllEmployees()
				.stream()
				.map(Employee::getPsNumber)
				.collect(Collectors.toList());

		rows = rows
				.stream()
				.filter(row -> psNumberList.contains(row.get(ALC_MAP.get("psNumber"))))
				.collect(Collectors.toList());

		List<Employee> employeeList = rows
				.stream()
				.map(row -> new Employee(row, ALC_MAP))
				.collect(Collectors.toList());

		adminDao.saveOrUpdateProjectAllocation(employeeList);
	}

	public Object generateRangeMultiReport(List<EntryRange> entries, String[] reportHeaders) {
		try {
			Object file = ExcelUtil.toByteArray(createTable(entries.toArray(), reportHeaders));
//			return Response.ok(file)
//					.header("Content-Disposition",
//							"attachment; filename=report-" + LocalDate.now().toString() + ".xlsx")
//					.build();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return null;//Response.serverError().build();
	}

	public Workbook createTable(Object[] rowArr, String[] reportHeaders) throws FileNotFoundException, IOException {
		Workbook wb = new XSSFWorkbook();
		XSSFSheet sheet = (XSSFSheet) wb.createSheet();

		int rowLength = rowArr.length;

		// set headers
		int colLength = reportHeaders.length;
		XSSFRow headerRow = sheet.createRow(0); // first row as column names

		IntStream.range(0, colLength).forEach(colIndex -> {
			XSSFCell cell = headerRow.createCell(colIndex);
			cell.setCellValue(reportHeaders[colIndex]);
		});

		// set row, column values
		IntStream.range(0, rowLength).forEach(rowIndex -> {
			String[] colArr = rowArr[rowIndex].toString().split(",");
			XSSFRow row = sheet.createRow(rowIndex + 1); // +1 as first row for headers

			IntStream.range(0, colLength).forEach(colIndex -> {
				XSSFCell cell = row.createCell(colIndex);
				cell.setCellValue(colArr[colIndex]);
				sheet.autoSizeColumn(colIndex);
			});
		});

		return wb;
	}

}
