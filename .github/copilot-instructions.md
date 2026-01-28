Meeting Room Booking System – RESTful + JWT (HttpOnly Cookie)
🔹 PROJECT CONTEXT
You are helping build a Meeting Room Booking System for a Software Engineering course project.
Designed for Vietnamese university staff and students to book meeting rooms efficiently.
Response in Vietnamese. Don't create any exessive MD structure, just focus on the content.

Core features:

- Meeting room booking with approval workflow
- JWT authentication stored in HttpOnly Cookie
- Google OAuth2 login
- Role-based authorization (USER, STAFF)
- Smart meeting room suggestion
- Calendar view (day/week)
- Chat between user and staff
- System designed to be highly testable

Tech stack:

- Backend: Java Spring Boot
- Frontend: Angular (latest stable)
- Database: Microsoft SQL Server (MSSQL)
- Authentication:
  - Google OAuth2
  - JWT issued by backend
  - JWT stored in HttpOnly Cookie

🔹 AUTHENTICATION & SECURITY

1. Authentication Flow

- Users can login via:
  a) Google OAuth2
  b) Internal username/password (optional)
- After successful login, backend issues a JWT
- JWT must be stored ONLY in HttpOnly Cookie
- Frontend must NEVER read or store JWT manually

2. Cookie Settings

- HttpOnly = true
- Secure = true (false allowed in local development)
- SameSite = Lax or Strict

3. Authorization

- Use Spring Security
- Roles:
  USER: normal employee
  STAFF: room manager / customer support
- Enforce authorization in both controller and service layers

🔹 RESTFUL API DESIGN RULES
Follow REST principles strictly:

- Use nouns for resources
- Use HTTP verbs correctly
- Use meaningful HTTP status codes

Example endpoints:
POST /api/auth/login/google
POST /api/auth/logout

GET /api/rooms
GET /api/rooms/suggestions
GET /api/calendar

POST /api/bookings
PUT /api/bookings/{id}
DELETE /api/bookings/{id}

PUT /api/bookings/{id}/approve
PUT /api/bookings/{id}/reject

🔹 CORE BUSINESS RULES

1. Booking Rules

- startTime < endTime
- Booking must be in the future
- Room capacity >= number of participants
- Room must support all requested equipment

2. Booking Status Lifecycle
   PENDING → APPROVED → REJECTED → CANCELLED

3. Permission Rules
   USER:

- Create booking
- Edit / cancel own booking (only if PENDING)
- View own bookings
- View calendar
- Chat with staff

STAFF:

- View all bookings
- Approve / reject booking
- View system calendar
- Reply chat

4. Boundary Rule

- endTime == startTime is allowed (no overlap)

🔹 SMART ROOM SUGGESTION
Implement a smart room suggestion feature:

Input:

- startTime
- endTime
- numberOfParticipants
- requiredEquipment

Output:

- List of available rooms sorted by suitability

Suggestion priority:

1. Capacity closest to required size
2. Exact equipment match
3. Least unused capacity
4. Availability during requested time

This logic must be deterministic and testable.

🔹 CALENDAR VIEW
Provide calendar view functionality:

- Day view
- Week view
- Color-coded booking status:
  PENDING, APPROVED, CANCELLED

Calendar rules:

- USER sees only own bookings
- STAFF sees all bookings
- Calendar data must be consistent with booking data

🔹 CONCURRENCY & TRANSACTION

- Assume multiple users may book the same room concurrently
- Always re-check availability when approving booking
- Use database locking or transaction isolation
- Avoid race conditions explicitly

🔹 LOGGING & NOTIFICATION

- Log every booking state change
- Fake email notification = application log
- Logs must include:
  bookingId
  action
  actor
  timestamp

🔹 FRONTEND (ANGULAR)

- Use Angular latest best practices
- Use HttpClient with { withCredentials: true }
- Do NOT store JWT in localStorage or sessionStorage
- Handle 401 / 403 globally via interceptor
- UI must reflect booking lifecycle clearly

🔹 TESTING-ORIENTED GUIDELINES
Design everything assuming it will be tested:

- Make invalid states explicit
- Reject invalid transitions
- Do not auto-correct invalid data silently
- APIs must be testable using Postman with cookie-based auth

Key test scenarios:

- Unauthorized access
- Role mismatch
- Concurrent booking
- Boundary time cases
- Smart suggestion correctness
- Calendar data consistency

🔹 BACKEND PACKAGE STRUCTURE
auth/
security/
room/
booking/
calendar/
chat/
notification/
common/
