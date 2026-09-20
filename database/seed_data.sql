-- ========================================================================
-- Healthcare Appointment & Patient Management Portal - Demo Seed Data
-- ========================================================================

USE healthcare_db;

-- 1. Insert Demo Users (BCrypt hash for 'password123' is $2a$10$w6B96DqJzFj7m7u8c6uH3.yTz6i4UjQkYtCjKjXb/1uWwB7tqW6nK or generated via Spring Boot)
-- Passwords will be automatically verified or initialized by Spring Boot DataInitializer as well.
INSERT INTO users (id, email, password, full_name, phone, role) VALUES
(1, 'admin@healthcare.com', '$2a$10$2vD0h4B8Q.j/kK0m9o1hEuNqE9mQo2tB7s6ZtQ7zF5rP0wY9f1h5y', 'Dr. System Admin', '+91 98765 00001', 'ROLE_ADMIN'),
(2, 'dr.sharma@healthcare.com', '$2a$10$2vD0h4B8Q.j/kK0m9o1hEuNqE9mQo2tB7s6ZtQ7zF5rP0wY9f1h5y', 'Dr. Rajesh Sharma', '+91 98765 00002', 'ROLE_DOCTOR'),
(3, 'dr.priya@healthcare.com', '$2a$10$2vD0h4B8Q.j/kK0m9o1hEuNqE9mQo2tB7s6ZtQ7zF5rP0wY9f1h5y', 'Dr. Priya Patel', '+91 98765 00003', 'ROLE_DOCTOR'),
(4, 'dr.anand@healthcare.com', '$2a$10$2vD0h4B8Q.j/kK0m9o1hEuNqE9mQo2tB7s6ZtQ7zF5rP0wY9f1h5y', 'Dr. Anand Verma', '+91 98765 00004', 'ROLE_DOCTOR'),
(5, 'patient@demo.com', '$2a$10$2vD0h4B8Q.j/kK0m9o1hEuNqE9mQo2tB7s6ZtQ7zF5rP0wY9f1h5y', 'Rahul Gupta', '+91 98765 11111', 'ROLE_PATIENT')
ON DUPLICATE KEY UPDATE email=email;

-- 2. Insert Doctor Profiles
INSERT INTO doctor_profiles (id, user_id, specialization, qualification, experience_years, consultation_fee, bio, city) VALUES
(1, 2, 'Cardiology', 'MBBS, MD, DM (Cardiology)', 12, 800.00, 'Senior Interventional Cardiologist specializing in heart failure, coronary angiography, and hypertension management.', 'New Delhi'),
(2, 3, 'Dermatology', 'MBBS, MD (Dermatology)', 8, 600.00, 'Expert in cosmetic dermatology, acne treatment, pediatric skin disorders, and laser therapies.', 'Mumbai'),
(3, 4, 'Pediatrics', 'MBBS, DCH, DNB (Pediatrics)', 10, 500.00, 'Dedicated pediatrician with special focus on newborn care, vaccinations, and adolescent medicine.', 'Bangalore')
ON DUPLICATE KEY UPDATE specialization=specialization;

-- 3. Insert Upcoming Time Slots (for the next few days)
INSERT INTO time_slots (doctor_id, slot_date, start_time, end_time, is_booked) VALUES
(1, CURRENT_DATE + INTERVAL 1 DAY, '09:00:00', '09:30:00', FALSE),
(1, CURRENT_DATE + INTERVAL 1 DAY, '09:30:00', '10:00:00', FALSE),
(1, CURRENT_DATE + INTERVAL 1 DAY, '10:00:00', '10:30:00', FALSE),
(1, CURRENT_DATE + INTERVAL 2 DAY, '11:00:00', '11:30:00', FALSE),
(2, CURRENT_DATE + INTERVAL 1 DAY, '14:00:00', '14:30:00', FALSE),
(2, CURRENT_DATE + INTERVAL 1 DAY, '14:30:00', '15:00:00', FALSE),
(3, CURRENT_DATE + INTERVAL 1 DAY, '16:00:00', '16:30:00', FALSE),
(3, CURRENT_DATE + INTERVAL 1 DAY, '16:30:00', '17:00:00', FALSE);
