# DevHive backend configuration

## Requirements

- Java 21
- MySQL
- A MySQL database named `devhive` (or a database specified by `DB_URL`)

Create the default database once in MySQL:

```sql
CREATE DATABASE devhive;
```

Hibernate is configured with `ddl-auto=update` and creates or updates application tables after connecting. It does not create the MySQL database itself.

## Configuration

The backend requires the following environment variables:

| Variable | Purpose |
| --- | --- |
| `DB_USERNAME` | MySQL account used by the application |
| `DB_PASSWORD` | Password for that MySQL account |
| `JWT_SECRET` | Private signing key with at least 32 characters |
| `DB_URL` | Optional JDBC URL; defaults to `jdbc:mysql://localhost:3306/devhive` |

`DB_URL` defaults to `jdbc:mysql://localhost:3306/devhive`. Do not commit real credentials or signing keys. Set the variables in the same environment used to launch the backend. For local PowerShell development, run these commands from `DevHiveAppBackend`:

```powershell
$env:DB_USERNAME = "your-mysql-user"
$env:DB_PASSWORD = "your-mysql-password"
$keyBytes = New-Object byte[] 48
[Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($keyBytes)
$env:JWT_SECRET = [Convert]::ToBase64String($keyBytes)
.\mvnw.cmd spring-boot:run
```

The API listens on `http://localhost:8080`. Keep the generated `JWT_SECRET` stable across restarts; changing it invalidates all existing login tokens.

## Tests

Run tests from `DevHiveAppBackend`:

```powershell
.\mvnw.cmd test
```

The integration tests connect to MySQL, so set `DB_USERNAME`, `DB_PASSWORD`, and `JWT_SECRET` in the test process environment first.

## Security

Do not commit credentials or signing keys. Any values previously committed to source control should be treated as exposed and rotated. After rotating the MySQL password, update `DB_PASSWORD`. Rotating `JWT_SECRET` signs users out by invalidating existing tokens.
