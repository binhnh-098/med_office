# Med Office Backend

Spring Boot backend for `med_office`.

## Stack

- Java 26
- Spring Boot 4
- Spring Security
- Spring Data JPA
- SQL Server
- Swagger/OpenAPI

## Run

1. Create a `.env` file in the project root:

```properties
DB_URL=jdbc:sqlserver://localhost:1433;databaseName=med-office;encrypt=true;trustServerCertificate=true
DB_USERNAME=sa
DB_PASSWORD=123456
```

2. Start the app:

```powershell
./mvnw spring-boot:run
```

The default profile is `dev`.

For production:

```powershell
$env:SPRING_PROFILES_ACTIVE='prod'
./mvnw spring-boot:run
```

## Build And Test

```powershell
./mvnw test
```

## Branch Strategy

- `develop`: branch for daily development
- `staging`: branch for demo/UAT deployment
- `main`: branch for production deployment

Recommended flow:

```text
develop -> staging -> main
```

Use pull requests between these branches instead of pushing changes manually.

## API

Base URL:

```text
http://localhost:8080
```

Swagger:

```text
http://localhost:8080/swagger-ui.html
```

### Login

`POST /api/login`

```json
{
  "username": "reception",
  "password": "clinic123"
}
```

### Current User

`GET /api/me`

Requires the session cookie returned by `login`.

### Logout

`POST /api/logout`

Requires the same session cookie.

## Notes

- Runtime data is read from the database only.
- No hardcoded runtime seed data is created by the application.
- Test data is isolated in `src/test/resources/data.sql`.
- GitHub Actions workflow is branch-based:
  - `develop` -> development pipeline
  - `staging` -> demo pipeline
  - `main` -> production pipeline
