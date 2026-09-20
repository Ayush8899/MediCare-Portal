package com.healthcare.portal.config;

import com.healthcare.portal.entity.DoctorProfile;
import com.healthcare.portal.entity.Hospital;
import com.healthcare.portal.entity.Role;
import com.healthcare.portal.entity.TimeSlot;
import com.healthcare.portal.entity.User;
import com.healthcare.portal.repository.DoctorProfileRepository;
import com.healthcare.portal.repository.TimeSlotRepository;
import com.healthcare.portal.repository.UserRepository;
import com.healthcare.portal.repository.HospitalRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Configuration
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final PasswordEncoder passwordEncoder;
    private final HospitalRepository hospitalRepository;

    public DataInitializer(UserRepository userRepository,
                           DoctorProfileRepository doctorProfileRepository,
                           TimeSlotRepository timeSlotRepository,
                           PasswordEncoder passwordEncoder, HospitalRepository hospitalRepository) {
        this.userRepository = userRepository;
        this.doctorProfileRepository = doctorProfileRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.passwordEncoder = passwordEncoder;
        this.hospitalRepository = hospitalRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            System.out.println("Initializing demo data for Healthcare Portal...");

            Hospital hospital = hospitalRepository.findByNameIgnoreCase("MediCare General Hospital").orElseGet(() -> hospitalRepository.save(new com.healthcare.portal.entity.Hospital("MediCare General Hospital","Main Campus","New Delhi","+91-0000000000","admin@healthcare.com")));

            // 1. Admin
            User admin = new User(
                    "admin@healthcare.com",
                    passwordEncoder.encode("Admin@123"),
                    "Dr. System Admin",
                    "+91 98765 00001",
                    Role.ROLE_ADMIN
            );
            userRepository.save(admin);

            // 2. Cardiologist
            User docUser1 = new User(
                    "dr.sharma@healthcare.com",
                    passwordEncoder.encode("doctor123"),
                    "Dr. Rajesh Sharma",
                    "+91 98765 00002",
                    Role.ROLE_DOCTOR
            );
            userRepository.save(docUser1);
            DoctorProfile doc1 = new DoctorProfile(
                    docUser1,
                    "Cardiology",
                    "MBBS, MD, DM (Cardiology)",
                    12,
                    BigDecimal.valueOf(800.00),
                    "Senior Interventional Cardiologist with extensive experience in coronary angioplasty, heart failure, and preventive cardiology.",
                    "New Delhi"
            );
            doc1.setHospital(hospital); doctorProfileRepository.save(doc1);

            // 3. Dermatologist
            User docUser2 = new User(
                    "dr.priya@healthcare.com",
                    passwordEncoder.encode("doctor123"),
                    "Dr. Priya Patel",
                    "+91 98765 00003",
                    Role.ROLE_DOCTOR
            );
            userRepository.save(docUser2);
            DoctorProfile doc2 = new DoctorProfile(
                    docUser2,
                    "Dermatology",
                    "MBBS, MD (Dermatology)",
                    8,
                    BigDecimal.valueOf(600.00),
                    "Specialist in clinical dermatology, acne therapies, pediatric skin care, and anti-aging treatments.",
                    "Mumbai"
            );
            doc2.setHospital(hospital); doctorProfileRepository.save(doc2);

            // 4. Pediatrician
            User docUser3 = new User(
                    "dr.anand@healthcare.com",
                    passwordEncoder.encode("doctor123"),
                    "Dr. Anand Verma",
                    "+91 98765 00004",
                    Role.ROLE_DOCTOR
            );
            userRepository.save(docUser3);
            DoctorProfile doc3 = new DoctorProfile(
                    docUser3,
                    "Pediatrics",
                    "MBBS, DCH, DNB (Pediatrics)",
                    10,
                    BigDecimal.valueOf(500.00),
                    "Compassionate child specialist focusing on newborn nutrition, developmental milestones, and routine vaccinations.",
                    "Bangalore"
            );
            doc3.setHospital(hospital); doctorProfileRepository.save(doc3);

            // 5. Demo Patient
            User patient = new User(
                    "patient@demo.com",
                    passwordEncoder.encode("patient123"),
                    "Rahul Gupta",
                    "+91 98765 11111",
                    Role.ROLE_PATIENT
            );
            userRepository.save(patient);

            // 6. Generate upcoming slots for tomorrow
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            List<DoctorProfile> doctors = List.of(doc1, doc2, doc3);
            for (DoctorProfile doc : doctors) {
                LocalTime[] times = {
                    LocalTime.of(9, 0), LocalTime.of(9, 30),
                    LocalTime.of(10, 0), LocalTime.of(10, 30),
                    LocalTime.of(11, 0), LocalTime.of(14, 0),
                    LocalTime.of(14, 30), LocalTime.of(15, 0)
                };
                for (LocalTime t : times) {
                    timeSlotRepository.save(new TimeSlot(doc, tomorrow, t, t.plusMinutes(30)));
                }
            }

            System.out.println("Demo data initialized successfully!");
            System.out.println("  Admin:   admin@healthcare.com / Admin@123");
            System.out.println("  Doctor:  dr.sharma@healthcare.com / doctor123");
            System.out.println("  Patient: patient@demo.com / patient123");
        }
    }
}
