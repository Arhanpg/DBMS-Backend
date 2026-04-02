package com.example.dbmstool.data.model

data class PresetQuery(
    val id: Int,
    val label: String,
    val description: String
)

data class QueryResult(
    val columns: List<String>,
    val rows: List<Map<String, Any?>>
)

data class CustomQueryRequest(
    val sql: String
)

data class TableListResponse(
    val tables: List<String>
)