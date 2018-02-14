package com.lti.lifht.entity;

import static java.time.LocalDate.parse;
import static java.time.format.DateTimeFormatter.ofPattern;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.lti.lifht.model.ExclusionRaw;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, of = { "psNumber" })
@Entity
public class Exclusion {
	@Id
	String psNumber;
	LocalDate startDate;

	public Exclusion(ExclusionRaw raw) {
		psNumber = raw.getPsNumber();
		startDate = parse(raw.getStartDate(), ofPattern("M/d/yy"));
	}
}
