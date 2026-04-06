from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from database import execute_query
import re

router = APIRouter(prefix="/custom", tags=["Custom Query"])

# Keywords that are completely blocked
BLOCKED_KEYWORDS = [
    "drop", "delete", "truncate", "alter",
    "insert", "update", "create", "replace",
    "grant", "revoke", "rename", "lock",
    "call", "exec", "execute", "load_file",
    "outfile", "dumpfile"
]


class CustomQueryRequest(BaseModel):
    sql: str


def is_safe_query(sql: str) -> tuple[bool, str]:
    """
    Returns (True, "") if query is safe.
    Returns (False, reason) if query is blocked.
    """
    # Strip comments first
    sql_stripped = re.sub(r'--.*', '', sql)
    sql_stripped = re.sub(r'/\*.*?\*/', '', sql_stripped, flags=re.DOTALL)
    sql_lower = sql_stripped.lower().strip()

    # Must start with SELECT
    if not sql_lower.startswith("select"):
        return False, "Only SELECT statements are allowed."

    # Check for blocked keywords as whole words
    for keyword in BLOCKED_KEYWORDS:
        pattern = r'\b' + keyword + r'\b'
        if re.search(pattern, sql_lower):
            return False, f"Forbidden keyword detected: '{keyword}'"

    # Block multiple statements
    if ";" in sql_stripped.rstrip(";"):
        return False, "Multiple statements are not allowed."

    return True, ""


@router.post("/run")
def run_custom_query(request: CustomQueryRequest):
    """
    Executes a user-provided SQL query.
    Only SELECT statements are allowed.
    Endpoint: POST /custom/run
    """
    sql = request.sql.strip()

    if not sql:
        raise HTTPException(status_code=400, detail="SQL query cannot be empty.")

    safe, reason = is_safe_query(sql)
    if not safe:
        raise HTTPException(status_code=400, detail=reason)

    try:
        result = execute_query(sql)
        return result
    except RuntimeError as e:
        raise HTTPException(status_code=500, detail=str(e))
