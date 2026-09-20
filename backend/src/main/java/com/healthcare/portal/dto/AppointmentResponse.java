package com.healthcare.portal.dto;

import com.healthcare.portal.entity.Appointment;
import com.healthcare.portal.entity.AppointmentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AppointmentResponse {

    private Long id;
    private Long patientId;
    private String patientName;
    private String patientEmail;
    private String patientPhone;

    private Long doctorId;
    private String doctorName;
    private String specialization;
    private BigDecimal consultationFee;

    private LocalDate appointmentDate;
    private String timeSlot;
    private String symptoms;
    private AppointmentStatus status;
    private LocalDateTime createdAt;
    private boolean hasPrescription;

    public AppointmentResponse() {
    }

    public static AppointmentResponse fromEntity(Appointment a, boolean hasPrescription) {
        AppointmentResponse res = new AppointmentResponse();
        res.setId(a.getId());
        res.setPatientId(a.getPatient().getId());
        res.setPatientName(a.getPatient().getFullName());
        res.setPatientEmail(a.getPatient().getEmail());
        res.setPatientPhone(a.getPatient().getPhone());

        res.setDoctorId(a.getDoctor().getId());
        res.setDoctorName(a.getDoctor().getUser().getFullName());
        res.setSpecialization(a.getDoctor().getSpecialization());
        res.setConsultationFee(a.getDoctor().getConsultationFee());

        res.setAppointmentDate(a.getAppointmentDate());
        res.setTimeSlot(a.getTimeSlot());
        res.setSymptoms(a.getSymptoms());
        res.setStatus(a.getStatus());
        res.setCreatedAt(a.getCreatedAt());
        res.setHasPrescription(hasPrescription);
        return res;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPatientEmail() {
        return patientEmail;
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
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

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public BigDecimal getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(BigDecimal consultationFee) {
        this.consultationFee = consultationFee;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isHasPrescription() {
        return hasPrescription;
    }

    public void setHasPrescription(boolean hasPrescription) {
        this.hasPrescription = hasPrescription;
    }
}
