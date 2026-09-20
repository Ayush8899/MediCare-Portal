package com.healthcare.portal.dto;

import com.healthcare.portal.entity.Prescription;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PrescriptionResponse {

    private Long id;
    private Long appointmentId;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private String doctorSpecialization;
    private LocalDate appointmentDate;
    private String diagnosis;
    private String medicines;
    private String advice;
    private LocalDateTime createdAt;

    public PrescriptionResponse() {
    }

    public static PrescriptionResponse fromEntity(Prescription p) {
        PrescriptionResponse res = new PrescriptionResponse();
        res.setId(p.getId());
        res.setAppointmentId(p.getAppointment().getId());
        res.setPatientId(p.getAppointment().getPatient().getId());
        res.setPatientName(p.getAppointment().getPatient().getFullName());
        res.setDoctorId(p.getAppointment().getDoctor().getId());
        res.setDoctorName(p.getAppointment().getDoctor().getUser().getFullName());
        res.setDoctorSpecialization(p.getAppointment().getDoctor().getSpecialization());
        res.setAppointmentDate(p.getAppointment().getAppointmentDate());
        res.setDiagnosis(p.getDiagnosis());
        res.setMedicines(p.getMedicines());
        res.setAdvice(p.getAdvice());
        res.setCreatedAt(p.getCreatedAt());
        return res;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorSpecialization() {
        return doctorSpecialization;
    }

    public void setDoctorSpecialization(String doctorSpecialization) {
        this.doctorSpecialization = doctorSpecialization;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getMedicines() {
        return medicines;
    }

    public void setMedicines(String medicines) {
        this.medicines = medicines;
    }

    public String getAdvice() {
        return advice;
    }

    public void setAdvice(String advice) {
        this.advice = advice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
