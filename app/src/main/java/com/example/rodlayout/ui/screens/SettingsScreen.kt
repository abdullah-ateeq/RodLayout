package com.example.rodlayout.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rodlayout.ui.theme.IndustrialOrange
import com.example.rodlayout.ui.viewmodel.AppTheme
import com.example.rodlayout.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by settingsViewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
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
            // Theme Section
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = IndustrialOrange, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Appearance & Theme", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        ThemeOptionRow(
                            title = "System Default",
                            subtitle = "Follow system dark or light preference",
                            icon = Icons.Default.SettingsBrightness,
                            selected = uiState.theme == AppTheme.SYSTEM,
                            onClick = { settingsViewModel.setTheme(AppTheme.SYSTEM) }
                        )
                        ThemeOptionRow(
                            title = "Industrial Dark",
                            subtitle = "Charcoal and safety-orange high contrast",
                            icon = Icons.Default.DarkMode,
                            selected = uiState.theme == AppTheme.DARK,
                            onClick = { settingsViewModel.setTheme(AppTheme.DARK) }
                        )
                        ThemeOptionRow(
                            title = "Clean Light",
                            subtitle = "Crisp white background for bright environments",
                            icon = Icons.Default.LightMode,
                            selected = uiState.theme == AppTheme.LIGHT,
                            onClick = { settingsViewModel.setTheme(AppTheme.LIGHT) }
                        )
                    }
                }
            }

            // Precision Section
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PrecisionManufacturing, contentDescription = null, tint = IndustrialOrange, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Decimal Display Precision", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    }

                    Text(
                        text = "Controls rounding for display and export. Underlying calculations remain mathematically exact.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(1, 2, 3, 4).forEach { precision ->
                            val isSelected = uiState.decimalPrecision == precision
                            FilterChip(
                                selected = isSelected,
                                onClick = { settingsViewModel.setDecimalPrecision(precision) },
                                label = { Text("$precision Dec") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = IndustrialOrange.copy(alpha = 0.18f),
                                    selectedLabelColor = IndustrialOrange
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Imperial Fractions Section
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SquareFoot, contentDescription = null, tint = IndustrialOrange, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Imperial Fractional Inches", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                Text("Show nearest fraction (e.g. 14 3/8\")", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                            }
                        }
                        Switch(
                            checked = uiState.showImperialFractions,
                            onCheckedChange = { settingsViewModel.setShowImperialFractions(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = IndustrialOrange, checkedTrackColor = IndustrialOrange.copy(alpha = 0.3f))
                        )
                    }

                    if (uiState.showImperialFractions) {
                        Text(
                            text = "Maximum Fraction Denominator:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(16, 32, 64).forEach { denom ->
                                val isSelected = uiState.imperialMaxDenominator == denom
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { settingsViewModel.setImperialMaxDenominator(denom) },
                                    label = { Text("1/$denom\"") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = IndustrialOrange.copy(alpha = 0.18f),
                                        selectedLabelColor = IndustrialOrange
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // Haptic Feedback
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Haptic Feedback", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Text("Vibrate subtly when adjusting numbers or tapping marks", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                    }
                    Switch(
                        checked = uiState.hapticFeedbackEnabled,
                        onCheckedChange = { settingsViewModel.setHapticFeedbackEnabled(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = IndustrialOrange, checkedTrackColor = IndustrialOrange.copy(alpha = 0.3f))
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ThemeOptionRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = IndustrialOrange)
        )
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) IndustrialOrange else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
        }
    }
}
