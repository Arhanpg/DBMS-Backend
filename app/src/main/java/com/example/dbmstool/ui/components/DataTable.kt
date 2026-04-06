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
    val colWidth = 140.dp
    val borderColor = MaterialTheme.colorScheme.outlineVariant
    val horizontalScrollState = rememberScrollState()

    if (result.columns.isEmpty()) {
        Text(
            text = "No data returned.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier.padding(16.dp)
        )
        return
    }

    // This Box prevents the HorizontalScroll from conflicting with the Vertical LazyColumn
    Box(
        modifier = modifier // Use the passed-in modifier (likely fillMaxSize or weight)
            .horizontalScroll(horizontalScrollState)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize() // Forces LazyColumn to occupy only available screen space
        ) {
            // Header row
            item {
                Row(
                    modifier = Modifier
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
                        modifier = Modifier
                            .width(colWidth * result.columns.size.coerceAtLeast(1))
                            .padding(16.dp)
                    )
                }
            } else {
                itemsIndexed(result.rows) { index, row ->
                    Row(
                        modifier = Modifier
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
