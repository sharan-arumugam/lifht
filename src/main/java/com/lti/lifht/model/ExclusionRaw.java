package com.lti.lifht.model;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExclusionRaw {

	HashMap<String, String> exclusionMap = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;
		{
			put("psNumber", "PS Number");
			put("startDate", "Start Date");
		}
	};

	private String psNumber;
	private String startDate;

	public ExclusionRaw(Map<String, String> columnMap) {
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field field : fields) {
			try {
				if (exclusionMap.containsKey(field.getName())) {
					field.set(this, columnMap.get(exclusionMap.get(field.getName())));
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

}
