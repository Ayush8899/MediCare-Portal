package com.healthcare.portal.service;

import com.healthcare.portal.dto.DoctorDto;
import com.healthcare.portal.entity.DoctorProfile;
import com.healthcare.portal.entity.TimeSlot;
import com.healthcare.portal.exception.ResourceNotFoundException;
import com.healthcare.portal.repository.DoctorProfileRepository;
import com.healthcare.portal.repository.TimeSlotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorProfileRepository doctorProfileRepository;
    private final TimeSlotRepository timeSlotRepository;

    public DoctorService(DoctorProfileRepository doctorProfileRepository, TimeSlotRepository timeSlotRepository) {
        this.doctorProfileRepository = doctorProfileRepository;
        this.timeSlotRepository = timeSlotRepository;
    }

    public List<DoctorDto> getAllDoctors(String specialization, String query) {
        List<DoctorProfile> profiles = doctorProfileRepository.searchDoctors(
                (specialization != null && !specialization.isBlank()) ? specialization : null,
                (query != null && !query.isBlank()) ? query : null
        );

        return profiles.stream().map(DoctorDto::fromEntity).collect(Collectors.toList());
    }

    public DoctorDto getDoctorById(Long id) {
        DoctorProfile profile = doctorProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + id));
        return DoctorDto.fromEntity(profile);
    }

    public List<String> getSpecializations() {
        return doctorProfileRepository.findDistinctSpecializations();
    }

    @Transactional
    public List<TimeSlot> getAvailableSlots(Long doctorId, LocalDate date) {
        DoctorProfile doctor = doctorProfileRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + doctorId));

        List<TimeSlot> existingSlots = timeSlotRepository.findByDoctorIdAndSlotDateOrderByStartTimeAsc(doctorId, date);

        // If no slots exist for this date yet, auto-generate standard consultation slots
        if (existingSlots.isEmpty()) {
            List<TimeSlot> defaultSlots = new ArrayList<>();
            LocalTime[] startTimes = {
                LocalTime.of(9, 0), LocalTime.of(9, 30),
                LocalTime.of(10, 0), LocalTime.of(10, 30),
                LocalTime.of(11, 0), LocalTime.of(11, 30),
                LocalTime.of(14, 0), LocalTime.of(14, 30),
                LocalTime.of(15, 0), LocalTime.of(15, 30),
                LocalTime.of(16, 0), LocalTime.of(16, 30)
            };

            for (LocalTime start : startTimes) {
                LocalTime end = start.plusMinutes(30);
                TimeSlot slot = new TimeSlot(doctor, date, start, end);
                defaultSlots.add(slot);
            }
            existingSlots = timeSlotRepository.saveAll(defaultSlots);
        }

        return existingSlots;
    }
}
