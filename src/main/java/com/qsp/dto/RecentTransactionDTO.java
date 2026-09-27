package com.qsp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecentTransactionDTO {
	
	private Long id;
	private Long profileId;
	private String icon;
	private String name;
	private BigDecimal amount;
	private LocalDate date;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private String type;
}
