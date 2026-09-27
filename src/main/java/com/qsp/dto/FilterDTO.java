package com.qsp.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FilterDTO {
	
	private String type;
	private LocalDate startDate;
	private LocalDate endDate;
	private String keyword;
	private String sortField;
	private String sortOrder;
}
