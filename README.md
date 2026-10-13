# FinOps AI

FinOps AI is a Spring Boot MVP for uploading CSV cloud utilization reports, analyzing cost efficiency, generating rule-based recommendations, and simulating alternative cloud architecture costs.

## Stack

- Java 17
- Spring Boot 3
- PostgreSQL or local H2
- Thymeleaf
- Maven
- Docker

## Run Locally

```powershell
mvn spring-boot:run
```

Open `http://localhost:8080`.

The default local database is in-memory H2. Use Docker Compose for PostgreSQL:

```powershell
docker compose up --build
```

## Test

```powershell
mvn test
```

## CSV Format

Use `samples/cloud-report.csv`.

```csv
Service,MonthlyCost,CPUUsage
EC2-t3.medium,50,12
RDS-db.t3.medium,40,18
S3,8,0
```

## API

- `POST /api/upload` multipart field `file`
- `GET /api/analysis/{uploadId}`
- `GET /api/recommendations/{uploadId}`
- `POST /api/simulation`

Example simulation request:

```json
{
  "scenario": "Lambda Migration"
}
```

## MVP Rules

- CPU below 20% is underutilized.
- CPU above 80% is overloaded.
- CPU from 20% to 80% is healthy.
- Underutilized resources receive right-sizing recommendations.
- No AWS SDK, billing API, authentication, multi-tenancy, or LLM integration in this version.
# FinOps AI

FinOps AI is a private, CSV-first cloud-cost workspace. Users can import utilization reports, review resource recommendations, manage recommendation states, set budgets, compare report snapshots, and run cost scenarios.

## Run locally

```powershell
mvn spring-boot:run
```

Open `http://localhost:8080`, register an account, and upload `samples/cloud-report.csv`.

## Production configuration

Use the `prod` Spring profile with PostgreSQL and provide `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`, and a long random `JWT_SECRET`. Flyway owns schema setup in production. Health checks are available at `/actuator/health`.

## AWS Cost Explorer

The optional `POST /api/cloud/aws/import` endpoint imports current-month service costs into the signed-in user's workspace. Enable it with `FINOPS_AWS_ENABLED=true`, then configure standard AWS SDK credentials with permission for `ce:GetCostAndUsage`. It is disabled by default; CSV upload remains fully supported without cloud credentials.
