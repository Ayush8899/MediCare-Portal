package com.healthcare.portal.controller;

import com.healthcare.portal.dto.DoctorDto;
import com.healthcare.portal.entity.TimeSlot;
import com.healthcare.portal.dto.TimeSlotResponse;
import com.healthcare.portal.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@Tag(name = "Doctors", description = "Public endpoints for exploring doctors, specializations, and time slots")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    @Operation(summary = "Search and filter doctors by specialization or keyword")
    public ResponseEntity<List<DoctorDto>> getAllDoctors(
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String query) {
        return ResponseEntity.ok(doctorService.getAllDoctors(specialization, query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detailed profile of a doctor by ID")
    public ResponseEntity<DoctorDto> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @GetMapping("/specializations")
    @Operation(summary = "Get all distinct medical specializations")
    public ResponseEntity<List<String>> getSpecializations() {
        return ResponseEntity.ok(doctorService.getSpecializations());
    }

    @GetMapping("/{id}/slots")
    @Operation(summary = "Get available consultation time slots for a specific date")
    public ResponseEntity<List<TimeSlotResponse>> getAvailableSlots(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date == null) {
            date = LocalDate.now().plusDays(1); // Default to tomorrow
        }
        return ResponseEntity.ok(doctorService.getAvailableSlots(id, date).stream().map(TimeSlotResponse::from).toList());
    }
}
