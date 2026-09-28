package com.qsp.controller;



import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.qsp.dto.AuthDTO;
import com.qsp.dto.ProfileDTO;
import com.qsp.service.ProfileService;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
public class ProfileController {

		
		private final ProfileService profileService;

		
		
		@PostMapping("/register")
		public ResponseEntity<ProfileDTO> registerProfile(@RequestBody ProfileDTO profileDTO){
			
			ProfileDTO registeredProfile = profileService.registerProfile(profileDTO);
			return ResponseEntity.status(HttpStatus.CREATED).body(registeredProfile);
		}
		
		@GetMapping("/activate")
		public ResponseEntity<String> activateProfile(@RequestParam String token){
			boolean isActivated= profileService.activateProfile(token);
			
			if (isActivated) {
				return ResponseEntity.ok("Account activated successfully. You can now login.");
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid activation token.");

			}
		}
		
		@PostMapping("/login")
		public ResponseEntity<Map<String, Object>> login(@RequestBody AuthDTO authDTO){
			try {
				if(!profileService.isActiveAccount(authDTO.getEmail())) {
					
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
							Map.of("Message", "Account is not active. Plese activate your account first.")
							);
				}
				Map<String, Object> response=profileService.authenticateAndGenerateToken(authDTO);
				return ResponseEntity.ok(response);
			} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
					"message", e.getMessage()));
			}
		}
		
		
		@GetMapping("/test")
		public String test() {
			return "Test Successfull";
		}
}
