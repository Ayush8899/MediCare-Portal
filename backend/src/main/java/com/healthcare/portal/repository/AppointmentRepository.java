package com.healthcare.portal.repository;

import com.healthcare.portal.entity.Appointment;
import com.healthcare.portal.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientIdOrderByAppointmentDateDesc(Long patientId);

    List<Appointment> findByDoctorIdOrderByAppointmentDateDesc(Long doctorId);

    List<Appointment> findByDoctorIdAndAppointmentDateOrderByTimeSlotAsc(Long doctorId, LocalDate appointmentDate);

    Boolean existsByDoctorIdAndAppointmentDateAndTimeSlotAndStatusNot(Long doctorId, LocalDate date, String slot, AppointmentStatus status);

    long countByStatus(AppointmentStatus status);

    @Query("SELECT COALESCE(SUM(a.doctor.consultationFee), 0) FROM Appointment a WHERE a.status = 'COMPLETED'")
    BigDecimal calculateTotalCompletedRevenue();
}
