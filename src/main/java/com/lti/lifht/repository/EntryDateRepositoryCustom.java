package com.lti.lifht.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.lti.lifht.entity.EntryDate;
import com.lti.lifht.model.EntryDateBean;
import com.lti.lifht.model.request.DateMultiPs;
import com.lti.lifht.model.request.RangeMultiPs;
import com.lti.lifht.model.request.RangeSinglePs;

public interface EntryDateRepositoryCustom {

    Integer saveOrUpdateDate(List<EntryDate> entryDateList);

    List<EntryDateBean> getPsEntryDate(RangeSinglePs request);

    List<EntryDateBean> getPsListEntryDate(DateMultiPs request);

    List<EntryDateBean> getPsListForAggregate(RangeMultiPs request);

    List<EntryDateBean> getPsListForAggregateDelta(RangeMultiPs request);

    Map<String, LocalDate> getValidSince(LocalDate fromDate, LocalDate toDate);
}
