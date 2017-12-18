package com.lti.lifht.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lti.lifht.model.RangeSinglePs;
import com.lti.lifht.model.EmployeeBean;
import com.lti.lifht.model.EntryDateBean;
import com.lti.lifht.model.EntryPairBean;

public class EmployeeDao extends BaseDao {

	private static final Logger logger = LoggerFactory.getLogger(EmployeeDao.class);

	private static EmployeeDao self;
	private static final StringBuilder sql = new StringBuilder();

	private EmployeeDao() {
		logger.info(EmployeeDao.class + "initialized");
	}

	public static EmployeeDao getInstance() {
		if (null != self) {
			return self;
		}
		self = new EmployeeDao();
		return self;
	}

	public List<EntryPairBean> getForDate(RangeSinglePs request) {

		List<EntryPairBean> entryPairList = null;
		sql.setLength(0);

		sql.append("SELECT p.swipe_date as date, p.swipe_in as swipein, p.swipe_out as swipeout,")
				.append(" p.swipe_door as door,  p.duration as duration, p.ps_number as number,")
				.append(" e.ps_name as name, e.business_unit as bu, e.email as email FROM entry_pair p, employee e")
				.append(" WHERE p.ps_number = e.ps_number AND p.ps_number = ? AND p.swipe_date = ?");

		PreparedStatement pst = prepareStatement(sql);
		try {
			pst.setString(1, request.getPsNumber());
			pst.setObject(2, request.getFromDate());

			logger.info(pst.toString());
			ResultSet rs = pst.executeQuery();

			entryPairList = new ArrayList<>();
			while (rs.next()) {
				entryPairList.add(new EntryPairBean(
						rs.getDate("date"),
						rs.getTime("swipein"),
						rs.getTime("swipeout"),
						rs.getString("duration"),
						rs.getString("door"),
						rs.getString("number"),
						new EmployeeBean(
								rs.getString("number"),
								rs.getString("name"),
								rs.getString("bu"),
								rs.getString("email"))));
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}

		return entryPairList;
	}

	public List<EntryDateBean> getForRange(RangeSinglePs request) {

		List<EntryDateBean> dateList = null;
		sql.setLength(0);

		sql.append("SELECT d.swipe_date as date, d.duration as duration, d.compliance as compliance,")
				.append(" d.filo as filo, d.swipe_door as door, d.ps_number as number,")
				.append(" e.ps_name as name, e.business_unit as bu, e.email as email FROM entry_date d, employee e")
				.append(" WHERE d.ps_number = e.ps_number AND d.ps_number = ? AND d.swipe_date BETWEEN ? AND ?");

		PreparedStatement pst = prepareStatement(sql);

		try {
			pst.setString(1, request.getPsNumber());
			pst.setObject(2, request.getFromDate());
			pst.setObject(3, request.getToDate());

			logger.info(pst.toString());

			ResultSet rs = pst.executeQuery();

			dateList = new ArrayList<>();

			while (rs.next()) {
				dateList.add(new EntryDateBean(
						rs.getDate("date").toLocalDate(),
						rs.getString("duration"),
						rs.getString("compliance"),
						rs.getString("filo"),
						rs.getString("door"),
						rs.getString("number"),
						new EmployeeBean(
								rs.getString("number"),
								rs.getString("name"),
								rs.getString("bu"),
								rs.getString("email"))));
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}

		return dateList;
	}

}
