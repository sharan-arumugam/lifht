package com.lti.lifht.service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lti.lifht.repository.AdminDao;
import com.lti.lifht.repository.EntryDao;
import com.lti.lifht.model.DateMultiPs;
import com.lti.lifht.model.RangeMultiPs;
import com.lti.lifht.model.RangeSinglePs;
import com.lti.lifht.model.Employee;
import com.lti.lifht.model.EntryDate;
import com.lti.lifht.model.EntryPair;
import com.lti.lifht.model.EntryRange;

public class AdminService {

	private static final Logger logger = LoggerFactory.getLogger(AdminService.class);
	private static final AdminDao dao = new AdminDao();
	private static final EntryDao entryDao = new EntryDao();

	public List<Employee> getAllEmployees() {
		return dao.getAllEmployees();
	}

	public List<EntryDate> getAllEntryDate() {
		return entryDao.getAllEntryDate();
	}

	public List<EntryDate> getRangeSingle(RangeSinglePs request) {
		return entryDao.getPsEntryDate(request);
	}

	public List<EntryDate> getDateMulti(DateMultiPs request) {
		return entryDao.getPsListEntryDate(request);
	}

	public List<EntryRange> getRangeMulti(RangeMultiPs request) {

		logger.info(request.toString());

		List<EntryRange> aggregateList = new ArrayList<>();
		List<EntryDate> psListForAggregate = entryDao.getPsListForAggregate(request);

		psListForAggregate.stream()
				.collect(Collectors.groupingBy(EntryDate::getPsNumber))
				.forEach((psNumber, groupedList) -> {

					String door = groupedList.stream()
							.map(EntryDate::getSwipeDoor)
							.findAny()
							.orElse("Invalid");

					Employee employee = groupedList.stream()
							.map(EntryDate::getEmployee)
							.findAny()
							.orElse(null);

					Duration durationSum = groupedList.stream()
							.filter(entry -> entry.getSwipeDate().getDayOfWeek() != DayOfWeek.SATURDAY)
							.filter(entry -> entry.getSwipeDate().getDayOfWeek() != DayOfWeek.SUNDAY)
							.map(EntryDate::getDuration)
							.reduce(Duration::plus)
							.orElse(Duration.ofMillis(0));

					Duration complianceSum = groupedList.stream()
							.filter(entry -> entry.getSwipeDate().getDayOfWeek() != DayOfWeek.SATURDAY)
							.filter(entry -> entry.getSwipeDate().getDayOfWeek() != DayOfWeek.SUNDAY)
							.map(EntryDate::getCompliance)
							.reduce(Duration::plus)
							.orElse(Duration.ofMillis(0));

					Duration filoSum = groupedList.stream()
							.filter(entry -> entry.getSwipeDate().getDayOfWeek() != DayOfWeek.SATURDAY)
							.filter(entry -> entry.getSwipeDate().getDayOfWeek() != DayOfWeek.SUNDAY)
							.map(EntryDate::getFilo)
							.reduce(Duration::plus)
							.orElse(Duration.ofMillis(0));

					aggregateList.add(new EntryRange(
							request.getFromDate(),
							request.getToDate(),
							durationSum,
							complianceSum,
							filoSum,
							door,
							psNumber,
							employee));
				});

		return aggregateList.stream()
				.sorted(Comparator.comparing(entry -> entry.getEmployee().getPsName()))
				.collect(Collectors.toList());
	}

	public void saveOrUpdateEntryDate() {
		List<EntryPair> pairList = getAllPairs();
		List<EntryDate> entryDateList = new ArrayList<>();

		pairList.stream()
				.collect(Collectors.groupingBy(EntryPair::getSwipeDate))
				.forEach((date, psList) -> {

					psList.stream()
							.collect(Collectors.groupingBy(EntryPair::getPsNumber))
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
										.map(EntryPair::getSwipeDoor)
										.findAny()
										.orElse("Invalid");

								Duration durationSum = groupedList.stream()
										.map(EntryPair::getDuration)
										.reduce(Duration::plus)
										.orElse(Duration.ofMillis(0));

								entryDateList.add(new EntryDate(psNumber, date, door, durationSum, firstIn, lastOut));
							});
				});
		entryDao.saveOrUpdateDate(entryDateList);
	}

	public List<EntryPair> getAllPairs() {
		return entryDao.getAllPairs();
	}
}
