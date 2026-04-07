package com.example.dbmstool.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dbmstool.data.model.PresetQuery
import com.example.dbmstool.ui.components.DataTable
import com.example.dbmstool.viewmodel.MainViewModel
import com.example.dbmstool.viewmodel.UiState

@Composable
fun QueriesScreen(viewModel: MainViewModel) {
    val presetState   by viewModel.presetQueries.collectAsState()
    val queryResults  by viewModel.queryResults.collectAsState()
    val queryInputs   by viewModel.queryInputs.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadPresetQueries() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        QueriesHeader()
        Spacer(Modifier.height(16.dp))

        when (val state = presetState) {
            is UiState.Loading -> LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
            )
            is UiState.Error -> InlineErrorBanner(state.message)
            is UiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.data, key = { it.id }) { query ->
                        QueryCard(
                            query        = query,
                            resultState  = queryResults[query.id],
                            inputValue   = queryInputs[query.id] ?: "",
                            onInputChange = { viewModel.updateQueryInput(query.id, it) },
                            onRun        = { viewModel.runPresetQuery(query) }
                        )
                    }
                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
            else -> {}
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Header
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun QueriesHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Preset Queries",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "14 built-in queries from the project spec",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.tertiaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Terminal,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Query card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun QueryCard(
    query: PresetQuery,
    resultState: UiState<*>?,
    inputValue: String,
    onInputChange: (String) -> Unit,
    onRun: () -> Unit
) {
    var expanded   by remember { mutableStateOf(false) }
    var isRunning  by remember { mutableStateOf(false) }
    val needsInput = query.params.isNotEmpty()

    // Sync running state with result
    LaunchedEffect(resultState) {
        if (resultState !is UiState.Loading) isRunning = false
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── Top row: badge + label + run button ───────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Query number badge + text
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QueryBadge(id = query.id)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = query.label,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = query.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(Modifier.width(10.dp))

                // Run button
                Button(
                    onClick = {
                        isRunning = true
                        expanded  = true
                        onRun()
                    },
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (isRunning && resultState is UiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Run",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = "Run",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // ── Input field ───────────────────────────────────────────
            if (needsInput) {
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = inputValue,
                    onValueChange = onInputChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(
                            text = query.params.firstOrNull()?.let { "Enter: $it" } ?: "Input value",
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )
            }

            // ── Expanded result ───────────────────────────────────────
            AnimatedVisibility(
                visible = expanded && resultState != null,
                enter = fadeIn() + expandVertically(),
                exit  = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(Modifier.height(14.dp))
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                    Spacer(Modifier.height(12.dp))

                    when (val state = resultState) {

                        is UiState.Loading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(
                                        modifier  = Modifier.size(28.dp),
                                        strokeWidth = 2.5.dp,
                                        color     = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(Modifier.height(10.dp))
                                    Text(
                                        text  = "Running query…",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        is UiState.Error -> InlineErrorBanner(state.message)

                        is UiState.Success -> {
                            val result = state.data as? com.example.dbmstool.data.model.QueryResult
                            if (result != null) {
                                ResultMetaRow(
                                    rowCount = result.rows.size,
                                    onCollapse = { expanded = false }
                                )
                                Spacer(Modifier.height(10.dp))
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

// ─────────────────────────────────────────────────────────────────────────────
// Small helpers
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun QueryBadge(id: Int) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Q$id",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun ResultMetaRow(rowCount: Int, onCollapse: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(15.dp)
            )
            Text(
                text = "$rowCount row${if (rowCount != 1) "s" else ""} returned",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        TextButton(
            onClick = onCollapse,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
        ) {
            Icon(
                Icons.Default.ExpandLess,
                contentDescription = "Collapse",
                modifier = Modifier.size(15.dp)
            )
            Spacer(Modifier.width(3.dp))
            Text(
                text = "Collapse",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun InlineErrorBanner(message: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            Icons.Default.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.size(16.dp).padding(top = 1.dp)
        )
        Column {
            Text(
                text = "Query failed",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
            )
        }
    }
}