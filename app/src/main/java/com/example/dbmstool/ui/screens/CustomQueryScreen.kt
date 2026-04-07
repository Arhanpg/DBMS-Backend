package com.example.dbmstool.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dbmstool.ui.components.DataTable
import com.example.dbmstool.viewmodel.MainViewModel
import com.example.dbmstool.viewmodel.UiState

@Composable
fun CustomQueryScreen(
    viewModel: MainViewModel,
    onAddUserClick: () -> Unit
) {
    val customSql    by viewModel.customSql.collectAsState()
    val customResult by viewModel.customResult.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {

        // ── Header ────────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(24.dp))
            CustomQueryHeader()
            Spacer(Modifier.height(20.dp))
        }

        // ── Safety notice ─────────────────────────────────────────────
        item {
            SafetyBanner()
            Spacer(Modifier.height(16.dp))
        }

        // ── Editor card ───────────────────────────────────────────────
        item {
            EditorCard(
                sql       = customSql,
                onChange  = { viewModel.updateCustomSql(it) },
                onRun     = { viewModel.runCustomQuery() },
                onClear   = {
                    viewModel.updateCustomSql("")
                    viewModel.clearCustomResult()
                },
                onAddUser = onAddUserClick,
                isRunning = customResult is UiState.Loading
            )
            Spacer(Modifier.height(24.dp))
        }

        // ── Divider with label ────────────────────────────────────────
        item {
            ResultsSectionLabel(state = customResult)
            Spacer(Modifier.height(12.dp))
        }

        // ── Results ───────────────────────────────────────────────────
        when (val state = customResult) {

            is UiState.Idle    -> item { IdleResultState() }

            is UiState.Loading -> item { LoadingResultState() }

            is UiState.Error   -> item { QueryErrorBanner(state.message) }

            is UiState.Success -> {
                item { ResultMetaBar(rowCount = state.data.rows.size) }
                item {
                    Spacer(Modifier.height(8.dp))
                    Box(modifier = Modifier.heightIn(max = 500.dp)) {
                        DataTable(result = state.data)
                    }
                }
            }
        }

        item { Spacer(Modifier.height(32.dp)) }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Header
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CustomQueryHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Custom Query",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Write and execute any SELECT statement",
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
                imageVector = Icons.Default.Code,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Safety banner
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SafetyBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = Modifier
                .size(16.dp)
                .padding(top = 1.dp)
        )
        Column {
            Text(
                text = "Read-only mode",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Only SELECT statements are allowed. DROP, DELETE, ALTER, UPDATE and INSERT are blocked.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.85f)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Editor card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun EditorCard(
    sql: String,
    onChange: (String) -> Unit,
    onRun: () -> Unit,
    onClear: () -> Unit,
    onAddUser: () -> Unit,
    isRunning: Boolean
) {
    val primary   = MaterialTheme.colorScheme.primary
    val tertiary  = MaterialTheme.colorScheme.tertiary
    val secondary = MaterialTheme.colorScheme.secondary

    // Pulsing animation for the run button glow when active
    val glowAlpha by rememberInfiniteTransition(label = "glow").animateFloat(
        initialValue = 0.25f,
        targetValue  = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Card(
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── Editor toolbar ────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.DataObject,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp),
                            tint = primary
                        )
                    }
                    Text(
                        text = "SQL Editor",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "${sql.length} chars",
                    style = MaterialTheme.typography.labelMedium,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }

            Spacer(Modifier.height(10.dp))

            // ── Text field ────────────────────────────────────────────
            OutlinedTextField(
                value = sql,
                onValueChange = onChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                placeholder = {
                    Text(
                        text = "SELECT * FROM Student\nWHERE current_status = 'Waiting'\nORDER BY name ASC;",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 18.sp
                    )
                },
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize   = 13.sp,
                    lineHeight = 20.sp,
                    color      = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor      = primary.copy(alpha = 0.6f),
                    unfocusedBorderColor    = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                    focusedContainerColor   = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
                )
            )

            Spacer(Modifier.height(18.dp))

            // ── ACTION BUTTONS — premium redesign ─────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                // ── Row 1: Run (weighted) + Clear ─────────────────────
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {

                    // ── RUN button — gradient + glow ──────────────────
                    val canRun = sql.isNotBlank() && !isRunning
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        // Soft glow behind button when enabled
                        if (canRun) {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .shadow(
                                        elevation = 12.dp,
                                        shape = RoundedCornerShape(12.dp),
                                        ambientColor = primary.copy(alpha = glowAlpha),
                                        spotColor    = primary.copy(alpha = glowAlpha)
                                    )
                            )
                        }
                        Button(
                            onClick = onRun,
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(12.dp),
                            enabled = canRun,
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor         = Color.Transparent,
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = if (canRun)
                                            Brush.horizontalGradient(listOf(primary, tertiary))
                                        else
                                            Brush.horizontalGradient(
                                                listOf(
                                                    MaterialTheme.colorScheme.surfaceVariant,
                                                    MaterialTheme.colorScheme.surfaceVariant
                                                )
                                            ),
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                AnimatedContent(
                                    targetState = isRunning,
                                    transitionSpec = {
                                        fadeIn(tween(200)) togetherWith fadeOut(tween(150))
                                    },
                                    label = "run_btn_content"
                                ) { running ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        if (running) {
                                            CircularProgressIndicator(
                                                modifier    = Modifier.size(16.dp),
                                                strokeWidth = 2.dp,
                                                color       = Color.White
                                            )
                                            Text(
                                                text       = "Running…",
                                                fontSize   = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color      = Color.White,
                                                letterSpacing = 0.3.sp
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .size(26.dp)
                                                    .clip(RoundedCornerShape(50))
                                                    .background(Color.White.copy(alpha = 0.18f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    Icons.Default.PlayArrow,
                                                    contentDescription = null,
                                                    tint     = Color.White,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            Text(
                                                text       = "Run Query",
                                                fontSize   = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color      = if (canRun) Color.White
                                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                                letterSpacing = 0.3.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ── CLEAR button — styled tonal ───────────────────
                    FilledTonalButton(
                        onClick = onClear,
                        modifier = Modifier.height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = sql.isNotBlank(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.55f),
                            contentColor   = MaterialTheme.colorScheme.onErrorContainer,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            disabledContentColor   = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    ) {
                        Icon(
                            Icons.Default.DeleteOutline,
                            contentDescription = "Clear",
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            text       = "Clear",
                            fontSize   = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.2.sp
                        )
                    }
                }

                // ── Row 2: Add User — full-width premium banner button ─
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    // Ambient glow layer
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .shadow(
                                elevation = 10.dp,
                                shape     = RoundedCornerShape(14.dp),
                                ambientColor = secondary.copy(alpha = 0.3f),
                                spotColor    = secondary.copy(alpha = 0.3f)
                            )
                    )
                    Button(
                        onClick = onAddUser,
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        listOf(
                                            secondary,
                                            secondary.copy(red = (secondary.red * 0.78f)),
                                        )
                                    ),
                                    shape = RoundedCornerShape(14.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            // Subtle shine strip at top
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(20.dp)
                                    .align(Alignment.TopCenter)
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 14.dp, topEnd = 14.dp,
                                            bottomStart = 0.dp, bottomEnd = 0.dp
                                        )
                                    )
                                    .background(Color.White.copy(alpha = 0.07f))
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(Color.White.copy(alpha = 0.20f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.PersonAdd,
                                        contentDescription = null,
                                        tint     = Color.White,
                                        modifier = Modifier.size(17.dp)
                                    )
                                }
                                Column(verticalArrangement = Arrangement.Center) {
                                    Text(
                                        text       = "Add New Student",
                                        fontSize   = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color      = Color.White,
                                        letterSpacing = 0.3.sp
                                    )
                                    Text(
                                        text     = "Open enrolment form",
                                        fontSize = 11.sp,
                                        color    = Color.White.copy(alpha = 0.70f),
                                        letterSpacing = 0.1.sp
                                    )
                                }
                                Spacer(Modifier.weight(1f))
                                Icon(
                                    Icons.Default.ArrowForwardIos,
                                    contentDescription = null,
                                    tint     = Color.White.copy(alpha = 0.55f),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
            // ── end action buttons ────────────────────────────────────
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Results section
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ResultsSectionLabel(state: UiState<*>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        HorizontalDivider(
            modifier  = Modifier.weight(1f),
            thickness = 0.5.dp,
            color     = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = when (state) {
                    is UiState.Success -> Icons.Default.CheckCircle
                    is UiState.Error   -> Icons.Default.Warning
                    is UiState.Loading -> Icons.Default.HourglassEmpty
                    else               -> Icons.Default.TableRows
                },
                contentDescription = null,
                modifier = Modifier.size(13.dp),
                tint = when (state) {
                    is UiState.Success -> MaterialTheme.colorScheme.primary
                    is UiState.Error   -> MaterialTheme.colorScheme.error
                    else               -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                }
            )
            Text(
                text  = "Results",
                style = MaterialTheme.typography.labelMedium,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
        HorizontalDivider(
            modifier  = Modifier.weight(1f),
            thickness = 0.5.dp,
            color     = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun IdleResultState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 36.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.TableRows,
                    contentDescription = null,
                    modifier = Modifier.size(26.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
            }
            Text(
                text = "No results yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Write a SELECT query above and tap Run",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun LoadingResultState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CircularProgressIndicator(
                modifier    = Modifier.size(32.dp),
                strokeWidth = 2.5.dp,
                color       = MaterialTheme.colorScheme.primary
            )
            Text(
                text  = "Executing query…",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun QueryErrorBanner(message: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier
                .size(16.dp)
                .padding(top = 1.dp)
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
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
private fun ResultMetaBar(rowCount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text  = "$rowCount row${if (rowCount != 1) "s" else ""} returned",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}