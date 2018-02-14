package com.lti.lifht.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.lti.lifht.entity.Exclusion;

@Component
public interface ExclusionRepository extends JpaRepository<Exclusion, String> {
}
