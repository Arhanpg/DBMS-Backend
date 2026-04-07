package com.example.dbmstool.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.VisualTransformation

// ─── Gender options ───────────────────────────────────────────────────────────
private val genderOptions = listOf("Male", "Female", "Other", "Prefer not to say")

@Composable
fun AddUserScreen(onSave: (Map<String, String>) -> Unit = {}) {

    // ── Form state ────────────────────────────────────────────────────────────
    var banner    by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName  by remember { mutableStateOf("") }
    var street    by remember { mutableStateOf("") }
    var city      by remember { mutableStateOf("") }
    var postcode  by remember { mutableStateOf("") }
    var mobile    by remember { mutableStateOf("") }
    var email     by remember { mutableStateOf("") }
    var dob       by remember { mutableStateOf("") }
    var gender    by remember { mutableStateOf("") }

    var saved     by remember { mutableStateOf(false) }

    // ── Accent gradient colours ───────────────────────────────────────────────
    val accentStart = MaterialTheme.colorScheme.primary
    val accentEnd   = MaterialTheme.colorScheme.tertiary

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {

        // ── Hero header ───────────────────────────────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                accentStart.copy(alpha = 0.18f),
                                accentEnd.copy(alpha = 0.06f),
                                Color.Transparent
                            )
                        )
                    )
            ) {
                // Decorative circles
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .offset(x = (-40).dp, y = (-40).dp)
                        .clip(CircleShape)
                        .background(accentStart.copy(alpha = 0.08f))
                )
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 30.dp, y = (-20).dp)
                        .clip(CircleShape)
                        .background(accentEnd.copy(alpha = 0.10f))
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 24.dp, bottom = 20.dp)
                ) {
                    // Avatar circle
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .shadow(12.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(accentStart, accentEnd)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "New Student",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Fill in the details below to enrol",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }

        // ── Progress chips ────────────────────────────────────────────────────
        item {
            val filled = listOf(banner, firstName, lastName, street,
                city, postcode, mobile, email, dob, gender).count { it.isNotBlank() }
            val total  = 10

            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Profile completion",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$filled / $total",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { filled / total.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(50)),
                    color = accentStart,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
            Spacer(Modifier.height(20.dp))
        }

        // ── Section: Identity ─────────────────────────────────────────────────
        item {
            SectionHeader(
                icon  = Icons.Default.Badge,
                title = "Identity",
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            FormCard {
                StyledField(
                    label    = "Banner Number",
                    value    = banner,
                    onChange = { banner = it },
                    icon     = Icons.Outlined.Tag,
                    keyboard = KeyboardType.Number,
                    hint     = "e.g. B00123456"
                )
                FieldDivider()
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        StyledField(
                            label    = "First Name",
                            value    = firstName,
                            onChange = { firstName = it },
                            icon     = Icons.Outlined.Person,
                            hint     = "John"
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        StyledField(
                            label    = "Last Name",
                            value    = lastName,
                            onChange = { lastName = it },
                            icon     = Icons.Outlined.Person,
                            hint     = "Doe"
                        )
                    }
                }
                FieldDivider()
                GenderSelector(selected = gender, onSelect = { gender = it })
                FieldDivider()
                StyledField(
                    label    = "Date of Birth",
                    value    = dob,
                    onChange = { dob = it },
                    icon     = Icons.Outlined.CalendarMonth,
                    hint     = "DD / MM / YYYY",
                    keyboard = KeyboardType.Number
                )
            }
        }

        item { Spacer(Modifier.height(16.dp)) }

        // ── Section: Address ──────────────────────────────────────────────────
        item {
            SectionHeader(
                icon  = Icons.Default.Home,
                title = "Address",
                color = MaterialTheme.colorScheme.tertiary
            )
        }

        item {
            FormCard {
                StyledField(
                    label    = "Street",
                    value    = street,
                    onChange = { street = it },
                    icon     = Icons.Outlined.EditRoad,
                    hint     = "123 Main Street"
                )
                FieldDivider()
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1.4f)) {
                        StyledField(
                            label    = "City",
                            value    = city,
                            onChange = { city = it },
                            icon     = Icons.Outlined.LocationCity,
                            hint     = "Dublin"
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        StyledField(
                            label    = "Postcode",
                            value    = postcode,
                            onChange = { postcode = it },
                            icon     = Icons.Outlined.MarkunreadMailbox,
                            hint     = "D01 XY00"
                        )
                    }
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }

        // ── Section: Contact ──────────────────────────────────────────────────
        item {
            SectionHeader(
                icon  = Icons.Default.ContactPhone,
                title = "Contact",
                color = MaterialTheme.colorScheme.secondary
            )
        }

        item {
            FormCard {
                StyledField(
                    label    = "Mobile",
                    value    = mobile,
                    onChange = { mobile = it },
                    icon     = Icons.Outlined.PhoneAndroid,
                    keyboard = KeyboardType.Phone,
                    hint     = "+353 87 000 0000"
                )
                FieldDivider()
                StyledField(
                    label    = "Email Address",
                    value    = email,
                    onChange = { email = it },
                    icon     = Icons.Outlined.AlternateEmail,
                    keyboard = KeyboardType.Email,
                    hint     = "john.doe@example.com"
                )
            }
        }

        item { Spacer(Modifier.height(32.dp)) }

        // ── Save / Cancel buttons ─────────────────────────────────────────────
        item {
            val allFilled = listOf(banner, firstName, lastName, street,
                city, postcode, mobile, email, dob, gender).all { it.isNotBlank() }

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                AnimatedContent(
                    targetState = saved,
                    transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
                    label = "save_btn"
                ) { isSaved ->
                    if (isSaved) {
                        // ── Success state ──────────────────────────────────
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                )
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = "Student saved successfully!",
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else {
                        // ── Normal state ───────────────────────────────────
                        Column {
                            Button(
                                onClick = {
                                    if (allFilled) {
                                        onSave(
                                            mapOf(
                                                "banner"    to banner,
                                                "firstName" to firstName,
                                                "lastName"  to lastName,
                                                "street"    to street,
                                                "city"      to city,
                                                "postcode"  to postcode,
                                                "mobile"    to mobile,
                                                "email"     to email,
                                                "dob"       to dob,
                                                "gender"    to gender
                                            )
                                        )
                                        saved = true
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                enabled = allFilled,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = 4.dp,
                                    pressedElevation = 1.dp
                                )
                            ) {
                                Icon(
                                    Icons.Default.Save,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    text = "Save Student",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (!allFilled) {
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Please fill in all fields to continue",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }

                            // ── Cancel button ──────────────────────────────
                            Spacer(Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = { onSave(emptyMap()) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    width = 1.dp
                                )
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    text = "Cancel",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Reusable components
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(icon: ImageVector, title: String, color: Color) {
    Row(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
        }
        Text(
            text = title.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.5.sp,
            color = color
        )
        HorizontalDivider(
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp),
            color = color.copy(alpha = 0.15f),
            thickness = 1.dp
        )
    }
}

@Composable
private fun FormCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            content = content
        )
    }
}

@Composable
private fun FieldDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 36.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
        thickness = 0.5.dp
    )
}

@Composable
private fun StyledField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    icon: ImageVector,
    hint: String = "",
    keyboard: KeyboardType = KeyboardType.Text
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (value.isNotBlank())
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier
                .size(20.dp)
                .animateContentSize()
        )
        Spacer(Modifier.width(12.dp))
        TextField(
            value = value,
            onValueChange = onChange,
            label = {
                Text(
                    text = label,
                    fontSize = 12.sp
                )
            },
            placeholder = {
                Text(
                    text = hint,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            },
            singleLine = true,
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(keyboardType = keyboard),
            colors = TextFieldDefaults.colors(
                focusedContainerColor   = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor   = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = Color.Transparent,
                focusedLabelColor       = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor     = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            ),
            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
        )
    }
}

@Composable
private fun GenderSelector(selected: String, onSelect: (String) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Wc,
                contentDescription = null,
                tint = if (selected.isNotBlank())
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Gender",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            genderOptions.forEach { option ->
                val isSelected = selected == option
                FilterChip(
                    selected = isSelected,
                    onClick  = { onSelect(option) },
                    label    = {
                        Text(
                            text = option,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    shape = RoundedCornerShape(50),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor     = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled          = true,
                        selected         = isSelected,
                        borderColor      = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                        selectedBorderColor = Color.Transparent,
                        borderWidth      = 1.dp,
                        selectedBorderWidth = 0.dp
                    )
                )
            }
        }
    }
}