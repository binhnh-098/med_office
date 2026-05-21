# Med Office Backend

Spring Boot backend for `med_office`.

## Stack

- Java 26
- Spring Boot 4
- Spring Security
- Spring Data JPA
- MySQL
- Swagger/OpenAPI

## Environments

This project supports two application environments:

- `dev` for local development
- `production` for VPS deployment

`prod` is still supported as a legacy alias for backward compatibility, but new setups should use `production`.

## Run

1. Create a `.env` file in the project root.
For local development, start from `.env.development.example`:

```powershell
Copy-Item .env.development.example .env
```

Then set the real values:

```properties
MED_OFFICE_DB_URL=jdbc:mysql://127.0.0.1:3306/med_office?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh
MED_OFFICE_DB_USERNAME=root
MED_OFFICE_DB_PASSWORD=change_me

AI_PROVIDER=nvidia
AI_BASE_URL=https://integrate.api.nvidia.com/v1
AI_API_KEY=your-nvidia-api-key
AI_MODEL=meta/llama-3.1-70b-instruct
AI_MOCK_ON_ERROR=false
AI_CONNECT_TIMEOUT=5s
AI_TIMEOUT_MS=30s
AI_MAX_TOKENS=512
```

2. Create MySQL schema:

```powershell
mysql -u root -p < src/main/resources/schema-mysql.sql
```

3. Start the app in development:

```powershell
./mvnw spring-boot:run
```

The default profile is `dev`.

For production locally:

```powershell
$env:SPRING_PROFILES_ACTIVE='production'
./mvnw spring-boot:run
```

For VPS deployment, use `deploy/.env.production.example` as the starting point for `.env`.

## Build And Test

```powershell
./mvnw test
```

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

### NVIDIA Chat

`POST /api/rowboat/chat`

Requires the same session cookie.

This endpoint proxies the NVIDIA chat completions API from the backend so the API key stays on the server.

```json
{
  "messages": [
    {
      "role": "user",
      "content": "Tom tat cong van nay cho toi"
    }
  ],
  "state": null
}
```

Sample success payload:

```json
{
  "code": 200,
  "message": "Rowboat chat completed successfully",
  "data": {
    "messages": [
      {
        "role": "assistant",
        "content": "Toi da san sang ho tro.",
        "agenticResponseType": "external"
      }
    ],
    "state": {
      "last_agent_name": "MainAgent"
    }
  }
}
```

## Notes

- Runtime data is read from the database only.
- No hardcoded runtime seed data is created by the application.
- Configure `MED_OFFICE_DB_URL`, `MED_OFFICE_DB_USERNAME`, and `MED_OFFICE_DB_PASSWORD` to choose the database used by the application.
- NVIDIA chat expects you to send the full conversation history in `messages`.
- `AI_API_KEY` is required unless `AI_MOCK_ON_ERROR=true` and you accept mock fallback responses.
