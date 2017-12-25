package com.lti.lifht.controller;

import static com.lti.lifht.constant.PatternConstant.HAS_ROLE_ADMIN;
import static com.lti.lifht.util.ExcelUtil.autoParse;
import static com.lti.lifht.util.ExcelUtil.parseXlsx;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lti.lifht.model.EntryRange;
import com.lti.lifht.model.request.RangeMultiPs;
import com.lti.lifht.service.AdminService;
import com.lti.lifht.service.IOService;

@RequestMapping("/io")
@PreAuthorize(HAS_ROLE_ADMIN)
@RestController
public class IOController {

    @Autowired
    IOService service;

    @Autowired
    AdminService adminService;

    @PostMapping("/import/head-count")
    public void importHeadCount(@RequestParam("head-count") MultipartFile headCount) throws IOException {

        List<Map<String, String>> rows = parseXlsx.apply(headCount.getInputStream());
        service.saveOrUpdateHeadCount(rows);
    }

    @PostMapping("/import/project-allocation")
    public void importAllocation(@RequestParam("project-allocation") MultipartFile allocation) throws IOException {

        List<Map<String, String>> rows = parseXlsx.apply(allocation.getInputStream());
        service.saveOrUpdateProjectAllocation(rows);
    }

    @PostMapping("/import/swipe-data")
    public void importSwipeData(@RequestParam("swipe-data") MultipartFile swipeData) throws IOException {
        List<Map<String, String>> rows = autoParse.apply(swipeData.getOriginalFilename(), swipeData.getInputStream());
        service.saveOrUpdateRawEntry(rows);
    }

    @GetMapping("/export/range-multi-ps")
    public void generateRangeMultiReport(HttpServletResponse response,
            @RequestParam("fromDate") String fromDate,
            @RequestParam("toDate") String toDate,
            @RequestParam("psNumberList") String psNumberList) throws IOException {

        RangeMultiPs request = new RangeMultiPs(fromDate, toDate,
                psNumberList.split(","));

        List<EntryRange> entries = adminService.getRangeMulti(request);
        String[] reportHeaders = EntryRange.fetchReportHeaders();

        response.setHeader("Content-Disposition",
                "attachment; filename=report-" + LocalDate.now().toString() + ".xlsx");
        service.generateRangeMultiReport(entries, reportHeaders).write(response.getOutputStream());

    }
}
