package com.healthcare.portal.controller;

import com.healthcare.portal.dto.DashboardStatsDto;
import com.healthcare.portal.dto.DoctorDto;
import com.healthcare.portal.dto.DoctorCreateRequest;
import com.healthcare.portal.entity.User;
import com.healthcare.portal.dto.UserSummary;
import com.healthcare.portal.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "Admin", description = "Administrative endpoints for platform metrics and management")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/doctors")
    @Operation(summary = "Create a doctor account for the hospital")
    public ResponseEntity<?> createDoctor(@RequestBody DoctorCreateRequest request) { return ResponseEntity.ok(adminService.createDoctor(request)); }

    @GetMapping("/stats")
    @Operation(summary = "Get platform metrics and statistics")
    public ResponseEntity<DashboardStatsDto> getDashboardStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @PutMapping("/doctors/{id}")
    @Operation(summary = "Update doctor profile managed by the hospital admin")
    public ResponseEntity<?> updateDoctor(@PathVariable Long id, @RequestBody DoctorCreateRequest request) { return ResponseEntity.ok(adminService.updateDoctor(id, request)); }

    @PatchMapping("/doctors/{id}/status")
    @Operation(summary = "Activate or deactivate a doctor account")
    public ResponseEntity<?> doctorStatus(@PathVariable Long id, @RequestParam boolean active) { return ResponseEntity.ok(adminService.setDoctorActive(id, active)); }

    @GetMapping("/doctors")
    @Operation(summary = "List all registered doctors")
    public ResponseEntity<List<DoctorDto>> getAllDoctors() {
        return ResponseEntity.ok(adminService.getAllDoctors());
    }

    @GetMapping("/users")
    @Operation(summary = "List all registered users")
    public ResponseEntity<List<UserSummary>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers().stream().map(UserSummary::from).toList());
    }
}
