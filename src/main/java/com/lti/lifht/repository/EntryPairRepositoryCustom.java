package com.lti.lifht.repository;

import java.util.List;

import com.lti.lifht.entity.EntryPair;

public interface EntryPairRepositoryCustom {

    void saveOrUpdatePair(List<EntryPair> pairList);
}
