# MediCare Portal

A full-stack healthcare appointment and patient management application built with Spring Boot, React, MySQL, and Razorpay.

I built this project to practice how a real application is split between a frontend, REST API, database, authentication, payments, email, and admin workflows. The code is organized so that new hospital modules can be added without changing the whole application.

## What it does

### Patient
- Register and log in
- Verify email with OTP
- Search doctors and view available slots
- Book and reschedule appointments
- Cancel appointments
- Pay consultation fees through Razorpay Test Mode
- View payment status and download PDF invoices
- View prescriptions and medical records
- Receive in-app and email notifications
- Reset a forgotten password

### Doctor
- Secure doctor login
- View and manage appointment workflow
- Manage consultation availability
- Create digital prescriptions
- Work with patient clinical information available to the doctor

### Admin
- Dashboard for patients, doctors, appointments and revenue
- Add, edit, activate and deactivate doctors
- Create doctor accounts with temporary passwords sent by email
- Manage hospital information
- View enterprise records such as lab orders, insurance claims, consents, billing and audit data
- Access the admin enterprise area from `/admin/enterprise`

## Main technologies

| Area | Technology |
|---|---|
| Backend | Java 17+, Spring Boot 3.3.3 |
| Security | Spring Security 6, JWT, BCrypt |
| API | Spring MVC REST |
| Database | MySQL 8.4 |
| Persistence | Spring Data JPA / Hibernate |
| Frontend | React 18, Vite, React Router |
| HTTP client | Axios |
| Payments | Razorpay Test Mode |
| Email | Spring Mail / SMTP |
| PDF | Apache PDFBox |
| API docs | Springdoc OpenAPI / Swagger |
| Deployment | Docker / Docker Compose |
| CI | GitHub Actions |

## Project structure

```text
MediCare-Portal/
├── backend/
│   ├── src/main/java/com/healthcare/portal/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── exception/
│   │   ├── repository/
│   │   ├── security/
│   │   └── service/
│   ├── src/main/resources/
│   ├── pom.xml
│   └── Dockerfile
├── frontend/
│   ├── src/
│   ├── package.json
│   └── nginx.conf
├── database/
│   ├── schema.sql
│   └── seed_data.sql
├── ops/
├── docker-compose.yml
├── Dockerfile
├── .env.example
└── README.md
```

## Architecture

The backend follows a simple layered structure:

```text
React UI
   ↓
Axios / REST API
   ↓
Controller
   ↓
Service
   ↓
Repository
   ↓
MySQL
```

Security is handled before the controller layer with Spring Security and a JWT filter. Passwords are stored using BCrypt rather than plain text.

## Local setup

### 1. Requirements

Install:

- JDK 17 or newer
- MySQL 8.x
- Node.js 20+
- Git

The Maven project targets Java 17 for portability. A newer JDK can also run it when the Spring Boot/Maven toolchain supports it.

### 2. Database

Create a MySQL database named `healthcare_db`, or let the application create it with the default JDBC URL.

For local development, create a `.env` from `.env.example` and set your database password and other credentials.

Do not put Gmail app passwords, Razorpay secrets, or production JWT secrets in `application.properties` or in Git.

### 3. Backend

Windows PowerShell:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Backend:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/api-docs
```

### 4. Frontend

Open a second terminal:

```powershell
cd frontend
npm install
npm run dev
```

Frontend:

```text
http://localhost:5173
```

## Demo login

The `DataInitializer` creates local demo accounts when the database is empty.

| Role | Email | Password |
|---|---|---|
| Admin | `admin@healthcare.com` | `Admin@123` |
| Doctor | `dr.sharma@healthcare.com` | `doctor123` |
| Doctor | `dr.priya@healthcare.com` | `doctor123` |
| Patient | `patient@demo.com` | `patient123` |

These credentials are for local development only. Change them before using the application outside a local/demo environment.

## Payment flow

The application uses Razorpay Test Mode for the payment flow:

```text
Create appointment
      ↓
Create Razorpay order
      ↓
Razorpay Checkout
      ↓
Server verifies payment signature
      ↓
Payment marked PAID
      ↓
Invoice generated
      ↓
Email + in-app notification
```

Email and notification failures are handled separately from payment verification, so a notification problem does not turn a successfully verified payment into a failed payment.

The refund API is available for paid appointments. Refund handling should be tested in Razorpay Test Mode before connecting it to a production cancellation policy.

## Security notes

- JWT authentication is stateless.
- Spring Security handles role-based authorization.
- Passwords use BCrypt.
- Payment signatures are verified on the server.
- Secrets are loaded from environment variables.
- CORS is configured for local frontend development.

For a real hospital deployment, this project still needs a formal security/privacy review, HTTPS, production secret management, rate limiting, monitoring, backup/restore testing, document storage controls, stronger access policies and jurisdiction-specific compliance work.

## Docker

The root `docker-compose.yml` is the main Docker setup. It starts MySQL, the Spring Boot backend and the React/Nginx frontend.

```powershell
docker compose up --build
```

The compose setup expects the environment variables shown in `.env.example`.

## Testing

Backend tests:

```powershell
cd backend
.\mvnw.cmd test
```

Frontend production build:

```powershell
cd frontend
npm install
npm run build
```

GitHub Actions runs backend tests and the frontend build on pushes and pull requests.

## Useful API groups

```text
POST /api/auth/register
POST /api/auth/login
GET  /api/auth/me

GET  /api/doctors
GET  /api/doctors/{id}/slots
POST /api/appointments
PUT  /api/appointments/{id}/reschedule
PATCH /api/appointments/{id}/status
GET  /api/appointments/my-appointments

POST /api/payments/create-order
POST /api/payments/verify
POST /api/payments/refund/{appointmentId}
GET  /api/invoices/{orderId}

POST /api/prescriptions
GET  /api/prescriptions/my-prescriptions
POST /api/medical-records
GET  /api/medical-records/patient/{id}

GET  /api/notifications
POST /api/auth/forgot-password
POST /api/auth/reset-password
POST /api/auth/send-email-otp
POST /api/auth/verify-email-otp

GET  /api/admin/stats
GET  /api/admin/doctors
POST /api/admin/doctors
PUT  /api/admin/doctors/{id}
PATCH /api/admin/doctors/{id}/status
```

## A few implementation details

### Appointment safety

Appointment booking is transactional and checks slot availability before saving. The project also uses optimistic locking where applicable to reduce the chance of two requests booking the same slot.

### Authentication

A successful login creates a JWT. The React application sends that token in the `Authorization: Bearer ...` header for protected API calls. The backend validates the token and loads the user's role into the Spring Security context.

### DTOs

DTOs are used at API boundaries instead of returning JPA entities everywhere. This keeps API responses smaller and avoids exposing fields such as password hashes through nested relationships.

### Email

Email is used for OTP verification, password recovery, account onboarding and payment-related notifications. SMTP credentials are supplied through environment variables.

## Resume description

**MediCare Portal — Healthcare Appointment & Patient Management System**

- Built a full-stack healthcare portal with Spring Boot, Spring Security, React and MySQL for patient, doctor and admin workflows.
- Implemented JWT authentication, BCrypt password hashing and role-based authorization.
- Developed appointment booking/rescheduling, doctor availability, digital prescriptions, medical records, notifications and PDF invoices.
- Integrated Razorpay Test Mode with server-side signature verification and payment status handling.
- Added email OTP/password recovery and admin-driven doctor account onboarding.
- Structured the backend using Controller → Service → Repository layers with JPA, DTOs and centralized exception handling.

Only mention features on your resume that you have personally tested and can explain in an interview.

## License

This project is intended as a learning and portfolio project. Add a license here if you decide to publish it under specific open-source terms.
