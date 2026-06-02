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

3. Optional: reset and insert sample data for local development:

```powershell
mysql --default-character-set=utf8mb4 -u med_office -p med_office --execute="SOURCE src/main/resources/sample-data-all.sql"
```

This seed file clears application data tables before inserting the local sample dataset. Do not run it against a database that contains real data.

Sample login accounts use password `clinic123`, for example `admin`, `doctor1`, `doctor2`, `nurse1`, and `reception`.

Sample account roles and modules:

```text
admin     -> GIAM_DOC   -> all modules
doctor1   -> BAC_SI     -> DASHBOARD, HO_SO_NHAN_VIEN, CHUYEN_KHOA, DOCTOR_MEALS, ROWBOAT
doctor2   -> BAC_SI     -> DASHBOARD, HO_SO_NHAN_VIEN, CHUYEN_KHOA, DOCTOR_MEALS, ROWBOAT
nurse1    -> DIEU_DUONG -> DASHBOARD, HO_SO_NHAN_VIEN, CHUYEN_KHOA, DOCTOR_MEALS
reception -> LE_TAN     -> DASHBOARD, HO_SO_NHAN_VIEN, CHUYEN_KHOA, CONG_VAN, DOCTOR_MEALS, ROWBOAT
```

4. Start the app in development:

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

The login and current-user responses include `roles` and `modules`, for example:

```json
{
  "roles": ["USER", "BAC_SI"],
  "modules": ["DASHBOARD", "HO_SO_NHAN_VIEN", "CHUYEN_KHOA", "DOCTOR_MEALS", "ROWBOAT"]
}
```

### Create User

`POST /api/signup`

Requires a logged-in account with role `GIAM_DOC`.

```json
{
  "username": "doctor3",
  "password": "clinic123",
  "hoSoNhanVienId": "44444444-4444-4444-4444-444444444441",
  "chucVuId": "11111111-1111-1111-1111-111111111113"
}
```

Passwords are hashed before saving. The selected employee profile is linked to the new account in the same transaction.

Use `GET /api/ho-so-nhan-vien?hasNguoiDungId=false&size=100` to load employee profiles that do not have an account yet.

### Manage User Roles

Requires role `GIAM_DOC`.

```text
GET /api/nguoi-dung
PUT /api/nguoi-dung/{id}/chuc-vu
PUT /api/nguoi-dung/{id}/status
```

Example role update:

```json
{
  "maChucVu": "TRUONG_KHOA"
}
```

Available sample role codes are `GIAM_DOC`, `TRUONG_KHOA`, `BAC_SI`, `DIEU_DUONG`, and `LE_TAN`.

Lock or unlock an account and its linked employee profile:

```json
{
  "active": false
}
```

## Module Permissions

Backend API access is enforced by role:

```text
GIAM_DOC
  All modules and user/role management.

TRUONG_KHOA
  HO_SO_NHAN_VIEN, CHUYEN_KHOA, CONG_VAN, DOCTOR_MEALS, ROWBOAT.

BAC_SI
  HO_SO_NHAN_VIEN read, CHUYEN_KHOA read, DOCTOR_MEALS, ROWBOAT.

DIEU_DUONG
  HO_SO_NHAN_VIEN read, CHUYEN_KHOA read, DOCTOR_MEALS.

LE_TAN
  HO_SO_NHAN_VIEN, CHUYEN_KHOA read, CONG_VAN, DOCTOR_MEALS, ROWBOAT.
```

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
