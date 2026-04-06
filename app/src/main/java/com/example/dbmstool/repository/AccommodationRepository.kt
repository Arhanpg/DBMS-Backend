package com.example.dbmstool.repository

import com.example.dbmstool.data.api.RetrofitClient
import com.example.dbmstool.data.model.CustomQueryRequest
import com.example.dbmstool.data.model.QueryRunRequest

class AccommodationRepository {
    private val api = RetrofitClient.api

    suspend fun getTables() = api.getTables()
    suspend fun getTableData(name: String) = api.getTableData(name)
    suspend fun getPresetQueries() = api.getPresetQueries()
    suspend fun runPresetQuery(id: Int, request: QueryRunRequest) =
        api.runPresetQuery(id, request)
    suspend fun runCustomQuery(sql: String) =
        api.runCustomQuery(CustomQueryRequest(sql))
}