package com.healthcare.portal.controller;

import com.healthcare.portal.entity.Hospital;
import com.healthcare.portal.repository.AuditLogRepository;
import com.healthcare.portal.repository.BillingInvoiceRepository;
import com.healthcare.portal.repository.ConsentRepository;
import com.healthcare.portal.repository.HospitalRepository;
import com.healthcare.portal.repository.InsuranceClaimRepository;
import com.healthcare.portal.repository.LabOrderRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/enterprise")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(
        name = "Enterprise Admin",
        description = "Hospital, compliance, billing and operational administration"
)
public class EnterpriseAdminController {

 private final HospitalRepository hospitals;
 private final AuditLogRepository audits;
 private final ConsentRepository consents;
 private final LabOrderRepository labs;
 private final InsuranceClaimRepository claims;
 private final BillingInvoiceRepository invoices;

 public EnterpriseAdminController(
         HospitalRepository hospitals,
         AuditLogRepository audits,
         ConsentRepository consents,
         LabOrderRepository labs,
         InsuranceClaimRepository claims,
         BillingInvoiceRepository invoices) {

  this.hospitals = hospitals;
  this.audits = audits;
  this.consents = consents;
  this.labs = labs;
  this.claims = claims;
  this.invoices = invoices;
 }

 @PostMapping("/hospitals")
 @Operation(summary = "Create hospital/branch")
 public Hospital createHospital(@RequestBody Hospital hospital) {
  return hospitals.save(hospital);
 }

 @GetMapping("/hospitals")
 public List<Hospital> getHospitals() {
  return hospitals.findAll();
 }

 @PatchMapping("/hospitals/{id}/status")
 public Map<String, Object> hospitalStatus(
         @PathVariable Long id,
         @RequestParam boolean active) {

  Hospital hospital = hospitals.findById(id)
          .orElseThrow(() ->
                  new RuntimeException("Hospital not found with id: " + id)
          );

  hospital.setActive(active);
  hospitals.save(hospital);

  Map<String, Object> response = new HashMap<>();
  response.put("id", id);
  response.put("active", active);

  return response;
 }

 @GetMapping("/audit-logs")
 public List<Map<String, Object>> getAuditLogs() {

  return audits.findAll()
          .stream()
          .map(audit -> {

           Map<String, Object> response = new HashMap<>();

           response.put("id", audit.getId());

           response.put(
                   "actorUserId",
                   audit.getActor() == null
                           ? null
                           : audit.getActor().getId()
           );

           response.put("action", audit.getAction());

           response.put(
                   "entityType",
                   audit.getEntityType()
           );

           response.put(
                   "entityId",
                   audit.getEntityId()
           );

           response.put(
                   "details",
                   audit.getDetails()
           );

           response.put(
                   "createdAt",
                   audit.getCreatedAt()
           );

           return response;
          })
          .toList();
 }

 @GetMapping("/consents")
 public List<Map<String, Object>> getConsents() {

  return consents.findAll()
          .stream()
          .map(consent -> {

           Map<String, Object> response = new HashMap<>();

           response.put("id", consent.getId());

           response.put(
                   "patientId",
                   consent.getPatient().getId()
           );

           response.put(
                   "consentType",
                   consent.getConsentType()
           );

           response.put(
                   "granted",
                   consent.isGranted()
           );

           response.put(
                   "purpose",
                   consent.getPurpose()
           );

           response.put(
                   "updatedAt",
                   consent.getUpdatedAt()
           );

           return response;
          })
          .toList();
 }

 @GetMapping("/lab-orders")
 public List<Map<String, Object>> getLabOrders() {

  return labs.findAll()
          .stream()
          .map(lab -> {

           Map<String, Object> response = new HashMap<>();

           response.put("id", lab.getId());

           response.put(
                   "patientId",
                   lab.getPatient().getId()
           );

           response.put(
                   "testName",
                   lab.getTestName()
           );

           response.put(
                   "status",
                   lab.getStatus()
           );

           response.put(
                   "amount",
                   lab.getAmount()
           );

           response.put(
                   "reportUrl",
                   lab.getReportUrl()
           );

           response.put(
                   "createdAt",
                   lab.getCreatedAt()
           );

           return response;
          })
          .toList();
 }

 @GetMapping("/insurance-claims")
 public List<Map<String, Object>> getInsuranceClaims() {

  return claims.findAll()
          .stream()
          .map(claim -> {

           Map<String, Object> response = new HashMap<>();

           response.put("id", claim.getId());

           response.put(
                   "patientId",
                   claim.getPatient().getId()
           );

           response.put(
                   "appointmentId",
                   claim.getAppointment() == null
                           ? null
                           : claim.getAppointment().getId()
           );

           response.put(
                   "provider",
                   claim.getProvider()
           );

           response.put(
                   "policyNumber",
                   claim.getPolicyNumber()
           );

           response.put(
                   "amount",
                   claim.getAmount()
           );

           response.put(
                   "status",
                   claim.getStatus()
           );

           response.put(
                   "createdAt",
                   claim.getCreatedAt()
           );

           return response;
          })
          .toList();
 }

 @GetMapping("/billing-invoices")
 public List<Map<String, Object>> getBillingInvoices() {

  return invoices.findAll()
          .stream()
          .map(invoice -> {

           Map<String, Object> response = new HashMap<>();

           response.put("id", invoice.getId());

           response.put(
                   "invoiceNumber",
                   invoice.getInvoiceNumber()
           );

           response.put(
                   "patientId",
                   invoice.getPatient().getId()
           );

           response.put(
                   "appointmentId",
                   invoice.getAppointment() == null
                           ? null
                           : invoice.getAppointment().getId()
           );

           response.put(
                   "amount",
                   invoice.getAmount()
           );

           response.put(
                   "status",
                   invoice.getStatus()
           );

           response.put(
                   "issuedAt",
                   invoice.getIssuedAt()
           );

           return response;
          })
          .toList();
 }
}