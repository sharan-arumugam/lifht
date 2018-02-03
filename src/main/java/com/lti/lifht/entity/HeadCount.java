package com.lti.lifht.entity;

import static com.lti.lifht.util.CommonUtil.parseHeadCountDate;
import static com.lti.lifht.util.CommonUtil.parseStringInt;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.lti.lifht.model.HeadCountRaw;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, of = { "dsId" })
@Entity
public class HeadCount {

    @Id
    private String dsId;
    private String psName;
    private String psNumber;
    private String imt;
    private Boolean offshore;
    private String location;
    private String manager;
    private Boolean billable;
    private Integer weeksNonBillable;
    private String badge;
    private LocalDate badgeExpiry;
    private String po;
    private String poDesc;
    private LocalDate poExpiry;
    private LocalDate bcpEndDate;

    public HeadCount(HeadCountRaw ref) {
        try {
            dsId = ref.getDsId();
            psName = ref.getPsName();
            psNumber = ref.getPsNumber();
            imt = ref.getImt();
            offshore = Boolean.valueOf(ref.getOffshore());
            location = ref.getLocation();
            manager = ref.getManager();
            billable = Boolean.valueOf(ref.getBillable());
            weeksNonBillable = parseStringInt.apply(ref.getWeeksNonBillable());
            badge = ref.getBadge();
            badgeExpiry = parseHeadCountDate.apply(ref.getBadgeExpiry());
            po = ref.getPo();
            poDesc = ref.getPoDesc();
            poExpiry = parseHeadCountDate.apply(ref.getPoExpiry());
            bcpEndDate = parseHeadCountDate.apply(ref.getBcpEndDate());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}