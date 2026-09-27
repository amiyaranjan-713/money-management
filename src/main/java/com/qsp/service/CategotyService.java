package com.qsp.service;



import java.util.List;


import org.springframework.stereotype.Service;

import com.qsp.dto.CategoryDTO;
import com.qsp.entity.CategoryEntity;
import com.qsp.entity.ProfileEntity;
import com.qsp.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategotyService {
	
	private final ProfileService profileService;
	private final CategoryRepository categoryRepository;
	
	
	public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
		ProfileEntity profile=profileService.getCurrentProfile();
		if (categoryRepository.existsByNameAndProfileId(categoryDTO.getName(), profile.getId())) {
			throw new RuntimeException("Category whith this name is already exist");
		}
		CategoryEntity newCategory= toEntity(categoryDTO, profile);
		newCategory = categoryRepository.save(newCategory);
		return toDTO(newCategory);
	}
	
	
	public List<CategoryDTO> getCatgoriesforCurrentUser(){
		ProfileEntity profile= profileService.getCurrentProfile();
		List<CategoryEntity> categories= categoryRepository.findByProfileId(profile.getId());
		return categories.stream().map(this::toDTO).toList();
	}
	
	
	public List<CategoryDTO> getCategoriesByTypeforCurrentUser(String type){
		ProfileEntity profile= profileService.getCurrentProfile();
		List<CategoryEntity> entities= categoryRepository.findByTypeAndProfileId(type, profile.getId());
		return entities.stream().map(this::toDTO).toList();
		
	}
	
	
	public CategoryDTO updateCategory(Long categoryId,CategoryDTO dto) {
		ProfileEntity profile= profileService.getCurrentProfile();
		CategoryEntity existingCategory= categoryRepository.findByIdAndProfileId(categoryId, profile.getId())
		.orElseThrow(()-> new RuntimeException("category not found or not accessable"));
		
		existingCategory.setName(dto.getName());
		existingCategory.setIcon(dto.getIcon());
		existingCategory.setType(dto.getType());
		existingCategory= categoryRepository.save(existingCategory);
		
		return toDTO(existingCategory);
		
	}
	
	private CategoryEntity toEntity(CategoryDTO categoryDTO,ProfileEntity profile) {
		return CategoryEntity.builder()
				.name(categoryDTO.getName())
				.icon(categoryDTO.getIcon())
				.profile(profile)
				.type(categoryDTO.getType())
				.build();
	}
	
	private CategoryDTO toDTO(CategoryEntity entity) {
		return CategoryDTO.builder()
				.id(entity.getId())
				.profileId(entity.getProfile() !=null ? entity.getProfile().getId() : null)
				.name(entity.getName())
				.icon(entity.getIcon())
				.createdAt(entity.getCreatedAt())
				.updatedAt(entity.getUpdatedAt())
				.type(entity.getType() )
				.build();
	}
}
