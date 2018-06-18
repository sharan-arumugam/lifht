package com.lti.lifht.model;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HeadCountRaw {

    HashMap<String, String> headCountMap = new HashMap<String, String>() {
        private static final long serialVersionUID = 1L;

        {
            put("dsId", "DSID");
            put("psName", "Name");
            put("imt", "IMT");
            put("imt1", "IMT-1");
            put("imt2", "IMT-2");
            put("offshore", "Offshore");
            put("location", "Location");
            put("manager", "Manager");
            put("billable", "Billable");
            put("weeksNonBillable", "Weeks Non-Billable");
            put("badge", "Badge");
            put("bageExpiry", "Badge Expiry");
            put("po", "PO #");
            put("poDesc", "PO Description");
            put("poExpiry", "PO Expiry");
            put("psNumber", "External Emp ID");
            put("bcpEndDate", "BCP End Date");
        }
    };

    private String dsId;
    private String psName;
    private String psNumber;
    private String imt;
    private String imt1;
    private String imt2;
    private String offshore;
    private String location;
    private String manager;
    private String billable;
    private String weeksNonBillable;
    private String badge;
    private String badgeExpiry;
    private String po;
    private String poDesc;
    private String poExpiry;
    private String bcpEndDate;

    public HeadCountRaw(Map<String, String> columnMap) {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                if (headCountMap.containsKey(field.getName())) {
                    field.set(this, columnMap.get(headCountMap.get(field.getName())));
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}