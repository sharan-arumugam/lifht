package com.lti.lifht.model;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AllocationRaw {

	HashMap<String, String> allocationMap = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;
		{
			put("deliveryBu", "DELIVERY BU");
			put("deptId", "DEPT ID");
			put("psNumber", "EMPLID");
			put("deputedBu", "DEPUTED BU");
			put("psName", "NAME OF EMPLOYEE");
			put("startDate", "START DATE");
			put("endDate", "END DATE");
			put("resourceCountry", "Resource Country");
			put("state", "STATE");
			put("grade", "GRADE");
			put("assignmentStartDate", "ASSIGNMENT START DATE");
			put("assignmentEndDate", "ASSIGNMENT END DATE");
			put("projectStartDate", "Project Start Date");
			put("projectEndDate", "Project End Date");
		}
	};

	private String deliveryBu;
	private String deptId;
	private String psNumber;
	private String deputedBu;
	private String psName;
	private String startDate;
	private String endDate;
	private String resourceCountry;
	private String state;
	private String grade;
	private String assignmentStartDate;
	private String assignmentEndDate;
	private String projectStartDate;
	private String projectEndDate;

	public AllocationRaw(Map<String, String> columnMap) {
		super();
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field field : fields) {
			try {
				if (allocationMap.containsKey(field.getName())) {
					field.set(this, columnMap.get(allocationMap.get(field.getName())));
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

}
