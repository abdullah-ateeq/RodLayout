package com.example.rodlayout.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.rodlayout.model.MeasurementUnit
import com.example.rodlayout.ui.theme.IndustrialOrange

@Composable
fun UnitSelectorChips(
    selectedUnit: MeasurementUnit,
    onUnitSelected: (MeasurementUnit) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MeasurementUnit.entries.forEach { unit ->
            val isSelected = unit == selectedUnit
            FilterChip(
                selected = isSelected,
                onClick = { onUnitSelected(unit) },
                label = {
                    Text(
                        text = unit.symbol,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = IndustrialOrange.copy(alpha = 0.18f),
                    selectedLabelColor = IndustrialOrange
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = MaterialTheme.colorScheme.outlineVariant,
                    selectedBorderColor = IndustrialOrange,
                    enabled = true,
                    selected = isSelected
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}
