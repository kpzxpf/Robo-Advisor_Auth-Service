# Authentication Service

## Overview
The Authentication Service is a secure, JWT-based authentication and user management system built with Spring Boot. It provides endpoints for user registration and authentication, generating JWT tokens for authenticated users.

## Features
- User registration with validation
- User authentication with JWT tokens
- Role-based access control
- Secure password storage with BCrypt encryption

## API Endpoints

### Authentication

#### Register a new user
- **URL**: `/api/auth/register`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "username": "user123",
    "password": "SecureP@ss123",
    "email": "user@example.com"
  }
  ```
- **Validation**:
  - Username: 3-50 characters, can only contain letters, numbers, and the characters `.`, `-`, `_`
  - Password: 8-100 characters, must contain at least one digit, lowercase letter, uppercase letter, special character, and no whitespace
  - Email: Valid email format, maximum 100 characters
- **Response**: Returns the created user data (excluding password)
  ```json
  {
    "id": 1,
    "username": "user123",
    "email": "user@example.com",
    "firstName": null,
    "lastName": null
  }
  ```

#### Login
- **URL**: `/api/auth/login`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "username": "user123",
    "password": "SecureP@ss123"
  }
  ```
- **Validation**:
  - Username: 3-50 characters, can only contain letters, numbers, and the characters `.`, `-`, `_`
  - Password: 8-100 characters, must contain at least one digit, lowercase letter, uppercase letter, special character, and no whitespace
- **Response**: Returns a JWT token
  ```
  eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMTIzIiwiaWF0IjoxNjE2MTUyMDAwLCJleHAiOjE2MTYyMzg0MDB9.signature
  ```

## Authentication Flow
1. **Registration**: User submits registration data, which is validated and stored in the database with an encrypted password.
2. **Login**: User submits login credentials, which are validated against the database.
3. **Token Generation**: Upon successful authentication, a JWT token is generated and returned to the user.
4. **Token Usage**: For subsequent requests to protected endpoints, the client includes the JWT token in the Authorization header (`Bearer <token>`).
5. **Token Validation**: The server validates the token and extracts the user information for authorization.

## Database Schema

### Users Table
| Column        | Type         | Constraints                |
|---------------|--------------|----------------------------|
| id            | SERIAL       | PRIMARY KEY                |
| username      | VARCHAR(50)  | UNIQUE, NOT NULL           |
| password      | VARCHAR(255) | NOT NULL                   |
| email         | VARCHAR(100) | UNIQUE, NOT NULL           |
| first_name    | VARCHAR(50)  |                            |
| last_name     | VARCHAR(50)  |                            |
| date_of_birth | DATE         |                            |
| address       | TEXT         |                            |
| phone_number  | VARCHAR(20)  |                            |
| created_at    | TIMESTAMP    | DEFAULT CURRENT_TIMESTAMP  |
| updated_at    | TIMESTAMP    | DEFAULT CURRENT_TIMESTAMP  |
| role_id       | INTEGER      | REFERENCES roles(id)       |

### Roles Table
| Column    | Type        | Constraints      |
|-----------|-------------|------------------|
| id        | SERIAL      | PRIMARY KEY      |
| role_name | VARCHAR(50) | UNIQUE, NOT NULL |

## Configuration

### Application Properties
The service can be configured through the `application.yaml` file:

```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true

  datasource:
    url: jdbc:postgresql://<host>:<port>/<database>
    username: <username>
    password: <password>
  jpa:
    hibernate:
      ddl-auto: none

  security:
    jwt:
      secret-code: <your-secret-key>
      expiration: 86400000  # 24 hours in milliseconds
```

### Configuration Options
- **Database Configuration**:
  - `spring.datasource.url`: JDBC URL for the PostgreSQL database
  - `spring.datasource.username`: Database username
  - `spring.datasource.password`: Database password

- **JWT Configuration**:
  - `spring.security.jwt.secret-code`: Secret key used for signing JWT tokens
  - `spring.security.jwt.expiration`: Token expiration time in milliseconds

- **Flyway Migration**:
  - `spring.flyway.enabled`: Enable/disable Flyway migrations
  - `spring.flyway.locations`: Location of migration scripts
  - `spring.flyway.baseline-on-migrate`: Create baseline on migrate if schema exists

## Deployment

### Prerequisites
- Java 24 or later
- PostgreSQL database
- Docker (optional)

### Building the Application
```bash
./gradlew build
```

### Running Locally
```bash
java -jar build/libs/AuthService-0.0.1-SNAPSHOT.jar
```

### Docker Deployment
1. Build the Docker image:
   ```bash
   docker build -t auth-service .
   ```

2. Run the container:
   ```bash
   docker run -p 8080:8080 \
     -e SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:<port>/<database> \
     -e SPRING_DATASOURCE_USERNAME=<username> \
     -e SPRING_DATASOURCE_PASSWORD=<password> \
     -e SPRING_SECURITY_JWT_SECRET_CODE=<your-secret-key> \
     auth-service
   ```

## Security Considerations
- The service uses BCrypt for password hashing
- JWT tokens are signed with HS512 algorithm
- CSRF protection is disabled for the API (common for stateless APIs)
- Session management is set to STATELESS
- Authentication endpoints are publicly accessible, all other endpoints require authentication