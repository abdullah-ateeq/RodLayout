package com.example.rodlayout.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SpaceBar
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rodlayout.model.BoundaryStyle
import com.example.rodlayout.model.LayoutDirection
import com.example.rodlayout.model.LayoutMode
import com.example.rodlayout.model.MeasurementOrigin
import com.example.rodlayout.model.MeasurementUnit
import com.example.rodlayout.ui.components.NumericInputField
import com.example.rodlayout.ui.components.RodDiagramSection
import com.example.rodlayout.ui.components.StatusBanners
import com.example.rodlayout.ui.components.SummaryStatsGrid
import com.example.rodlayout.ui.components.UnitSelectorChips
import com.example.rodlayout.ui.theme.IndustrialOrange
import com.example.rodlayout.ui.viewmodel.RodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: RodViewModel,
    onNavigateToResults: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val config = uiState.config
    val result = uiState.result
    var showPresetMenu by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "RodLayout",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Precision Fabrication & Marking",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                },
                actions = {
                    // Presets dropdown
                    IconButton(onClick = { showPresetMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = "Presets",
                            tint = IndustrialOrange
                        )
                    }
                    DropdownMenu(
                        expanded = showPresetMenu,
                        onDismissRequest = { showPresetMenu = false }
                    ) {
                        viewModel.presets.forEach { preset ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(preset.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(preset.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                },
                                onClick = {
                                    viewModel.applyPreset(preset)
                                    showPresetMenu = false
                                }
                            )
                        }
                    }

                    // Reverse direction button
                    IconButton(onClick = { viewModel.toggleDirection() }) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Reverse Direction",
                            tint = if (config.direction == LayoutDirection.REVERSED) IndustrialOrange else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Reset button
                    IconButton(onClick = { viewModel.resetToDefaults() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Inputs"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mode Selector Tabs (Fixed Spacing vs Mark Count)
            PrimaryTabRow(
                selectedTabIndex = if (config.layoutMode == LayoutMode.FIXED_SPACING) 0 else 1,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = IndustrialOrange,
                modifier = Modifier.background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = config.layoutMode == LayoutMode.FIXED_SPACING,
                    onClick = { viewModel.setLayoutMode(LayoutMode.FIXED_SPACING) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SpaceBar, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Fixed Spacing", fontWeight = FontWeight.SemiBold)
                        }
                    }
                )
                Tab(
                    selected = config.layoutMode == LayoutMode.MARK_COUNT,
                    onClick = { viewModel.setLayoutMode(LayoutMode.MARK_COUNT) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FormatListNumbered, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Mark Count", fontWeight = FontWeight.SemiBold)
                        }
                    }
                )
            }

            // Quick Unit Selector
            UnitSelectorChips(
                selectedUnit = config.unit,
                onUnitSelected = { unit -> viewModel.setMeasurementUnit(unit, convertValues = true) }
            )

            // Setup Dimensions Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Rod Setup",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Total Rod Length
                    val stepLengthVal = if (config.unit == MeasurementUnit.MILLIMETERS) 50.0 else if (config.unit == MeasurementUnit.CENTIMETERS) 5.0 else 1.0
                    NumericInputField(
                        valueText = uiState.lengthInput,
                        onValueChange = { viewModel.updateTotalLength(it) },
                        label = "Total Rod Length",
                        unitSuffix = config.unit.symbol,
                        onIncrement = { viewModel.stepLength(stepLengthVal) },
                        onDecrement = { viewModel.stepLength(-stepLengthVal) }
                    )

                    // Start and End Offsets
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        NumericInputField(
                            valueText = uiState.startOffsetInput,
                            onValueChange = { viewModel.updateStartOffset(it) },
                            label = "Start Offset",
                            unitSuffix = config.unit.symbol,
                            modifier = Modifier.weight(1f)
                        )
                        NumericInputField(
                            valueText = uiState.endOffsetInput,
                            onValueChange = { viewModel.updateEndOffset(it) },
                            label = "End Offset",
                            unitSuffix = config.unit.symbol,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Mode-specific input: Target Spacing or Mark Count
                    if (config.layoutMode == LayoutMode.FIXED_SPACING) {
                        val stepSpacingVal = if (config.unit == MeasurementUnit.MILLIMETERS) 10.0 else if (config.unit == MeasurementUnit.CENTIMETERS) 1.0 else 0.5
                        NumericInputField(
                            valueText = uiState.spacingInput,
                            onValueChange = { viewModel.updateTargetSpacing(it) },
                            label = "Mark Spacing",
                            unitSuffix = config.unit.symbol,
                            onIncrement = { viewModel.stepSpacing(stepSpacingVal) },
                            onDecrement = { viewModel.stepSpacing(-stepSpacingVal) }
                        )
                    } else {
                        NumericInputField(
                            valueText = uiState.countInput,
                            onValueChange = { viewModel.updateMarkCount(it) },
                            label = "Number of Marks",
                            unitSuffix = "marks",
                            isIntegerOnly = true,
                            onIncrement = { viewModel.stepCount(1) },
                            onDecrement = { viewModel.stepCount(-1) }
                        )
                    }
                }
            }

            // Advanced Settings Accordion
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Tune, contentDescription = null, tint = IndustrialOrange, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Mark Settings & Origin",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(onClick = { viewModel.toggleAdvancedSettings() }) {
                            Icon(
                                imageVector = if (uiState.isAdvancedSettingsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = "Toggle Advanced Settings"
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = uiState.isAdvancedSettingsExpanded,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Mark Width & End Margin
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                NumericInputField(
                                    valueText = uiState.markWidthInput,
                                    onValueChange = { viewModel.updateMarkWidth(it) },
                                    label = "Mark / Kerf Width",
                                    unitSuffix = config.unit.symbol,
                                    modifier = Modifier.weight(1f)
                                )
                                NumericInputField(
                                    valueText = uiState.endMarginInput,
                                    onValueChange = { viewModel.updateEndMargin(it) },
                                    label = "Extra End Margin",
                                    unitSuffix = config.unit.symbol,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Boundary Placement Style
                            Text(
                                text = "Boundary Placement",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                BoundaryStyle.entries.forEach { style ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        RadioButton(
                                            selected = config.boundaryStyle == style,
                                            onClick = { viewModel.setBoundaryStyle(style) },
                                            colors = RadioButtonDefaults.colors(selectedColor = IndustrialOrange)
                                        )
                                        Column {
                                            Text(style.displayName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                            Text(style.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }

                            // Measurement Origin
                            Text(
                                text = "Measurement Origin (Zero Point)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                MeasurementOrigin.entries.forEach { origin ->
                                    val isSelected = config.origin == origin
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.setMeasurementOrigin(origin) },
                                        label = { Text(origin.displayName, fontSize = 12.sp) },
                                        leadingIcon = if (isSelected) {
                                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                        } else null,
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = IndustrialOrange.copy(alpha = 0.15f),
                                            selectedLabelColor = IndustrialOrange
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            // Direction Indicator
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Direction: ${config.direction.displayName} ${config.direction.symbol}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = if (config.direction == LayoutDirection.NORMAL) "Start offset at left edge" else "Start offset at right edge (Reversed)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                }
                                OutlinedButton(
                                    onClick = { viewModel.toggleDirection() },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Flip Direction", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Presets row (Quick access)
            Column {
                Text(
                    text = "Quick Presets",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    viewModel.presets.forEach { preset ->
                        FilterChip(
                            selected = false,
                            onClick = { viewModel.applyPreset(preset) },
                            label = { Text(preset.title, fontSize = 12.sp) }
                        )
                    }
                }
            }

            // Status, Error, and Warning Banners
            StatusBanners(result = result)

            // Summary Stats Grid
            SummaryStatsGrid(result = result, unit = config.unit)

            // Visual Diagram
            RodDiagramSection(
                config = config,
                result = result,
                selectedMark = uiState.selectedMark,
                onMarkSelected = { viewModel.selectMark(it) }
            )

            // Big Action Button to Results Table
            Button(
                onClick = onNavigateToResults,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IndustrialOrange)
            ) {
                Icon(Icons.Default.FormatListNumbered, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "View Marking Table & Export (${result.markCount} marks)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
