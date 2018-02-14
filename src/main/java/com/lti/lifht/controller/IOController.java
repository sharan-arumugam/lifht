package com.lti.lifht.controller;

import static com.lti.lifht.constant.PatternConstant.HAS_ANY_ROLE_ADMIN;
import static com.lti.lifht.constant.PatternConstant.HAS_ROLE_SUPER;
import static com.lti.lifht.util.ExcelUtil.autoParse;
import static com.lti.lifht.util.ExcelUtil.parseXlsx;
import static java.util.Comparator.comparing;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.springframework.http.HttpStatus.NOT_MODIFIED;
import static org.springframework.http.ResponseEntity.accepted;
import static org.springframework.http.ResponseEntity.status;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.TreeMap;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.lti.lifht.model.EmployeeBean;
import com.lti.lifht.model.EntryDateBean;
import com.lti.lifht.model.EntryRange;
import com.lti.lifht.model.request.RangeMultiPs;
import com.lti.lifht.service.AdminService;
import com.lti.lifht.service.IOService;
import com.lti.lifht.util.LocalDateStream;

@RequestMapping("/io")
@RestController
public class IOController {

    @Autowired
    IOService service;

    @Autowired
    AdminService adminService;

    @GetMapping("/reconcile/head-count")
    @PreAuthorize(HAS_ROLE_SUPER)
    public JsonNode reconcileHeadCount() {
        return service.reconcileHeadCount();
    }

    @GetMapping("/reconcile/allocation")
    @PreAuthorize(HAS_ROLE_SUPER)
    public JsonNode reconcileAllocation() {
        return service.reconcileAllocation();
    }

    @GetMapping("/reconcile/swipe")
    @PreAuthorize(HAS_ROLE_SUPER)
    public JsonNode reconcileSwipe() {
        return service.reconcileSwipe();
    }

    @GetMapping("/reconcile/all")
    @PreAuthorize(HAS_ROLE_SUPER)
    public JsonNode reconcileAll() {
        return service.reconcileAll();
    }

    @PostMapping("/import/head-count")
    @PreAuthorize(HAS_ROLE_SUPER)
    public ResponseEntity<Object> importHeadCount(@RequestParam("head-count") MultipartFile headCount) {
        try {
            List<Map<String, String>> rows = parseXlsx.apply(headCount.getInputStream());
            service.saveOrUpdateHeadCount(rows);
            service.saveHeadCountForReconciliation(rows);
            return accepted().build();
        } catch (Exception e) {
            return status(NOT_MODIFIED).build();
        }
    }

    @PostMapping("/import/project-allocation")
    @PreAuthorize(HAS_ROLE_SUPER)
    public ResponseEntity<Object> importAllocation(@RequestParam("project-allocation") MultipartFile allocation) {
        try {
            List<Map<String, String>> rows = parseXlsx.apply(allocation.getInputStream());
            service.saveOrUpdateProjectAllocation(rows);
            service.saveAllocationForReconciliation(rows);
            return accepted().build();
        } catch (Exception e) {
            return status(NOT_MODIFIED).build();
        }
    }

    @PostMapping("/import/swipe-data")
    @PreAuthorize(HAS_ROLE_SUPER)
    public ResponseEntity<Object> importSwipeData(@RequestParam("swipe-data") MultipartFile swipeData) {
        try {
            service.saveOrUpdateRawEntry(autoParse.apply(swipeData.getOriginalFilename(), swipeData.getInputStream()));
            return accepted().build();
        } catch (Exception e) {
            return status(NOT_MODIFIED).build();
        }
    }

    @GetMapping("/export/range-multi-ps")
    @PreAuthorize(HAS_ANY_ROLE_ADMIN)
    public void generateRangeMultiReport(HttpServletResponse response, @RequestParam("fromDate") String fromDate,
            @RequestParam("toDate") String toDate) throws IOException {

        RangeMultiPs request = new RangeMultiPs(fromDate, toDate, null);
        List<EntryRange> cumulative = adminService.getRangeMulti(request, true);

        Workbook workbook;
        LocalDateStream localDateStream = new LocalDateStream(request.getFromDate(), request.getToDate());
        Map<LocalDate, Map<String, EntryDateBean>> datePsBeanMap = new HashMap<>();
        Map<String, StringJoiner> reportMap = new HashMap<>();

        List<Map<LocalDate, List<EntryDateBean>>> nestedDateMultiList = localDateStream.stream()
                .map(adminService::getDateMulti).filter(Objects::nonNull).collect(toList());

        nestedDateMultiList.forEach(map -> {
            map.forEach((date, list) -> {
                datePsBeanMap.put(date, list.stream().collect(
                        toMap(EntryDateBean::getPsNumber, identity(), (value, duplicate) -> value, TreeMap::new)));
            });
        });

        Map<String, EmployeeBean> psEmpMap = cumulative.stream().filter(Objects::nonNull)
                .filter(entry -> null != entry.getPsNumber())
                .collect(toMap(EntryRange::getPsNumber,
                        entryRange -> null != entryRange.getEmployee() && null != entryRange.getEmployee()
                                ? entryRange.getEmployee()
                                : new EmployeeBean(),
                        (value, duplicate) -> value));

        cumulative.stream().collect(toMap(EntryRange::getPsNumber, identity())).forEach((ps, entryRangeBean) -> {
            StringJoiner joiner = new StringJoiner(",");
            EmployeeBean employee = entryRangeBean.getEmployee();
            joiner.add(null != employee.getBusinessUnit() ? employee.getBusinessUnit() : "")
                    .add(null != employee.getDsId() ? employee.getDsId() : "").add(ps)
                    .add(null != employee.getPsName() ? employee.getPsName() : "")
                    .add(entryRangeBean.getValidSince() + "").add(entryRangeBean.getDaysPresent() + "")
                    .add(entryRangeBean.getFiloString()).add(entryRangeBean.getDurationString())
                    .add(entryRangeBean.getComplianceString());
            reportMap.put(ps, joiner);
        });

        datePsBeanMap.entrySet().stream().sorted(Entry.comparingByKey()).map(Entry::getValue)
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

        workbook = service.generateRangeMultiDatedReport(new XSSFWorkbook(), cumulativeHeaders, datePsBeanMap.keySet(),
                reportMap.values().stream().sorted(comparing(joiner -> {
                    String psName = joiner.toString().split(",")[3];
                    return null != psName && !"null".equals(psName) ? psName : "";
                })).toArray());

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
