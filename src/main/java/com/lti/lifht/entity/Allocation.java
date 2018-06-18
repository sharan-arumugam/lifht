package com.lti.lifht.entity;

import static com.lti.lifht.util.CommonUtil.formatAllocationDate;
import static com.lti.lifht.util.CommonUtil.parseAllocationDate;
import static com.lti.lifht.util.CommonUtil.splitValue;

import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lti.lifht.model.AllocationRaw;
import com.lti.lifht.util.CommonUtil;

import groovy.transform.ToString;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, of = { "psNumber" })
@Entity
public class Allocation {

    @Id
    private String psNumber;
    private String deliveryBu;
    private String deptId;
    private String deputedBu;
    private String psName;
    @JsonIgnore
    private LocalDate startDate;
    @JsonIgnore
    private LocalDate endDate;
    private String resourceCountry;
    private String resourceState;
    private String grade;
    private String projectId;
    private String projectName;
    private String projectCategory;
    private String resourceLocation;
    
    public Allocation(AllocationRaw ref) {
        this.deliveryBu = ref.getDeliveryBu();
        this.deptId = ref.getDeptId();
        this.psNumber = ref.getPsNumber();
        this.deputedBu = ref.getDeputedBu();
        this.psName = ref.getPsName();
        this.startDate = parseAllocationDate.apply(ref.getStartDate());
        this.endDate = parseAllocationDate.apply(ref.getEndDate());
        this.resourceCountry = splitValue.apply(ref.getResourceCountry(),0);
        this.resourceState = splitValue.apply(ref.getResourceState(),1);
        this.grade = ref.getGrade();
        this.projectId = ref.getProjectId();
        this.projectName = ref.getProjectName();
        this.projectCategory = ref.getProjectCategory();
        this.resourceLocation = ref.getResourceLocation();
    }

    @JsonProperty(value = "startDate")
    public String startDate() {
        return formatAllocationDate.apply(startDate);
    }

    @JsonProperty(value = "endDate")
    public String endDate() {
        return formatAllocationDate.apply(endDate);
    }


}
