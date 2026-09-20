package com.healthcare.portal.controller;

import com.healthcare.portal.dto.PrescriptionRequest;
import com.healthcare.portal.dto.PrescriptionResponse;
import com.healthcare.portal.security.CustomUserDetails;
import com.healthcare.portal.service.PrescriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
@Tag(name = "Prescriptions", description = "Endpoints for digital prescriptions and medical records")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_DOCTOR')")
    @Operation(summary = "Doctor issues a digital prescription for an appointment")
    public ResponseEntity<PrescriptionResponse> createPrescription(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PrescriptionRequest request) {
        return ResponseEntity.ok(prescriptionService.createPrescription(request, userDetails.getId()));
    }

    @GetMapping("/appointment/{appointmentId}")
    @Operation(summary = "Get prescription associated with a specific appointment")
    public ResponseEntity<PrescriptionResponse> getPrescriptionByAppointment(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionByAppointment(appointmentId));
    }

    @GetMapping("/my-prescriptions")
    @Operation(summary = "Get all prescriptions for the logged-in patient")
    public ResponseEntity<List<PrescriptionResponse>> getPatientPrescriptions(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(prescriptionService.getPatientPrescriptions(userDetails.getId()));
    }
}
