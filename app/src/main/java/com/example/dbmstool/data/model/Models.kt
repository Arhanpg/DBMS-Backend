package com.example.dbmstool.data.model

data class PresetQuery(
    val id: Int,
    val label: String,
    val description: String,
    val params: List<String> = emptyList()
)

data class QueryResult(
    val columns: List<String>,
    val rows: List<Map<String, Any?>>
)

data class CustomQueryRequest(
    val sql: String
)

data class QueryRunRequest(
    val banner_number: String? = null,
    val semester: String? = null,
    val hall_name: String? = null
)