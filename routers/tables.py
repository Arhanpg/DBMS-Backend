from fastapi import APIRouter, HTTPException
from database import execute_query, get_connection
from mysql.connector import Error

router = APIRouter(prefix="/tables", tags=["Tables"])

# These must exactly match your MySQL table names (case-sensitive on Aiven Linux)
ALLOWED_TABLES = [
    "Student",
    "Adviser",
    "Hall",
    "HallRoom",
    "Flat",
    "FlatRoom",
    "Lease",
    "Invoice",
    "Staff",
    "Inspection",
    "NextOfKin",
    "Course",
]


@router.get("/")
def list_tables():
    """Returns the list of all available tables."""
    return {"tables": ALLOWED_TABLES}


@router.get("/{table_name}")
def get_table_data(table_name: str):
    """
    Returns all rows from the requested table.
    Only tables in ALLOWED_TABLES are accessible.
    """
    if table_name not in ALLOWED_TABLES:
        raise HTTPException(
            status_code=404,
            detail=f"Table '{table_name}' not found or not accessible."
        )
    try:
        result = execute_query(f"SELECT * FROM `{table_name}` LIMIT 200")
        return result
    except RuntimeError as e:
        raise HTTPException(status_code=500, detail=str(e))
