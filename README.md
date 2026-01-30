# Apartment Management System

A complete, ready-to-run starter for an Apartment Management System with:
- **Backend:** Spring Boot (Java) + PostgreSQL
- **Frontend:** React.js (modern, responsive)
- **API communication:** REST
- **Database:** PostgreSQL (with sample schema and data)

## 🚀 Quick Access URLs

### Backend (Port 8080)
- **Swagger UI:** http://localhost:8080/swagger-ui/index.html
- **API Documentation:** http://localhost:8080/api-docs
- **Health Check:** http://localhost:8080/actuator/health

### Frontend (Port 3000)
- **React Application:** http://localhost:3000
- **Login Page:** http://localhost:3000/login
- **Dashboard:** http://localhost:3000/dashboard

## 📋 Project Structure
```
apartment-management/
├── backend/      # Spring Boot app
├── frontend/     # React app
├── API_ENDPOINTS.md  # Complete API documentation
└── README.md     # Setup instructions
```

## 📚 Documentation

- **[API_ENDPOINTS.md](API_ENDPOINTS.md)** - Complete API documentation with all endpoints
- **Swagger UI** - Interactive API documentation at http://localhost:8080/swagger-ui/index.html
- **API Docs** - Raw OpenAPI specification at http://localhost:8080/api-docs
Project Structure

text
Apply

apartment-management/├── backend/      # Spring Boot app├── frontend/     # React app└── README.md     # Setup instructions
Features in the Starter

User authentication (simple, for demo)
CRUD for apartments, tenants, complaints, payments
REST API (Spring Boot)
PostgreSQL schema and sample data
React UI: login, dashboard, basic forms/tables
What You’ll Get

All code files for backend and frontend
SQL file to set up the database
Step-by-step instructions to run locally (with or without Docker)

Key Features to Build

Apartment/Unit management (CRUD)
Tenant management (CRUD)
Rent and maintenance collection tracking
Complaint/issue tracking (with status)
Payment reminders/notifications
Reports and analytics
User roles (admin, manager, tenant)
Document storage (leases, receipts, etc.)

## 🔮 Future Enhancements

- Support multiple tenants per flat via a Lease/Occupancy model.
- Associate owners/managers to multiple flats/apartments.
- Model household/roommate groups within a flat.
- Add tenant move-in/move-out history per flat.
