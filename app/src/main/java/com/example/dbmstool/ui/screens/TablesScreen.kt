package com.example.dbmstool.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dbmstool.ui.components.DataTable
import com.example.dbmstool.viewmodel.MainViewModel
import com.example.dbmstool.viewmodel.UiState

@Composable
fun TablesScreen(viewModel: MainViewModel) {
    val tablesState by viewModel.tables.collectAsState()
    val tableDataState by viewModel.tableData.collectAsState()
    val selectedTable by viewModel.selectedTable.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadTables()
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Live Table Viewer",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Select a table to browse its data",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))

        when (val state = tablesState) {
            is UiState.Loading -> {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            is UiState.Error -> {
                ErrorCard(message = state.message)
            }
            is UiState.Success -> {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.data) { tableName ->
                        FilterChip(
                            selected = selectedTable == tableName,
                            onClick = { viewModel.loadTableData(tableName) },
                            label = { Text(tableName, fontSize = 13.sp) }
                        )
                    }
                }
            }
            else -> {}
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))

        if (selectedTable == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No table selected",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }
        } else {
            Text(
                text = selectedTable ?: "",
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            when (val state = tableDataState) {
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.padding(32.dp))
                    }
                }
                is UiState.Error -> ErrorCard(state.message)
                is UiState.Success -> DataTable(
                    result = state.data,
                    modifier = Modifier.fillMaxSize()
                )
                else -> {}
            }
        }
    }
}

@Composable
fun ErrorCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Text(
            text = "Error: $message",
            color = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.padding(12.dp),
            fontSize = 13.sp
        )
    }
}