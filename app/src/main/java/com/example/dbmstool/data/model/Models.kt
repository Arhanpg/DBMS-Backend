package com.example.dbmstool.data.model

data class PresetQuery(
    val id: Int,
    val label: String,
    val description: String,
    val params: List<ParamDescriptor> = emptyList()
)

data class ParamDescriptor(
    val name: String,
    val hint: String = ""
)

// Body sent with every preset-query run call.
// Non-parameterised queries just send an empty list.
data class RunQueryRequest(
    val param_values: List<String> = emptyList()
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
