package com.example.dbmstool.data.model

// -----------------------------
// Query Parameter Model
// -----------------------------
data class QueryParam(
    val name: String,
    val hint: String,
    val type: String
)

// -----------------------------
// Preset Query Metadata
// -----------------------------
data class PresetQuery(
    val id: Int,
    val label: String,
    val description: String,
    val params: List<QueryParam> = emptyList()
)

// -----------------------------
// Query Result (Response from backend)
// -----------------------------
data class QueryResult(
    val columns: List<String>,
    val rows: List<Map<String, Any?>>
)

// -----------------------------
// Custom Query Request
// -----------------------------
data class CustomQueryRequest(
    val sql: String
)

// -----------------------------
// Preset Query Run Request
// IMPORTANT: Matches backend expectation
// -----------------------------
data class QueryRunRequest(
    val param_values: List<String> = emptyList()
)