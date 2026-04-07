# DBMS-Backend

Backend API for the **University Accommodation Office DBMS project**, built with FastAPI and MySQL.

This README is designed to be used as-is on both the `main` and `master` branches.

## Features

- FastAPI REST backend with Swagger docs (`/docs`) and ReDoc (`/redoc`)
- Health endpoints for API and database connectivity checks
- Safe table browsing for predefined accommodation-related tables
- 14 preset, parameter-aware SQL query endpoints
- Custom SQL execution endpoint restricted to safe `SELECT`-only queries
- Deployment-ready configuration for Render, Vercel, and Procfile-based platforms

## Tech Stack

- Python 3
- FastAPI
- Uvicorn
- MySQL (`mysql-connector-python`)
- `python-dotenv` for local environment variable loading

## Project Structure

```text
DBMS-Backend/
├── api/
│   └── index.py              # Vercel entry point
├── data/
│   ├── __init__.py
│   └── preset_queries.py     # 14 predefined SQL reports
├── routers/
│   ├── __init__.py
│   ├── custom.py             # Custom SELECT query endpoint + safety checks
│   ├── queries.py            # Preset query listing/execution
│   └── tables.py             # Allowed table listing/fetch
├── database.py               # MySQL connection + query execution helper
├── main.py                   # FastAPI app entry point
├── requirements.txt
├── render.yaml
├── vercel.json
└── Procfile
```

## API Overview

### Health

- `GET /`  
  Returns service status and docs path.
- `GET /health`  
  Checks database connectivity.

### Tables

- `GET /tables/`  
  Returns list of allowed tables.
- `GET /tables/{table_name}`  
  Returns up to 200 rows from an allowed table.

Allowed tables:

`Student`, `Adviser`, `Hall`, `HallRoom`, `Flat`, `FlatRoom`, `Lease`, `Invoice`, `Staff`, `Inspection`, `NextOfKin`, `Course`

### Preset Queries

- `GET /queries/`  
  Returns metadata for all 14 preset queries.
- `POST /queries/{query_id}/run`  
  Executes a preset query by ID.

Request body for parameterized queries:

```json
{
  "param_values": ["value1", "value2"]
}
```

### Custom Query

- `POST /custom/run`  
  Executes user SQL only if it passes safety validation.

Request body:

```json
{
  "sql": "SELECT * FROM Student LIMIT 10"
}
```

Safety checks in `custom.py`:

- Only statements starting with `SELECT` are allowed
- Dangerous keywords (`DROP`, `DELETE`, `UPDATE`, etc.) are blocked
- Multiple statements are blocked

## Local Setup

### 1) Clone and move into the project

```bash
git clone <your-repo-url>
cd DBMS-Backend
```

### 2) Create and activate virtual environment

```bash
python -m venv .venv
source .venv/bin/activate   # Linux/macOS
```

Windows (PowerShell):

```powershell
.venv\Scripts\Activate.ps1
```

### 3) Install dependencies

```bash
pip install -r requirements.txt
```

### 4) Configure environment variables

Create a `.env` file in repo root:

```env
DB_HOST=your-db-host
DB_PORT=3306
DB_USER=your-db-user
DB_PASSWORD=your-db-password
DB_NAME=your-db-name
```

### 5) Run locally

```bash
uvicorn main:app --reload
```

Open:

- API: `http://127.0.0.1:8000`
- Swagger: `http://127.0.0.1:8000/docs`
- ReDoc: `http://127.0.0.1:8000/redoc`

## Deployment Notes

### Render

`render.yaml` is already configured with:

- Build command: `pip install -r requirements.txt`
- Start command: `uvicorn main:app --host 0.0.0.0 --port $PORT`
- Required env vars: `DB_HOST`, `DB_PORT`, `DB_USER`, `DB_PASSWORD`, `DB_NAME`

### Vercel

- `vercel.json` and `api/index.py` are included for Python deployment.
- Entry app object is imported from `main.py`.

## Error Handling and Response Format

- Table/query execution typically returns:

```json
{
  "columns": ["col1", "col2"],
  "rows": [{ "col1": "value", "col2": "value" }]
}
```

- Runtime DB failures are surfaced as HTTP 500 errors.
- Invalid table names and invalid preset query IDs return HTTP 404.
- Invalid custom SQL (non-SELECT, blocked keywords, multiple statements) returns HTTP 400.

## Notes

- CORS is currently open to all origins (`allow_origins=["*"]`).
- Database connection is created per request and closed safely after query execution.
