from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from routers import tables, queries, custom

app = FastAPI(
    title="University Accommodation Office API",
    description="REST API for the University Accommodation DBMS project",
    version="1.0.0"
)

# CORS — allows the Android app (and Swagger UI) to reach the API
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Register routers
app.include_router(tables.router)
app.include_router(queries.router)
app.include_router(custom.router)


@app.get("/", tags=["Health"])
def root():
    return {
        "status": "running",
        "message": "University Accommodation API is live",
        "docs": "/docs"
    }


@app.get("/health", tags=["Health"])
def health_check():
    """Checks if the database connection is reachable."""
    try:
        from database import get_connection
        conn = get_connection()
        conn.close()
        return {"status": "ok", "database": "connected"}
    except Exception as e:
        return {"status": "error", "database": str(e)}