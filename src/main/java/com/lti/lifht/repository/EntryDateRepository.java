package com.lti.lifht.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lti.lifht.entity.EntryDate;

public interface EntryDateRepository extends JpaRepository<EntryDate, Integer>, EntryDateRepositoryCustom {
    List<EntryDate> findAll();
}
