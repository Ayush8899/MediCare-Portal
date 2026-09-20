package com.healthcare.portal.controller;

import com.healthcare.portal.dto.AppointmentRequest;
import com.healthcare.portal.dto.AppointmentResponse;
import com.healthcare.portal.dto.StatusUpdateRequest;
import com.healthcare.portal.entity.Role;
import com.healthcare.portal.security.CustomUserDetails;
import com.healthcare.portal.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@Tag(name = "Appointments", description = "Endpoints for booking and managing consultations")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    @Operation(summary = "Book an appointment with transactional double-booking prevention")
    public ResponseEntity<AppointmentResponse> bookAppointment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody AppointmentRequest request) {
        return ResponseEntity.ok(appointmentService.bookAppointment(userDetails.getId(), request));
    }

    @PutMapping("/{id}/reschedule")
    @Operation(summary="Reschedule an appointment")
    public ResponseEntity<AppointmentResponse> reschedule(@PathVariable Long id,@AuthenticationPrincipal CustomUserDetails u,@RequestParam String date,@RequestParam String slot){return ResponseEntity.ok(appointmentService.reschedule(id,u.getId(),java.time.LocalDate.parse(date),slot));}

    @GetMapping("/my-appointments")
    @Operation(summary = "Get list of appointments for the logged-in user")
    public ResponseEntity<List<AppointmentResponse>> getMyAppointments(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String roleStr = userDetails.getAuthorities().iterator().next().getAuthority();
        Role role = Role.valueOf(roleStr);

        if (role == Role.ROLE_DOCTOR) {
            return ResponseEntity.ok(appointmentService.getDoctorAppointments(userDetails.getId()));
        } else {
            return ResponseEntity.ok(appointmentService.getPatientAppointments(userDetails.getId()));
        }
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update status of an appointment (e.g., CONFIRMED, COMPLETED, CANCELLED)")
    public ResponseEntity<AppointmentResponse> updateStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(appointmentService.updateStatus(id, request.getStatus(), userDetails.getId()));
    }
}
