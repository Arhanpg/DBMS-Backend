package com.example.dbmstool.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class TableInfo(
    val name: String,
    val description: String,
    val icon: ImageVector,
    val fields: String
)

val tableDescriptions = listOf(
    TableInfo("Student", "Full-time students currently renting or on waiting list",
        Icons.Default.Person, "banner_no, name, address, DOB, category, status, major…"),
    TableInfo("Adviser", "Staff members assigned to monitor student welfare",
        Icons.Default.SupervisorAccount, "adviser_id, full_name, position, department, phone…"),
    TableInfo("Hall", "University halls of residence with manager info",
        Icons.Default.Apartment, "hall_id, name, address, telephone, manager_id"),
    TableInfo("HallRoom", "Individual rooms in halls with rent rate and place number",
        Icons.Default.MeetingRoom, "room_number, place_number, monthly_rent, hall_id"),
    TableInfo("Flat", "Student apartments for groups of 3–5 students",
        Icons.Default.Home, "flat_id, flat_number, address, total_bedrooms, manager_id"),
    TableInfo("FlatRoom", "Individual bedrooms in student flats",
        Icons.Default.Bed, "room_number, place_number, monthly_rent, flat_id"),
    TableInfo("Lease", "Rental agreements between students and the office",
        Icons.Default.Description, "lease_number, banner_no, place_number, duration, dates…"),
    TableInfo("Invoice", "Semester invoices sent to students for rent",
        Icons.Default.Receipt, "invoice_number, lease_number, semester, payment_due, paid…"),
    TableInfo("Staff", "Residence office staff members",
        Icons.Default.Badge, "staff_number, name, position, location, DOB, gender…"),
    TableInfo("Inspection", "Flat inspection records by staff",
        Icons.Default.Checklist, "inspection_id, flat_id, date, staff_name, satisfactory…"),
    TableInfo("NextOfKin", "Emergency contact info for students",
        Icons.Default.ContactPhone, "kin_id, banner_no, name, relationship, address, phone"),
    TableInfo("Course", "University courses linked to students",
        Icons.Default.School, "course_number, title, instructor, phone, email, department"),
)

@Composable
fun HomeScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "University Accommodation",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Database Management System",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Database Tables",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(tableDescriptions) { table ->
            TableInfoCard(table)
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun TableInfoCard(table: TableInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = table.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(top = 2.dp, end = 12.dp)
                    .size(24.dp)
            )
            Column {
                Text(
                    text = table.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = table.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    text = table.fields,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}