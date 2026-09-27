package com.qsp.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qsp.dto.ExpenseDTO;
import com.qsp.entity.CategoryEntity;
import com.qsp.entity.ExpenseEntity;
import com.qsp.entity.ProfileEntity;
import com.qsp.repository.CategoryRepository;
import com.qsp.repository.ExpenseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpanseService {
	
	
	private final CategoryRepository categoryRepository;
	private final ExpenseRepository expenseRepository;
	private final ProfileService profileService;
	
	public ExpenseDTO addExpanse(ExpenseDTO dto) {
		ProfileEntity profile= profileService.getCurrentProfile();
		CategoryEntity category= categoryRepository.findById(dto.getCategoryId()).
				orElseThrow(()->new RuntimeException("Category not found"));
		ExpenseEntity newExpense = toEntity(dto, profile, category);
		newExpense= expenseRepository.save(newExpense);
		return toDto(newExpense);
	}
	
	
	public List<ExpenseDTO> getCurrentMonthExpenseForCurrentUser(){
		ProfileEntity profile= profileService.getCurrentProfile();
		LocalDate now= LocalDate.now();
		LocalDate startDate= now.withDayOfMonth(1);
		LocalDate endDate= now.withDayOfMonth(now.lengthOfMonth());
		List<ExpenseEntity> list= expenseRepository.findByProfileIdAndDateBetween(profile.getId(), startDate, endDate);
		return list.stream().map(this::toDto).toList();
	}
	
	public void deleteExpense(Long expenseId) {
		ProfileEntity profile=profileService.getCurrentProfile();
		ExpenseEntity entity= expenseRepository.findById(expenseId)
				.orElseThrow(()-> new RuntimeException("Expense not found"));
		if(!entity.getProfile().getId().equals(profile.getId())) {
			throw new RuntimeException("Unauthorized to delete this expence");
		}
		expenseRepository.delete(entity);
		
	}
	
	public List<ExpenseDTO> getList5expenseForCurrentuser(){
		ProfileEntity profile= profileService.getCurrentProfile();
		List<ExpenseEntity> list=expenseRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
		return list.stream().map(this::toDto).toList();
		
	}
	
	
	public BigDecimal getTotalExpenseForCurrentUser() {
		ProfileEntity profile=profileService.getCurrentProfile();
		BigDecimal total= expenseRepository.findTotalExpenseByProfileId(profile.getId());
		return total != null? total:BigDecimal.ZERO;
		
	}
	
	public List<ExpenseDTO> filterExpenses(LocalDate startdate,
											LocalDate endDate,
											String keyword,
											Sort sort){
		
		ProfileEntity profile =profileService.getCurrentProfile();
		List<ExpenseEntity> list = expenseRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startdate, endDate, keyword, sort);
		return list.stream().map(this :: toDto).toList();
	}
	
	@Transactional(readOnly = true)
	public List<ExpenseDTO> getExpensesFromUserOnDate(Long profileId, LocalDate date){
		List<ExpenseEntity> list = expenseRepository.findByProfileIdAndDate(profileId, date);
		return list.stream().map(this::toDto).toList();
	}
	
	
	private ExpenseEntity toEntity(ExpenseDTO expenseDTO, ProfileEntity profile, CategoryEntity category) {
		return ExpenseEntity.builder()
				.name(expenseDTO.getName())
				.icon(expenseDTO.getIcon())
				.amount(expenseDTO.getAmount())
				.date(expenseDTO.getDate())
				.profile(profile)
				.category(category)
				.build();
	}
	
	
	private ExpenseDTO toDto(ExpenseEntity entity) {
		return ExpenseDTO.builder()
				.id(entity.getId())
				.name(entity.getName())
				.icon(entity.getIcon())
				.categoryId(entity.getCategory() !=null ? entity.getCategory().getId() : null)
				.categoryName(entity.getCategory() != null ? entity.getCategory().getName() : "N/A")
				.amount(entity.getAmount())
				.date(entity.getDate())
				.createdAt(entity.getCreatedAt())
				.updatedAt(entity.getUpdatedAt())
				.build();
				
	}
}
