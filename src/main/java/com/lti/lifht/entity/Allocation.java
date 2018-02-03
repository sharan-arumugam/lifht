package com.lti.lifht.entity;

import static com.lti.lifht.util.CommonUtil.parseAllocationDate;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.lti.lifht.model.AllocationRaw;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Allocation {

    private String deliveryBu;
    private String deptId;
    @Id
    private String psNumber;
    private String deputedBu;
    private String psName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String resourceCountry;
    private String state;
    private String grade;
    private LocalDate assignmentStartDate;
    private LocalDate assignmentEndDate;
    private LocalDate projectStartDate;
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

}
