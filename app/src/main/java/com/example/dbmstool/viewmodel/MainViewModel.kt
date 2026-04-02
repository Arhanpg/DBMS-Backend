package com.example.dbmstool.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dbmstool.data.model.PresetQuery
import com.example.dbmstool.data.model.QueryResult
import com.example.dbmstool.repository.AccommodationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

class MainViewModel : ViewModel() {
    private val repo = AccommodationRepository()

    // Tables
    private val _tables = MutableStateFlow<UiState<List<String>>>(UiState.Idle)
    val tables: StateFlow<UiState<List<String>>> = _tables

    private val _tableData = MutableStateFlow<UiState<QueryResult>>(UiState.Idle)
    val tableData: StateFlow<UiState<QueryResult>> = _tableData

    private val _selectedTable = MutableStateFlow<String?>(null)
    val selectedTable: StateFlow<String?> = _selectedTable

    // Preset queries
    private val _presetQueries = MutableStateFlow<UiState<List<PresetQuery>>>(UiState.Idle)
    val presetQueries: StateFlow<UiState<List<PresetQuery>>> = _presetQueries

    private val _queryResults = MutableStateFlow<Map<Int, UiState<QueryResult>>>(emptyMap())
    val queryResults: StateFlow<Map<Int, UiState<QueryResult>>> = _queryResults

    // Custom query
    private val _customSql = MutableStateFlow("")
    val customSql: StateFlow<String> = _customSql

    private val _customResult = MutableStateFlow<UiState<QueryResult>>(UiState.Idle)
    val customResult: StateFlow<UiState<QueryResult>> = _customResult

    fun loadTables() {
        viewModelScope.launch {
            _tables.value = UiState.Loading
            try {
                val result = repo.getTables()
                _tables.value = UiState.Success(result["tables"] ?: emptyList())
            } catch (e: Exception) {
                _tables.value = UiState.Error(e.message ?: "Failed to load tables")
            }
        }
    }

    fun loadTableData(tableName: String) {
        _selectedTable.value = tableName
        viewModelScope.launch {
            _tableData.value = UiState.Loading
            try {
                _tableData.value = UiState.Success(repo.getTableData(tableName))
            } catch (e: Exception) {
                _tableData.value = UiState.Error(e.message ?: "Failed to load table data")
            }
        }
    }

    fun loadPresetQueries() {
        viewModelScope.launch {
            _presetQueries.value = UiState.Loading
            try {
                _presetQueries.value = UiState.Success(repo.getPresetQueries())
            } catch (e: Exception) {
                _presetQueries.value = UiState.Error(e.message ?: "Failed to load queries")
            }
        }
    }

    fun runPresetQuery(id: Int) {
        viewModelScope.launch {
            _queryResults.value = _queryResults.value + (id to UiState.Loading)
            try {
                val result = repo.runPresetQuery(id)
                _queryResults.value = _queryResults.value + (id to UiState.Success(result))
            } catch (e: Exception) {
                _queryResults.value = _queryResults.value +
                        (id to UiState.Error(e.message ?: "Query failed"))
            }
        }
    }

    fun updateCustomSql(sql: String) {
        _customSql.value = sql
    }

    fun runCustomQuery() {
        val sql = _customSql.value.trim()
        if (sql.isEmpty()) return
        viewModelScope.launch {
            _customResult.value = UiState.Loading
            try {
                _customResult.value = UiState.Success(repo.runCustomQuery(sql))
            } catch (e: Exception) {
                _customResult.value = UiState.Error(e.message ?: "Query failed")
            }
        }
    }

    fun clearCustomResult() {
        _customResult.value = UiState.Idle
    }
}