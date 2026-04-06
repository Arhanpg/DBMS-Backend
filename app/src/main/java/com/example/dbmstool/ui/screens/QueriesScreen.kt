package com.example.dbmstool.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dbmstool.data.model.PresetQuery
import com.example.dbmstool.ui.components.DataTable
import com.example.dbmstool.viewmodel.MainViewModel
import com.example.dbmstool.viewmodel.UiState

@Composable
fun QueriesScreen(viewModel: MainViewModel) {
    val presetState by viewModel.presetQueries.collectAsState()
    val queryResults by viewModel.queryResults.collectAsState()
    val queryInputs by viewModel.queryInputs.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadPresetQueries()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Preset Queries",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "14 built-in queries from the project spec",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))

        when (val state = presetState) {
            is UiState.Loading -> LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            // Corrected ErrorCard call to use a standard Text or custom Error component if defined
            is UiState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error)
            is UiState.Success -> {
                LazyColumn(
                    // Using fillMaxSize here is fine as long as parents aren't scrollable
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.data) { query ->
                        QueryCard(
                            query = query,
                            resultState = queryResults[query.id],
                            inputValue = queryInputs[query.id] ?: "",
                            onInputChange = { viewModel.updateQueryInput(query.id, it) },
                            onRun = { viewModel.runPresetQuery(query) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
            else -> {}
        }
    }
}

@Composable
fun QueryCard(
    query: PresetQuery,
    resultState: UiState<*>?,
    inputValue: String,
    onInputChange: (String) -> Unit,
    onRun: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val needsInput = query.params.isNotEmpty()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Q${query.id}. ${query.label}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = query.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        onRun()
                        expanded = true
                    },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Run",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Run", fontSize = 13.sp)
                }
            }

            if (needsInput) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = inputValue,
                    onValueChange = onInputChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Input Value", fontSize = 12.sp) },
                    singleLine = true
                )
            }

            AnimatedVisibility(visible = expanded && resultState != null) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))

                    when (val state = resultState) {
                        is UiState.Loading -> {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                            }
                        }
                        is UiState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error)
                        is UiState.Success -> {
                            val result = state.data as? com.example.dbmstool.data.model.QueryResult
                            if (result != null) {
                                Text(
                                    text = "${result.rows.size} row(s) returned",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                // FIX: Wrap DataTable in a Box with a FIXED HEIGHT
                                // This prevents the infinite height constraint error
                                Box(modifier = Modifier.heightIn(max = 400.dp)) {
                                    DataTable(result = result)
                                }
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}