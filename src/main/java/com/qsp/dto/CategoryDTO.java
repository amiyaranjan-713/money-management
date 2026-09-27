package com.qsp.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDTO {
	
	private Long id;
	private Long profileId;
	private String name;
	private String icon;
	private String type;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
	
}
