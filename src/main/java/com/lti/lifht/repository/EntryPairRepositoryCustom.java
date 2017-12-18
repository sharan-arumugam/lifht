package com.lti.lifht.repository;

import java.util.List;

import com.lti.lifht.model.EntryDateBean;
import com.lti.lifht.model.EntryPairBean;

public interface EntryPairRepositoryCustom {

	void saveOrUpdatePair(List<EntryPairBean> pairList);

	void saveOrUpdateDate(List<EntryDateBean> entryDateList);
}
