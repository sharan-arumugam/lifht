package com.lti.lifht.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import com.lti.lifht.entity.Allocation;

@Component
public interface AllocationRepository extends JpaRepository<Allocation, String> {

    @Query("from Allocation alloc where alloc.psNumber not in :list")
    public List<Allocation> psNumberNotIn(@Param("list") List<String> list);
}
