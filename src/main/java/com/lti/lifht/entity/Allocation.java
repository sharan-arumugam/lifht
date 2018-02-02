package com.lti.lifht.entity;

import static java.time.format.DateTimeFormatter.ofPattern;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.time.LocalDate;
import java.util.function.Function;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.lti.lifht.model.AllocationRaw;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Allocation {

	@Transient
	private final Function<String, LocalDate> parseStringDate = dateString -> isNotBlank(dateString)
			? LocalDate.parse(dateString, ofPattern("d-MMM-yy"))
			: null;

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
		this.startDate = parseStringDate.apply(ref.getStartDate());
		this.endDate = parseStringDate.apply(ref.getEndDate());
		this.resourceCountry = ref.getResourceCountry();
		this.state = ref.getState();
		this.grade = ref.getGrade();
		this.assignmentStartDate = parseStringDate.apply(ref.getAssignmentStartDate());
		this.assignmentEndDate = parseStringDate.apply(ref.getAssignmentEndDate());
		this.projectStartDate = parseStringDate.apply(ref.getProjectStartDate());
		this.projectEndDate = parseStringDate.apply(ref.getProjectEndDate());
	}

}
