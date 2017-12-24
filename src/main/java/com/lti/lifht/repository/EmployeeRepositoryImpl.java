package com.lti.lifht.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.lti.lifht.model.EmployeeBean;

@Repository
@Transactional
public class EmployeeRepositoryImpl implements EmployeeRepositoryCustom {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeRepositoryImpl.class);

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public void saveOrUpdateHeadCount(List<EmployeeBean> employeeList) {

        employeeList.forEach(employee -> {
            try {

                StringBuilder sql = new StringBuilder();

                sql.append("INSERT INTO employee(ps_number, ps_name, apple_manager, lti_mail)")
                        .append(" VALUES (?,?,?,?)")
                        .append(" ON DUPLICATE KEY UPDATE")
                        .append(" ps_name = VALUES (ps_name),")
                        .append(" apple_manager = VALUES (apple_manager),")
                        .append(" lti_mail = VALUES (lti_mail)");

                Query insert = entityManager.createNativeQuery(sql.toString());

                insert.setParameter(1, employee.getPsNumber());
                insert.setParameter(2, employee.getPsName());
                insert.setParameter(3, employee.getManager());
                insert.setParameter(4, employee.getEmail());
                insert.executeUpdate();

            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        });
    }

    @Override
    public void saveOrUpdateProjectAllocation(List<EmployeeBean> employeeList) {

        employeeList.forEach(employee -> {
            try {

                StringBuilder sql = new StringBuilder();

                sql.append("INSERT INTO employee(ps_number, business_unit)")
                        .append(" VALUES (?,?)")
                        .append(" ON DUPLICATE KEY UPDATE")
                        .append(" business_unit = VALUES (business_unit)");

                Query insert = entityManager.createNativeQuery(sql.toString());
                insert.setParameter(1, employee.getPsNumber());
                insert.setParameter(2, employee.getBusinessUnit());
                insert.executeUpdate();

            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        });
    }

}