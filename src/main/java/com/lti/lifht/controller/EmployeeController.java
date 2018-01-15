package com.lti.lifht.controller;

import static com.lti.lifht.constant.PatternConstant.HAS_ANY_ROLE_ADMIN;
import static com.lti.lifht.constant.PatternConstant.HAS_ANY_ROLE_EMPLOYEE;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lti.lifht.model.EmployeeBean;
import com.lti.lifht.model.EntryDateBean;
import com.lti.lifht.model.EntryPairBean;
import com.lti.lifht.model.EntryRange;
import com.lti.lifht.model.request.DateMultiPs;
import com.lti.lifht.model.request.DateSinglePs;
import com.lti.lifht.model.request.RangeMultiPs;
import com.lti.lifht.model.request.RangeSinglePs;
import com.lti.lifht.service.AdminService;

@RestController
@RequestMapping("/api")
public class EmployeeController {

	@Autowired
	AdminService service;

	@GetMapping("/all")
	@PreAuthorize(HAS_ANY_ROLE_ADMIN)
	public List<EmployeeBean> getAllEmployees() {
		return service.getAllEmployees()
				.stream()
				.filter(entry -> null != entry.getPsName())
				.filter(entry -> NumberUtils.isCreatable(entry.getPsNumber()))
				.sorted(Comparator.comparing(EmployeeBean::getPsName))
				.collect(Collectors.toList());
	}

	@PostMapping("/swipe/date-single-ps")
	@PreAuthorize(HAS_ANY_ROLE_EMPLOYEE)
	public List<EntryPairBean> getDateSinglePs(@RequestBody DateSinglePs request) {
		return service.getDateSinglePs(request);
	}

	@PostMapping("/swipe/range-single-ps")
	@PreAuthorize(HAS_ANY_ROLE_EMPLOYEE)
	public List<EntryDateBean> getRangeSingle(@RequestBody RangeSinglePs request) {
		return service.getRangeSingle(request);
	}

	@PostMapping("/swipe/date-multi-ps")
	@PreAuthorize(HAS_ANY_ROLE_ADMIN)
	public List<EntryDateBean> getDateMulti(@RequestBody DateMultiPs request) {
		return service.getDateMulti(request, false);
	}

	@PostMapping("/swipe/range-multi-ps")
	@PreAuthorize(HAS_ANY_ROLE_ADMIN)
	public List<EntryRange> getRangeMulti(@RequestBody RangeMultiPs request) {
		return service.getRangeMulti(request, false);
	}

}
