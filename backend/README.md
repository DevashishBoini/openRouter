# OpenRouter Backend API

Spring Boot REST API with JWT authentication, PostgreSQL database, and AI model/provider management.

## Prerequisites

- Java 17+
- IntelliJ IDEA
- PostgreSQL Database (Supabase recommended)

## Setup

### 1. Open Project in IntelliJ
1. `File` → `Open` → Select the `backend` folder
2. Click **"Setup SDK"** if prompted → Select Java 17
3. Wait for Maven to download dependencies

### 2. Configure Database

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://YOUR-HOST:5432/postgres
spring.datasource.username=YOUR-USERNAME
spring.datasource.password=YOUR-PASSWORD

# JWT Secret (change in production!)
jwt.secret=your-secret-key-here
jwt.expiration=3600000
```

For Supabase: **Settings → Database → Connection String [Session Pooler]**

## Run Application

### In IntelliJ:
1. Open `src/main/java/backend/MainApplication.java`
2. Click green ▶️ button next to `main()` method
3. Wait for: `Started MainApplication in X seconds`

### Keyboard shortcuts:
- **Mac:** `⌃ R` or `⇧ F10`
- **Windows/Linux:** `Shift + F10`

Server runs on: **http://localhost:8080**

## API Endpoints

### 🔓 Public Endpoints (No Authentication)

#### Authentication
```bash
# Signup
POST /v1/auth/signup
Content-Type: application/json
{
  "email": "user@example.com",
  "password": "password123"
}

# Login
POST /v1/auth/login
Content-Type: application/json
{
  "email": "user@example.com",
  "password": "password123"
}
# Returns: { "token": "jwt-token", "userId": "uuid" }
```

#### Models & Providers (Public Access)
```bash
# Get all models
GET /api/v1/models

# Get all providers
GET /api/v1/providers

# Get all providers for a specific model (with pricing)
GET /api/v1/models/{modelId}/providers
```

### 🔒 Protected Endpoints (Requires JWT Token)

All protected endpoints require the `Authorization` header:
```
Authorization: Bearer {your-jwt-token}
```

#### API Key Management
```bash
# Create new API key
POST /api/v1/api-keys/create
Content-Type: application/json
Authorization: Bearer {token}
{
  "apiKeyName": "My API Key"
}

# Get all API keys
GET /api/v1/api-keys
Authorization: Bearer {token}

# Disable/Enable API key
PATCH /api/v1/api-keys/disable/{apiKeyId}
Content-Type: application/json
Authorization: Bearer {token}
{
  "disabled": true
}

# Delete API key (soft delete)
PATCH /api/v1/api-keys/delete/{apiKeyId}
Authorization: Bearer {token}
```

#### Payments
```bash
# Onramp 1000 credits
POST /api/v1/payments/onramp
Authorization: Bearer {token}
```

### Example curl Commands

```bash
# Signup
curl -X POST http://localhost:8080/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'

# Login
curl -X POST http://localhost:8080/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'

# Get models (no auth required)
curl http://localhost:8080/api/v1/models

# Create API key (requires auth)
curl -X POST http://localhost:8080/api/v1/api-keys/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"apiKeyName":"My Key"}'

# Onramp credits
curl -X POST http://localhost:8080/api/v1/payments/onramp \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Project Structure

```
backend/
├── src/main/java/backend/
│   ├── MainApplication.java              # Application entry point
│   │
│   ├── controller/                       # REST API Controllers
│   │   ├── AuthController.java           # Signup, login
│   │   ├── ApiKeyController.java         # API key CRUD
│   │   ├── ModelController.java          # Models, providers
│   │   └── PaymentController.java        # Credit onramping
│   │
│   ├── service/                          # Business Logic
│   │   ├── UserService.java              # User operations, auth
│   │   ├── ApiKeyService.java            # API key management
│   │   ├── ModelService.java             # Model/provider queries
│   │   └── PaymentService.java           # Payment transactions
│   │
│   ├── repository/                       # Data Access (JPA)
│   │   ├── UserRepository.java
│   │   ├── ApiKeyRepository.java
│   │   ├── ModelRepository.java
│   │   ├── ProviderRepository.java
│   │   ├── ModelProviderMappingRepository.java
│   │   └── OnRampTransactionRepository.java
│   │
│   ├── dbModel/                          # Database Entities
│   │   ├── User.java                     # Users (email, password, credits)
│   │   ├── ApiKey.java                   # API keys (hashed, soft delete)
│   │   ├── Model.java                    # AI models
│   │   ├── Provider.java                 # AI providers
│   │   ├── ModelProviderMapping.java     # Model-provider pricing
│   │   ├── Company.java                  # Model companies
│   │   ├── OnRampTransaction.java        # Payment history
│   │   └── Conversation.java             # Chat history
│   │
│   ├── dto/                              # Data Transfer Objects
│   │   ├── Requests/                     # Request DTOs
│   │   │   ├── SignupRequest.java
│   │   │   ├── LoginRequest.java
│   │   │   ├── ApiKeyCreateRequest.java
│   │   │   └── ApiKeyDisabledStatusUpdateRequest.java
│   │   ├── Responses/                    # Response DTOs
│   │   │   ├── SignupResponse.java
│   │   │   ├── LoginResponse.java
│   │   │   ├── ApiKeyCreateResponse.java
│   │   │   ├── ApiKeyGetResponse.java
│   │   │   ├── ModelResponse.java
│   │   │   ├── ProviderResponse.java
│   │   │   ├── ModelProviderResponse.java
│   │   │   └── OnRampResponse.java
│   │   ├── BaseApiResponse.java
│   │   ├── SuccessResponse.java
│   │   └── ErrorResponse.java
│   │
│   ├── security/                         # Security Configuration
│   │   ├── UserPrincipal.java            # Custom user details
│   │   └── JwtAuthenticationEntryPoint.java
│   │
│   ├── filter/                           # Request Filters
│   │   ├── JwtAuthenticationFilter.java  # JWT validation
│   │   └── RequestTracingFilter.java     # Request logging
│   │
│   ├── config/
│   │   └── SecurityConfig.java           # Spring Security config
│   │
│   ├── advice/                           # Global Exception Handling
│   │   ├── GlobalExceptionHandler.java   # Exception handlers
│   │   └── ApiResponseAdvice.java        # Response wrapping
│   │
│   ├── annotation/                       # Custom Annotations
│   │   ├── CurrentUser.java              # Inject authenticated user
│   │   └── SuccessMessage.java           # Success message annotation
│   │
│   ├── utils/                            # Utilities
│   │   ├── JwtHandler.java               # JWT generation/validation
│   │   ├── ApiKeyHandler.java            # API key generation/hashing
│   │   └── PasswordHandler.java          # Password hashing (BCrypt)
│   │
│   ├── mapper/
│   │   └── DtoMapper.java                # Entity ↔ DTO mapping (MapStruct)
│   │
│   └── exception/                        # Custom Exceptions
│       ├── ResourceNotFoundException.java
│       ├── EmailAlreadyExistsException.java
│       └── InvalidCredentialsException.java
│
└── src/main/resources/
    ├── application.properties            # Configuration
    └── logback-spring.xml                # Logging configuration
```

## Database Schema

| Entity | Description |
|--------|-------------|
| **User** | User accounts (email, password, credits) |
| **ApiKey** | API keys with hashing, soft delete, usage tracking |
| **Model** | AI model definitions (name, slug, company) |
| **Provider** | AI service providers (name, website) |
| **ModelProviderMapping** | Model-provider relationships with pricing |
| **Company** | Companies that create AI models |
| **OnRampTransaction** | Payment/credit transaction history |
| **Conversation** | Chat conversation history |

Tables are automatically created/updated by Hibernate.

## Architecture

```
Request → Filter → Controller → Service → Repository → Database
          (JWT)   (REST API)   (Business) (Data)      (PostgreSQL)
                  (DTOs)        (Entities)
```

### Key Layers:

- **Filter:** JWT validation, request tracing
- **Controller:** REST endpoints, request validation
- **Service:** Business logic, transactions
- **Repository:** Database operations (Spring Data JPA)
- **Entity:** Database models (JPA/Hibernate)
- **DTO:** Request/response objects
- **Advice:** Global exception handling, response wrapping

## Security Features

- ✅ **JWT Authentication** - Stateless token-based auth
- ✅ **Password Hashing** - BCrypt with salt
- ✅ **API Key Hashing** - Secure API key storage
- ✅ **Soft Delete** - API keys marked as deleted, not removed
- ✅ **User Ownership Validation** - Users can only access their own resources
- ✅ **Request Tracing** - All requests logged with unique trace ID
- ✅ **Global Exception Handling** - Consistent error responses

### Public Routes (No Auth):
- `/v1/auth/**` - Authentication endpoints
- `/api/v1/models` - Model listings
- `/api/v1/providers` - Provider listings
- `/api/v1/models/*/providers` - Model-provider mappings

### Protected Routes (Auth Required):
- `/api/v1/**` - All other endpoints

## Error Handling

All errors return consistent JSON responses:

```json
{
  "success": false,
  "message": "Error description",
  "data": null
}
```

### HTTP Status Codes:
- `200 OK` - Success
- `201 Created` - Resource created
- `400 Bad Request` - Validation error, invalid input
- `401 Unauthorized` - Missing/invalid JWT token
- `404 Not Found` - Resource not found
- `409 Conflict` - Email already exists
- `500 Internal Server Error` - Server error
- `503 Service Unavailable` - Database error

## Technologies

- **Spring Boot 3.2.3** - Application framework
- **Spring Security** - Authentication & authorization
- **Spring Data JPA** - Database access
- **Hibernate** - ORM (Object-Relational Mapping)
- **PostgreSQL** - Database
- **JWT (jjwt)** - Token-based authentication
- **MapStruct** - DTO mapping
- **Lombok** - Boilerplate reduction
- **Logback** - Logging
- **Maven** - Dependency management

## Development

### Run Tests
- Right-click test file → **"Run"**
- Or: **Ctrl + Shift + F10** (Mac: `⌃ ⇧ R`)

### Build JAR
**Maven panel** → **Lifecycle** → Double-click **package**

Creates: `target/backend-0.0.1-SNAPSHOT.jar`

Run JAR:
```bash
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

### Stop Application
- Click red stop button ⏹️
- Or: **Ctrl + F2** (Mac: `⌘ F2`)

## Configuration Options

```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT
jwt.secret=your-secret-key-minimum-256-bits
jwt.expiration=3600000

# Logging
logging.level.backend=INFO
```

## Notes

- **API Keys:** Returned only once during creation - save securely
- **JWT Expiration:** Default 1 hour (3600000 ms)
- **Onramp Amount:** Fixed at 1000 credits per transaction (Razorpay integration planned)
- **Model/Provider Management:** Admin-only, managed directly in database (no API endpoints)
- **Soft Deletes:** API keys are marked as deleted, not physically removed

## Future Enhancements

- [ ] Razorpay payment integration
- [ ] Rate limiting
- [ ] API key usage analytics
- [ ] Email verification
- [ ] Password reset functionality
- [ ] Admin panel for model/provider management
