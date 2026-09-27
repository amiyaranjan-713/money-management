package com.qsp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qsp.dto.CategoryDTO;
import com.qsp.service.CategotyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {
	
	private final CategotyService categotyService;
	@PostMapping
	public ResponseEntity<CategoryDTO> saveCategory(@RequestBody CategoryDTO categoryDTO){
		CategoryDTO saveCategory= categotyService.saveCategory(categoryDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(saveCategory);
	}
	
	@GetMapping
	public ResponseEntity<List<CategoryDTO>> getCategories(){
		List<CategoryDTO> categoris= categotyService.getCatgoriesforCurrentUser();
		return ResponseEntity.ok(categoris);
	}
	
	@GetMapping("/{type}")
	public ResponseEntity<List<CategoryDTO> > getcateCategoryByTypeForCurrentUser(@PathVariable String type){
		List<CategoryDTO> list= categotyService.getCategoriesByTypeforCurrentUser(type);
		return  ResponseEntity.ok(list);
		
	}
	
	@PutMapping("/{categoryId}")
	public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long categoryId, @RequestBody CategoryDTO categoryDTO){
		CategoryDTO updatedcategory = categotyService.updateCategory(categoryId, categoryDTO);
		return ResponseEntity.ok(updatedcategory);
	}
	
}
