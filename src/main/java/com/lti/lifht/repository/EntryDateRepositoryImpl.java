package com.lti.lifht.repository;

import static java.util.stream.IntStream.rangeClosed;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.lti.lifht.entity.EntryDate;
import com.lti.lifht.model.EmployeeBean;
import com.lti.lifht.model.EntryDateBean;
import com.lti.lifht.model.request.DateMultiPs;
import com.lti.lifht.model.request.RangeMultiPs;
import com.lti.lifht.model.request.RangeSinglePs;

@Repository
@Transactional
@SuppressWarnings("unchecked")
public class EntryDateRepositoryImpl implements EntryDateRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Integer saveOrUpdateDate(List<EntryDate> entryDateList) {

        List<Integer> updateCountList = new ArrayList<>();

        entryDateList.forEach(entry -> {

            StringBuilder sql = new StringBuilder();
            String params = IntStream.rangeClosed(1, 8).boxed().map(e -> "?").collect(Collectors.joining(","));
            sql.append(
                    "INSERT INTO entry_date (swipe_date, swipe_door, duration, compliance, first_in, last_out, filo, ps_number)")
                    .append(" VALUES (" + params + ")")
                    .append(" ON DUPLICATE KEY UPDATE ps_number = VALUES (ps_number)");

            Query insert = entityManager.createNativeQuery(sql.toString());

            insert.setParameter(1, entry.getSwipeDate());
            insert.setParameter(2, entry.getSwipeDoor());
            insert.setParameter(3, entry.getDuration());
            insert.setParameter(4, entry.getCompliance());
            insert.setParameter(5, entry.getFirstIn());
            insert.setParameter(6, entry.getLastOut());
            insert.setParameter(7, entry.getFilo());
            insert.setParameter(8, entry.getPsNumber());
            updateCountList.add(insert.executeUpdate());
        });

        return updateCountList.stream()
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();
    }

    @Override
    public List<EntryDateBean> getPsListForAggregate(RangeMultiPs request, boolean isReport) {

        List<String> psNumberList = request.getPsNumberList();
        String psParams = psNumberList.stream().map(e -> "?")
                .collect(Collectors.joining(","));

        int psCount = isReport ? 0 : psNumberList.size();

        String paramConstraint = isReport ? "" : " AND d.ps_number in (" + psParams + ")";

        StringBuilder sql = new StringBuilder();

        sql.append("SELECT e.ps_number, e.ps_name, e.business_unit, e.lti_mail, e.ds_id,")
                .append(" d.swipe_date, d.duration, d.filo, d.compliance, d.swipe_door, e.active")
                .append(" FROM entry_date d right outer join employee e on e.ps_number = d.ps_number where e.active <> 'N'")
                .append(paramConstraint + " AND d.swipe_date BETWEEN ? AND ?");

        Query select = entityManager.createNativeQuery(sql.toString());

        if (!isReport) {
            rangeClosed(1, psCount).forEachOrdered(index -> {
                select.setParameter(index, psNumberList.get(index - 1));
            });
        }

        select.setParameter(psCount + 1, request.getFromDate());
        select.setParameter(psCount + 2, request.getToDate());

        List<Object[]> resultList = select.getResultList();

        List<EntryDateBean> forAggregateList = new ArrayList<>();

        resultList.forEach(rs -> {
            forAggregateList.add(new EntryDateBean(rs));
        });

        return forAggregateList;
    }

    @Override
    public List<EntryDateBean> getPsListEntryDate(DateMultiPs request, boolean isReport) {

        String psParams = request.getPsNumberList().stream().map(e -> "?").collect(Collectors.joining(","));

        List<String> psNumberList = request.getPsNumberList();
        int psCount = isReport ? 0 : psNumberList.size();

        String paramConstraint = isReport ? "" : " AND d.ps_number in (" + psParams + ")";

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT e.ps_number, e.ps_name, e.business_unit, e.lti_mail, e.ds_id,")
                .append(" d.swipe_date, d.duration, d.filo, d.compliance, d.swipe_door, d.first_in, d.last_out")
                .append(" FROM entry_date d right outer join employee e on e.ps_number = d.ps_number and e.active = 'Y'")
                .append(paramConstraint + " AND d.swipe_date = ? ORDER BY e.ps_name");

        Query select = entityManager.createNativeQuery(sql.toString());

        if (!isReport) {
            rangeClosed(1, psCount).forEachOrdered(index -> {
                select.setParameter(index, psNumberList.get(index - 1));
            });
        }

        select.setParameter(psCount + 1, request.getDate());

        List<Object[]> resultList = select.getResultList();
        List<EntryDateBean> entryDateList = new ArrayList<>();

        resultList.forEach(rs -> {
            entryDateList.add(new EntryDateBean(rs, rs[10], rs[11]));
        });

        return entryDateList;
    }

    @Override
    public List<EntryDateBean> getPsEntryDate(RangeSinglePs request) {

        StringBuilder sql = new StringBuilder();

        sql.append("SELECT e.ps_number, e.ps_name,")
                .append(" e.business_unit, e.lti_mail, e.ds_id,")
                .append(" d.swipe_date, d.duration, d.filo,")
                .append(" d.compliance, d.swipe_door,")
                .append(" d.first_in, d.last_out")
                .append(" FROM entry_date d, employee e WHERE e.ps_number = d.ps_number and e.active = 'Y'")
                .append(" AND d.ps_number = ? AND d.swipe_date BETWEEN ? AND ?");

        Query select = entityManager.createNativeQuery(sql.toString());

        select.setParameter(1, request.getPsNumber());
        select.setParameter(2, request.getFromDate());
        select.setParameter(3, request.getToDate());

        List<Object[]> resultList = select.getResultList();

        List<EntryDateBean> entryDateList = new ArrayList<>();
        resultList.forEach(rs -> {
            entryDateList.add(new EntryDateBean(rs, rs[10], rs[11]));
        });

        return entryDateList;
    }

    @Override
    public List<EntryDateBean> getPsListForAggregateDelta(RangeMultiPs request) {

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ps_number, swipe_date, duration, filo, compliance, swipe_door")
                .append(" FROM entry_date where ps_number NOT IN (SELECT ps_number from employee)")
                .append(" AND swipe_date BETWEEN ? AND ?");

        Query select = entityManager.createNativeQuery(sql.toString());

        select.setParameter(1, request.getFromDate());
        select.setParameter(2, request.getToDate());

        List<Object[]> resultList = select.getResultList();

        List<EntryDateBean> entryDateList = new ArrayList<>();
        resultList.forEach(rs -> {
            entryDateList.add(new EntryDateBean(
                    String.valueOf(rs[0]),
                    LocalDate.parse(String.valueOf(rs[1])),
                    Duration.ofMillis(Long.valueOf(String.valueOf(rs[2]))),
                    Duration.ofMillis(Long.valueOf(String.valueOf(rs[3]))),
                    Duration.ofMillis(Long.valueOf(String.valueOf(rs[4]))),
                    String.valueOf(rs[5]),
                    new EmployeeBean()));
        });

        return entryDateList;
    }

    @Override
    public Map<String, LocalDate> getValidSince(LocalDate fromDate, LocalDate toDate) {

        Map<String, LocalDate> psValidSinceMap = new HashMap<>();

        StringBuilder sql = new StringBuilder();

        sql.append("SELECT ps_number, MIN(swipe_date) FROM entry_date")
                .append(" WHERE swipe_date BETWEEN ? AND ?")
                .append(" GROUP BY ps_number");

        Query select = entityManager.createNativeQuery(sql.toString());

        select.setParameter(1, fromDate);
        select.setParameter(2, toDate);

        List<Object[]> resultList = select.getResultList();

        resultList.forEach(rs -> {
            psValidSinceMap.put(String.valueOf(rs[0]),
                    LocalDate.parse(String.valueOf(rs[1])));
        });

        return psValidSinceMap;
    }

    @Override
    public List<EntryDateBean> getPsListEntryDateDelta(DateMultiPs request) {

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ps_number, swipe_date, duration, filo, compliance, swipe_door")
                .append(" FROM entry_date where ps_number NOT IN (SELECT ps_number from employee)")
                .append(" AND swipe_date = ?");

        Query select = entityManager.createNativeQuery(sql.toString());

        select.setParameter(1, request.getDate());

        List<Object[]> resultList = select.getResultList();

        List<EntryDateBean> entryDateList = new ArrayList<>();
        resultList.forEach(rs -> {
            entryDateList.add(new EntryDateBean(
                    String.valueOf(rs[0]),
                    LocalDate.parse(String.valueOf(rs[1])),
                    Duration.ofMillis(Long.valueOf(String.valueOf(rs[2]))),
                    Duration.ofMillis(Long.valueOf(String.valueOf(rs[3]))),
                    Duration.ofMillis(Long.valueOf(String.valueOf(rs[4]))),
                    String.valueOf(rs[5]),
                    new EmployeeBean()));
        });

        return entryDateList;
    }

}