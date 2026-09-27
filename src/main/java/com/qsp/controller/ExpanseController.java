package com.qsp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qsp.dto.ExpenseDTO;
import com.qsp.service.ExpanseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expenses")
public class ExpanseController {
	
	private final ExpanseService expanseService;
	
	@PostMapping
	public ResponseEntity<ExpenseDTO> addExpanse(@RequestBody ExpenseDTO dto){
		ExpenseDTO saved= expanseService.addExpanse(dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(saved);
	}
	
	@GetMapping
	public ResponseEntity<List<ExpenseDTO>> getExpenses(){
		List<ExpenseDTO> expenses = expanseService.getCurrentMonthExpenseForCurrentUser();
		return ResponseEntity.ok(expenses);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteExpense(@PathVariable Long id){
		expanseService.deleteExpense(id);
		return ResponseEntity.noContent().build();
	}
	
	
}
