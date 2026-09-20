package com.healthcare.portal.service;

import com.healthcare.portal.entity.Appointment;
import com.healthcare.portal.entity.Payment;
import com.healthcare.portal.exception.BadRequestException;
import com.healthcare.portal.exception.ResourceNotFoundException;
import com.healthcare.portal.repository.AppointmentRepository;
import com.healthcare.portal.repository.PaymentRepository;
import com.healthcare.portal.repository.UserRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class PaymentService {

 private final PaymentRepository payments;
 private final AppointmentRepository appointments;
 private final UserRepository users;
 private final EmailService email;
 private final NotificationService notifications;

 @Value("${app.razorpay.key-id:}")
 private String keyId;

 @Value("${app.razorpay.key-secret:}")
 private String keySecret;

 public PaymentService(
         PaymentRepository payments,
         AppointmentRepository appointments,
         UserRepository users,
         EmailService email,
         NotificationService notifications) {

  this.payments = payments;
  this.appointments = appointments;
  this.users = users;
  this.email = email;
  this.notifications = notifications;
 }

 // =========================================================
 // REFUND PAYMENT
 // =========================================================

 public Map<String, Object> refund(
         Long userId,
         Long appointmentId) {

  if (keyId.isBlank() || keySecret.isBlank()) {
   throw new BadRequestException(
           "Razorpay keys are not configured"
   );
  }

  Payment payment = payments.findAll()
          .stream()
          .filter(p ->
                  p.getAppointment() != null
                          && p.getAppointment()
                          .getId()
                          .equals(appointmentId)
                          && "PAID".equals(p.getStatus())
          )
          .findFirst()
          .orElseThrow(() ->
                  new BadRequestException(
                          "Paid payment not found"
                  )
          );

  if (!payment.getPatient()
          .getId()
          .equals(userId)) {

   throw new BadRequestException(
           "Not your payment"
   );
  }

  try {

   RazorpayClient client =
           new RazorpayClient(
                   keyId,
                   keySecret
           );

   JSONObject request =
           new JSONObject();

   request.put(
           "amount",
           payment.getAmount()
                   .multiply(
                           BigDecimal.valueOf(100)
                   )
                   .longValue()
   );

   client.payments.refund(
           payment.getRazorpayPaymentId(),
           request
   );

   payment.setStatus("REFUNDED");

   payments.save(payment);

   return Map.of(
           "status",
           "REFUNDED"
   );

  } catch (Exception e) {

   e.printStackTrace();

   throw new BadRequestException(
           "Refund failed: "
                   + e.getMessage()
   );
  }
 }

 // =========================================================
 // CREATE RAZORPAY ORDER
 // =========================================================

 public Map<String, Object> createOrder(
         Long userId,
         Long appointmentId) {

  if (keyId.isBlank() || keySecret.isBlank()) {

   throw new BadRequestException(
           "Razorpay keys are not configured"
   );
  }

  // Find appointment
  Appointment appointment =
          appointments.findById(appointmentId)
                  .orElseThrow(() ->
                          new ResourceNotFoundException(
                                  "Appointment not found"
                          )
                  );

  // Make sure appointment belongs to logged-in patient
  if (!appointment.getPatient()
          .getId()
          .equals(userId)) {

   throw new BadRequestException(
           "Not your appointment"
   );
  }

  // Get doctor's consultation fee
  BigDecimal amount =
          appointment.getDoctor()
                  .getConsultationFee();

  if (amount == null ||
          amount.compareTo(BigDecimal.ZERO) <= 0) {

   throw new BadRequestException(
           "Invalid consultation fee"
   );
  }

  try {

   // Create Razorpay client
   RazorpayClient client =
           new RazorpayClient(
                   keyId,
                   keySecret
           );

   // Razorpay order request
   JSONObject request =
           new JSONObject();

   // Convert rupees to paise
   long amountInPaise =
           amount.multiply(
                   BigDecimal.valueOf(100)
           ).longValue();

   request.put(
           "amount",
           amountInPaise
   );

   request.put(
           "currency",
           "INR"
   );

   request.put(
           "receipt",
           "APT-" + appointmentId
   );

   // Create Razorpay order
   Order order =
           client.orders.create(request);

   Object orderIdObject =
           order.get("id");

   if (orderIdObject == null) {

    throw new BadRequestException(
            "Razorpay did not return an order ID"
    );
   }

   String razorpayOrderId =
           orderIdObject.toString();

   // =================================================
   // SAVE PAYMENT
   // =================================================

   Payment payment =
           new Payment(
                   appointment.getPatient(),
                   appointment,
                   appointment.getDoctor(),
                   amount,
                   razorpayOrderId,
                   appointment.getTimeSlot(),
                   appointment.getSymptoms()
           );

   payment =
           payments.save(payment);

   // Send order details to frontend
   return Map.of(
           "keyId",
           keyId,

           "orderId",
           razorpayOrderId,

           "amount",
           amount,

           "currency",
           "INR",

           "paymentId",
           payment.getId()
   );

  } catch (BadRequestException e) {

   throw e;

  } catch (Exception e) {

   e.printStackTrace();

   throw new BadRequestException(
           "Unable to create Razorpay order: "
                   + e.getMessage()
   );
  }
 }

 // =========================================================
 // VERIFY PAYMENT
 // =========================================================

 @Transactional
 public Map<String, Object> verify(
         Long userId,
         String orderId,
         String paymentId,
         String signature) {

  // Find payment order
  Payment payment =
          payments.findByRazorpayOrderId(orderId)
                  .orElseThrow(() ->
                          new ResourceNotFoundException(
                                  "Payment order not found"
                          )
                  );

  // Make sure payment belongs to patient
  if (!payment.getPatient()
          .getId()
          .equals(userId)) {

   throw new BadRequestException(
           "Not your payment"
   );
  }

  // =====================================================
  // 1. VERIFY RAZORPAY SIGNATURE
  // =====================================================

  try {

   JSONObject options =
           new JSONObject();

   options.put(
           "razorpay_order_id",
           orderId
   );

   options.put(
           "razorpay_payment_id",
           paymentId
   );

   options.put(
           "razorpay_signature",
           signature
   );

   Utils.verifyPaymentSignature(
           options,
           keySecret
   );

  } catch (Exception e) {

   e.printStackTrace();

   // Only Razorpay verification failure
   // should mark payment as FAILED
   payment.setStatus("FAILED");

   payments.save(payment);

   throw new BadRequestException(
           "Payment verification failed: "
                   + e.getMessage()
   );
  }

  // =====================================================
  // 2. PAYMENT VERIFIED SUCCESSFULLY
  // =====================================================

  payment.setRazorpayPaymentId(paymentId);

  payment.setRazorpaySignature(signature);

  payment.setStatus("PAID");

  payment.setPaidAt(
          LocalDateTime.now()
  );

  payments.save(payment);

  // =====================================================
  // 3. SEND PAYMENT CONFIRMATION EMAIL
  // =====================================================

  try {

   email.send(
           payment.getPatient().getEmail(),

           "Appointment Payment Confirmed",

           "Your payment for appointment #"
                   + payment.getAppointment().getId()
                   + " was successful. "
                   + "Amount: INR "
                   + payment.getAmount()
   );

   System.out.println(
           "Payment confirmation email sent successfully."
   );

  } catch (Exception e) {

   // IMPORTANT:
   // Email failure must NOT change payment to FAILED

   System.err.println(
           "Payment is PAID, but confirmation email failed."
   );

   e.printStackTrace();
  }

  // =====================================================
  // 4. CREATE NOTIFICATION
  // =====================================================

  try {

   notifications.notify(
           payment.getPatient(),

           "Payment successful",

           "Appointment #"
                   + payment.getAppointment().getId()
                   + " has been paid successfully."
   );

   System.out.println(
           "Payment notification created successfully."
   );

  } catch (Exception e) {

   // Notification failure must NOT change payment status

   System.err.println(
           "Payment is PAID, but notification failed."
   );

   e.printStackTrace();
  }

  // =====================================================
  // 5. RETURN SUCCESS
  // =====================================================

  return Map.of(
          "status",
          "PAID",

          "orderId",
          orderId,

          "paymentId",
          paymentId
  );
 }
}