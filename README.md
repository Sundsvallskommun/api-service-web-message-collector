# WebMessageCollector

_Collects messages from the Open-E platform and stores them in a database for subsequent retrieval by other systems._

## Getting Started

### Prerequisites

- **Java 21 or higher**
- **Maven**
- **MariaDB**
- **Git**
- **[Dependent services](#dependencies)**

### Installation

1. **Clone the repository:**

   ```bash
   git clone git@github.com:Sundsvallskommun/api-service-web-message-collector.git
   ```
2. **Configure the application:**

   Before running the application, you need to set up configuration settings.
   See [Configuration](#Configuration)

   **Note:** Ensure all required configurations are set; otherwise, the application may fail to start.

3. **Ensure dependent services are running:**

   If this microservice depends on other services, make sure they are up and accessible. See [Dependencies](#dependencies) for more details.

4. **Build and run the application:**

   ```bash
   mvn spring-boot:run
   ```

## Dependencies

This microservice depends on the following services:

- **Open-e Platform**
  - **Purpose:** This service retrieves messages from the Open-e Platform.
  - **Repository:** [Open-ePlatform](https://github.com/Open-ePlatform/Open-ePlatform)
  - **Setup Instructions:** Refer to its documentation for installation and configuration steps.

Ensure that these services are running and properly configured before starting this microservice.

## API Documentation

Access the API documentation via Swagger UI:

- **Swagger UI:** [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

Alternatively, refer to the `openapi.yml` file located in the `src/main/resources/api/` for the OpenAPI specification.

## Usage

### API Endpoints

Refer to the [API Documentation](#api-documentation) for detailed information on available endpoints.

### Example Request

```bash
curl -X GET http://localhost:8080/api/2281/messages
```

## Configuration

Configuration is crucial for the application to run successfully. Ensure all necessary settings are configured in `application.yml`.

### Key Configuration Parameters

- **Server Port:**

  ```yaml
  server:
    port: 8080
  ```
- **Database Settings:**

  ```yaml
  spring:
    datasource:
      url: jdbc:mysql://localhost:3306/your_database
      username: your_db_username
      password: your_db_password
  ```
- **Open-E settings:**

  The following YAML configuration outlines the settings for integrating with the Open-E platform. These settings are divided into different environments, each with its own scheduler and connection parameters.

  ```yaml
  integration:
    open-e:
      environments:
        2281: 
          scheduler:
            enabled: true
            cron: '0 */5 * * * *'
            lock-at-most-for: PT3M
          internal:
            base-url: https://someurl.se
            username: your_username
            password: your_password
            familyIds:
              - 123
            connect-timeout: 10
            read-timeout: 30
          external:
            base-url: https://someurl.se
            username: your_username
            password: your_password
            familyIds:
              - 456
            connect-timeout: 10
            read-timeout: 30
  ```
- **integration.open-e.environments.2281**: This section defines the settings for municipalityId 2281. If needed you can define as many environments as you need.
  - **scheduler**:
    - **enabled**: Indicates whether the scheduler is enabled (`true`).
    - **cron**: Specifies the cron expression for scheduling tasks (`'0 */5 * * * *'`), which means every 5 minutes.
    - **lock-at-most-for**: Defines the maximum duration for which the scheduler can hold a lock (`PT3M`), which is 3 minutes.
      The following configuration is divided into internal and external sections, allowing you to configure the retrieval of messages from two different Open-E platform instances.
  - **internal/external**:
    - **base-url**: The base URL for the connection (`https://someurl.se`).
    - **username**: The username for the connection (`your_username`).
    - **password**: The password for the connection (`https://someurl.se`).
    - **familyIds**: A list of family IDs for internal use (`[123]`).
    - **connect-timeout**: The connection timeout duration in seconds (`10`).
    - **read-timeout**: The read timeout duration in seconds (`30`).

### Database Initialization

The project is set up with [Flyway](https://github.com/flyway/flyway) for database migrations. Flyway is disabled by default so you will have to enable it to automatically populate the database schema upon application startup.

```yaml
spring:
  flyway:
    enabled: true
```

- **No additional setup is required** for database initialization, as long as the database connection settings are correctly configured.

### Additional Notes

- **Application Profiles:**

  Use Spring profiles (`dev`, `prod`, etc.) to manage different configurations for different environments.

- **Logging Configuration:**

  Adjust logging levels if necessary.

## Contributing

Contributions are welcome! Please see [CONTRIBUTING.md](https://github.com/Sundsvallskommun/.github/blob/main/.github/CONTRIBUTING.md) for guidelines.

## License

This project is licensed under the [MIT License](LICENSE).

## Code status

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-web-message-collector&metric=alert_status)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-web-message-collector)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-web-message-collector&metric=reliability_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-web-message-collector)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-web-message-collector&metric=security_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-web-message-collector)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-web-message-collector&metric=sqale_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-web-message-collector)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-web-message-collector&metric=vulnerabilities)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-web-message-collector)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-web-message-collector&metric=bugs)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-web-message-collector)

---

© 2024 Sundsvalls kommun
