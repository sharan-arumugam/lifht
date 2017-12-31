package com.lti.lifht.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lti.lifht.entity.EntryPair;

public interface EntryPairRepository extends JpaRepository<EntryPair, Integer>, EntryPairRepositoryCustom {

    @Query("select pair from EntryPair pair where pair.swipeDate between :minDate and :maxDate")
    List<EntryPair> findBetween(@Param("minDate") LocalDate minDate, @Param("maxDate") LocalDate maxDate);

}
