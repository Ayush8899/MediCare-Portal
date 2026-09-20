package com.healthcare.portal.service;

import com.healthcare.portal.dto.DashboardStatsDto;
import com.healthcare.portal.dto.DoctorDto;
import com.healthcare.portal.entity.AppointmentStatus;
import com.healthcare.portal.entity.Role;
import com.healthcare.portal.entity.User;
import com.healthcare.portal.entity.DoctorProfile;
import com.healthcare.portal.repository.AppointmentRepository;
import com.healthcare.portal.repository.DoctorProfileRepository;
import com.healthcare.portal.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final AppointmentRepository appointmentRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final com.healthcare.portal.repository.HospitalRepository hospitalRepository;

    public AdminService(
            UserRepository userRepository,
            DoctorProfileRepository doctorProfileRepository,
            AppointmentRepository appointmentRepository,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder,
            EmailService emailService,
            com.healthcare.portal.repository.HospitalRepository hospitalRepository) {

        this.userRepository = userRepository;
        this.doctorProfileRepository = doctorProfileRepository;
        this.appointmentRepository = appointmentRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.hospitalRepository = hospitalRepository;
    }

    // =========================
    // ADMIN DASHBOARD STATS
    // =========================
    public DashboardStatsDto getDashboardStats() {

        long totalPatients =
                userRepository.countByRole(Role.ROLE_PATIENT);

        // IMPORTANT:
        // Count only ACTIVE doctors
        long totalDoctors =
                userRepository.countByRoleAndActive(
                        Role.ROLE_DOCTOR,
                        true
                );

        long totalAppointments =
                appointmentRepository.count();

        long completed =
                appointmentRepository.countByStatus(
                        AppointmentStatus.COMPLETED
                );

        long pending =
                appointmentRepository.countByStatus(
                        AppointmentStatus.PENDING
                );

        long cancelled =
                appointmentRepository.countByStatus(
                        AppointmentStatus.CANCELLED
                );

        BigDecimal revenue =
                appointmentRepository.calculateTotalCompletedRevenue();

        return new DashboardStatsDto(
                totalPatients,
                totalDoctors,
                totalAppointments,
                completed,
                pending,
                cancelled,
                revenue != null ? revenue : BigDecimal.ZERO
        );
    }

    // =========================
    // GET ALL DOCTORS
    // =========================
    public List<DoctorDto> getAllDoctors() {

        return doctorProfileRepository.findAll()
                .stream()
                .map(DoctorDto::fromEntity)
                .collect(Collectors.toList());
    }

    // =========================
    // CREATE DOCTOR
    // =========================
    @Transactional
    public Map<String, Object> createDoctor(
            com.healthcare.portal.dto.DoctorCreateRequest r) {

        if (userRepository.existsByEmail(r.getEmail())) {
            throw new com.healthcare.portal.exception.ConflictException(
                    "Email is already registered"
            );
        }

        String chars =
                "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789@#";

        SecureRandom random = new SecureRandom();

        StringBuilder pwd = new StringBuilder();

        for (int i = 0; i < 12; i++) {
            pwd.append(
                    chars.charAt(
                            random.nextInt(chars.length())
                    )
            );
        }

        User u = new User(
                r.getEmail(),
                passwordEncoder.encode(pwd.toString()),
                r.getFullName(),
                r.getPhone(),
                Role.ROLE_DOCTOR
        );

        u.setForcePasswordChange(true);
        u.setEmailVerified(false);
        u.setActive(true);

        u = userRepository.save(u);

        com.healthcare.portal.entity.Hospital hospital =
                hospitalRepository
                        .findByNameIgnoreCase(
                                "MediCare General Hospital"
                        )
                        .orElseGet(() ->
                                hospitalRepository.save(
                                        new com.healthcare.portal.entity.Hospital(
                                                "MediCare General Hospital",
                                                "Hospital Main Campus",
                                                r.getCity() == null
                                                        ? "New Delhi"
                                                        : r.getCity(),
                                                "+91-0000000000",
                                                null
                                        )
                                )
                        );

        DoctorProfile d = new DoctorProfile(
                u,
                r.getSpecialization(),
                r.getQualification(),
                r.getExperienceYears() == null
                        ? 0
                        : r.getExperienceYears(),
                r.getConsultationFee() == null
                        ? BigDecimal.valueOf(500)
                        : r.getConsultationFee(),
                r.getBio(),
                r.getCity() == null
                        ? "New Delhi"
                        : r.getCity()
        );

        d.setHospital(hospital);

        doctorProfileRepository.save(d);

        emailService.send(
                u.getEmail(),
                "Your Healthcare Doctor Account",
                "Your doctor account has been created.\n" +
                        "Email: " + u.getEmail() + "\n" +
                        "Temporary password: " + pwd + "\n" +
                        "Please login and change your password."
        );

        return Map.of(
                "doctorId", d.getId(),
                "email", u.getEmail(),
                "message", "Doctor created and credentials emailed"
        );
    }

    // =========================
    // UPDATE DOCTOR
    // =========================
    @Transactional
    public Map<String, Object> updateDoctor(
            Long doctorId,
            com.healthcare.portal.dto.DoctorCreateRequest r) {

        DoctorProfile d =
                doctorProfileRepository
                        .findById(doctorId)
                        .orElseThrow(() ->
                                new com.healthcare.portal.exception.ResourceNotFoundException(
                                        "Doctor not found"
                                )
                        );

        User u = d.getUser();

        u.setFullName(r.getFullName());
        u.setPhone(r.getPhone());

        userRepository.save(u);

        d.setSpecialization(r.getSpecialization());
        d.setQualification(r.getQualification());

        d.setExperienceYears(
                r.getExperienceYears() == null
                        ? 0
                        : r.getExperienceYears()
        );

        d.setConsultationFee(
                r.getConsultationFee() == null
                        ? BigDecimal.valueOf(500)
                        : r.getConsultationFee()
        );

        d.setBio(r.getBio());
        d.setCity(r.getCity());

        doctorProfileRepository.save(d);

        return Map.of(
                "doctorId", doctorId,
                "message", "Doctor profile updated"
        );
    }

    // =========================
    // ACTIVATE / DEACTIVATE
    // =========================
    @Transactional
    public Map<String, Object> setDoctorActive(
            Long doctorId,
            boolean active) {

        DoctorProfile d =
                doctorProfileRepository
                        .findById(doctorId)
                        .orElseThrow(() ->
                                new com.healthcare.portal.exception.ResourceNotFoundException(
                                        "Doctor not found"
                                )
                        );

        User u = d.getUser();

        u.setActive(active);

        userRepository.save(u);

        return Map.of(
                "doctorId", doctorId,
                "active", active
        );
    }

    // =========================
    // ALL USERS
    // =========================
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}