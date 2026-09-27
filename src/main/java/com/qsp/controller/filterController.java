package com.qsp.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qsp.dto.ExpenseDTO;
import com.qsp.dto.FilterDTO;
import com.qsp.dto.IncomeDTO;
import com.qsp.service.ExpanseService;
import com.qsp.service.IncomeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/filter")
@RequiredArgsConstructor
public class filterController {

	private final ExpanseService expanseService;
	private final IncomeService incomeService;
	
	@PostMapping
	public ResponseEntity<?> filterTransactions(@RequestBody FilterDTO filter){
		LocalDate startDate= filter.getStartDate() !=null ? filter.getStartDate() : LocalDate.MIN;
		LocalDate endDate= filter.getEndDate() != null ? filter.getEndDate() : LocalDate.now();
		String keyword= filter.getKeyword() != null ? filter.getKeyword() : "";
		String sortField= filter.getSortField() != null ? filter.getSortField() : "date";
		Sort.Direction direction= "desc".equalsIgnoreCase(filter.getSortOrder()) ? Sort.Direction.DESC : Sort.Direction.ASC;
		Sort sort=Sort.by(direction, sortField);
		if("income".equals(filter.getType())) {
			List<IncomeDTO> incomes = incomeService.filterIncome(startDate, endDate, keyword, sort);
			return ResponseEntity.ok(incomes);
		}else if ("expense".equalsIgnoreCase(filter.getType())) {
			List<ExpenseDTO> expense= expanseService.filterExpenses(startDate, endDate, keyword, sort);
			return ResponseEntity.ok(expense);
		}else {
			return ResponseEntity.badRequest().body("Invalid typpe. Must be 'Income' or 'expense'");
		}
	}
}
