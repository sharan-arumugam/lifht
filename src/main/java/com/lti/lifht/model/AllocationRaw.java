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
			put("deliveryBu", "Base BU");
			put("deptId", "Base Dept");
			put("psNumber", "PS Number");
			put("deputedBu", "Deputed BU");
			put("psName", "Name");
			put("startDate", "Start Date");
			put("endDate", "End Date");
			put("resourceCountry","Location");
			put("resourceState", "Location");
			put("grade", "Grade");
			put("projectId", "Project Id");
			put("projectName", "Project Name");
			put("projectCategory", "Project Category");
			put("resourceLocation", "Onsite/Offshore Allocation");
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
	private String resourceState;
	private String grade;
	private String projectId;
	private String projectName;
	private String projectCategory;
	private String resourceLocation;

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
