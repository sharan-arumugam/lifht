package com.lti.lifht.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.lti.lifht.entity.HeadCount;

@Component
public interface HeadCountRepository extends JpaRepository<HeadCount, String> {
}
