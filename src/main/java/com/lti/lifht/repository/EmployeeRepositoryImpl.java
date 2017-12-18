package com.lti.lifht.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.lti.lifht.entity.Employee;

@Repository
@Transactional
public class EmployeeRepositoryImpl implements EmployeeRepositoryCustom {

	private static final Logger logger = LoggerFactory.getLogger(EmployeeRepositoryImpl.class);

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public List<Employee> getFirstNamesLike(String name) {
		Query query = entityManager.createNativeQuery("SELECT em.* FROM lifht.employee as em " +
				"WHERE em.ps_name LIKE ?", Employee.class);
		query.setParameter(1, name + "%");
		logger.info(query.getResultList().toString());
		return query.getResultList();
	}
}