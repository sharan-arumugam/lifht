package com.lti.lifht.controller;

import static com.lti.lifht.util.ExcelUtil.parseXlsx;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger logger = LoggerFactory.getLogger(IOController.class);

	@Autowired
	IOService service;

	@PostMapping("/import/head-count")
	public Object importHeadCount(@RequestParam("head-count") MultipartFile headCount) throws IOException {
		List<Map<String, String>> rows = parseXlsx.apply(headCount.getInputStream());
		logger.info(rows.toString());
		// service.saveOrUpdateHeadCount(rows);
		return null;
	}

	// @POST
	// @Path("/import/project-allocation")
	// @Consumes(MediaType.MULTIPART_FORM_DATA)
	// public Response importProjectAllocation(@FormDataParam("project-allocation")
	// InputStream inputStream,
	// @FormDataParam("project-allocation") FormDataContentDisposition metaData)
	// throws IOException {
	//
	// List<Map<String, String>> rows = parseXlsx.apply(inputStream);
	// service.saveOrUpdateProjectAllocation(rows);
	//
	// return null != rows && rows.size() > 0 ?
	// Response.status(Status.ACCEPTED).build()
	// : Response.status(Status.NOT_ACCEPTABLE).build();
	//
	// }
	//
	// @POST
	// @Path("/import/swipe-data")
	// @Consumes(MediaType.MULTIPART_FORM_DATA)
	// public Response importSwipeData(@FormDataParam("swipe-data") InputStream
	// inputStream,
	// @FormDataParam("swipe-data") FormDataContentDisposition metaData) throws
	// IOException {
	//
	// List<Map<String, String>> rows = autoParse.apply(metaData.getFileName(),
	// inputStream);
	// service.saveOrUpdateRawEntry(rows);
	//
	// return null != rows && rows.size() > 0 ?
	// Response.status(Status.ACCEPTED).build()
	// : Response.status(Status.NOT_ACCEPTABLE).build();
	// }
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
