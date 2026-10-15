# QueueEase: Multi-Industry Local Business Appointments & Walk-In Queue Management

**QueueEase** is a full-stack web application designed for managing appointments and live walk-in queues across diverse small local service businesses: **Clinics & Healthcare**, **Play Areas & Family Entertainment Centers**, **Salons & Personal Grooming**, **Auto & Equipment Repairs**, **Gadget/Electronics Repair**, and **Pet Care & Grooming**.

It unifies walk-in queue management with advance appointment bookings in a clean, familiar, light-colored interface.

---

## 💡 Key Highlights

- **Live Walk-In Queue Tracker**: Customers join a virtual queue from their phones or desktop browsers, receive a digital ticket (e.g. `C-101`, `J-102`, `A-101`, `P-101`), view their exact live position, and watch their estimated wait time update automatically.
- **Dynamic Wait Time Recalculation**: Whenever staff update visit status (calling the next customer, starting service, or marking a ticket complete), QueueEase dynamically recalculates the wait time for all waiting customers:
  $$\text{Estimated Wait} = (\text{Customers Ahead} \times \text{Average Service Duration}) + \text{Remaining In-Service Time}$$
- **Multi-Industry Categories & Filter**: Customers can filter businesses by category pills:
  - 🩺 **Clinics & Health** (Apex Family Care & Dental Clinic)
  - 🎪 **Play Areas & Kids** (JumpZone Kids Play Park & Adventure Arena)
  - ✂️ **Salons & Grooming** (Luxe Hair & Beauty Lounge)
  - 🚗 **Auto & Repairs** (Precision Auto Care & Quick Lube)
  - 📱 **Gadget / Tech Repairs** (QuickFix Express Phone & Laptop Repair)
  - 🐾 **Pet Care** (Happy Paws Pet Spa & Veterinary Clinic)
- **Conflict-Free Appointment Scheduling**: Customers book appointments in advance. QueueEase strictly checks operating hours (e.g. 08:30 AM – 07:30 PM) and prevents double-booking or conflicting time slots.
- **Duplicate Queue Protection**: A customer is safeguarded against holding duplicate active tickets at the same business simultaneously.
- **Staff Operations Console**: Desk staff and operators can call next customers to designated counters/stations/rooms, transition visit milestones (`WAITING` $\rightarrow$ `CALLED` $\rightarrow$ `IN_SERVICE` $\rightarrow$ `COMPLETED` / `CANCELLED`), add in-person walk-ins, customize service menus, and view hourly flow analytics.
- **Administrator Console**: Admins oversee all user accounts, approve or suspend business listings, and review system-wide traffic metrics.

---

## 🛠️ Technology Stack

- **Backend**: Java 17, Spring Boot 3.3.4, Spring Data JPA, Spring Security with BCrypt & JWT (JJWT 0.12.5), Maven.
- **Database**: MySQL 8.0 (localhost:3306, database: `queueease`).
- **Frontend**: React 19, Vite, Lucide Icons, clean light CSS design system.
- **Architecture**: RESTful APIs between React frontend and Spring Boot backend.

---

## 🎨 Design Direction

QueueEase strictly adheres to a **simple, familiar, light-colored design**:
- Clean white card surfaces (`#ffffff`) on a soft, light warm-gray background (`#f8fafc`).
- Crisp, dark readable typography (`#0f172a` headings, `#334155` body text).
- Single calm primary accent: slate blue (`#2563eb`), with subtle status indicators (amber for waiting, emerald green for in-service/completed, soft red for cancellations).
- **Zero** dark mode, black backgrounds, neon colors, glowing effects, gradients, glassmorphism, or AI-themed visuals.
- Fully responsive across desktop screens, tablets, and mobile devices.

---

## 👥 Demo Accounts (1-Click Switcher in UI Header)

The application header features a **Quick Demo Switcher** allowing instant 1-click login without typing:

| Role / Category | Demo User | Email | Password | Business Context |
| :--- | :--- | :--- | :--- | :--- |
| **Customer** | Sarah Jenkins | `sarah@example.com` | `customer123` | Browses businesses, joins queues, books slots |
| **Customer** | Liam Chen | `liam@example.com` | `password123` | Active customer in line |
| **Staff: Clinic** | Dr. Maya Lin | `clinic_staff@queueease.com` | `staff123` | Apex Family Care & Dental Clinic |
| **Staff: Play Park** | Leo Brooks | `play_staff@queueease.com` | `staff123` | JumpZone Kids Play Park & Adventure Arena |
| **Staff: Salon** | Alex Vance | `salon_staff@queueease.com` | `staff123` | Luxe Hair & Beauty Lounge |
| **Staff: Auto Shop** | Marcus Miller | `auto_staff@queueease.com` | `staff123` | Precision Auto Care & Quick Lube |
| **Staff: Tech Repair** | Chloe Bennett | `tech_staff@queueease.com` | `staff123` | QuickFix Express Phone & Laptop Repair |
| **Staff: Pet Spa** | Emma Watson | `pet_staff@queueease.com` | `staff123` | Happy Paws Pet Spa & Veterinary Clinic |
| **Administrator** | System Admin | `admin@queueease.com` | `admin123` | System Console (User & Business Management) |

---

## 🏢 Seeded Diverse Local Businesses

1. **Apex Family Care & Dental Clinic** (`Clinics & Health`)
   - Services: General Physician Consultation ($50.00, 20m), Ultrasonic Dental Cleaning & Exam ($85.00, 40m), Pediatric Child Wellness Visit ($60.00, 25m), Seasonal Flu Vaccination & Vitals ($30.00, 15m).
   - Hours: Mon-Sat 08:30 AM - 07:30 PM
2. **JumpZone Kids Play Park & Adventure Arena** (`Play Areas & Kids`)
   - Services: 60-Min Open Trampoline & Soft Play Pass ($18.00, 60m), Toddler Sensory Zone ($12.00, 45m), Laser Tag Mission ($10.00, 20m), All-Access 2-Hour Play Combo ($28.00, 120m).
   - Hours: Mon-Sat 08:30 AM - 07:30 PM
3. **Luxe Hair & Beauty Lounge** (`Salons & Grooming`)
   - Services: Precision Haircut & Blowdry ($45.00, 45m), Classic Fade & Razor Lineup ($30.00, 30m), Balayage & Conditioning Gloss ($125.00, 90m), Express Dry Haircut ($25.00, 20m).
   - Hours: Mon-Sat 08:30 AM - 07:30 PM
4. **Precision Auto Care & Quick Lube** (`Auto & Repairs`)
   - Services: Full Synthetic Oil & Filter Service ($65.00, 30m), Brake System Inspection & Pad Service ($130.00, 45m), OBD-II Computerized Diagnostic Scan ($40.00, 20m).
   - Hours: Mon-Sat 08:30 AM - 07:30 PM
5. **QuickFix Express Phone & Laptop Repair** (`Auto & Repairs`)
   - Services: Smartphone Screen Replacement ($79.00, 30m), Battery Diagnostics & Replacement ($49.00, 25m), Charging Port & Speaker Debris Cleaning ($25.00, 15m).
   - Hours: Mon-Sat 08:30 AM - 07:30 PM
6. **Happy Paws Pet Spa & Veterinary Clinic** (`Pet Care`)
   - Services: Full Dog Bath, Groom & De-Shed ($55.00, 60m), Puppy Gentle Wash & Ear Cleaning ($35.00, 35m), Routine Vet Wellness & Vaccine Review ($45.00, 20m), Pedicure & Paw Balm Treatment ($18.00, 15m).
   - Hours: Mon-Sat 08:30 AM - 07:30 PM

---

## 🚀 Setup & Running Instructions

### Prerequisites
- **Java 17** (or higher)
- **MySQL 8.0** running on `localhost:3306`
- **Node.js 18+** & npm (for frontend modification)

### Project Architecture
```text
QueuEase/
├── backend/                   # Spring Boot 3 & Java 17 Application
│   ├── src/                   # REST controllers, JPA entities, security, services
│   ├── pom.xml                # Maven configuration with MySQL, PostgreSQL, H2
│   ├── Dockerfile             # Multi-stage container build
│   └── mvnw.cmd               # Maven wrapper
├── frontend/                  # React 19 & Vite Web Application
│   ├── src/                   # Components, views, API client, design system
│   ├── package.json           # Dependencies and scripts
│   ├── vercel.json            # Vercel SPA routing
│   └── vite.config.js         # Build tooling & proxy
├── Dockerfile                 # Root Dockerfile for Render deployment
├── render.yaml                # Render Blueprint specification
├── vercel.json                # Root Vercel deployment configuration
├── start-queueease.bat        # 1-Click Windows launcher
└── push-to-github.bat         # Git push helper
```

### Database Configuration
The application connects to MySQL via `backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/queueease?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=${DB_USER:root}
spring.datasource.password=${DB_PASSWORD:1256}
```
*(Override via environment variable `DB_PASSWORD` if your local MySQL root password differs)*.

### Run the Pre-Built Full-Stack Application
The pre-compiled React frontend is embedded directly in Spring Boot:
```powershell
# From the project directory:
java -jar backend\target\queueease-1.0.0.jar
```
*(Or simply double-click `start-queueease.bat`)*.
Open **[http://localhost:8080](http://localhost:8080)** in your browser.

### Development Mode (Optional)
1. **Backend**:
   ```powershell
   cd backend
   .\mvnw.cmd spring-boot:run
   ```
2. **Frontend** (in another terminal):
   ```powershell
   cd frontend
   npm run dev
   ```
   Open **[http://localhost:5173](http://localhost:5173)** (Vite proxies `/api` to port 8080).

---

## 🛡️ Core Safeguards & Validation

1. **Duplicate Queue Guard**:
   - `POST /api/queue/join` checks if the customer already holds an active ticket (`WAITING`, `CALLED`, or `IN_SERVICE`) at the chosen business. If so, it returns HTTP 400 preventing duplicate entries.
2. **Business Hours Enforcement**:
   - `POST /api/bookings` verifies that the appointment starts and finishes strictly within the business's operating hours (`openTime` and `closeTime`).
3. **Collision Detection**:
   - Appointments check existing bookings for the same business/time window and prevent conflicting double bookings.
4. **Role-Based API Security**:
   - `/api/admin/**` $\rightarrow$ requires `ROLE_ADMIN`
   - `/api/staff/**` $\rightarrow$ requires `ROLE_STAFF` or `ROLE_ADMIN`
   - `/api/queue/**`, `/api/bookings/**` $\rightarrow$ authenticated customer
   - `/api/businesses/**`, `/api/auth/**` $\rightarrow$ public

---

## 🔌 Main API Endpoints Summary

| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Public | Register customer or business staff account |
| `POST` | `/api/auth/login` | Public | Sign in and retrieve JWT Bearer token |
| `GET` | `/api/businesses` | Public | List all active businesses with live wait times |
| `GET` | `/api/businesses/category/{category}` | Public | Filter businesses by category |
| `GET` | `/api/businesses/{id}` | Public | Business details, hours, and service menu |
| `POST` | `/api/queue/join` | Customer | Join walk-in queue and receive ticket |
| `POST` | `/api/queue/{id}/leave` | Customer | Cancel ticket and leave queue |
| `GET` | `/api/queue/business/{id}/active` | Customer | Check active ticket status and wait estimate |
| `GET` | `/api/queue/my-history` | Customer | Customer's queue visit history |
| `POST` | `/api/bookings` | Customer | Schedule advance appointment |
| `GET` | `/api/bookings/my-bookings` | Customer | Customer's scheduled appointments |
| `POST` | `/api/bookings/{id}/cancel` | Customer | Cancel upcoming appointment |
| `GET` | `/api/staff/queue/business/{id}` | Staff | Active queue entries for business |
| `POST` | `/api/staff/queue/business/{id}/call-next` | Staff | Call next customer to station/counter/room |
| `PATCH` | `/api/staff/queue/{id}/status` | Staff | Update queue status (CALLED, IN_SERVICE, COMPLETED, CANCELLED) |
| `POST` | `/api/staff/queue/business/{id}/walk-in` | Staff | Front-desk staff adds in-person walk-in |
| `PUT` | `/api/businesses/{id}` | Staff | Update business capacity, hours, and duration rules |
| `POST` | `/api/businesses/{id}/services` | Staff | Add new service |
| `DELETE` | `/api/businesses/services/{id}` | Staff | Remove service |
| `GET` | `/api/admin/users` | Admin | List all user accounts with status toggle |
| `PATCH` | `/api/admin/users/{id}/toggle-active` | Admin | Activate or disable user account |
| `GET` | `/api/admin/businesses` | Admin | List all business listings |
| `PATCH` | `/api/admin/businesses/{id}/toggle-active` | Admin | Approve or suspend business listing |
| `GET` | `/api/admin/stats` | Admin | Platform statistics |
