package com.healthcare.portal.service;

import com.healthcare.portal.dto.AuthRequest;
import com.healthcare.portal.dto.AuthResponse;
import com.healthcare.portal.dto.RegisterRequest;
import com.healthcare.portal.entity.DoctorProfile;
import com.healthcare.portal.entity.Role;
import com.healthcare.portal.entity.User;
import com.healthcare.portal.exception.BadRequestException;
import com.healthcare.portal.exception.ConflictException;
import com.healthcare.portal.repository.DoctorProfileRepository;
import com.healthcare.portal.repository.UserRepository;
import com.healthcare.portal.security.CustomUserDetails;
import com.healthcare.portal.security.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       DoctorProfileRepository doctorProfileRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.doctorProfileRepository = doctorProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new BadRequestException("User not found"));

        Long doctorProfileId = null;
        if (user.getRole() == Role.ROLE_DOCTOR) {
            Optional<DoctorProfile> profile = doctorProfileRepository.findByUserId(user.getId());
            if (profile.isPresent()) {
                doctorProfileId = profile.get().getId();
            }
        }

        return new AuthResponse(
                jwt,
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().name(),
                doctorProfileId
        );
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email is already registered: " + request.getEmail());
        }

        Role assignedRole = request.getRole() != null ? request.getRole() : Role.ROLE_PATIENT;
        // Restrict direct ADMIN registration via open endpoint
        if (assignedRole == Role.ROLE_ADMIN) {
            assignedRole = Role.ROLE_PATIENT;
        }

        User user = new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getFullName(),
                request.getPhone(),
                assignedRole
        );

        User savedUser = userRepository.save(user);

        Long doctorProfileId = null;
        if (assignedRole == Role.ROLE_DOCTOR) {
            DoctorProfile profile = new DoctorProfile(
                    savedUser,
                    request.getSpecialization() != null ? request.getSpecialization() : "General Physician",
                    request.getQualification() != null ? request.getQualification() : "MBBS",
                    request.getExperienceYears() != null ? request.getExperienceYears() : 2,
                    request.getConsultationFee() != null ? request.getConsultationFee() : BigDecimal.valueOf(500.00),
                    request.getBio() != null ? request.getBio() : "Experienced healthcare specialist.",
                    request.getCity() != null ? request.getCity() : "New Delhi"
            );
            DoctorProfile savedProfile = doctorProfileRepository.save(profile);
            doctorProfileId = savedProfile.getId();
        }

        // Authenticate new user automatically
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        String jwt = jwtUtils.generateJwtToken(authentication);

        return new AuthResponse(
                jwt,
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedUser.getRole().name(),
                doctorProfileId
        );
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found with email: " + email));
    }
}
