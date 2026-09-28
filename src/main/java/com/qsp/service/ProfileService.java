package com.qsp.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.activation.url}")
    private String activationUrl;



    public ProfileDTO registerProfile(ProfileDTO profileDTO) {

        if (profileRepository.findByEmail(profileDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        ProfileEntity newProfileEntity = toEntity(profileDTO);

      
        String activationToken = UUID.randomUUID().toString();

        newProfileEntity.setActivationToken(activationToken);

        
        newProfileEntity.setIsActive(false);

       
        newProfileEntity = profileRepository.save(newProfileEntity);

       
        String activationLink =
                activationUrl + "/activate?token=" + activationToken;

     
        String subject = "Activate your Money Management account";

        String body = """
                <h2>Welcome to Money Management</h2>

                <p>Thank you for registering.</p>

                <p>Please click the link below to activate your account:</p>

                <p>
                    <a href="%s">Activate Account</a>
                </p>

                <p>If you did not create this account, please ignore this email.</p>
                """.formatted(activationLink);

      
        emailService.sendEmail(
                newProfileEntity.getEmail(),
                subject,
                body
        );

        return toDTO(newProfileEntity);
    }


    // =========================
    // DTO -> ENTITY
    // =========================

    public ProfileEntity toEntity(ProfileDTO profileDTO) {

        return ProfileEntity.builder()
                .id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .email(profileDTO.getEmail())
                .password(passwordEncoder.encode(profileDTO.getPassword()))
                .profileImageUrl(profileDTO.getProfileImageUrl())
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .isActive(false)
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



    public boolean activateProfile(String activationToken) {

        return profileRepository.findByActivationToken(activationToken)
                .map(profile -> {

                    // Already activated
                    if (Boolean.TRUE.equals(profile.getIsActive())) {
                        return true;
                    }

                    // Activate account
                    profile.setIsActive(true);

                    // Token can no longer be reused
                    profile.setActivationToken(null);

                    profileRepository.save(profile);

                    return true;
                })
                .orElse(false);
    }


    // =========================
    // CHECK ACTIVE ACCOUNT
    // =========================

    public boolean isActiveAccount(String email) {

        return profileRepository.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false);
    }



    public ProfileEntity getCurrentProfile() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return profileRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Profile not found with this email: "
                                + authentication.getName()
                        )
                );
    }


    public ProfileDTO getPublicprofile(String email) {

        ProfileEntity currentUser;

        if (email == null || email.isBlank()) {

            currentUser = getCurrentProfile();

        } else {

            currentUser = profileRepository.findByEmail(email)
                    .orElseThrow(() ->
                            new UsernameNotFoundException(
                                    "Profile not found with email: " + email
                            )
                    );
        }

        return toDTO(currentUser);
    }


    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authDTO.getEmail(),
                            authDTO.getPassword()
                    )
            );

            ProfileEntity profile = profileRepository
                    .findByEmail(authDTO.getEmail())
                    .orElseThrow(() ->
                            new UsernameNotFoundException(
                                    "Profile not found with email: "
                                    + authDTO.getEmail()
                            )
                    );

            
            if (!Boolean.TRUE.equals(profile.getIsActive())) {

                throw new RuntimeException(
                        "Please activate your account before login"
                );
            }

            // 4. Generate JWT
            String token = jwtService.generateToken(profile);

           
            return Map.of(
                    "token", token,
                    "user", toDTO(profile)
            );

        } catch (RuntimeException e) {

            throw e;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Invalid email or password"
            );
        }
    }
}