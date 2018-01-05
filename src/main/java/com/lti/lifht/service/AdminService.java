package com.lti.lifht.service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.lti.lifht.entity.Employee;
import com.lti.lifht.model.EmployeeBean;
import com.lti.lifht.model.EntryDateBean;
import com.lti.lifht.model.EntryPairBean;
import com.lti.lifht.model.EntryRange;
import com.lti.lifht.model.request.DateMultiPs;
import com.lti.lifht.model.request.DateSinglePs;
import com.lti.lifht.model.request.RangeMultiPs;
import com.lti.lifht.model.request.RangeSinglePs;
import com.lti.lifht.repository.EmployeeRepository;
import com.lti.lifht.repository.EntryDateRepository;
import com.lti.lifht.repository.EntryPairRepository;

@Service
public class AdminService {

	@Autowired
	EmployeeRepository employeeRepo;

	@Autowired
	EntryDateRepository entryDateRepo;

	@Autowired
	EntryPairRepository entryPairRepo;

	private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

	public List<EntryPairBean> getDateSinglePs(@RequestBody DateSinglePs request) {
		return entryPairRepo.getDateSinlgePs(request);
	}

	public Optional<Employee> findByPsNumber(String psNumber) {
		return employeeRepo.findByPsNumber(psNumber);
	}

	public Optional<Employee> findByResetToken(String resetToken) {
		return employeeRepo.findByResetToken(resetToken);
	}

	public Employee save(Employee entity) {
		return employeeRepo.saveAndFlush(entity);
	}

	public List<EmployeeBean> getAllEmployees() {
		return employeeRepo
				.findAll()
				.stream()
				.map(EmployeeBean::new)
				.collect(Collectors.toList());
	}

	public List<EntryDateBean> getRangeSingle(RangeSinglePs request) {
		return entryDateRepo.getPsEntryDate(request);
	}

	public Map<LocalDate, List<EntryDateBean>> getDateMulti(LocalDate date, String[] psNumbers) {
		return getDateMulti(new DateMultiPs(date, psNumbers)).stream()
				.collect(Collectors.groupingBy(EntryDateBean::getSwipeDate));
	}

	public List<EntryDateBean> getDateMulti(DateMultiPs request) {
		return entryDateRepo.getPsListEntryDate(request);
	}

	public List<EntryRange> getRangeMulti(RangeMultiPs request, boolean includeDelta) {

		logger.info(request.toString(), ":: delta :: " + includeDelta);

		List<EntryRange> aggregateList = new ArrayList<>();
		List<EntryDateBean> psListForAggregate = entryDateRepo.getPsListForAggregate(request);

		if (includeDelta) {
			psListForAggregate.addAll(entryDateRepo.getPsListForAggregateDelta(request));
		}

		Map<String, LocalDate> psValidSinceMap = entryDateRepo.getValidSince(
				request.getFromDate(),
				request.getToDate());

		psListForAggregate.stream()
				.filter(entry -> NumberUtils.isCreatable(entry.getPsNumber()))
				.collect(Collectors.groupingBy(EntryDateBean::getPsNumber))
				.forEach((psNumber, groupedList) -> {

					String door = groupedList.stream()
							.map(EntryDateBean::getSwipeDoor)
							.findAny()
							.orElse("Invalid");

					EmployeeBean employee = groupedList.stream()
							.map(EntryDateBean::getEmployee)
							.findAny()
							.orElse(null);

					Duration durationSum = groupedList.stream()
							.filter(Objects::nonNull)
							.filter(entry -> null != entry.getSwipeDate())
							.filter(entry -> entry.getSwipeDate().getDayOfWeek() != DayOfWeek.SATURDAY)
							.filter(entry -> entry.getSwipeDate().getDayOfWeek() != DayOfWeek.SUNDAY)
							.map(EntryDateBean::getDuration)
							.reduce(Duration::plus)
							.orElse(Duration.ofMillis(0));

					Duration complianceSum = groupedList.stream()
							.filter(Objects::nonNull)
							.filter(entry -> null != entry.getSwipeDate())
							.filter(entry -> entry.getSwipeDate().getDayOfWeek() != DayOfWeek.SATURDAY)
							.filter(entry -> entry.getSwipeDate().getDayOfWeek() != DayOfWeek.SUNDAY)
							.map(EntryDateBean::getCompliance)
							.reduce(Duration::plus)
							.orElse(Duration.ofMillis(0));

					Duration filoSum = groupedList.stream()
							.filter(Objects::nonNull)
							.filter(entry -> null != entry.getSwipeDate())
							.filter(entry -> entry.getSwipeDate().getDayOfWeek() != DayOfWeek.SATURDAY)
							.filter(entry -> entry.getSwipeDate().getDayOfWeek() != DayOfWeek.SUNDAY)
							.map(EntryDateBean::getFilo)
							.reduce(Duration::plus)
							.orElse(Duration.ofMillis(0));

					Long daysPresent = groupedList.stream()
							.filter(Objects::nonNull)
							.filter(entry -> null != entry.getSwipeDate())
							.filter(entry -> entry.getSwipeDate().getDayOfWeek() != DayOfWeek.SATURDAY)
							.filter(entry -> entry.getSwipeDate().getDayOfWeek() != DayOfWeek.SUNDAY)
							.count();

					aggregateList.add(new EntryRange(
							request.getFromDate(),
							request.getToDate(),
							psValidSinceMap.get(psNumber),
							daysPresent.intValue(),
							durationSum,
							complianceSum,
							filoSum,
							door,
							psNumber,
							employee));
				});

		return aggregateList.stream()
				.sorted(Comparator.comparing(
						entry -> entry.getEmployee().getPsName(),
						Comparator.nullsFirst(Comparator.naturalOrder())))
				.collect(Collectors.toList());
	}

}
