package com.lti.lifht.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResourceDetails {

    public static final Map<String, String> RESOURCE_MAP = new LinkedHashMap<>();
    static {
        RESOURCE_MAP.put("psNumber", "PS Number");
        RESOURCE_MAP.put("deputedBu", "Base BU");
        RESOURCE_MAP.put("projectId", "Project Id");
        RESOURCE_MAP.put("dsId", "DSID");
        RESOURCE_MAP.put("imt", "IMT");
        RESOURCE_MAP.put("imt1", "IMT-1");
        RESOURCE_MAP.put("imt2", "IMT-2");
        RESOURCE_MAP.put("psName", "Name");
        RESOURCE_MAP.put("offshore", "Offshore");
        RESOURCE_MAP.put("grade", "Grade");
        RESOURCE_MAP.put("manager", "Functional Manager");
        RESOURCE_MAP.put("sow", "SOW#");
        RESOURCE_MAP.put("hrs", "No of Hrs");
        RESOURCE_MAP.put("rate", "Rate");
        RESOURCE_MAP.put("sow_status", "SOW Status");
        RESOURCE_MAP.put("po", "PO #");
    }

    private String psNumber;
    private String deputedBu;
    private String projectId;
    private String dsId;
    private String imt;
    private String imt1;
    private String imt2;
    private String psName;
    private String offshore;
    private String grade;
    private String manager;
    private String sow;
    private String hrs;
    private String rate;
    private String sow_status;
    private String po;

    public ResourceDetails(Object[] resultResource) {

         psNumber = String.valueOf(resultResource[0]);
         deputedBu = String.valueOf(resultResource[1]);
         projectId = String.valueOf(resultResource[2]);
         dsId = String.valueOf(resultResource[5]);
         imt = String.valueOf(resultResource[6]);
         imt1 = String.valueOf(resultResource[7]);
         imt2 = String.valueOf(resultResource[8]);
         psName = String.valueOf(resultResource[9]);
         offshore = String.valueOf(resultResource[3]);
         grade = String.valueOf(resultResource[4]);
         manager = String.valueOf(resultResource[11]);
         po = String.valueOf(resultResource[10]);

    }
}
