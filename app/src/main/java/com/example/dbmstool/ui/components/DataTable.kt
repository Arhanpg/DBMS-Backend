package com.example.dbmstool.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dbmstool.data.model.QueryResult

@Composable
fun DataTable(result: QueryResult, modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    val colWidth = 140.dp
    val borderColor = MaterialTheme.colorScheme.outlineVariant

    // Show message when there are no columns at all (empty result set)
    if (result.columns.isEmpty()) {
        Text(
            text = "No data returned.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier.padding(16.dp)
        )
        return
    }

    Box(modifier = modifier) {
        LazyColumn {
            // Header row
            item {
                Row(
                    modifier = Modifier
                        .horizontalScroll(scrollState)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    result.columns.forEach { col ->
                        Text(
                            text = col,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier
                                .width(colWidth)
                                .border(0.5.dp, borderColor)
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            maxLines = 1
                        )
                    }
                }
            }

            if (result.rows.isEmpty()) {
                item {
                    Text(
                        text = "No rows returned.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                // Use itemsIndexed so we have a reliable index for row striping
                itemsIndexed(result.rows) { index, row ->
                    Row(
                        modifier = Modifier
                            .horizontalScroll(scrollState)
                            .background(
                                if (index % 2 == 0)
                                    MaterialTheme.colorScheme.surface
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                            )
                    ) {
                        result.columns.forEach { col ->
                            Text(
                                text = row[col]?.toString() ?: "\u2014",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .width(colWidth)
                                    .border(0.5.dp, borderColor)
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }
    }
}
