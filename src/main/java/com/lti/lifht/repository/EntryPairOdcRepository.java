package com.lti.lifht.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lti.lifht.entity.EntryPairOdc;

public interface EntryPairOdcRepository extends JpaRepository<EntryPairOdc, Integer> {

    @Query("select pair from EntryPairOdc pair where pair.swipeDate between :minDate and :maxDate")
    List<EntryPairOdc> findBetween(@Param("minDate") LocalDate minDate, @Param("maxDate") LocalDate maxDate);

}
