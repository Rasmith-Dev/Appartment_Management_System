#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${GREEN}Starting Apartment Management System...${NC}"

# Ensure Java 17 is available for Spring Boot 3.x
if [ -z "$JAVA_HOME" ] && [ -d "/opt/homebrew/opt/openjdk@17" ]; then
    export JAVA_HOME="/opt/homebrew/opt/openjdk@17"
    export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"
fi

# Function to start backend
start_backend() {
    echo -e "${BLUE}Starting Backend (Spring Boot) on port 8080...${NC}"
    cd backend
    mvn spring-boot:run
}

# Function to start frontend
start_frontend() {
    echo -e "${BLUE}Starting Frontend (React) on port 3000...${NC}"
    cd frontend
    npm start
}

# Start both applications in parallel
start_backend &
BACKEND_PID=$!

# Wait a moment for backend to start
sleep 5

start_frontend &
FRONTEND_PID=$!

echo -e "${GREEN}Both applications are starting...${NC}"
echo -e "${GREEN}Backend will be available at: http://localhost:8080${NC}"
echo -e "${GREEN}Frontend will be available at: http://localhost:3000${NC}"
echo -e "${GREEN}Swagger UI will be available at: http://localhost:8080/swagger-ui/index.html${NC}"

# Wait for both processes
wait $BACKEND_PID $FRONTEND_PID 