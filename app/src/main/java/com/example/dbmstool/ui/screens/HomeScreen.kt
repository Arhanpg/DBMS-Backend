package com.example.dbmstool.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class TableInfo(
    val name: String,
    val description: String,
    val icon: ImageVector,
    val fields: String,
    val category: String
)

val tableDescriptions = listOf(
    TableInfo("Student", "Full-time students currently renting or on waiting list",
        Icons.Default.Person, "banner_no, name, address, DOB, category, status, major…", "People"),
    TableInfo("Adviser", "Staff members assigned to monitor student welfare",
        Icons.Default.SupervisorAccount, "adviser_id, full_name, position, department, phone…", "People"),
    TableInfo("NextOfKin", "Emergency contact info for students",
        Icons.Default.ContactPhone, "kin_id, banner_no, name, relationship, address, phone", "People"),
    TableInfo("Staff", "Residence office staff members",
        Icons.Default.Badge, "staff_number, name, position, location, DOB, gender…", "People"),
    TableInfo("Hall", "University halls of residence with manager info",
        Icons.Default.Apartment, "hall_id, name, address, telephone, manager_id", "Housing"),
    TableInfo("HallRoom", "Individual rooms in halls with rent rate and place number",
        Icons.Default.MeetingRoom, "room_number, place_number, monthly_rent, hall_id", "Housing"),
    TableInfo("Flat", "Student apartments for groups of 3–5 students",
        Icons.Default.Home, "flat_id, flat_number, address, total_bedrooms, manager_id", "Housing"),
    TableInfo("FlatRoom", "Individual bedrooms in student flats",
        Icons.Default.KingBed, "room_number, place_number, monthly_rent, flat_id", "Housing"),
    TableInfo("Lease", "Rental agreements between students and the office",
        Icons.Default.Description, "lease_number, banner_no, place_number, duration, dates…", "Finance"),
    TableInfo("Invoice", "Semester invoices sent to students for rent",
        Icons.Default.Receipt, "invoice_number, lease_number, semester, payment_due, paid…", "Finance"),
    TableInfo("Inspection", "Flat inspection records by staff",
        Icons.Default.CheckCircle, "inspection_id, flat_id, date, staff_name, satisfactory…", "Admin"),
    TableInfo("Course", "University courses linked to students",
        Icons.Default.School, "course_number, title, instructor, phone, email, department", "Admin"),
)

private val categories = listOf("All") + tableDescriptions.map { it.category }.distinct()

private val categoryColors: Map<String, androidx.compose.ui.graphics.Color>
    @Composable get() = mapOf(
        "People"  to MaterialTheme.colorScheme.tertiary,
        "Housing" to MaterialTheme.colorScheme.primary,
        "Finance" to MaterialTheme.colorScheme.secondary,
        "Admin"   to MaterialTheme.colorScheme.outline,
    )

@Composable
fun HomeScreen() {
    var selectedCategory by remember { mutableStateOf("All") }
    val filtered = remember(selectedCategory) {
        if (selectedCategory == "All") tableDescriptions
        else tableDescriptions.filter { it.category == selectedCategory }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        // ── Header ────────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(24.dp))
            HomeHeader()
            Spacer(Modifier.height(20.dp))
        }

        // ── Stats row ─────────────────────────────────────────────────
        item {
            StatsRow()
            Spacer(Modifier.height(16.dp))
        }

        // ── Category filter ───────────────────────────────────────────
        item {
            Text(
                text = "Database Tables",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(10.dp))
            CategoryFilterRow(
                selected = selectedCategory,
                onSelect = { selectedCategory = it }
            )
            Spacer(Modifier.height(4.dp))
        }

        // ── Cards ─────────────────────────────────────────────────────
        items(filtered, key = { it.name }) { table ->
            TableInfoCard(table)
        }

        item { Spacer(Modifier.height(24.dp)) }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Sub-components
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun HomeHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "University Accommodation",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = "Database Management System",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Storage,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun StatsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.TableChart,
            value = "${tableDescriptions.size}",
            label = "Tables"
        )
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Category,
            value = "${categories.size - 1}",
            label = "Categories"
        )
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.People,
            value = "4",
            label = "People tables"
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    value: String,
    label: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun CategoryFilterRow(selected: String, onSelect: (String) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories) { cat ->
            FilterChip(
                selected = selected == cat,
                onClick = { onSelect(cat) },
                label = {
                    Text(
                        text = cat,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                shape = RoundedCornerShape(8.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}

@Composable
fun TableInfoCard(table: TableInfo) {
    val catColors = categoryColors

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icon box
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = table.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(14.dp))

            // Text content
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = table.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    CategoryBadge(
                        label = table.category,
                        color = catColors[table.category] ?: MaterialTheme.colorScheme.outline
                    )
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = table.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(8.dp))

                // Fields pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = table.fields,
                        style = MaterialTheme.typography.labelMedium,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryBadge(label: String, color: androidx.compose.ui.graphics.Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}