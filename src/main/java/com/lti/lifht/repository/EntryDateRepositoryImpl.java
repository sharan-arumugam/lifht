package com.lti.lifht.repository;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger logger = LoggerFactory.getLogger(EntryDateRepositoryImpl.class);

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
	public List<EntryDateBean> getPsListForAggregate(RangeMultiPs request) {

		String psParams = request.getPsNumberList().stream().map(e -> "?").collect(Collectors.joining(","));

		List<String> psNumberList = request.getPsNumberList();
		int psCount = psNumberList.size();

		StringBuilder sql = new StringBuilder();

		sql.append("SELECT e.ps_number as number, e.ps_name as name,")
				.append(" e.business_unit as bu, e.lti_mail as email, e.ds_id as dsid,")
				.append(" d.swipe_date as date, d.duration as duration, d.filo as filo,")
				.append(" d.compliance as compliance, d.swipe_door as door")
				.append(" FROM entry_date d, employee e WHERE e.ps_number = d.ps_number")
				.append(" AND d.ps_number in (" + psParams + ") AND d.swipe_date BETWEEN ? AND ?");

		Query select = entityManager.createNativeQuery(sql.toString());

		for (int i = 0; i < psCount; i++) {
			select.setParameter(i + 1, psNumberList.get(i));
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
	public List<EntryDateBean> getPsListEntryDate(DateMultiPs request) {

		String psParams = request.getPsNumberList().stream().map(e -> "?").collect(Collectors.joining(","));

		List<String> psNumberList = request.getPsNumberList();
		int psCount = psNumberList.size();

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT e.ps_number as number, e.ps_name as name,")
				.append(" e.business_unit as bu, e.lti_mail as email, e.ds_id as dsid,")
				.append(" d.swipe_date as date, d.duration as duration, d.filo as filo,")
				.append(" d.compliance as compliance, d.swipe_door as door")
				.append(" FROM entry_date d, employee e WHERE e.ps_number = d.ps_number")
				.append(" AND d.ps_number in (" + psParams + ") AND d.swipe_date = ?");

		Query select = entityManager.createNativeQuery(sql.toString());

		for (int i = 0; i < psCount; i++) {
			select.setParameter(i + 1, psNumberList.get(i));
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
				.append(" FROM entry_date d, employee e WHERE e.ps_number = d.ps_number")
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

}