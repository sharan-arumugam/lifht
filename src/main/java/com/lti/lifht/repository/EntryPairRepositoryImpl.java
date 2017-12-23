package com.lti.lifht.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.lti.lifht.model.EntryDateBean;
import com.lti.lifht.model.EntryPairBean;

@Repository
@Transactional
public class EntryPairRepositoryImpl implements EntryPairRepositoryCustom {

	private static final Logger logger = LoggerFactory.getLogger(EntryPairRepositoryImpl.class);

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public void saveOrUpdatePair(List<EntryPairBean> pairList) {

		pairList.forEach(entry -> {

			StringBuilder sql = new StringBuilder();

			sql.append("INSERT INTO entry_pair (swipe_date, swipe_in, swipe_out, swipe_door, duration, ps_number)")
					.append(" VALUES (?,?,?,?,?,?) ON DUPLICATE KEY UPDATE ps_number = VALUES (ps_number)");

			try {
				Query insert = entityManager.createNativeQuery(sql.toString());

				insert.setParameter(1, entry.getSwipeDate().toString());
				insert.setParameter(2, entry.getSwipeIn().toString());
				insert.setParameter(3, entry.getSwipeOut().toString());
				insert.setParameter(4, entry.getSwipeDoor());
				insert.setParameter(5, entry.getDuration().toMillis());
				insert.setParameter(6, entry.getPsNumber());
				insert.executeUpdate();

			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		});

	}

	@Override
	public void saveOrUpdateDate(List<EntryDateBean> entryDateList) {

		entryDateList.forEach(entry -> {

			StringBuilder sql = new StringBuilder();
			sql.append(
					"INSERT INTO entry_date (swipe_date, swipe_door, duration, compliance, first_in, last_out, filo, ps_number)")
					.append(" VALUES (?,?,?,?,?,?,?,?)").append(" ON DUPLICATE KEY UPDATE")
					.append(" swipe_door = VALUES (swipe_door),").append(" duration = VALUES (duration),")
					.append(" compliance = VALUES (compliance),").append(" first_in = VALUES (first_in),")
					.append(" last_out = VALUES (last_out),").append(" filo = VALUES (filo),")
					.append(" ps_number = VALUES (ps_number)");

			try {
				Query insert = entityManager.createNativeQuery(sql.toString());

				insert.setParameter(1, entry.getSwipeDate().toString());
				insert.setParameter(2, entry.getSwipeDoor());
				insert.setParameter(3, entry.getDuration().toMillis());
				insert.setParameter(4, entry.getCompliance().toMillis());
				insert.setParameter(5, entry.getFirstIn().toString());
				insert.setParameter(6, entry.getLastOut().toString());
				insert.setParameter(7, entry.getFilo().toMillis());
				insert.setParameter(8, entry.getPsNumber());
				insert.executeUpdate();

			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		});
	}

}