package com.lti.lifht.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
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
import com.lti.lifht.model.RangeMultiPs;

@Repository
@Transactional
public class EntryPairRepositoryImpl implements EntryPairRepositoryCustom {

    private static final Logger logger = LoggerFactory.getLogger(EntryPairRepositoryImpl.class);

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public void saveOrUpdatePair(List<EntryPair> pairList) {

        List<Integer> updated = new ArrayList<>();

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
}