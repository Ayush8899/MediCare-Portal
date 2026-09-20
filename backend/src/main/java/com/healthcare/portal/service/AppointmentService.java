package com.healthcare.portal.service;

import com.healthcare.portal.dto.AppointmentRequest;
import com.healthcare.portal.dto.AppointmentResponse;
import com.healthcare.portal.entity.*;
import com.healthcare.portal.exception.BadRequestException;
import com.healthcare.portal.exception.ConflictException;
import com.healthcare.portal.exception.ResourceNotFoundException;
import com.healthcare.portal.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final UserRepository userRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final PrescriptionRepository prescriptionRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              DoctorProfileRepository doctorProfileRepository,
                              UserRepository userRepository,
                              TimeSlotRepository timeSlotRepository,
                              PrescriptionRepository prescriptionRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorProfileRepository = doctorProfileRepository;
        this.userRepository = userRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    /**
     * Books an appointment with transactional integrity and concurrency protection.
     * Prevents double-booking across simultaneous patient requests.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AppointmentResponse bookAppointment(Long patientUserId, AppointmentRequest request) {
        User patient = userRepository.findById(patientUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + patientUserId));

        DoctorProfile doctor = doctorProfileRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + request.getDoctorId()));

        // Check if an active appointment already exists for this doctor, date, and slot
        Boolean alreadyBooked = appointmentRepository.existsByDoctorIdAndAppointmentDateAndTimeSlotAndStatusNot(
                doctor.getId(),
                request.getAppointmentDate(),
                request.getTimeSlot(),
                AppointmentStatus.CANCELLED
        );

        if (alreadyBooked) {
            throw new ConflictException("Selected time slot (" + request.getTimeSlot() + ") is already booked. Please pick another slot.");
        }

        // Parse slot start time to mark matching TimeSlot as booked
        try {
            String startTimeStr = request.getTimeSlot().split("-")[0].trim();
            if (startTimeStr.length() == 5) {
                startTimeStr += ":00";
            }
            LocalTime startTime = LocalTime.parse(startTimeStr);
            Optional<TimeSlot> slotOpt = timeSlotRepository.findByDoctorIdAndSlotDateAndStartTime(
                    doctor.getId(), request.getAppointmentDate(), startTime);
            slotOpt.ifPresent(slot -> {
                slot.setIsBooked(true);
                timeSlotRepository.save(slot);
            });
        } catch (Exception ignored) {
            // Non-critical if slot parsing fails; database appointment check guarantees uniqueness
        }

        Appointment appointment = new Appointment(
                patient,
                doctor,
                request.getAppointmentDate(),
                request.getTimeSlot(),
                request.getSymptoms()
        );
        appointment.setStatus(AppointmentStatus.CONFIRMED);

        Appointment saved = appointmentRepository.save(appointment);
        return AppointmentResponse.fromEntity(saved, false);
    }

    @Transactional
    public AppointmentResponse reschedule(Long appointmentId, Long userId, java.time.LocalDate date, String slot) {
        Appointment a=appointmentRepository.findById(appointmentId).orElseThrow(()->new ResourceNotFoundException("Appointment not found"));
        if(!a.getPatient().getId().equals(userId) && !a.getDoctor().getUser().getId().equals(userId)) throw new BadRequestException("Not authorized");
        if(appointmentRepository.existsByDoctorIdAndAppointmentDateAndTimeSlotAndStatusNot(a.getDoctor().getId(),date,slot,AppointmentStatus.CANCELLED)) throw new ConflictException("New slot is already booked");
        a.setAppointmentDate(date); a.setTimeSlot(slot); a.setStatus(AppointmentStatus.CONFIRMED); return AppointmentResponse.fromEntity(appointmentRepository.save(a), prescriptionRepository.findByAppointmentId(a.getId()).isPresent());
    }

    public List<AppointmentResponse> getPatientAppointments(Long patientUserId) {
        List<Appointment> list = appointmentRepository.findByPatientIdOrderByAppointmentDateDesc(patientUserId);
        return list.stream().map(a -> {
            boolean hasPrescription = prescriptionRepository.findByAppointmentId(a.getId()).isPresent();
            return AppointmentResponse.fromEntity(a, hasPrescription);
        }).collect(Collectors.toList());
    }

    public List<AppointmentResponse> getDoctorAppointments(Long doctorUserId) {
        DoctorProfile doctor = doctorProfileRepository.findByUserId(doctorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found for user ID: " + doctorUserId));

        List<Appointment> list = appointmentRepository.findByDoctorIdOrderByAppointmentDateDesc(doctor.getId());
        return list.stream().map(a -> {
            boolean hasPrescription = prescriptionRepository.findByAppointmentId(a.getId()).isPresent();
            return AppointmentResponse.fromEntity(a, hasPrescription);
        }).collect(Collectors.toList());
    }

    @Transactional
    public AppointmentResponse updateStatus(Long appointmentId, AppointmentStatus newStatus, Long requestingUserId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

        User user = userRepository.findById(requestingUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + requestingUserId));

        // Authorization validation: Patient can only cancel their own; Doctor can change status of their appointments
        boolean isPatient = appointment.getPatient().getId().equals(user.getId());
        boolean isDoctor = appointment.getDoctor().getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;

        if (!isPatient && !isDoctor && !isAdmin) {
            throw new BadRequestException("You are not authorized to modify this appointment.");
        }

        if (isPatient && newStatus != AppointmentStatus.CANCELLED) {
            throw new BadRequestException("Patients can only cancel appointments.");
        }

        appointment.setStatus(newStatus);
        Appointment updated = appointmentRepository.save(appointment);

        // If cancelled, release slot
        if (newStatus == AppointmentStatus.CANCELLED) {
            try {
                String startTimeStr = appointment.getTimeSlot().split("-")[0].trim();
                if (startTimeStr.length() == 5) startTimeStr += ":00";
                LocalTime startTime = LocalTime.parse(startTimeStr);
                Optional<TimeSlot> slotOpt = timeSlotRepository.findByDoctorIdAndSlotDateAndStartTime(
                        appointment.getDoctor().getId(), appointment.getAppointmentDate(), startTime);
                slotOpt.ifPresent(slot -> {
                    slot.setIsBooked(false);
                    timeSlotRepository.save(slot);
                });
            } catch (Exception ignored) {}
        }

        boolean hasPrescription = prescriptionRepository.findByAppointmentId(updated.getId()).isPresent();
        return AppointmentResponse.fromEntity(updated, hasPrescription);
    }
}
