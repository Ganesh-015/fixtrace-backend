# FixTrace — Smart Lab Maintenance & Repair Tracker

FixTrace is a production-quality, monolithic backend Spring Boot REST API application designed for colleges, universities, research facilities, and vocational workshops. It provides end-to-end tracking of laboratory hardware and equipment assets, failure and fault reporting, technician assignment, state-machine repair lifecycles, full audit history, and administrative analytics dashboards.

---

## 1. Technology Stack

- **Java**: 25 (Java SE 25 LTS)
- **Framework**: Spring Boot 4.1.x
- **Modules**:
  - `Spring Web` (RESTful Controller & MVC architecture)
  - `Spring Data JPA` (ORM, dynamic specifications, pagination, repositories)
  - `Spring Security` (Stateless token-based security, method-level authorization)
  - `Bean Validation` (Jakarta validation annotations)
  - `Spring Boot Actuator` (Production health, info, and operational metrics)
- **Database**: MySQL 8.x (with H2 in-memory mode for automated unit & integration testing)
- **Authentication**: JWT (JSON Web Tokens via JJWT 0.12.6, HMAC-SHA256)
- **Password Hashing**: BCrypt (`BCryptPasswordEncoder`)
- **API Documentation**: OpenAPI 3.0 / Swagger UI (`springdoc-openapi` 3.1.x)
- **Build & Dependency Management**: Apache Maven 3.9+
- **Testing**: JUnit 5, MockMvc, AssertJ, Spring Boot Test

---

## 2. Architecture & Design Principles

FixTrace strictly enforces a clean, layered monolithic REST architecture:

```
[ HTTP Requests ]
       ↓
[ Controller Layer ] (HTTP translation, validation, status codes, Swagger annotations)
       ↓
[ Service Layer ]    (Business rules, transactions, state machines, notifications)
       ↓
[ Repository Layer ] (Spring Data JPA, dynamic Specifications, aggregations)
       ↓
[ Database ]         (MySQL relational tables with strict foreign keys & indexes)
```

### Architectural Guarantees
- **No Entity Leakage**: JPA Entities are never directly exposed through REST endpoints. Clear Request/Response DTOs mediate all client interactions.
- **Stateless Authentication**: Sessions are entirely stateless (`SessionCreationPolicy.STATELESS`). The server verifies digitally signed JWT tokens on every request.
- **Secure Context Extraction**: When creating issues or repair records, the system extracts the authenticated user identity directly from Spring Security's `SecurityContextHolder`—clients cannot forge reporter or technician identities.
- **Strict State Machine Workflow**: Issue status transitions are strictly validated. Invalid jumps (e.g. `REPORTED` directly to `RESOLVED` or modifying `CLOSED` issues) throw typed domain exceptions.
- **Historical Record Integrity**: Equipment is retired via soft-deletion (`EquipmentStatus.RETIRED`) rather than hard deletion, ensuring historical failure audits and repair logs are permanently preserved.

---

## 3. Package Structure

```
com.fixtrace
├── FixtraceApplication.java
│
├── config
│   ├── DataSeeder.java            # Automatic development data seeder (labs, equipment, issues, repairs)
│   ├── JacksonConfig.java         # Global ObjectMapper configuration (ISO-8601 timestamps)
│   ├── OpenApiConfig.java         # OpenAPI / Swagger UI Bearer Auth configuration
│   └── SecurityConfig.java        # Spring Security filter chain and endpoint access rules
│
├── controller
│   ├── AuthController.java        # Register, login, and /me profile
│   ├── DashboardController.java   # Admin maintenance analytics and chart APIs
│   ├── EquipmentController.java   # Asset registration, filters, pagination, maintenance history
│   ├── IssueController.java       # Issue reporting, assignment, status machine transitions
│   ├── LabController.java         # Lab facilities management and room allocations
│   ├── NotificationController.java# In-app user notifications and read receipts
│   ├── RepairRecordController.java# Technical diagnosis and repair logs
│   └── UserController.java        # User administration and active technician directory
│
├── dto
│   ├── ApiResponse.java           # Unified API response wrapper
│   ├── PageResponse.java          # Standardized pagination response wrapper
│   ├── auth/                      # RegisterRequest, LoginRequest, LoginResponse
│   ├── dashboard/                 # DashboardSummaryResponse, ChartItemResponse, FailureProneEquipmentResponse
│   ├── equipment/                 # CreateEquipmentRequest, EquipmentResponse, MaintenanceHistoryResponse
│   ├── issue/                     # CreateIssueRequest, AssignIssueRequest, Status/Priority requests, IssueResponse
│   ├── lab/                       # CreateLabRequest, UpdateLabRequest, LabResponse
│   ├── notification/              # NotificationResponse
│   ├── repair/                    # RepairRecordRequest, RepairRecordResponse
│   └── user/                      # UserResponse, UpdateUserRequest
│
├── entity
│   ├── Equipment.java             # Laboratory asset entity
│   ├── Issue.java                 # Fault report and workflow tracker
│   ├── Lab.java                   # Physical laboratory facility
│   ├── Notification.java          # Event notification for users
│   ├── RepairRecord.java          # Detailed technical service log
│   └── User.java                  # System user with role and credentials
│
├── enums
│   ├── EquipmentCategory.java     # COMPUTER, MONITOR, PRINTER, PROJECTOR, UPS, etc.
│   ├── EquipmentStatus.java       # WORKING, UNDER_REPAIR, DAMAGED, RETIRED
│   ├── IssuePriority.java         # LOW, MEDIUM, HIGH, CRITICAL
│   ├── IssueStatus.java           # REPORTED, ACKNOWLEDGED, ASSIGNED, IN_PROGRESS, etc.
│   ├── NotificationType.java      # ISSUE_ASSIGNED, ISSUE_STATUS_CHANGED, etc.
│   └── Role.java                  # ADMIN, TECHNICIAN, STAFF, STUDENT
│
├── exception
│   ├── BadRequestException.java
│   ├── DuplicateResourceException.java
│   ├── ErrorResponse.java         # Standardized error payload with field errors
│   ├── ForbiddenException.java
│   ├── GlobalExceptionHandler.java# @RestControllerAdvice with structured JSON responses
│   ├── InvalidStatusTransitionException.java
│   ├── ResourceNotFoundException.java
│   └── UnauthorizedException.java
│
├── mapper
│   ├── EquipmentMapper.java
│   ├── IssueMapper.java
│   ├── LabMapper.java
│   ├── NotificationMapper.java
│   ├── RepairRecordMapper.java
│   └── UserMapper.java
│
├── repository
│   ├── EquipmentRepository.java
│   ├── IssueRepository.java
│   ├── LabRepository.java
│   ├── NotificationRepository.java
│   ├── RepairRecordRepository.java
│   └── UserRepository.java
│
├── security
│   ├── CustomAccessDeniedHandler.java
│   ├── CustomUserDetails.java
│   ├── CustomUserDetailsService.java
│   ├── JwtAuthenticationEntryPoint.java
│   ├── JwtAuthenticationFilter.java
│   └── JwtService.java
│
└── service
    ├── AuthService.java & impl/AuthServiceImpl.java
    ├── DashboardService.java & impl/DashboardServiceImpl.java
    ├── EquipmentService.java & impl/EquipmentServiceImpl.java
    ├── IssueService.java & impl/IssueServiceImpl.java
    ├── LabService.java & impl/LabServiceImpl.java
    ├── NotificationService.java & impl/NotificationServiceImpl.java
    ├── RepairRecordService.java & impl/RepairRecordServiceImpl.java
    └── UserService.java & impl/UserServiceImpl.java
```

---

## 4. Database Schema & Relationships

The database is built on relational integrity and optimized with indexing:

```
+------------+          +-------------+
|    User    |          |     Lab     |
+------------+          +-------------+
      | 1                     | 1
      |                       |
      | *                     | *
      | (reportedBy/assigned) +-----------------------+
      |                                               |
      v                                               v
+------------+          +-------------+        +---------------+
|   Issue    |<--------*|  Equipment  |*------>|     Lab       |
+------------+          +-------------+        +---------------+
      | 1
      |
      | *
      v
+-------------------+
|   RepairRecord    |
+-------------------+
```

### Table Overview
1. **`users`**: User identity, hashed passwords (`BCrypt`), role (`ADMIN`, `TECHNICIAN`, `STAFF`, `STUDENT`), `active` state flag.
2. **`labs`**: Laboratory details (`lab_code` [unique], `name`, `building`, `floor`, `room_number`, `active`).
3. **`equipment`**: Laboratory assets (`asset_code` [unique], `name`, `category`, `brand`, `model`, `serial_number` [unique], `status`, foreign key `lab_id`).
4. **`issues`**: Problem reports (`issue_code` [unique: `FT-YYYY-NNNNNN`], `title`, `description`, `priority`, `status`, foreign keys `reported_by_id`, `equipment_id`, `lab_id`, `assigned_technician_id`).
5. **`repair_records`**: Technical work logs (`diagnosis`, `action_taken`, `parts_used`, `repair_notes`, timestamps, foreign keys `issue_id`, `technician_id`).
6. **`notifications`**: User notifications (`title`, `message`, `type`, `is_read`, foreign key `user_id`).

---

## 5. Issue Lifecycle & State Machine Workflow

FixTrace enforces an explicit state machine for equipment issues:

```
[ REPORTED ]
     |
     +--------------------------+-----------------------+
     |                          |                       |
     v                          v                       v
[ ACKNOWLEDGED ]         [ ASSIGNED ]             [ REJECTED ] (Terminal)
     |                          |
     +------------+-------------+
                  |
                  v
           [ IN_PROGRESS ] <===================> [ WAITING_FOR_PARTS ]
                  |
                  v
            [ RESOLVED ]
                  |
                  +---------------------------+
                  |                           |
                  v                           v
              [ CLOSED ]                 [ REOPENED ]
                  |                           |
                  +--- (via /reopen) ---------> [ IN_PROGRESS ]
```

### Automatic Equipment Status Side-Effects:
- **When an issue is reported**: If the equipment was `WORKING`, it transitions to `DAMAGED` (or `UNDER_REPAIR` if priority is `CRITICAL`).
- **When assigned or in repair**: Equipment status transitions to `UNDER_REPAIR`.
- **When resolved**: The system inspects whether the equipment has any other open, unresolved issues. If none remain, the equipment is automatically restored to `WORKING`.
- **Protection**: Issues cannot be reported on `RETIRED` equipment.

---

## 6. User Roles & Permission Matrix

| Operation | ADMIN | TECHNICIAN | STAFF | STUDENT |
| :--- | :---: | :---: | :---: | :---: |
| **User Management** (CRUD, Deactivate) | Yes | No | No | No |
| **Lab Management** (Create, Update, Delete) | Yes | No | No | No |
| **Equipment Management** (Create, Update, Retire) | Yes | No | No | No |
| **View Labs & Equipment** | Yes | Yes | Yes | Yes |
| **Report New Issue** | Yes | Yes | Yes | Yes |
| **View Issues** | All | All / Assigned | Own Reported | Own Reported |
| **Assign Technician** | Yes | No | No | No |
| **Update Issue Priority** | Yes | No | No | No |
| **Update Issue Status** | Yes | Assigned Only | No | No |
| **Create Repair Record** | Yes | Assigned Only | No | No |
| **Modify Repair Record** | Yes | Own Record | No | No |
| **Close Issue** | Yes | No | If Reporter | If Reporter |
| **Reopen Issue** | Yes | No | If Reporter | If Reporter |
| **Maintenance Analytics Dashboard** | Yes | No | No | No |

---

## 7. Pre-Seeded Development Credentials

When `app.seed.enabled=true` (enabled by default), the system initializes:

| Email | Password | Role | Description |
| :--- | :--- | :--- | :--- |
| `admin@fixtrace.com` | `Admin@123` | `ADMIN` | System administrator with full privileges |
| `tech.alex@fixtrace.com` | `Tech@123` | `TECHNICIAN` | Senior hardware & networking technician |
| `tech.sarah@fixtrace.com` | `Tech@123` | `TECHNICIAN` | Electronics & instrumentation specialist |
| `prof.john@university.edu` | `Staff@123` | `STAFF` | Department faculty staff member |
| `labadmin.mary@university.edu` | `Staff@123` | `STAFF` | Lab administrator staff member |
| `student.kevin@university.edu` | `Student@123` | `STUDENT` | Undergraduate computer engineering student |
| `student.lisa@university.edu` | `Student@123` | `STUDENT` | Electronics student |

---

## 8. Setup & How to Run

### Prerequisites
- **Java 25 SDK** installed
- **Apache Maven 3.9+** installed
- **MySQL Server 8.0+** running locally (or via Docker)

### Step 1: Prepare MySQL Database
Open your MySQL shell or client and execute:
```sql
CREATE DATABASE IF NOT EXISTS fixtrace_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Step 2: Configure Credentials
You can either set environment variables or edit `src/main/resources/application.properties`:

**Using Environment Variables (Recommended):**
```bash
# Windows PowerShell
$env:DB_URL="jdbc:mysql://localhost:3306/fixtrace_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_password"
$env:JWT_SECRET="404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"
$env:SEED_DATA_ENABLED="true"

# Linux / macOS
export DB_URL="jdbc:mysql://localhost:3306/fixtrace_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export DB_USERNAME="root"
export DB_PASSWORD="your_password"
export JWT_SECRET="404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"
export SEED_DATA_ENABLED="true"
```

### Step 3: Run the Application
```bash
# Build and run using Maven
mvn spring-boot:run
```

The application will start on `http://localhost:8080`.

---

## 9. How to Run Automated Tests

The test suite runs against an isolated, high-speed in-memory H2 database in MySQL compatibility mode. Tests require no external database setup.

```bash
mvn test
```

All 21 comprehensive integration and unit tests will run and report status.

---

## 10. Swagger / OpenAPI Documentation

Once the backend is running, access interactive API documentation at:

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON Spec**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

To test secured endpoints in Swagger UI:
1. Send `POST /api/auth/login` with `admin@fixtrace.com` and `Admin@123`.
2. Copy the `accessToken` from the response.
3. Click the **Authorize 🔓** button at the top right of the Swagger UI.
4. Paste the token into the `Value` box and click **Authorize**.

---

## 11. Testing with Postman

A pre-configured Postman collection is included in the project root: [`FixTrace.postman_collection.json`](./FixTrace.postman_collection.json).

### Postman Testing Workflow:
1. Open Postman -> click **Import** -> select `FixTrace.postman_collection.json`.
2. The collection variables are pre-configured:
   - `baseUrl`: `http://localhost:8080`
   - `jwtToken`: (automatically populated upon login)
3. Open folder **1. Authentication** -> run **Login - Admin**. The test script automatically captures the returned JWT token and stores it in the `jwtToken` variable for all subsequent requests.
4. Run requests in this logical sequence:
   - **Labs**: Create and list facilities
   - **Equipment**: Register workstations, search with filters (`?search=dell`), inspect maintenance history
   - **Issues**: Report an issue as student, assign a technician as admin, update status as technician
   - **Repair Records**: Record diagnosis and parts used
   - **Notifications**: Check unread alerts
   - **Dashboard**: View summary KPIs and chart aggregations

---

## 12. Complete API Endpoint Reference

### Authentication (`/api/auth`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Public | Register new user account |
| `POST` | `/api/auth/login` | Public | Authenticate user and obtain JWT token |
| `GET` | `/api/auth/me` | Authenticated | Retrieve authenticated user profile |

### User Management (`/api/users`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/users` | Admin | List users with pagination and role filter |
| `GET` | `/api/users/technicians` | Admin | Get list of active technicians for assignment |
| `GET` | `/api/users/{id}` | Admin / Self | View user profile by ID |
| `PUT` | `/api/users/{id}` | Admin / Self | Update profile (name, phone, role, active) |
| `DELETE` | `/api/users/{id}` | Admin | Deactivate user account |

### Lab Facilities (`/api/labs`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/labs` | Admin | Register new lab facility |
| `GET` | `/api/labs` | Authenticated | List all labs with equipment counts |
| `GET` | `/api/labs/{id}` | Authenticated | Get lab details by ID |
| `PUT` | `/api/labs/{id}` | Admin | Update lab details |
| `DELETE` | `/api/labs/{id}` | Admin | Soft delete lab (mark inactive) |
| `GET` | `/api/labs/{id}/equipment`| Authenticated | Get all equipment located in a lab |

### Equipment & Assets (`/api/equipment`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/equipment` | Admin | Register new hardware asset |
| `GET` | `/api/equipment` | Authenticated | List equipment (search, status, category, lab, paged) |
| `GET` | `/api/equipment/{id}` | Authenticated | Get equipment details |
| `PUT` | `/api/equipment/{id}` | Admin | Update equipment details |
| `DELETE` | `/api/equipment/{id}` | Admin | Retire equipment (preserves history) |
| `GET` | `/api/equipment/{id}/issues` | Authenticated | List all issues reported for equipment |
| `GET` | `/api/equipment/{id}/repair-history` | Authenticated | List all repair records for equipment |
| `GET` | `/api/equipment/{id}/maintenance-history` | Authenticated | Aggregated downtime, failures, and full history |

### Issue Reporting & Workflow (`/api/issues`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/issues` | Authenticated | Report problem (auto-extracts authenticated reporter) |
| `GET` | `/api/issues` | Authenticated | List issues (students/staff see own; admin/tech see all) |
| `GET` | `/api/issues/{id}` | Authenticated | Get issue details by ID |
| `PUT` | `/api/issues/{id}/assign` | Admin | Assign issue to a technician |
| `PUT` | `/api/issues/{id}/priority` | Admin | Update issue priority (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`) |
| `PUT` | `/api/issues/{id}/status` | Admin / Tech | Transition issue status through state machine |
| `PUT` | `/api/issues/{id}/close` | Reporter / Admin | Close issue (requires `RESOLVED` status first) |
| `PUT` | `/api/issues/{id}/reopen` | Reporter / Admin | Reopen resolved or closed issue |
| `POST` | `/api/issues/{id}/repair-records` | Admin / Tech | Create repair log for issue |
| `GET` | `/api/issues/{id}/repair-records` | Authenticated | List repair records for issue |

### Repair Records (`/api/repair-records`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/repair-records/{id}` | Authenticated | Get single repair record by ID |
| `PUT` | `/api/repair-records/{id}` | Admin / Author | Update diagnosis, parts used, or actions taken |

### Notifications (`/api/notifications`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/notifications` | Authenticated | View all notifications for current user |
| `GET` | `/api/notifications/unread`| Authenticated | View unread notifications |
| `PUT` | `/api/notifications/{id}/read` | Authenticated | Mark notification as read |
| `PUT` | `/api/notifications/read-all` | Authenticated | Mark all notifications as read |

### Administrative Dashboard (`/api/dashboard`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/dashboard/summary` | Admin | High-level KPIs, asset counts, avg resolution time |
| `GET` | `/api/dashboard/issues-by-status` | Admin | Issue counts and percentages by status |
| `GET` | `/api/dashboard/issues-by-priority` | Admin | Issue counts and percentages by priority |
| `GET` | `/api/dashboard/issues-by-lab` | Admin | Distribution of failures across labs |
| `GET` | `/api/dashboard/failure-prone-equipment` | Admin | Equipment ranked by highest failure count |

### Actuator Monitoring (`/actuator`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/actuator/health` | Public | System and database health status |
| `GET` | `/actuator/info` | Public | Application metadata |

---

## 13. Example JSON Request & Response Payloads

### 1. User Login
**`POST /api/auth/login`**
```json
{
    "email": "admin@fixtrace.com",
    "password": "Admin@123"
}
```

**Response (`200 OK`):**
```json
{
    "success": true,
    "message": "Authentication successful",
    "data": {
        "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
        "tokenType": "Bearer",
        "userId": 1,
        "email": "admin@fixtrace.com",
        "name": "System Admin",
        "role": "ADMIN"
    },
    "timestamp": "2026-10-20T09:00:00"
}
```

### 2. Report Issue
**`POST /api/issues`** (Header: `Authorization: Bearer <token>`)
```json
{
    "equipmentId": 2,
    "title": "Display distortion and horizontal scan lines",
    "description": "Flickering horizontal lines appear across panel when refresh rate is set to 144Hz.",
    "priority": "HIGH"
}
```

**Response (`201 Created`):**
```json
{
    "success": true,
    "message": "Issue reported successfully",
    "data": {
        "id": 6,
        "issueCode": "FT-2026-000006",
        "title": "Display distortion and horizontal scan lines",
        "description": "Flickering horizontal lines appear across panel when refresh rate is set to 144Hz.",
        "reportedById": 6,
        "reportedByName": "Kevin Flynn",
        "reportedByEmail": "student.kevin@university.edu",
        "equipmentId": 2,
        "equipmentAssetCode": "EQ-CS-002",
        "equipmentName": "Dell Precision 3660 Workstation",
        "labId": 1,
        "labCode": "LAB-CS-101",
        "labName": "Computer Science Main Lab",
        "priority": "HIGH",
        "status": "REPORTED",
        "assignedTechnicianId": null,
        "assignedTechnicianName": null,
        "assignedTechnicianEmail": null,
        "reportedAt": "2026-10-20T09:02:15",
        "assignedAt": null,
        "resolvedAt": null,
        "updatedAt": "2026-10-20T09:02:15"
    },
    "timestamp": "2026-10-20T09:02:15"
}
```

### 3. Validation Error Example
**`POST /api/auth/register`** (Invalid data)
```json
{
    "name": "A",
    "email": "not-an-email",
    "password": "123"
}
```

**Response (`400 Bad Request`):**
```json
{
    "timestamp": "2026-10-20T09:05:00",
    "status": 400,
    "error": "Validation Failed",
    "message": "Invalid request payload",
    "path": "/api/auth/register",
    "fieldErrors": {
        "name": "Name must be between 2 and 100 characters",
        "email": "Email must be valid",
        "password": "Password must be at least 6 characters"
    }
}
```
