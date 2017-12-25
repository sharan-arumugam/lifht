package com.lti.lifht.repository;

import java.util.List;

import com.lti.lifht.entity.EntryDate;
import com.lti.lifht.model.EntryDateBean;
import com.lti.lifht.model.request.DateMultiPs;
import com.lti.lifht.model.request.RangeMultiPs;
import com.lti.lifht.model.request.RangeSinglePs;

public interface EntryDateRepositoryCustom {

    void saveOrUpdateDate(List<EntryDate> entryDateList);

    List<EntryDateBean> getPsEntryDate(RangeSinglePs request);

    List<EntryDateBean> getPsListEntryDate(DateMultiPs request);

    List<EntryDateBean> getPsListForAggregate(RangeMultiPs request);

    List<EntryDateBean> getPsListForAggregateDelta(RangeMultiPs request);
}
