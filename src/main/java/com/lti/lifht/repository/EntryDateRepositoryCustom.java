package com.lti.lifht.repository;

import java.util.List;

import com.lti.lifht.entity.EntryDate;
import com.lti.lifht.model.EntryDateBean;
import com.lti.lifht.model.RangeMultiPs;

public interface EntryDateRepositoryCustom {

    void saveOrUpdateDate(List<EntryDate> entryDateList);

    List<EntryDateBean> getPsListForAggregate(RangeMultiPs request);
}
