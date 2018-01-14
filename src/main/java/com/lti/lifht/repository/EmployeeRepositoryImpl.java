package com.lti.lifht.repository;

import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.rangeClosed;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.lti.lifht.model.EmployeeBean;

@Repository
@Transactional
public class EmployeeRepositoryImpl implements EmployeeRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Integer saveOrUpdateHeadCount(List<EmployeeBean> employeeList) {

        List<Integer> updateCountList = new ArrayList<>();

        employeeList.forEach(employee -> {

            StringBuilder sql = new StringBuilder();

            sql.append("INSERT INTO employee(ps_number, ps_name, apple_manager, lti_mail, ds_id, active)")
                    .append(" VALUES (?, ?, ?, ?, ?, ?)")
                    .append(" ON DUPLICATE KEY UPDATE")
                    .append(" ps_name = VALUES (ps_name),")
                    .append(" apple_manager = VALUES (apple_manager),")
                    .append(" lti_mail = VALUES (lti_mail), ")
                    .append(" ds_id = VALUES (ds_id),")
                    .append(" active = VALUES (active)");

            Query insert = entityManager.createNativeQuery(sql.toString());

            insert.setParameter(1, employee.getPsNumber());
            insert.setParameter(2, employee.getPsName());
            insert.setParameter(3, employee.getManager());
            insert.setParameter(4, employee.getEmail());
            insert.setParameter(5, employee.getDsId());
            insert.setParameter(6, "Y"); // set active
            updateCountList.add(insert.executeUpdate());
        });

        return updateCountList.stream()
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();
    }

    @Override
    public Integer saveOrUpdateProjectAllocation(List<EmployeeBean> employeeList) {

        List<Integer> updateCountList = new ArrayList<>();

        employeeList.forEach(employee -> {

            StringBuilder sql = new StringBuilder();

            sql.append("INSERT INTO employee(ps_number, business_unit)")
                    .append(" VALUES (?, ?)")
                    .append(" ON DUPLICATE KEY UPDATE")
                    .append(" business_unit = VALUES (business_unit)");

            Query insert = entityManager.createNativeQuery(sql.toString());
            insert.setParameter(1, employee.getPsNumber());
            insert.setParameter(2, employee.getBusinessUnit());
            updateCountList.add(insert.executeUpdate());
        });

        return updateCountList.stream()
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();
    }

    @Override
    public void resetAccess(List<String> psNumberList) {

        int psCount = psNumberList.size();
        String params = rangeClosed(1, psCount)
                .boxed()
                .map(e -> "?")
                .collect(joining(","));

        StringBuilder sql = new StringBuilder();
        sql.append("update employee set active = 'N' where ps_number not in (" + params + ") and active <> 'A'");
        Query update = entityManager.createNativeQuery(sql.toString());

        rangeClosed(1, psCount).forEachOrdered(index -> {
            update.setParameter(index, psNumberList.get(index - 1));
        });

        update.executeUpdate();
    }

}