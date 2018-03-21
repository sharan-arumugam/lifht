package com.lti.lifht.controller;

import static com.lti.lifht.constant.ExcelConstant.SWP_MAP;
import static com.lti.lifht.constant.PatternConstant.HAS_ANY_ROLE_ADMIN;
import static com.lti.lifht.constant.PatternConstant.HAS_ROLE_SUPER;
import static com.lti.lifht.util.CommonUtil.formatDuration2;
import static com.lti.lifht.util.ExcelUtil.autoParse;
import static com.lti.lifht.util.ExcelUtil.parseXlsx;
import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.util.Comparator.comparing;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.springframework.http.HttpStatus.NOT_MODIFIED;
import static org.springframework.http.ResponseEntity.accepted;
import static org.springframework.http.ResponseEntity.status;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
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
import com.lti.lifht.util.CommonUtil;
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

    @PostMapping("/import/exclusion")
    @PreAuthorize(HAS_ROLE_SUPER)
    public ResponseEntity<Object> importExclusion(@RequestParam("exclusion") MultipartFile exclusion) {
        try {
            List<Map<String, String>> rows = parseXlsx.apply(exclusion.getInputStream());
            service.saveOrUpdateExclusion(rows);
            return accepted().build();
        } catch (Exception e) {
            return status(NOT_MODIFIED).build();
        }
    }

    @PostMapping("/import/swipe-data")
    @PreAuthorize(HAS_ROLE_SUPER)
    public ResponseEntity<Object> importSwipeData(@RequestParam("swipe-data") MultipartFile swipeData,
            @RequestParam("send-mail") String sendMail, HttpServletRequest request) {
        try {
            List<Map<String, String>> rows = autoParse.apply(swipeData.getOriginalFilename(),
                    swipeData.getInputStream());

            service.saveOrUpdateEntry(rows);

            if (Boolean.valueOf(sendMail)) {
                String swipeDate = rows.stream()
                        .filter(row -> !row.get(SWP_MAP.get("eventNumber")).startsWith("--"))
                        .findAny()
                        .get()
                        .get(SWP_MAP.get("swipeDate"));

                String appUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

                service.notifyNonCompliant(swipeDate, appUrl);
            }

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

        // List<EntryRange> billable = cumulative.stream()
        // .filter(range -> null != range.getEmployee().getBillable()
        // ? range.getEmployee().getBillable().equalsIgnoreCase("yes")
        // : false)
        // .collect(Collectors.toList());

        List<EntryRange> withDsId = cumulative.stream()
                .filter(range -> null != range.getEmployee().getDsId())
                .collect(Collectors.toList());

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

        Map<String, EmployeeBean> psEmpMap = withDsId.stream().filter(Objects::nonNull)
                .filter(entry -> null != entry.getPsNumber())
                .collect(toMap(EntryRange::getPsNumber,
                        entryRange -> null != entryRange.getEmployee() && null != entryRange.getEmployee()
                                ? entryRange.getEmployee()
                                : new EmployeeBean(),
                        (value, duplicate) -> value));

        withDsId.stream().collect(toMap(EntryRange::getPsNumber, identity())).forEach((ps, entryRangeBean) -> {
            StringJoiner joiner = new StringJoiner(",");
            EmployeeBean employee = entryRangeBean.getEmployee();
            joiner.add(null != employee.getBusinessUnit() ? employee.getBusinessUnit() : "")
                    .add(null != employee.getDsId() ? employee.getDsId() : "").add(ps)
                    .add(null != employee.getPsName() ? employee.getPsName() : "")
                    .add(entryRangeBean.getValidSince() + "").add(entryRangeBean.getDaysPresent() + "")
                    .add(formatDuration2(entryRangeBean.getFilo()))
                    .add(formatDuration2(entryRangeBean.getDuration()))
                    .add(formatDuration2(entryRangeBean.getCompliance()));
            reportMap.put(ps, joiner);
        });

        datePsBeanMap.entrySet().stream().sorted(Entry.comparingByKey()).map(Entry::getValue)
                .forEach(psEntryBeanMap -> {
                    psEmpMap.forEach((ps, employee) -> {
                        reportMap.get(ps)
                                .add(null != psEntryBeanMap.get(ps) ? formatDuration2(psEntryBeanMap.get(ps).getFilo())
                                        : "-")
                                .add(null != psEntryBeanMap.get(ps)
                                        ? formatDuration2(psEntryBeanMap.get(ps).getDuration())
                                        : "-");
                    });
                });

        StringJoiner cumulativeHeaders = EntryRange.fetchReportHeaders();

        Map<String, StringJoiner> averageMap = new HashMap<>();

        nestedDateMultiList.forEach(dateBeanListMap -> {
            StringJoiner joiner = new StringJoiner(",");
            dateBeanListMap.forEach((date, beanList) -> {

                List<EntryDateBean> billableList = beanList
                        .stream()
                        .filter(bean -> null != bean.getEmployee().getBillable())
                        .filter(bean -> bean.getEmployee().getBillable().equalsIgnoreCase("yes"))
                        .collect(toList());

                Duration averageSum = billableList
                        .stream()
                        .map(EntryDateBean::getDuration)
                        .reduce(Duration::plus)
                        .orElse(Duration.ZERO);

                double averageFull = (double) averageSum.toMinutes() / (60 * billableList.size());

                double average = (double) Math.round(averageFull * 100.0) / 100.0;

                joiner.add(date.format(ISO_DATE))
                        .add(String.valueOf(billableList.size()))
                        .add(String.valueOf(average));

                averageMap.put(date.format(ISO_DATE), joiner);
            });

        });

        response.setHeader("Content-Disposition",
                "attachment; filename=report-" + LocalDate.now().toString() + ".xlsx");

        workbook = service.generateRangeMultiDatedReport(new XSSFWorkbook(), cumulativeHeaders, datePsBeanMap.keySet(),
                reportMap.values().stream().sorted(comparing(joiner -> {
                    String psName = joiner.toString().split(",")[3];
                    return null != psName && !"null".equals(psName) ? psName : "";
                })).toArray(),
                averageMap);

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
