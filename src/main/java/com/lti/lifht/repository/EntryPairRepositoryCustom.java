package com.lti.lifht.repository;

import java.util.List;

import com.lti.lifht.entity.EntryPair;
import com.lti.lifht.model.EntryPairBean;
import com.lti.lifht.model.request.DateSinglePs;

public interface EntryPairRepositoryCustom {

    void saveOrUpdatePair(List<EntryPair> pairList, String doorName);

    List<EntryPairBean> getDateSinlgePs(DateSinglePs request);
}
