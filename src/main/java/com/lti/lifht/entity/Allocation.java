package com.lti.lifht.entity;

import static com.lti.lifht.util.CommonUtil.formatAllocationDate;
import static com.lti.lifht.util.CommonUtil.parseAllocationDate;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lti.lifht.model.AllocationRaw;

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
    private String state;
    private String grade;
    @JsonIgnore
    private LocalDate assignmentStartDate;
    @JsonIgnore
    private LocalDate assignmentEndDate;
    @JsonIgnore
    private LocalDate projectStartDate;
    @JsonIgnore
    private LocalDate projectEndDate;

    public Allocation(AllocationRaw ref) {
        this.deliveryBu = ref.getDeliveryBu();
        this.deptId = ref.getDeptId();
        this.psNumber = ref.getPsNumber();
        this.deputedBu = ref.getDeputedBu();
        this.psName = ref.getPsName();
        this.startDate = parseAllocationDate.apply(ref.getStartDate());
        this.endDate = parseAllocationDate.apply(ref.getEndDate());
        this.resourceCountry = ref.getResourceCountry();
        this.state = ref.getState();
        this.grade = ref.getGrade();
        this.assignmentStartDate = parseAllocationDate.apply(ref.getAssignmentStartDate());
        this.assignmentEndDate = parseAllocationDate.apply(ref.getAssignmentEndDate());
        this.projectStartDate = parseAllocationDate.apply(ref.getProjectStartDate());
        this.projectEndDate = parseAllocationDate.apply(ref.getProjectEndDate());
    }

    @JsonProperty(value = "startDate")
    public String startDate() {
        return formatAllocationDate.apply(startDate);
    }

    @JsonProperty(value = "endDate")
    public String endDate() {
        return formatAllocationDate.apply(endDate);
    }

    @JsonProperty(value = "assignmentStartDate")
    public String assignmentStartDate() {
        return formatAllocationDate.apply(assignmentStartDate);
    }

    @JsonProperty(value = "assignmentEndDate")
    public String assignmentEndDate() {
        return formatAllocationDate.apply(assignmentEndDate);
    }

    @JsonProperty(value = "projectStartDate")
    public String projectStartDate() {
        return formatAllocationDate.apply(projectStartDate);
    }

    @JsonProperty(value = "projectEndDate")
    public String projectEndDate() {
        return formatAllocationDate.apply(projectEndDate);
    }

}
