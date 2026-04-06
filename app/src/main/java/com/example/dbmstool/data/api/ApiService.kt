package com.example.dbmstool.data.api

import com.example.dbmstool.data.model.CustomQueryRequest
import com.example.dbmstool.data.model.PresetQuery
import com.example.dbmstool.data.model.QueryResult
import com.example.dbmstool.data.model.QueryRunRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @GET("tables")
    suspend fun getTables(): Map<String, List<String>>

    @GET("tables/{name}")
    suspend fun getTableData(@Path("name") name: String): QueryResult

    @GET("queries")
    suspend fun getPresetQueries(): List<PresetQuery>

    @POST("queries/{id}/run")
    suspend fun runPresetQuery(
        @Path("id") id: Int,
        @Body request: QueryRunRequest = QueryRunRequest()
    ): QueryResult

    @POST("query/custom")
    suspend fun runCustomQuery(@Body request: CustomQueryRequest): QueryResult
}