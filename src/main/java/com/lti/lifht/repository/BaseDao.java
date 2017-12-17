package com.lti.lifht.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseDao {
	private static final Logger logger = LoggerFactory.getLogger(BaseDao.class);

	public static PreparedStatement prepareStatement(StringBuilder sqlBuilder) {
		return prepareStatement(sqlBuilder.toString());
	}

	public static PreparedStatement prepareStatement(String sql) {
		return null;
	}
}