# MediCare Portal – Enterprise Hospital Roadmap

## Included in this edition
- Patient/doctor/admin JWT authentication and RBAC
- Admin-created patient and doctor accounts with temporary-password email onboarding
- Forgot password/reset links and email OTP verification
- Doctor search, availability/time slots, appointments, rescheduling, cancellation
- Razorpay payment, server-side signature verification, refund foundation, PDF invoice
- Digital prescriptions and medical-record APIs
- Patient/doctor/admin dashboard foundations
- Email notifications and in-app notification center
- Hospital/branch master and doctor-to-hospital assignment
- Doctor lifecycle management by hospital admin
- Patient consent records
- Laboratory order/report reference workflow
- Insurance claim records and status workflow
- Billing invoice records
- Audit-log foundation
- Email-based 2FA foundation
- Swagger/OpenAPI
- Docker + Docker Compose
- Automated test/CI pipeline
- MySQL backup script
- Environment-based secrets

## Run locally
1. Configure MySQL or use Docker Compose.
2. Copy `.env.example` to `.env` and replace secrets.
3. Backend: `cd backend` then `mvn spring-boot:run` or `mvnw.cmd spring-boot:run` on Windows.
4. Frontend: `cd frontend`, `npm install`, `npm run dev`.
5. Swagger: `http://localhost:8080/swagger-ui.html`.

## Important production work before real hospital use
This repository is an enterprise-oriented software foundation, not a claim of regulatory compliance. Before live patient use, complete professional security/privacy/compliance review, penetration testing, HTTPS, secrets management, encryption/key management, secure cloud/object storage for medical documents, immutable audit logging, backup/restore and disaster recovery tests, monitoring/alerting, WAF/rate limiting, stronger MFA enforcement, retention/deletion policies, consent workflows, and jurisdiction-specific healthcare/privacy requirements.

## Suggested next modules
Pharmacy inventory and dispensing, lab-result structured data, radiology/PACS integration, insurance pre-authorization, hospital billing/discount/tax rules, staff/department/ward/bed management, queue/token management, telemedicine, referral management, clinical notes, appointment reminders, analytics, data warehouse, and mobile apps.
