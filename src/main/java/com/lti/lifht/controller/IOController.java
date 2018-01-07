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
            @RequestParam("toDate") String toDate,
            @RequestParam("psNumberList") String psNumberList) throws IOException {

        RangeMultiPs request = new RangeMultiPs(fromDate, toDate,
                psNumberList.split(","));

        List<EntryRange> cumulative = adminService.getRangeMulti(request, true);

        LocalDateStream localDateStream = new LocalDateStream(request.getFromDate(), request.getToDate());

        List<Map<LocalDate, List<EntryDateBean>>> nested = localDateStream.stream()
                .map(date -> adminService.getDateMulti(date, psNumberList.split(",")))
                .collect(Collectors.toList());

        Map<LocalDate, Map<String, EntryDateBean>> datePsBeanMap = new HashMap<>();

        nested.forEach(map -> {
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

        Map<String, StringJoiner> psDatedMap = new HashMap<>();

        System.out.println(cumulative.stream().collect(Collectors.groupingBy(EntryRange::getPsNumber)));

        Map<String, String> psNumName = cumulative.stream()
                .filter(Objects::nonNull)
                .filter(entry -> null != entry.getPsNumber())
                .collect(Collectors.toMap(
                        EntryRange::getPsNumber,
                        e -> null != e.getEmployee()
                                && null != e.getEmployee().getPsName()
                                        ? e.getEmployee().getPsName()
                                        : "",
                        (value, duplicate) -> value));

        Map<String, String> psNumBu = cumulative.stream()
                .filter(Objects::nonNull)
                .filter(entry -> null != entry.getPsNumber())
                .collect(Collectors.toMap(
                        EntryRange::getPsNumber,
                        e -> null != e.getEmployee()
                                && null != e.getEmployee().getBusinessUnit()
                                        ? e.getEmployee().getBusinessUnit()
                                        : "",
                        (value, duplicate) -> value));

        datePsBeanMap
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(es -> {
                    Map<String, EntryDateBean> psd = es.getValue();

                    psNumName.entrySet()
                            .stream()
                            .sorted(Comparator.comparing(Entry::getValue,
                                    Comparator.nullsFirst(Comparator.naturalOrder()))) // by ps name
                            .forEachOrdered(psNumNameEntry -> {
                                String ps = psNumNameEntry.getKey();
                                if (psDatedMap.containsKey(ps)) {
                                    psDatedMap.get(ps)
                                            .add(null != psd.get(ps) ? psd.get(ps).getFiloString() : " - ")
                                            .add(null != psd.get(ps) ? psd.get(ps).getDurationString() : " - ");
                                } else {
                                    StringJoiner joiner = new StringJoiner(",");
                                    joiner.add(ps)
                                            .add(null != psNumName.get(ps) ? psNumName.get(ps) : "N/A")
                                            .add(null != psNumBu.get(ps) ? psNumBu.get(ps) : "N/A")
                                            .add(null != psd.get(ps) ? psd.get(ps).getFiloString() : " - ")
                                            .add(null != psd.get(ps) ? psd.get(ps).getDurationString() : " - ");
                                    psDatedMap.put(ps, joiner);
                                }
                            });
                });

        String[] cumulativeHeaders = EntryRange.fetchReportHeaders();

        response.setHeader("Content-Disposition",
                "attachment; filename=report-"
                        + LocalDate.now().toString()
                        + ".xlsx");

        Workbook wb = service.generateRangeMultiDatedReport(new XSSFWorkbook(),
                cumulative.toArray(),
                cumulativeHeaders,
                psDatedMap,
                datePsBeanMap.keySet());

        wb.write(response.getOutputStream());
        wb.close();
    }
}
