package com.lti.lifht.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.lti.lifht.entity.EntryPair;
import com.lti.lifht.model.EntryPairBean;
import com.lti.lifht.model.request.DateSinglePs;

@Repository
@Transactional
@SuppressWarnings("unchecked")
public class EntryPairRepositoryImpl implements EntryPairRepositoryCustom {

    private static final Logger logger = LoggerFactory.getLogger(EntryPairRepositoryImpl.class);

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public void saveOrUpdatePair(List<EntryPair> pairList) {

        pairList.forEach(entry -> {

            StringBuilder sql = new StringBuilder();
            String params = IntStream.rangeClosed(1, 6).boxed().map(e -> "?").collect(Collectors.joining(","));
            sql.append("INSERT INTO entry_pair (swipe_date, swipe_in, swipe_out, swipe_door, duration, ps_number)")
                    .append(" VALUES (" + params + ") ON DUPLICATE KEY UPDATE ps_number = VALUES (ps_number)");

            try {
                Query insert = entityManager.createNativeQuery(sql.toString());

                insert.setParameter(1, entry.getSwipeDate());
                insert.setParameter(2, entry.getSwipeIn());
                insert.setParameter(3, entry.getSwipeOut());
                insert.setParameter(4, entry.getSwipeDoor());
                insert.setParameter(5, entry.getDuration());
                insert.setParameter(6, entry.getPsNumber());
                insert.executeUpdate();

            } catch (Exception e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
        });
    }

    @Override
    public List<EntryPairBean> getDateSinlgePs(DateSinglePs request) {

        StringBuilder sql = new StringBuilder();

        sql.append("SELECT e.ps_number, e.ps_name,")
                .append(" e.business_unit, e.lti_mail, e.ds_id,")
                .append(" p.swipe_date, p.duration,")
                .append(" p.swipe_door, p.swipe_in, p.swipe_out")
                .append(" FROM entry_pair p, employee e")
                .append(" WHERE p.ps_number = e.ps_number AND p.ps_number = ? AND p.swipe_date = ?");

        Query select = entityManager.createNativeQuery(sql.toString());
        select.setParameter(1, request.getPsNumber());
        select.setParameter(2, request.getDate());

        List<Object[]> rawList = select.getResultList();

        List<EntryPairBean> entryPairList = new ArrayList<>();

        rawList.forEach(rs -> {
            entryPairList.add(new EntryPairBean(rs));
        });

        return entryPairList;
    }
}