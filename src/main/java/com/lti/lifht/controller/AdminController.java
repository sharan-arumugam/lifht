package com.lti.lifht.controller;

import static com.lti.lifht.constant.PatternConstant.HAS_ANY_ROLE_ADMIN;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lti.lifht.model.DateMultiPs;
import com.lti.lifht.model.EmployeeBean;
import com.lti.lifht.model.EntryDateBean;
import com.lti.lifht.model.EntryRange;
import com.lti.lifht.model.RangeMultiPs;
import com.lti.lifht.model.RangeSinglePs;
import com.lti.lifht.service.AdminService;

@RestController
@RequestMapping("/admin")
@PreAuthorize(HAS_ANY_ROLE_ADMIN)
public class AdminController {

    @Autowired
    AdminService service;

    @GetMapping("/all")
    public List<EmployeeBean> getAllEmployees() {
        return service.getAllEmployees();
    }

    @PostMapping("/swipe/range-single-ps")
    public List<EntryDateBean> getRangeSingle(@RequestBody RangeSinglePs request) {
        return service.getRangeSingle(request);
    }

    @PostMapping("/swipe/date-multi-ps")
    public List<EntryDateBean> getDateMulti(@RequestBody DateMultiPs request) {
        return service.getDateMulti(request);
    }

    @PostMapping("/swipe/range-multi-ps")
    public List<EntryRange> getRangeMulti(@RequestBody RangeMultiPs request) {
        return service.getRangeMulti(request);
    }

}
