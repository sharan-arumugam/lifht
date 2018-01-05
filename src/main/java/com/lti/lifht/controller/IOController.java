package com.lti.lifht.controller;

import static com.lti.lifht.constant.PatternConstant.HAS_ROLE_ADMIN;
import static com.lti.lifht.util.ExcelUtil.autoParse;
import static com.lti.lifht.util.ExcelUtil.parseXlsx;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.lti.lifht.model.EntryRange;
import com.lti.lifht.model.request.RangeMultiPs;
import com.lti.lifht.service.AdminService;
import com.lti.lifht.service.IOService;
import com.lti.lifht.util.LocalDateStream;

@RequestMapping("/io")
@PreAuthorize(HAS_ROLE_ADMIN)
@RestController
public class IOController {

	@Autowired
	IOService service;

	@Autowired
	AdminService adminService;

	@PostMapping("/import/head-count")
	public ModelAndView importHeadCount(ModelAndView mv, @RequestParam("head-count") MultipartFile headCount)
			throws IOException {

		List<Map<String, String>> rows = parseXlsx.apply(headCount.getInputStream());

		Integer updateCount = service.saveOrUpdateHeadCount(rows);

		mv.addObject("messageHc", "updated headcount: " + updateCount + " entries updated");
		mv.setViewName("upload");

		return mv;
	}

	@PostMapping("/import/project-allocation")
	public ModelAndView importAllocation(ModelAndView mv, @RequestParam("project-allocation") MultipartFile allocation)
			throws IOException {

		List<Map<String, String>> rows = parseXlsx.apply(allocation.getInputStream());

		Integer updateCount = service.saveOrUpdateProjectAllocation(rows);

		mv.addObject("messagePa", "updated project-allocation: " + updateCount + " entries updated");
		mv.setViewName("upload");

		return mv;
	}

	@PostMapping("/import/swipe-data")
	public ModelAndView importSwipeData(ModelAndView mv, @RequestParam("swipe-data") MultipartFile swipeData)
			throws IOException {
		List<Map<String, String>> rows = autoParse.apply(swipeData.getOriginalFilename(), swipeData.getInputStream());

		Integer updateCount = service.saveOrUpdateRawEntry(rows);

		mv.addObject("messageSwipe", "updated swipes: " + updateCount + " entries updated");
		mv.setViewName("upload");

		return mv;
	}

	@GetMapping("/export/range-multi-ps")
	public void generateRangeMultiReport(HttpServletResponse response,
			@RequestParam("fromDate") String fromDate,
			@RequestParam("toDate") String toDate,
			@RequestParam("psNumberList") String psNumberList) throws IOException {

		RangeMultiPs request = new RangeMultiPs(fromDate, toDate,
				psNumberList.split(","));

		List<EntryRange> cumulative = adminService.getRangeMulti(request, true);

		LocalDateStream localDateStream = new LocalDateStream(request.getFromDate(), request.getToDate());

		List<String> mew2 = new ArrayList<>();

		localDateStream.stream()
				.map(date -> { System.out.println(date);
					return adminService.getDateMulti(date, psNumberList.split(","));
					})
				.forEach(dateMultiPsMap -> {
					dateMultiPsMap.forEach((date, entryDateBeanList) -> {
						String mew = entryDateBeanList.stream().map(entryBean -> {
							return entryBean.getPsNumber() + " : " + date + " : " + entryBean.getFiloString();
						}).collect(Collectors.joining(","));
						mew2.add(mew);
					});
				});

		System.out.println(mew2);

		String[] reportHeaders = EntryRange.fetchReportHeaders();

		response.setHeader("Content-Disposition",
				"attachment; filename=report-" + LocalDate.now().toString() + ".xlsx");
		service.generateRangeMultiReport(cumulative, reportHeaders).write(response.getOutputStream());

	}
}
