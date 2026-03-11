# Vehicle Service

A Spring Boot microservice for managing vehicle data. It integrates with two downstream services:

- **user-profile service** – fetches the owner's personal details for a given vehicle  
- **identity-service** – sends OTP notifications to vehicle owners

The repository layer uses an in-memory `HashMap` as a lightweight data store (pre-loaded with sample data).

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2 |
| Build tool | Maven |
| Testing | JUnit 5 · Mockito · MockMvc |
| In-memory DB | `java.util.HashMap` |

---

## Project Structure

```
src/
└── main/
    └── java/com/vehicleservice/
        ├── VehicleServiceApplication.java   # Entry point
        ├── config/
        │   └── AppConfig.java               # Bean definitions (RestTemplate)
        ├── controller/
        │   └── VehicleController.java       # REST endpoints
        ├── service/
        │   └── VehicleService.java          # Business logic
        ├── repository/
        │   └── VehicleRepository.java       # HashMap-backed store + sample data
        ├── client/
        │   ├── UserProfileClient.java       # Calls user-profile service
        │   └── IdentityServiceClient.java   # Calls identity-service
        ├── model/
        │   ├── Vehicle.java
        │   ├── UserProfile.java
        │   ├── VehicleWithOwner.java
        │   ├── OtpRequest.java
        │   └── OtpResponse.java
        └── exception/
            ├── VehicleNotFoundException.java
            ├── DownstreamServiceException.java
            └── GlobalExceptionHandler.java
```

---

## API Endpoints

### Vehicle CRUD

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/vehicles` | List all vehicles |
| `GET` | `/api/vehicles/{id}` | Get vehicle by ID |
| `GET` | `/api/vehicles/owner/{ownerUserId}` | List vehicles by owner |
| `POST` | `/api/vehicles` | Add a new vehicle |
| `PUT` | `/api/vehicles/{id}` | Update a vehicle |
| `DELETE` | `/api/vehicles/{id}` | Delete a vehicle |

### Downstream integrations

| Method | Path | Downstream call | Description |
|--------|------|----------------|-------------|
| `GET` | `/api/vehicles/{id}/owner` | user-profile service | Get vehicle with owner profile |
| `POST` | `/api/vehicles/{id}/send-otp?purpose=` | identity-service | Send OTP to vehicle owner |

---

## Configuration

Edit `src/main/resources/application.properties`:

```properties
server.port=8080

# URLs for downstream services
user-profile.service.url=http://localhost:8081
identity.service.url=http://localhost:8082
```

---

## Running Locally

```bash
# Build
mvn clean package -DskipTests

# Run
java -jar target/vehicle-service-1.0.0.jar
```

Or with Maven:

```bash
mvn spring-boot:run
```

The service starts on **http://localhost:8080**.

---

## Sample Requests

### List all vehicles
```bash
curl http://localhost:8080/api/vehicles
```

### Get vehicle by ID
```bash
curl http://localhost:8080/api/vehicles/v-001
```

### Add a new vehicle
```bash
curl -X POST http://localhost:8080/api/vehicles \
  -H "Content-Type: application/json" \
  -d '{
    "registrationNumber": "KA03XY1111",
    "make": "Honda",
    "model": "City",
    "year": 2023,
    "ownerUserId": "user-200",
    "color": "Silver",
    "fuelType": "Petrol",
    "vehicleType": "Sedan"
  }'
```

### Get vehicle with owner profile (calls user-profile service)
```bash
curl http://localhost:8080/api/vehicles/v-001/owner
```

### Send OTP to vehicle owner (calls identity-service)
```bash
curl -X POST "http://localhost:8080/api/vehicles/v-001/send-otp?purpose=OWNERSHIP_TRANSFER"
```

---

## Running Tests

```bash
mvn test
```

28 tests across controller, service, and repository layers.
