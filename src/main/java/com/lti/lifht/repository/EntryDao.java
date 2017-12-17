package com.lti.lifht.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lti.lifht.model.DateMultiPs;
import com.lti.lifht.model.EntryRaw;
import com.lti.lifht.model.RangeMultiPs;
import com.lti.lifht.model.RangeSinglePs;
import com.lti.lifht.model.EmployeeBean;
import com.lti.lifht.model.EntryDate;
import com.lti.lifht.model.EntryPair;

public class EntryDao extends BaseDao {

	private static final Logger logger = LoggerFactory.getLogger(EntryDao.class);
	private static final StringBuilder sql = new StringBuilder();

	public void saveOrUpdateRawEntry(List<EntryRaw> rawList) {

		rawList.forEach(entry -> {

			sql.setLength(0);
			sql.append("INSERT INTO entry_raw (swipe_date, swipe_time, swipe_door, ps_number)")
					.append(" VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE ps_number = VALUES (ps_number)");

			try {
				PreparedStatement insert = prepareStatement(sql);
				insert.setObject(1, entry.getSwipeDate());
				insert.setObject(2, entry.getSwipeTime());
				insert.setString(3, entry.getSwipeDoor());
				insert.setObject(4, entry.getPsNumber());
				logger.info(insert.toString());
				// insert.addBatch();
				// insert.executeLargeBatch();
				insert.execute();

			} catch (SQLException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		});
	}

	public void saveOrUpdatePair(List<EntryPair> pairList) {

		pairList.forEach(entry -> {

			sql.setLength(0);
			sql.append("INSERT INTO entry_pair (swipe_date, swipe_in, swipe_out, swipe_door, duration, ps_number)")
					.append(" VALUES (?,?,?,?,?,?) ON DUPLICATE KEY UPDATE ps_number = VALUES (ps_number)");

			try {
				PreparedStatement insert = prepareStatement(sql);

				insert.setObject(1, entry.getSwipeDate());
				insert.setObject(2, entry.getSwipeIn());
				insert.setObject(3, entry.getSwipeOut());
				insert.setString(4, entry.getSwipeDoor());
				insert.setString(5, entry.getDuration().toString());
				insert.setString(6, entry.getPsNumber());
				logger.info(insert.toString());
				// insert.addBatch();
				// insert.executeLargeBatch();
				insert.execute();

			} catch (SQLException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		});

	}

	public void saveOrUpdateDate(List<EntryDate> entryDateList) {

		entryDateList.forEach(entry -> {

			sql.setLength(0);
			sql.append(
					"INSERT INTO entry_date (swipe_date, swipe_door, duration, compliance, first_in, last_out, filo, ps_number)")
					.append(" VALUES (?,?,?,?,?,?,?,?)").append(" ON DUPLICATE KEY UPDATE")
					.append(" swipe_door = VALUES (swipe_door),").append(" duration = VALUES (duration),")
					.append(" compliance = VALUES (compliance),").append(" first_in = VALUES (first_in),")
					.append(" last_out = VALUES (last_out),").append(" filo = VALUES (filo),")
					.append(" ps_number = VALUES (ps_number)");

			try {
				PreparedStatement insert = prepareStatement(sql);

				insert.setObject(1, entry.getSwipeDate());
				insert.setString(2, entry.getSwipeDoor());
				insert.setObject(3, entry.getDuration().toString());
				insert.setObject(4, entry.getCompliance().toString());
				insert.setObject(5, entry.getFirstIn());
				insert.setObject(6, entry.getLastOut());
				insert.setString(7, entry.getFilo().toString());
				insert.setString(8, entry.getPsNumber());
				logger.info(insert.toString());
				// insert.addBatch();
				// insert.executeBatch();
				insert.execute();

			} catch (SQLException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		});
	}

	public List<EntryPair> getAllPairs() {

		List<EntryPair> pairList = null;

		sql.setLength(0);
		sql.append("SELECT swipe_date, swipe_in, swipe_out, swipe_door, duration, ps_number FROM entry_pair");

		try {
			ResultSet resultSet = prepareStatement(sql).executeQuery();

			pairList = new ArrayList<>();
			while (resultSet.next()) {
				pairList.add(new EntryPair(resultSet.getDate("swipe_date"), resultSet.getTime("swipe_in"),
						resultSet.getTime("swipe_out"), resultSet.getString("swipe_door"),
						resultSet.getString("duration"), resultSet.getString("ps_number")));
			}

		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		return pairList;
	}

	public List<EntryDate> getAllEntryDate() {

		List<EntryDate> entryDateList = null;

		sql.setLength(0);
		sql.append("SELECT e.ps_name as name, e.business_unit as bu, e.email as email,")
				.append(" d.swipe_date as date, d.duration as duration, d.filo as filo")
				.append(" d.compliance as compliance, d.swipe_door as door, d.ps_number as number")
				.append(" FROM entry_date d, employee e WHERE e.ps_number = d.ps_number");

		try {
			ResultSet resultSet = prepareStatement(sql).executeQuery();

			entryDateList = new ArrayList<>();
			while (resultSet.next()) {
				entryDateList.add(new EntryDate(resultSet.getDate("date").toLocalDate(),
						resultSet.getString("duration"), resultSet.getString("compliance"), resultSet.getString("filo"),
						resultSet.getString("door"), resultSet.getString("number"),
						new EmployeeBean(resultSet.getString("number"), resultSet.getString("name"),
								resultSet.getString("bu"), resultSet.getString("email"))));
			}

		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		return entryDateList;
	}

	public List<EntryDate> getPsEntryDate(RangeSinglePs request) {

		List<EntryDate> entryDateList = null;

		sql.setLength(0);
		sql.append("SELECT e.ps_name as name, e.business_unit as bu, e.email as email,")
				.append(" d.swipe_date as date, d.duration as duration, d.compliance as compliance,")
				.append(" d.first_in as firstin, d.last_out as lastout, d.filo as filo,")
				.append(" d.swipe_door as door, d.ps_number as number")
				.append(" FROM entry_date d, employee e WHERE e.ps_number = d.ps_number")
				.append(" AND d.ps_number = ? AND d.swipe_date BETWEEN ? AND ?");

		try {
			PreparedStatement pst = prepareStatement(sql);

			pst.setString(1, request.getPsNumber());
			pst.setObject(2, request.getFromDate());
			pst.setObject(3, request.getToDate());
			// logger.info(pst.toString());
			ResultSet rs = pst.executeQuery();

			entryDateList = new ArrayList<>();
			while (rs.next()) {
				entryDateList.add(new EntryDate(rs.getDate("date"), rs.getString("duration"),
						rs.getString("compliance"), rs.getString("filo"), rs.getString("door"), rs.getString("number"),
						rs.getTime("firstin").toLocalTime(), rs.getTime("lastout").toLocalTime(),
						new EmployeeBean(rs.getString("number"), rs.getString("name"), rs.getString("bu"),
								rs.getString("email"))));
			}

		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		return entryDateList;
	}

	public List<EntryDate> getPsListEntryDate(DateMultiPs request) {

		List<EntryDate> entryDateList = null;

		String psParams = request.getPsNumberList().stream().map(e -> "?").collect(Collectors.joining(","));

		List<String> psNumberList = request.getPsNumberList();
		int psCount = psNumberList.size();

		sql.setLength(0);
		sql.append("SELECT e.ps_name as name, e.business_unit as bu, e.email as email,")
				.append(" d.swipe_date as date, d.duration as duration, d.filo as filo,")
				.append(" d.compliance as compliance, d.swipe_door as door, d.ps_number as number")
				.append(" FROM entry_date d, employee e WHERE e.ps_number = d.ps_number")
				.append(" AND d.ps_number in (" + psParams + ") AND d.swipe_date = ?");

		try {
			PreparedStatement pst = prepareStatement(sql);

			for (int i = 0; i < psCount; i++) {
				pst.setString(i + 1, psNumberList.get(i));
			}

			pst.setObject(psCount + 1, request.getDate());
			// logger.info(pst.toString());
			ResultSet rs = pst.executeQuery();

			entryDateList = new ArrayList<>();
			while (rs.next()) {
				entryDateList.add(new EntryDate(rs.getDate("date").toLocalDate(), rs.getString("duration"),
						rs.getString("compliance"), rs.getString("filo"), rs.getString("door"), rs.getString("number"),
						new EmployeeBean(rs.getString("number"), rs.getString("name"), rs.getString("bu"),
								rs.getString("email"))));
			}

		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		return entryDateList;
	}

	public List<EntryDate> getPsListForAggregate(RangeMultiPs request) {

		List<EntryDate> forAggregateList = null;

		String psParams = request.getPsNumberList().stream().map(e -> "?").collect(Collectors.joining(","));

		List<String> psNumberList = request.getPsNumberList();
		// logger.info(psNumberList.toString());
		int psCount = psNumberList.size();

		sql.setLength(0);
		sql.append("SELECT e.ps_name as name, e.business_unit as bu, e.email as email,")
				.append(" d.swipe_date as date, d.duration as duration, d.filo as filo,")
				.append(" d.compliance as compliance, d.swipe_door as door, d.ps_number as number")
				.append(" FROM entry_date d, employee e WHERE e.ps_number = d.ps_number")
				.append(" AND d.ps_number in (" + psParams + ") AND d.swipe_date BETWEEN ? AND ?");

		try {
			PreparedStatement pst = prepareStatement(sql);

			for (int i = 0; i < psCount; i++) {
				pst.setString(i + 1, psNumberList.get(i));
			}

			pst.setObject(psCount + 1, request.getFromDate());
			pst.setObject(psCount + 2, request.getToDate());
			// logger.info(pst.toString());

			ResultSet rs = pst.executeQuery();

			forAggregateList = new ArrayList<>();
			while (rs.next()) {
				forAggregateList.add(new EntryDate(rs.getDate("date").toLocalDate(), rs.getString("duration"),
						rs.getString("compliance"), rs.getString("filo"), rs.getString("door"), rs.getString("number"),
						new EmployeeBean(rs.getString("number"), rs.getString("name"), rs.getString("bu"),
								rs.getString("email"))));
			}

		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		return forAggregateList;
	}
}
