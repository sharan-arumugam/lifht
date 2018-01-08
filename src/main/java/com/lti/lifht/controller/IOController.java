package com.lti.lifht.controller;

import static com.lti.lifht.constant.PatternConstant.HAS_ROLE_ADMIN;
import static com.lti.lifht.util.ExcelUtil.autoParse;
import static com.lti.lifht.util.ExcelUtil.parseXlsx;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.lti.lifht.model.EmployeeBean;
import com.lti.lifht.model.EntryDateBean;
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
			@RequestParam("toDate") String toDate) throws IOException {

		RangeMultiPs request = new RangeMultiPs(fromDate, toDate, null);

		List<EntryRange> cumulative = adminService.getRangeMulti(request, true);

		LocalDateStream localDateStream = new LocalDateStream(request.getFromDate(), request.getToDate());

		List<Map<LocalDate, List<EntryDateBean>>> nestedDateMultiList = localDateStream.stream()
				.map(adminService::getDateMulti)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());

		Map<LocalDate, Map<String, EntryDateBean>> datePsBeanMap = new HashMap<>();

		nestedDateMultiList.forEach(map -> {
			map.forEach((date, list) -> {
				datePsBeanMap.put(date,
						list.stream()
								.collect(Collectors.toMap(
										e -> e.getPsNumber(),
										Function.identity(),
										(value, duplicate) -> value,
										TreeMap::new)));
			});
		});

		Map<String, StringJoiner> reportMap = new HashMap<>();

		Map<String, EmployeeBean> psEmpMap = cumulative.stream()
				.filter(Objects::nonNull)
				.filter(entry -> null != entry.getPsNumber())
				.collect(Collectors.toMap(
						EntryRange::getPsNumber,
						entryRange -> null != entryRange.getEmployee() && null != entryRange.getEmployee()
								? entryRange.getEmployee()
								: new EmployeeBean(),
						(value, duplicate) -> value));

		cumulative.stream()
				.collect(Collectors.toMap(EntryRange::getPsNumber, Function.identity()))
				.forEach((ps, entryRangeBean) -> {
					StringJoiner joiner = new StringJoiner(",");
					EmployeeBean employee = entryRangeBean.getEmployee();
					joiner.add(null != employee.getBusinessUnit() ? employee.getBusinessUnit() : "")
							.add(null != employee.getDsId() ? employee.getDsId() : "")
							.add(ps)
							.add(null != employee.getPsName() ? employee.getPsName() : "")
							.add(entryRangeBean.getValidSince() + "")
							.add(entryRangeBean.getDaysPresent() + "")
							.add(entryRangeBean.getFiloString())
							.add(entryRangeBean.getDurationString())
							.add(entryRangeBean.getComplianceString());
					reportMap.put(ps, joiner);
				});

		datePsBeanMap
				.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByKey())
				.map(Entry::getValue)
				.forEach(psEntryBeanMap -> {
					psEmpMap.forEach((ps, employee) -> {
						reportMap.get(ps)
								.add(null != psEntryBeanMap.get(ps) ? psEntryBeanMap.get(ps).getFiloString() : "-")
								.add(null != psEntryBeanMap.get(ps) ? psEntryBeanMap.get(ps).getDurationString() : "-");
					});
				});

		StringJoiner cumulativeHeaders = EntryRange.fetchReportHeaders();

		response.setHeader("Content-Disposition",
				"attachment; filename=report-" + LocalDate.now().toString() + ".xlsx");

		Workbook workbook = service.generateRangeMultiDatedReport(new XSSFWorkbook(),
				cumulativeHeaders,
				datePsBeanMap.keySet(),
				reportMap.values()
						.stream()
						.sorted(Comparator.comparing(joiner -> {
							String psName = joiner.toString().split(",")[3];
							return null != psName && !"null".equals(psName) ? psName : "";
						}))
						.toArray());

		workbook.write(response.getOutputStream());
		workbook.close();
	}
}
