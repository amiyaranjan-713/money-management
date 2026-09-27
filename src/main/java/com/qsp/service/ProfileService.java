package com.qsp.service;


import java.util.Map;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.qsp.dto.AuthDTO;
import com.qsp.dto.ProfileDTO;
import com.qsp.entity.ProfileEntity;
import com.qsp.repository.ProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {
	
	private final ProfileRepository profileRepository;
	private final EmailService emailService;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	
	public ProfileDTO registerProfile(ProfileDTO profileDTO) {
		
	ProfileEntity newProfileEntity=	toEntity(profileDTO);
	
	newProfileEntity.setActivationToken(UUID.randomUUID().toString());
	newProfileEntity= profileRepository.save(newProfileEntity);
	
	String activationLink= "http://localhost:8081/api/v1.0/activate?token="+newProfileEntity.getActivationToken();
	String subject= "Activate your moneyManagement account";
	String body="Click on the following link to activate your account: "+ activationLink;
	
	emailService.sendEmail(newProfileEntity.getEmail(), subject, body);	
	
	return toDTO(newProfileEntity);
		
	}
	public ProfileEntity toEntity(ProfileDTO profileDTO) {
		return ProfileEntity.builder()
				.id(profileDTO.getId())
				.fullName(profileDTO.getFullName())
				.email(profileDTO.getEmail())
				.password(passwordEncoder.encode(profileDTO.getPassword()))
				.profileImageUrl(profileDTO.getProfileImageUrl())
				.createdAt(profileDTO.getCreatedAt())
				.updatedAt(profileDTO.getUpdatedAt())
				.build();
	}
	
	public ProfileDTO toDTO(ProfileEntity profileEntity) {

	    return ProfileDTO.builder()
	            .id(profileEntity.getId())
	            .fullName(profileEntity.getFullName())
	            .email(profileEntity.getEmail())
	            .profileImageUrl(profileEntity.getProfileImageUrl())
	            .createdAt(profileEntity.getCreatedAt())
	            .updatedAt(profileEntity.getUpdatedAt())
	            .build();
	}
	
	public boolean activateProfile(String activation) {
		return profileRepository.findByActivationToken(activation)
				.map( profile->{
					profile.setIsActive(true);
					profileRepository.save(profile);
					return true;
				})
				.orElse(false);
	}
	
	public boolean isActiveAccount(String email) {
		return profileRepository.findByEmail(email)
				.map(ProfileEntity::getIsActive )
				.orElse(false);
	}
	
	public ProfileEntity getCurrentProfile() {
		Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
		return profileRepository.findByEmail(authentication.getName())
				.orElseThrow(()-> new UsernameNotFoundException("Profile not found with this email: "+ authentication.getName()));
	}
	
	public ProfileDTO getPublicprofile(String email) {
		
		ProfileEntity currentUser=null;
		if(email==null) {
		currentUser= getCurrentProfile();	
		}else {
			currentUser= profileRepository.findByEmail(email)
					.orElseThrow(()-> new UsernameNotFoundException("Profile not found with email: " +email));
		}
		
		return ProfileDTO.builder()
				.id(currentUser.getId())
				.fullName(currentUser.getFullName())
				.email(currentUser.getEmail())
				.profileImageUrl(currentUser.getProfileImageUrl())
				.createdAt(currentUser.getCreatedAt())
				.updatedAt(currentUser.getUpdatedAt())
				.build();
	}
	
	public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO){
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword()));
			ProfileEntity profile = profileRepository.findByEmail(authDTO.getEmail())
			        .orElseThrow(() ->
			                new UsernameNotFoundException(
			                        "Profile not found with email: " + authDTO.getEmail()
			                )
			        );

			String token = jwtService.generateToken(profile);
			return Map.of(
					"token", token,
					"user", getPublicprofile(authDTO.getEmail())
					);
		} catch (Exception e) {
			throw new RuntimeException("invalid mail or password");
		}
	}
}
