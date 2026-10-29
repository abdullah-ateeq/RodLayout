package com.example.rodlayout.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rodlayout.model.CalculationResult
import com.example.rodlayout.model.MeasurementUnit
import com.example.rodlayout.ui.theme.IndustrialOrange
import com.example.rodlayout.ui.theme.StatusError
import com.example.rodlayout.ui.theme.StatusWarning

@Composable
fun SummaryStatsGrid(
    result: CalculationResult,
    unit: MeasurementUnit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                title = "Total Marks",
                value = result.markCount.toString(),
                subtitle = if (result.markCount > 0) "Generated" else "None",
                highlightColor = IndustrialOrange,
                modifier = Modifier.weight(1f)
            )

            val spacingVal = unit.fromMm(result.actualSpacingMm)
            val spacingFormatted = if (result.markCount > 1) unit.formatWithSymbol(spacingVal) else "N/A"
            MetricCard(
                title = "Actual Spacing",
                value = spacingFormatted,
                subtitle = "Between marks",
                highlightColor = Color(0xFF3B82F6),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val usableVal = unit.fromMm(result.usableSpanMm)
            MetricCard(
                title = "Usable Span",
                value = unit.formatWithSymbol(usableVal),
                subtitle = "Between offsets",
                highlightColor = Color(0xFF10B981),
                modifier = Modifier.weight(1f)
            )

            val firstPos = result.firstMarkPosMm?.let { unit.formatWithSymbol(unit.fromMm(it)) } ?: "-"
            val lastPos = result.lastMarkPosMm?.let { unit.formatWithSymbol(unit.fromMm(it)) } ?: "-"
            MetricCard(
                title = "Span Limits",
                value = if (result.markCount > 0) "$firstPos → $lastPos" else "-",
                subtitle = "First & last marks",
                highlightColor = Color(0xFF8B5CF6),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    highlightColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(highlightColor, RoundedCornerShape(3.dp))
                )
                Text(
                    text = " $title",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun StatusBanners(
    result: CalculationResult,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        if (!result.isSuccess && result.error != null) {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = StatusError.copy(alpha = 0.12f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = "Error",
                        tint = StatusError,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = " ${result.error}",
                        style = MaterialTheme.typography.bodySmall,
                        color = StatusError,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }
            }
        }

        for (warning in result.warnings) {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = StatusWarning.copy(alpha = 0.12f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.WarningAmber,
                        contentDescription = "Warning",
                        tint = StatusWarning,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = " $warning",
                        style = MaterialTheme.typography.bodySmall,
                        color = StatusWarning,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }
            }
        }
    }
}
