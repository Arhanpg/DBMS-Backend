from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from typing import List, Optional
from database import execute_query
from data.preset_queries import PRESET_QUERIES

router = APIRouter(prefix="/queries", tags=["Preset Queries"])


class RunQueryRequest(BaseModel):
    """Optional list of parameter values in the same order as the query's `params` list."""
    param_values: Optional[List[str]] = []


@router.get("/")
def list_queries():
    """Returns metadata + param descriptors for all 14 preset queries (SQL not exposed)."""
    return [
        {
            "id": q["id"],
            "label": q["label"],
            "description": q["description"],
            "params": q.get("params", [])
        }
        for q in PRESET_QUERIES
    ]


@router.post("/{query_id}/run")
def run_preset_query(query_id: int, request: RunQueryRequest = RunQueryRequest()):
    """
    Executes a preset query by its ID.
    For parameterized queries (d, e, g, k) pass param_values in the request body.
    Example body for query 4:  { "param_values": ["S100"] }
    """
    query = next((q for q in PRESET_QUERIES if q["id"] == query_id), None)
    if not query:
        raise HTTPException(
            status_code=404,
            detail=f"No preset query found with id {query_id}"
        )

    expected_params = query.get("params", [])
    provided = request.param_values or []

    if len(provided) != len(expected_params):
        raise HTTPException(
            status_code=422,
            detail=(
                f"Query {query_id} expects {len(expected_params)} parameter(s) "
                f"({[p['name'] for p in expected_params]}), "
                f"but {len(provided)} were provided."
            )
        )

    try:
        params_tuple = tuple(provided) if provided else None
        result = execute_query(query["sql"], params=params_tuple)
        return result
    except RuntimeError as e:
        raise HTTPException(status_code=500, detail=str(e))
