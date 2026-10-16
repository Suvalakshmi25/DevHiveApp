# DevHive

DevHive is a team workspace for organizations, projects, tasks, members, code snippets, and notifications. The frontend is an Angular application; the backend is a Spring Boot API backed by MySQL.

## Prerequisites

- Node.js and npm
- Java 21
- MySQL

## Local development setup

### 1. Configure and start the backend

Create the `devhive` database in MySQL:

```sql
CREATE DATABASE devhive;
```

In PowerShell, open a terminal in `DevHiveAppBackend`, set the required environment variables, then start Spring Boot:

```powershell
$env:DB_USERNAME = "your-mysql-user"
$env:DB_PASSWORD = "your-mysql-password"
$keyBytes = New-Object byte[] 48
[Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($keyBytes)
$env:JWT_SECRET = [Convert]::ToBase64String($keyBytes)
.\mvnw.cmd spring-boot:run
```

Keep the same `JWT_SECRET` across restarts. Generating a new one invalidates existing login tokens. The API listens on `http://localhost:8080`; Hibernate creates or updates the tables in the database.

See [the backend README](../DevHiveAppBackend/README.md) for all backend configuration options, test commands, and secret-rotation guidance.

### 2. Install and start the frontend

In a separate terminal, open `DevHiveAppFrontend` and run:

```powershell
npm ci
npm start
```

Open [http://localhost:4200](http://localhost:4200). The frontend uses the local API at `http://localhost:8080`; the backend allows requests from the local Angular development server.

## Common commands

Run these from `DevHiveAppFrontend`:

```powershell
npm run build
npm test -- --watch=false
```

Run backend tests from `DevHiveAppBackend` after setting the required database and JWT environment variables in that terminal:

```powershell
.\mvnw.cmd test
```

## Security

Do not commit database credentials or JWT signing keys. If a credential or signing key was previously committed, treat it as exposed and rotate it. Never paste secrets into issues, chat, or source files.
