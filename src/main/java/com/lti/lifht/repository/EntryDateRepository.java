package com.lti.lifht.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lti.lifht.entity.EntryDate;

public interface EntryDateRepository extends JpaRepository<EntryDate, Integer>, EntryDateRepositoryCustom {
    List<EntryDate> findAll();

    @Query("select distinct(psNumber) from EntryDate ed where ed.psNumber not in :list")
    public List<String> psNumberNotIn(@Param("list") List<String> list);

}
