package com.lti.lifht.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lti.lifht.repository.EmployeeDao;
import com.lti.lifht.model.RangeSinglePs;
import com.lti.lifht.model.EntryDateBean;
import com.lti.lifht.model.EntryPairBean;

public class EmployeeService {

	private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

	private static EmployeeService self;
	private static EmployeeDao dao = EmployeeDao.getInstance();

	private EmployeeService() {
	}

	public static EmployeeService getInstance() {
		if (null != self) {
			return self;
		}
		self = new EmployeeService();
		return self;
	}

	public List<EntryPairBean> getForDate(RangeSinglePs request) {
		logger.info(request.toString());
		return dao.getForDate(request);
	}

	public List<EntryDateBean> getForRange(RangeSinglePs request) {
		logger.info(request.toString());
		return dao.getForRange(request);
	}
}
