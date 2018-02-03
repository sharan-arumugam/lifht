package com.lti.lifht.entity;

import static java.lang.Integer.parseInt;
import static java.time.LocalDate.parse;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.math.NumberUtils.isCreatable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    @Id
    private String dsId;
    private String psName;
    private String psNumber;
    private String imt;
    private String offshore;
    private String location;
    private String manager;
    private String billable;
    private Integer weeksNonBillable;
    private String badge;
    private LocalDate bageExpiry;
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
            offshore = ref.getOffshore();
            location = ref.getLocation();
            manager = ref.getManager();
            billable = ref.getBillable();

            weeksNonBillable = isCreatable(ref.getWeeksNonBillable())
                    ? parseInt(ref.getWeeksNonBillable())
                    : 0;

            badge = ref.getBadge();

            bageExpiry = isNotBlank(ref.getBageExpiry())
                    ? parse(ref.getBageExpiry(), formatter)
                    : null;

            po = ref.getPo();
            poDesc = ref.getPoDesc();

            poExpiry = isNotBlank(ref.getPoExpiry())
                    ? parse(ref.getPoExpiry(), formatter)
                    : null;

            bcpEndDate = isNotBlank(ref.getBcpEndDate())
                    ? parse(ref.getBcpEndDate(), formatter)
                    : null;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}