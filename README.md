# LoanPaymentSystem - Full Stack (Single Application Backend + React UI)
Short Description- Loan Payment System within a single Spring Boot application. The application should have two core domains: 
1. Loan Domain: Manages loan creation and retrieval. 
2. Payment Domain: Handles payments made towards loans.


This repository contains:
- **Backend**: 
Spring Boot 3 (Java 21), 
JWT Auth (Register/Login/Roles), 
H2 + Postgres profiles, 
Flyway migrations, 
Redis cache (optional), 
RabbitMQ notifications (optional), 
Actuator, 
Swagger/OpenAPI.

- **Frontend**: 
React + Bootstrap 5 (Vite) with Register/Login/Dashboard pages.

- **DevOps**: 
Docker + docker-compose, 
Jenkins pipeline, 
Kubernetes templates, 
Terraform template.

- **1). Project Structure**: 

├── backend/                 # Spring Boot API (port 8081, context-path /api)
├── frontend/                # React UI (served on port 3000 via nginx in docker)
├── docker-compose.yml
├── Jenkinsfile
├── k8s/                     # Kubernetes templates
└── terraform/               # Terraform template

**2) Requirements (local)**

### Backend
- Java **21**
- Maven **3.9+**

### Frontend
- Node.js **20+**
- npm **9+**


**2) To clone the project from GitHub and build, test, and run it locally:**

The public repo is tmanmoseri1/LoanPaymentSystem on the main branch.

1. Clone the project
```bash
Open a terminal and run:
git clone https://github.com/tmanmoseri1/LoanPaymentSystem.git
cd LoanPaymentSystem
```
2. Build and test the backend locally
```bash
Go to the backend folder:
cd LoanPaymentSystem/backend

Run test
mvn clean test

Then packgage it:
mvn clean package

Run the frontend locally
mvn clean spring-boot:run
```
Open:  `http://localhost:8081/api`

3. Build and test the frontend locally
```bash
Go to the frontend folder:
cd LoanPaymentSystem/frontend

Install dependencies
npm install

Build the frontend
npm build

Run the frontend locally
npm run dev
 ```
Open: `http://localhost:5173`


**4) Build, run, test locally (without Docker)**

### 4.1 Backend (H2 in-memory)
From repo root:
```bash
cd backend
mvn clean test
mvn spring-boot:run -Dspring-boot.run.profiles=h2

- API: `http://localhost:8081/api`
- H2 Console: `http://localhost:8081/api/h2-console`  
  JDBC URL: `jdbc:h2:mem:loans`

  Default admin is created on startup:
- `admin / admin123`
```
### 4.2 Frontend (dev mode)
From repo root:
```bash
cd frontend
npm install
npm run dev
```
Open: `http://localhost:5173`

The React app calls the backend at `http://localhost:8081/api`.

### 5) Run everything with Docker Compose (recommended)

From repo root:
```bash
docker compose up --build

Services:
- Postgres: `localhost:5432`
- Redis: `localhost:6379`
- RabbitMQ: `localhost:5672` + Management UI `http://localhost:15672` (guest/guest)
- Backend: `http://localhost:8081/api`
- Frontend: `http://localhost:3000`

Stop:

docker compose down
```
## Run everything with Docker manually
	
run backend container  
----------------------
```bash
docker run -d \
    --name backend-service \
    --network loan-net \
    -p 8081:8081 \
    loan-backend:local
create a Docker network
--------------------------
docker network create loan-net

run frontend container on the same network
-------------------------------------------
docker run -d \
    --name loan-frontend \
    --network loan-net \
    -p 3000:80 \
    loan-frontend:local

    Stop:
    docker compose down
```

## 6) API Endpoints (core)

### Auth
- `POST /api/auth/register` (CUSTOMER registration)
- `POST /api/auth/login` → returns `{ token, username, roles }`

### Loan Domain
- `POST /api/loans` → create loan  
- `GET /api/loans/{loanId}` → loan details  
- `GET /api/loans/me` → my loans

### Payment Domain
- `POST /api/payments` → record payment for a loan  
- `GET /api/payments/loan/{loanId}` → list payments for a loan

### Business rules
- Payment reduces loan `remainingBalance`
- Payment must not exceed remaining balance
- Loan becomes `SETTLED` when balance reaches `0.00`

---

## 7) Swagger / OpenAPI

Swagger UI:
- `http://localhost:8081/api/swagger-ui.html`

How to authorize in Swagger UI:
1. Call **POST /auth/login**
2. Copy the `token`
3. Click **Authorize** → paste `Bearer <token>`

---

## 8) Unit tests

Backend unit/integration tests:
```bash
cd backend
mvn test
```

Included tests:
- Loan creation works (`LoanServiceTest`)
- Payment reduces balance and settles when full payment (`PaymentServiceTest`)

---

