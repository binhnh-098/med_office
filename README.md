# Med Office Backend

Spring Boot backend for `med_office`.

## Stack

- Java 26
- Spring Boot 4
- Spring Security
- Spring Data JPA
- MySQL
- Swagger/OpenAPI

## Run

1. Create a `.env` file in the project root:

```properties
MED_OFFICE_DB_URL=jdbc:mysql://localhost:3306/med_office?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh
MED_OFFICE_DB_USERNAME=root
MED_OFFICE_DB_PASSWORD=Binh090801!
ROWBOAT_ENABLED=true
ROWBOAT_HOST=https://app.rowboatlabs.com
ROWBOAT_PROJECT_ID=your-rowboat-project-id
ROWBOAT_API_KEY=your-rowboat-api-key
```

2. Create MySQL schema:

```powershell
mysql -u root -p < src/main/resources/schema-mysql.sql
```

3. Start the app:

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

### Rowboat Chat

`POST /api/rowboat/chat`

Requires the same session cookie.

This endpoint proxies the official Rowboat chat API from the backend so the API key stays on the server.

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
- Test data is isolated in `src/test/resources/data.sql`.
- Rowboat requires you to send the full conversation history in `messages` and the previous `state` on every next turn.
