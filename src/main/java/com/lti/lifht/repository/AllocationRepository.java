package com.lti.lifht.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.lti.lifht.entity.Allocation;

@Component
public interface AllocationRepository extends JpaRepository<Allocation, String> {
}
