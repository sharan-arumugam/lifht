package com.lti.lifht.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lti.lifht.model.EmployeeBean;
import com.lti.lifht.service.AdminService;

@RestController
@RequestMapping("/admin")
public class AdminController {

	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

	@Autowired
	AdminService service;

	@GetMapping("/all")
	public List<EmployeeBean> getAllEmployees() {
		logger.info("/admin/all :: GET");
		return service.getAllEmployees();
	}

	@GetMapping("/like")
	public List<EmployeeBean> getLike() {
		return service.getFirstNamesLike();
	}

	// @POST
	// @Path("/swipe/range-single-ps")
	// @Consumes(MediaType.APPLICATION_JSON)
	// @Produces(MediaType.APPLICATION_JSON)
	// public Response getRangeSingle(RangeSinglePs request) {
	// List<EntryDate> entries = service.getRangeSingle(request);
	// return null != entries
	// ? Response.ok()
	// .entity(entries)
	// .build()
	// : Response.serverError()
	// .build();
	// }
	//
	// @POST
	// @Path("/swipe/date-multi-ps")
	// @Consumes(MediaType.APPLICATION_JSON)
	// @Produces(MediaType.APPLICATION_JSON)
	// public Response getDateMulti(DateMultiPs request) {
	// logger.info(request.toString());
	// List<EntryDate> entries = service.getDateMulti(request);
	// return null != entries
	// ? Response.ok()
	// .entity(entries)
	// .build()
	// : Response.serverError()
	// .build();
	// }
	//
	// @POST
	// @Path("/swipe/range-multi-ps")
	// @Consumes(MediaType.APPLICATION_JSON)
	// @Produces(MediaType.APPLICATION_JSON)
	// public Response getRangeMulti(RangeMultiPs request) {
	// logger.info(request.toString());
	// List<EntryRange> entries = service.getRangeMulti(request);
	// return null != entries
	// ? Response.ok()
	// .entity(entries)
	// .build()
	// : Response.serverError()
	// .build();
	// }

}
