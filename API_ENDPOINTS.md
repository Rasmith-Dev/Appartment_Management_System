# Apartment Management System - API Endpoints & URLs

## 🚀 Quick Access URLs

### Backend URLs (Port 8080)
- **Swagger UI:** http://localhost:8080/swagger-ui/index.html
- **API Documentation:** http://localhost:8080/api-docs
- **Health Check:** http://localhost:8080/actuator/health

### Frontend URLs (Port 3000)
- **React Application:** http://localhost:3000
- **Login Page:** http://localhost:3000/login
- **Dashboard:** http://localhost:3000/dashboard

## 📋 API Endpoints

### Authentication
- `POST /api/auth/signup` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout

### Apartments
- `GET /api/apartments` - Get all apartments
- `GET /api/apartments/{id}` - Get apartment by ID
- `POST /api/apartments` - Create new apartment
- `PUT /api/apartments/{id}` - Update apartment
- `DELETE /api/apartments/{id}` - Delete apartment

### Tenants
- `GET /api/tenants` - Get all tenants
- `GET /api/tenants/{id}` - Get tenant by ID
- `POST /api/tenants` - Create new tenant
- `PUT /api/tenants/{id}` - Update tenant
- `DELETE /api/tenants/{id}` - Delete tenant

### Flats
- `GET /api/flats` - Get all flats
- `GET /api/flats/{id}` - Get flat by ID
- `POST /api/flats` - Create new flat
- `PUT /api/flats/{id}` - Update flat
- `DELETE /api/flats/{id}` - Delete flat

### Complaints
- `GET /api/complaints` - Get all complaints
- `GET /api/complaints/{id}` - Get complaint by ID
- `POST /api/complaints` - Create new complaint
- `PUT /api/complaints/{id}` - Update complaint
- `DELETE /api/complaints/{id}` - Delete complaint

### Payments
- `GET /api/payments` - Get all payments
- `GET /api/payments/{id}` - Get payment by ID
- `POST /api/payments` - Create new payment
- `PUT /api/payments/{id}` - Update payment
- `DELETE /api/payments/{id}` - Delete payment

### Documents
- `GET /api/documents` - Get all documents
- `GET /api/documents/{id}` - Get document by ID
- `POST /api/documents` - Upload document
- `PUT /api/documents/{id}` - Update document
- `DELETE /api/documents/{id}` - Delete document

## 🔧 Development URLs

### Database
- **PostgreSQL:** localhost:5432
- **Database Name:** apartment_management
- **Username:** postgres
- **Password:** postgres

### Build & Run Commands

#### Backend (Spring Boot)
```bash
cd backend
export JAVA_HOME="/opt/homebrew/opt/openjdk@17"
export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"
mvn spring-boot:run
```

#### Frontend (React)
```bash
cd frontend
npm start
```

#### Both Applications
```bash
chmod +x start-apps.sh
./start-apps.sh
```

## 📊 Monitoring & Health

### Health Checks
- **Application Health:** http://localhost:8080/actuator/health
- **Info Endpoint:** http://localhost:8080/actuator/info
- **Metrics:** http://localhost:8080/actuator/metrics

### Logs
- **Application Logs:** Check console output when running `mvn spring-boot:run`
- **Database Logs:** Check PostgreSQL logs

## 🔐 Security

### JWT Configuration
- **Secret Key:** Configured in `application.properties`
- **Expiration:** 24 hours (86400000 ms)
- **Algorithm:** HS512

### CORS Configuration
- **Allowed Origins:** http://localhost:3000
- **Allowed Methods:** GET, POST, PUT, DELETE, OPTIONS
- **Allowed Headers:** Authorization, Content-Type

## 📝 Notes

- All API endpoints require JWT authentication except `/api/auth/*`
- Swagger UI provides interactive API documentation
- Health check endpoint is useful for monitoring application status
- Database schema is automatically created on first run 