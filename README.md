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
