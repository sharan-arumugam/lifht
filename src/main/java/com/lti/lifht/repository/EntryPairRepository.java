package com.lti.lifht.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lti.lifht.entity.EntryPair;

public interface EntryPairRepository extends JpaRepository<EntryPair, Integer>, EntryPairRepositoryCustom {

	List<EntryPair> findAll();

}
