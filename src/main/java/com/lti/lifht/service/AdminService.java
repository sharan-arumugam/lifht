package com.lti.lifht.service;

import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsFirst;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.math.NumberUtils.isCreatable;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.lti.lifht.entity.Employee;
import com.lti.lifht.entity.Exclusion;
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
import com.lti.lifht.repository.ExclusionRepository;

@Service
public class AdminService {

    @Autowired
    private EmployeeRepository employeeRepo;

    @Autowired
    private EntryDateRepository entryDateRepo;

    @Autowired
    private EntryPairRepository entryPairRepo;

    @Autowired
    private ExclusionRepository exclusionRepo;

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

    public Map<LocalDate, List<EntryDateBean>> getDateMulti(LocalDate date) {
        return getDateMulti(new DateMultiPs(date, null), true).stream()
                .filter(Objects::nonNull)
                .filter(entryDate -> null != entryDate.getSwipeDate())
                .collect(Collectors.groupingBy(EntryDateBean::getSwipeDate));
    }

    public List<EntryDateBean> getDateMulti(DateMultiPs request, boolean isReport) {
        List<EntryDateBean> entryList = entryDateRepo.getPsListEntryDate(request, isReport);
        if (isReport) {
            entryList.addAll(entryDateRepo.getPsListEntryDateDelta(request));
        }
        return entryList;
    }

    public List<EntryRange> getRangeMulti(RangeMultiPs request, boolean isReport) {

        List<EntryRange> aggregateList = new ArrayList<>();
        List<EntryDateBean> psListForAggregate = entryDateRepo.getPsListForAggregate(request, isReport);

        if (isReport) {
            psListForAggregate.addAll(entryDateRepo.getPsListForAggregateDelta(request));
        }

        Map<String, Exclusion> exclusionMap = exclusionRepo
                .findAll()
                .stream()
                .collect(toMap(Exclusion::getPsNumber, identity()));

        Map<String, LocalDate> psValidSinceMap = entryDateRepo.getValidSince(
                request.getFromDate(),
                request.getToDate());

        psListForAggregate.stream()
                .filter(entry -> isCreatable(entry.getPsNumber()))
                .collect(groupingBy(EntryDateBean::getPsNumber))
                .forEach((psNumber, groupedList) -> {

                    Supplier<Stream<EntryDateBean>> groupedStream = () -> groupedList.stream()
                            .filter(Objects::nonNull)
                            .filter(entry -> null != entry.getSwipeDate())
                            .filter(entry -> exclusionMap.containsKey(psNumber)
                                    ? entry.getSwipeDate().isAfter(exclusionMap.get(psNumber).getStartDate().minusDays(1))
                                    : true)
                            .filter(entry -> entry.getSwipeDate().getDayOfWeek() != SATURDAY)
                            .filter(entry -> entry.getSwipeDate().getDayOfWeek() != SUNDAY);

                    Function<Stream<Duration>, Duration> summer = durationStream -> durationStream
                            .reduce(Duration::plus)
                            .orElse(Duration.ofMillis(0));

                    String door = groupedList.stream()
                            .map(EntryDateBean::getSwipeDoor)
                            .findAny()
                            .orElse("Invalid");

                    EmployeeBean employee = groupedList.stream()
                            .map(EntryDateBean::getEmployee)
                            .findAny()
                            .orElse(null);

                    Duration durationSum = summer.apply(groupedStream.get().map(EntryDateBean::getDuration));
                    Duration complianceSum = summer.apply(groupedStream.get().map(EntryDateBean::getCompliance));
                    Duration filoSum = summer.apply(groupedStream.get().map(EntryDateBean::getFilo));
                    Long daysPresent = groupedStream.get().count();

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
                .sorted(comparing(
                        entry -> entry.getEmployee().getPsName(),
                        nullsFirst(naturalOrder())))
                .collect(toList());
    }

    public EntryRange getRangeSingleSum(RangeSinglePs request) {

        List<EntryRange> aggregateList = new ArrayList<>();
        List<EntryDateBean> rangeSingleList = getRangeSingle(request);

        Map<String, Exclusion> exclusionMap = exclusionRepo
                .findAll()
                .stream()
                .collect(toMap(Exclusion::getPsNumber, identity()));

        Map<String, LocalDate> psValidSinceMap = entryDateRepo.getValidSince(
                request.getFromDate(),
                request.getToDate());

        rangeSingleList.stream()
                .filter(entry -> isCreatable(entry.getPsNumber()))
                .collect(groupingBy(EntryDateBean::getPsNumber))
                .forEach((psNumber, groupedList) -> {

                    Supplier<Stream<EntryDateBean>> groupedStream = () -> groupedList.stream()
                            .filter(Objects::nonNull)
                            .filter(entry -> null != entry.getSwipeDate())
                            .filter(entry -> exclusionMap.containsKey(psNumber)
                                    ? entry.getSwipeDate().isAfter(exclusionMap.get(psNumber).getStartDate().minusDays(1))
                                    : true)
                            .filter(entry -> entry.getSwipeDate().getDayOfWeek() != SATURDAY)
                            .filter(entry -> entry.getSwipeDate().getDayOfWeek() != SUNDAY);

                    Function<Stream<Duration>, Duration> summer = durationStream -> durationStream
                            .reduce(Duration::plus)
                            .orElse(Duration.ofMillis(0));

                    String door = groupedList.stream()
                            .map(EntryDateBean::getSwipeDoor)
                            .findAny()
                            .orElse("Invalid");

                    EmployeeBean employee = groupedList.stream()
                            .map(EntryDateBean::getEmployee)
                            .findAny()
                            .orElse(null);

                    Duration durationSum = summer.apply(groupedStream.get().map(EntryDateBean::getDuration));
                    Duration complianceSum = summer.apply(groupedStream.get().map(EntryDateBean::getCompliance));
                    Duration filoSum = summer.apply(groupedStream.get().map(EntryDateBean::getFilo));
                    Long daysPresent = groupedStream.get().count();

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

        return aggregateList.size() > 0 ? aggregateList.get(0) : null;
    }

}
