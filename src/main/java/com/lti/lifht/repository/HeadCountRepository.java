package com.lti.lifht.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import com.lti.lifht.entity.HeadCount;

@Component
public interface HeadCountRepository extends JpaRepository<HeadCount, String> {

    @Query("from HeadCount hc where hc.psNumber not in :list")
    List<HeadCount> psNumberNotIn(@Param("list") List<String> list);
}
