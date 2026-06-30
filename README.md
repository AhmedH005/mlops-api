# MLOps Pipeline Management API

A JAX-RS REST API (Jersey 2 + Grizzly) for managing ML workspaces, models, and evaluation metrics.

## Build & Run

```bash
mvn package
java -jar target/mlops-api-1.0-SNAPSHOT.jar
```

Server starts on **http://localhost:8080/api/v1**

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | /api/v1/ | Discovery – API info & resource links |
| GET | /api/v1/workspaces | List all workspaces |
| POST | /api/v1/workspaces | Create a workspace |
| GET | /api/v1/workspaces/{id} | Get workspace by ID |
| HEAD | /api/v1/workspaces/{id} | Check workspace exists |
| DELETE | /api/v1/workspaces/{id} | Delete workspace (must be empty) |
| GET | /api/v1/models | List all models (optional `?status=` filter) |
| POST | /api/v1/models | Register a new model |
| GET | /api/v1/models/{id} | Get model by ID |
| GET | /api/v1/models/{id}/metrics | List evaluation metrics for a model |
| POST | /api/v1/models/{id}/metrics | Add an evaluation metric |

## Seed Data

On startup the in-memory store contains:
- Workspace `WSVISION-01` (Computer Vision Lab)
- Models `MOD-8832` (DEPLOYED), `MOD-1103` (TRAINING), `MOD-4471` (DEPRECATED)
- One starter metric for `MOD-8832`
