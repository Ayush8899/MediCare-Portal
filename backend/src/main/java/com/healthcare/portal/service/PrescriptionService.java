package com.healthcare.portal.service;

import com.healthcare.portal.dto.PrescriptionRequest;
import com.healthcare.portal.dto.PrescriptionResponse;
import com.healthcare.portal.entity.Appointment;
import com.healthcare.portal.entity.AppointmentStatus;
import com.healthcare.portal.entity.Prescription;
import com.healthcare.portal.exception.BadRequestException;
import com.healthcare.portal.exception.ConflictException;
import com.healthcare.portal.exception.ResourceNotFoundException;
import com.healthcare.portal.repository.AppointmentRepository;
import com.healthcare.portal.repository.PrescriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository,
                               AppointmentRepository appointmentRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional
    public PrescriptionResponse createPrescription(PrescriptionRequest request, Long doctorUserId) {
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + request.getAppointmentId()));

        if (!appointment.getDoctor().getUser().getId().equals(doctorUserId)) {
            throw new BadRequestException("Only the designated doctor can issue a prescription for this appointment.");
        }

        if (prescriptionRepository.findByAppointmentId(appointment.getId()).isPresent()) {
            throw new ConflictException("A prescription has already been issued for this appointment.");
        }

        Prescription prescription = new Prescription(
                appointment,
                request.getDiagnosis(),
                request.getMedicines(),
                request.getAdvice()
        );

        Prescription saved = prescriptionRepository.save(prescription);

        // Mark appointment as COMPLETED
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);

        return PrescriptionResponse.fromEntity(saved);
    }

    public PrescriptionResponse getPrescriptionByAppointment(Long appointmentId) {
        Prescription prescription = prescriptionRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("No prescription found for appointment ID: " + appointmentId));
        return PrescriptionResponse.fromEntity(prescription);
    }

    public List<PrescriptionResponse> getPatientPrescriptions(Long patientUserId) {
        List<Prescription> list = prescriptionRepository.findByAppointmentPatientIdOrderByCreatedAtDesc(patientUserId);
        return list.stream().map(PrescriptionResponse::fromEntity).collect(Collectors.toList());
    }
}
