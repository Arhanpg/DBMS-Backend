package com.example.dbmstool.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dbmstool.data.model.QueryResult

@Composable
fun DataTable(result: QueryResult, modifier: Modifier = Modifier) {
    val colWidth = 148.dp
    val horizontalScrollState = rememberScrollState()

    if (result.columns.isEmpty()) {
        EmptyState(message = "No data returned.", modifier = modifier)
        return
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            // ── Toolbar row ────────────────────────────────────────────
            TableToolbar(
                columnCount = result.columns.size,
                rowCount = result.rows.size
            )

            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // ── Table body ─────────────────────────────────────────────
            Box(
                modifier = Modifier.horizontalScroll(horizontalScrollState)
            ) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {

                    // Header
                    item {
                        Row(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
                        ) {
                            result.columns.forEach { col ->
                                Text(
                                    text = col.uppercase(),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp,
                                    letterSpacing = 0.6.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier
                                        .width(colWidth)
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // Empty rows state
                    if (result.rows.isEmpty()) {
                        item {
                            EmptyState(
                                message = "Query ran successfully — no rows returned.",
                                modifier = Modifier.width(colWidth * result.columns.size.coerceAtLeast(1))
                            )
                        }
                    } else {
                        itemsIndexed(result.rows) { index, row ->
                            DataRow(
                                index = index,
                                row = row,
                                columns = result.columns,
                                colWidth = colWidth
                            )
                        }
                    }
                }
            }

            // ── Footer ─────────────────────────────────────────────────
            if (result.rows.isNotEmpty()) {
                TableFooter(
                    rowCount = result.rows.size,
                    columnCount = result.columns.size
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Sub-components
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TableToolbar(columnCount: Int, rowCount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Query Results",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatChip(label = "$rowCount rows")
            StatChip(label = "$columnCount cols")
        }
    }
}

@Composable
private fun StatChip(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
private fun DataRow(
    index: Int,
    row: Map<String, Any?>,
    columns: List<String>,
    colWidth: androidx.compose.ui.unit.Dp
) {
    val bg = if (index % 2 == 0)
        MaterialTheme.colorScheme.surface
    else
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)

    Row(
        modifier = Modifier.background(bg),
        verticalAlignment = Alignment.CenterVertically
    ) {
        columns.forEach { col ->
            val value = row[col]?.toString()
            Text(
                text = value ?: "—",
                fontSize = 13.sp,
                color = if (value != null)
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                fontStyle = if (value == null)
                    androidx.compose.ui.text.font.FontStyle.Italic
                else
                    androidx.compose.ui.text.font.FontStyle.Normal,
                modifier = Modifier
                    .width(colWidth)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TableFooter(rowCount: Int, columnCount: Int) {
    HorizontalDivider(
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$rowCount rows · $columnCount columns",
            style = MaterialTheme.typography.labelMedium,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
        Text(
            text = "Scroll →",
            style = MaterialTheme.typography.labelMedium,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )
    }
}

@Composable
private fun EmptyState(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }
}