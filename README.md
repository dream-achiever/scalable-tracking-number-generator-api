# Tracking Number Generator API

A production-grade RESTful API for generating unique parcel tracking numbers built with Spring Boot 3.2 and Java 21.

## Features

- **Unique Tracking Numbers**: Generates tracking numbers matching pattern `^[A-Z0-9]{8,16}$`
- **Concurrent Safety**: Thread-safe generation with retry mechanism for collision handling
- **Horizontal Scaling**: Designed for distributed deployment across multiple instances
- **Observability**: Comprehensive logging, metrics, and health checks
- **Validation**: Robust input validation with detailed error messages
- **Testing**: Comprehensive unit and integration tests with Testcontainers

## Technology Stack

- **Java 21** - Latest LTS version with modern language features
- **Spring Boot 3.2** - Latest stable version with Spring Framework 6
- **Spring Data JPA** - Data persistence with Hibernate
- **PostgreSQL** - Primary database for production
- **H2** - In-memory database for development and testing
- **Micrometer** - Application metrics and monitoring
- **Testcontainers** - Integration testing with real databases
- **Docker** - Containerization for deployment

## API Specification

### Endpoint

```
GET /api/v1/next-tracking-number
```

### Query Parameters

| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| `origin_country_id` | String | Yes | ISO 3166-1 alpha-2 country code | "MY" |
| `destination_country_id` | String | Yes | ISO 3166-1 alpha-2 country code | "ID" |
| `weight` | Double | Yes | Package weight in kg (up to 3 decimals) | 1.234 |
| `created_at` | String | Yes | RFC 3339 timestamp (Z format) | "2018-11-20T19:29:32Z" |
| `customer_id` | String | Yes | UUID of the customer | "de619854-b59b-425e-9db4-943979e1bd49" |
| `customer_name` | String | Yes | Customer name (max 255 chars) | "RedBox Logistics" |
| `customer_slug` | String | Yes | Kebab-case customer slug | "redbox-logistics" |

### Response

```json
{
  "tracking_number": "ABC12345",
  "created_at": "2018-11-20T19:29:32+08:00",
  "request_id": "de619854-b59b-425e-9db4-943979e1bd49",
  "customer_id": "de619854-b59b-425e-9db4-943979e1bd49",
  "customer_name": "RedBox Logistics"
}
```

## Quick Start

### Prerequisites

- Java 21 or higher
- Maven 3.6 or higher
- Docker and Docker Compose (for containerized deployment)

### Running Locally

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd tracking-number-generator-api
   ```

2. **Run with Maven**
   ```bash
   # Development mode (uses H2 in-memory database)
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   
   # Production mode (requires PostgreSQL)
   mvn spring-boot:run -Dspring-boot.run.profiles=prod
   ```

3. **Access the API**
   - API Base URL: `http://localhost:8080/api/v1`
   - Health Check: `http://localhost:8080/api/v1/next-tracking-number/health`
   - H2 Console (dev only): `http://localhost:8080/h2-console`

### Running with Docker

1. **Build and run with Docker Compose**
   ```bash
   docker-compose up --build
   ```

2. **Access the services**
   - API: `http://localhost:8080/api/v1`
   - Prometheus: `http://localhost:9090`
   - Grafana: `http://localhost:3000` (admin/admin)

## Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn verify
```

### Test Coverage
```bash
mvn jacoco:report
```

## Configuration

### Environment Profiles

- **dev**: Uses H2 in-memory database, debug logging
- **prod**: Uses PostgreSQL, production logging, optimized settings
- **test**: Uses Testcontainers with PostgreSQL for integration tests

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Active profile | `dev` |
| `DB_USERNAME` | Database username | `tracking_user` |
| `DB_PASSWORD` | Database password | `tracking_password` |
| `DATABASE_URL` | Database connection URL | `jdbc:postgresql://localhost:5432/tracking_db` |
| `PORT` | Server port | `8080` |

## Monitoring and Observability

### Health Checks

- **Health Endpoint**: `/api/v1/next-tracking-number/health`
- **Actuator Health**: `/api/v1/actuator/health`
- **Metrics**: `/api/v1/actuator/metrics`
- **Prometheus**: `/api/v1/actuator/prometheus`

### Metrics

- `tracking.number.generation.requests` - Total generation requests
- `tracking.number.generation.failures` - Total generation failures
- `tracking.number.generation.duration` - Generation operation duration

### Logging

- Structured logging with SLF4J
- Configurable log levels per package
- File logging in production environment

## Architecture

### Layered Architecture

```
├── controller/          # REST controllers and exception handling
├── service/            # Business logic and transaction management
├── repository/         # Data access layer with JPA
├── domain/             # Domain models and DTOs
├── config/             # Configuration classes
└── integration/        # Integration tests
```

### Concurrency Handling

- **Database Sequences**: Uses PostgreSQL sequences for unique ID generation
- **Retry Mechanism**: Exponential backoff for collision resolution
- **Pessimistic Locking**: Prevents race conditions in concurrent scenarios
- **Transaction Management**: Ensures data consistency

## Deployment

### Docker Deployment

1. **Build the image**
   ```bash
   docker build -t tracking-number-generator-api .
   ```

2. **Run the container**
   ```bash
   docker run -p 8080:8080 \
     -e SPRING_PROFILES_ACTIVE=prod \
     -e DB_USERNAME=tracking_user \
     -e DB_PASSWORD=tracking_password \
     -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/tracking_db \
     tracking-number-generator-api
   ```

### Cloud Deployment

The application is ready for deployment on:
- **AWS ECS/Fargate**
- **Google Cloud Run**
- **Azure Container Instances**
- **Heroku**

### Production Considerations

- Use managed PostgreSQL service (AWS RDS, Google Cloud SQL, Azure Database)
- Configure proper logging aggregation (ELK stack, CloudWatch, etc.)
- Set up monitoring and alerting
- Use load balancers for horizontal scaling
- Configure proper security (HTTPS, authentication, etc.)

## API Examples

### Generate Tracking Number

```bash
curl -X GET "http://localhost:8080/api/v1/next-tracking-number" \
  -H "Content-Type: application/json" \
  -G \
  -d "origin_country_id=MY" \
  -d "destination_country_id=ID" \
  -d "weight=1.234" \
  -d "created_at=2018-11-20T19:29:32Z" \
  -d "customer_id=de619854-b59b-425e-9db4-943979e1bd49" \
  -d "customer_name=RedBox Logistics" \
  -d "customer_slug=redbox-logistics"
```

### Health Check

```bash
curl -X GET "http://localhost:8080/api/v1/next-tracking-number/health"
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support and questions, please open an issue in the repository or contact the development team.