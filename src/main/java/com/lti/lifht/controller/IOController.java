package com.lti.lifht.controller;

import static com.lti.lifht.util.ExcelUtil.autoParse;
import static com.lti.lifht.util.ExcelUtil.parseXlsx;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lti.lifht.service.IOService;

@RequestMapping("/io")
@RestController
public class IOController {

	@Autowired
	IOService service;

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
		System.out.println(swipeData.getName() + " " + swipeData.getOriginalFilename());
		List<Map<String, String>> rows = autoParse.apply(swipeData.getOriginalFilename(), swipeData.getInputStream());
		service.saveOrUpdateRawEntry(rows);
	}
	//
	// @GET
	// @Path("/export/range-multi-ps")
	// @Produces("application/vnd.ms-excel")
	// public Response generateRangeMultiReport(@QueryParam("fromDate") String
	// fromDate,
	// @QueryParam("toDate") String toDate,
	// @QueryParam("psNumberList") String psNumberList) throws IOException {
	//
	// RangeMultiPs request = new RangeMultiPs(fromDate, toDate,
	// psNumberList.split(","));
	// logger.info(request.toString());
	//
	// List<EntryRange> entries = adminService.getRangeMulti(request);
	// String[] reportHeaders = EntryRange.fetchReportHeaders();
	//
	// return service.generateRangeMultiReport(entries, reportHeaders);
	// }
}
