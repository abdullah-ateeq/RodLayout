package com.example.rodlayout.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rodlayout.model.CalculationResult
import com.example.rodlayout.model.LayoutDirection
import com.example.rodlayout.model.MarkItem
import com.example.rodlayout.model.MeasurementOrigin
import com.example.rodlayout.model.MeasurementUnit
import com.example.rodlayout.model.RodLayoutConfig
import com.example.rodlayout.ui.theme.DimensionLineColor
import com.example.rodlayout.ui.theme.IndustrialOrange
import com.example.rodlayout.ui.theme.IndustrialOrangeBright
import com.example.rodlayout.ui.theme.OffsetHatchColor
import com.example.rodlayout.ui.theme.RodBodyDark
import com.example.rodlayout.ui.theme.RodBodyLight
import com.example.rodlayout.ui.theme.RodStrokeDark
import com.example.rodlayout.ui.theme.RodStrokeLight
import kotlin.math.abs
import kotlin.math.max

@Composable
fun RodDiagramSection(
    config: RodLayoutConfig,
    result: CalculationResult,
    selectedMark: MarkItem?,
    onMarkSelected: (MarkItem?) -> Unit,
    modifier: Modifier = Modifier
) {
    var zoomScale by remember { mutableFloatStateOf(1.0f) }
    val isDark = MaterialTheme.colorScheme.background.red < 0.5f

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with title and zoom controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Visual Marking Diagram",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (result.marks.isNotEmpty()) "Tap any mark to inspect details" else "No marks to display",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { zoomScale = max(0.8f, zoomScale - 0.25f) },
                        enabled = zoomScale > 0.8f
                    ) {
                        Icon(
                            imageVector = Icons.Default.ZoomOut,
                            contentDescription = "Zoom Out",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "${(zoomScale * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    IconButton(
                        onClick = { zoomScale = minOf(3.0f, zoomScale + 0.25f) },
                        enabled = zoomScale < 3.0f
                    ) {
                        Icon(
                            imageVector = Icons.Default.ZoomIn,
                            contentDescription = "Zoom In",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Horizontally scrollable diagram canvas
            val scrollState = rememberScrollState()
            val baseCanvasWidth = 360.dp
            val effectiveWidth = baseCanvasWidth * zoomScale

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = if (isDark) Color(0xFF14181E) else Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(vertical = 12.dp)
                    .horizontalScroll(scrollState)
            ) {
                RodCanvas(
                    config = config,
                    result = result,
                    selectedMark = selectedMark,
                    onMarkSelected = onMarkSelected,
                    isDark = isDark,
                    modifier = Modifier
                        .width(maxOf(effectiveWidth, 340.dp))
                        .height(230.dp)
                )
            }

            // Selected Mark Detail Card
            if (selectedMark != null) {
                Spacer(modifier = Modifier.height(12.dp))
                SelectedMarkInfoBadge(
                    mark = selectedMark,
                    unit = config.unit,
                    origin = config.origin,
                    onClose = { onMarkSelected(null) }
                )
            }
        }
    }
}

@Composable
private fun RodCanvas(
    config: RodLayoutConfig,
    result: CalculationResult,
    selectedMark: MarkItem?,
    onMarkSelected: (MarkItem?) -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val rodBodyColor = if (isDark) RodBodyDark else RodBodyLight
    val rodStrokeColor = if (isDark) RodStrokeDark else RodStrokeLight
    val textPaintColor = if (isDark) android.graphics.Color.WHITE else android.graphics.Color.DKGRAY
    val secondaryTextColor = if (isDark) android.graphics.Color.LTGRAY else android.graphics.Color.GRAY

    Canvas(
        modifier = modifier.pointerInput(result.marks) {
            detectTapGestures { tapOffset ->
                if (result.totalLengthMm <= 0 || result.marks.isEmpty()) return@detectTapGestures

                val padLeft = 40f
                val padRight = 40f
                val availableWidth = size.width - padLeft - padRight

                // Find closest mark within tolerance
                var closestMark: MarkItem? = null
                var minDistance = Float.MAX_VALUE
                val touchTolerance = 36f

                for (mark in result.marks) {
                    val markFraction = (mark.positionFromStartMm / result.totalLengthMm).toFloat()
                    val markX = padLeft + (markFraction * availableWidth)
                    val distance = abs(tapOffset.x - markX)
                    if (distance < touchTolerance && distance < minDistance) {
                        minDistance = distance
                        closestMark = mark
                    }
                }

                onMarkSelected(closestMark)
            }
        }
    ) {
        val totalLengthMm = result.totalLengthMm
        if (totalLengthMm <= 0) return@Canvas

        val padLeft = 40f
        val padRight = 40f
        val availableWidth = size.width - padLeft - padRight

        val rodY = size.height * 0.45f
        val rodHeight = 36f

        // Draw Total Rod Body
        drawRoundRect(
            color = rodBodyColor,
            topLeft = Offset(padLeft, rodY),
            size = Size(availableWidth, rodHeight),
            cornerRadius = CornerRadius(6f, 6f)
        )
        drawRoundRect(
            color = rodStrokeColor,
            topLeft = Offset(padLeft, rodY),
            size = Size(availableWidth, rodHeight),
            cornerRadius = CornerRadius(6f, 6f),
            style = Stroke(width = 2.5f)
        )

        // Draw Start Offset Region
        val startOffsetFraction = (config.startOffsetMm / totalLengthMm).toFloat()
        val startOffsetWidth = startOffsetFraction * availableWidth
        if (startOffsetWidth > 1f) {
            drawRoundRect(
                color = OffsetHatchColor,
                topLeft = Offset(padLeft, rodY),
                size = Size(startOffsetWidth, rodHeight),
                cornerRadius = CornerRadius(6f, 6f)
            )
            // Dashed line at start offset boundary
            drawLine(
                color = IndustrialOrange,
                start = Offset(padLeft + startOffsetWidth, rodY - 6f),
                end = Offset(padLeft + startOffsetWidth, rodY + rodHeight + 6f),
                strokeWidth = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
            )
        }

        // Draw End Offset Region
        val endOffsetFraction = (config.endOffsetMm / totalLengthMm).toFloat()
        val endOffsetWidth = endOffsetFraction * availableWidth
        if (endOffsetWidth > 1f) {
            val endStartX = padLeft + availableWidth - endOffsetWidth
            drawRoundRect(
                color = OffsetHatchColor,
                topLeft = Offset(endStartX, rodY),
                size = Size(endOffsetWidth, rodHeight),
                cornerRadius = CornerRadius(6f, 6f)
            )
            drawLine(
                color = IndustrialOrange,
                start = Offset(endStartX, rodY - 6f),
                end = Offset(endStartX, rodY + rodHeight + 6f),
                strokeWidth = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
            )
        }

        // Center line indicator
        val centerX = padLeft + (availableWidth / 2f)
        drawLine(
            color = Color.LightGray.copy(alpha = 0.6f),
            start = Offset(centerX, rodY - 14f),
            end = Offset(centerX, rodY + rodHeight + 14f),
            strokeWidth = 1.5f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
        )

        // Draw Marks
        for (mark in result.marks) {
            val markFraction = (mark.positionFromStartMm / totalLengthMm).toFloat()
            val markX = padLeft + (markFraction * availableWidth)
            val isSelected = selectedMark?.index == mark.index

            // Mark pin/drill hole
            val pinRadius = if (isSelected) 7f else 4.5f
            val pinColor = if (isSelected) IndustrialOrangeBright else IndustrialOrange

            // Vertical tick through the rod
            drawLine(
                color = pinColor,
                start = Offset(markX, rodY - 10f),
                end = Offset(markX, rodY + rodHeight + 10f),
                strokeWidth = if (isSelected) 3.5f else 2f
            )

            // Center drill dot
            drawCircle(
                color = if (isSelected) Color.White else pinColor,
                radius = pinRadius,
                center = Offset(markX, rodY + (rodHeight / 2f))
            )
            if (isSelected) {
                drawCircle(
                    color = IndustrialOrangeBright,
                    radius = pinRadius + 3f,
                    center = Offset(markX, rodY + (rodHeight / 2f)),
                    style = Stroke(width = 2f)
                )
            }

            // Draw mark number above the rod
            val textPaint = android.graphics.Paint().apply {
                color = if (isSelected) android.graphics.Color.parseColor("#FF6B00") else textPaintColor
                textSize = if (isSelected) 28f else 22f
                isAntiAlias = true
                textAlign = android.graphics.Paint.Align.CENTER
                isFakeBoldText = isSelected
            }

            drawContext.canvas.nativeCanvas.drawText(
                "${mark.index}",
                markX,
                rodY - 16f,
                textPaint
            )
        }

        // Draw Overall Rod Dimension line at the bottom
        drawDimensionLine(
            xStart = padLeft,
            xEnd = padLeft + availableWidth,
            y = rodY + rodHeight + 35f,
            label = "Total: ${config.unit.formatWithSymbol(config.totalLength)}",
            textColor = textPaintColor,
            lineColor = DimensionLineColor
        )

        // Draw Usable Span Dimension line at the top
        val usableStartX = padLeft + startOffsetWidth
        val usableEndX = padLeft + availableWidth - endOffsetWidth
        if (usableEndX > usableStartX + 20f) {
            drawDimensionLine(
                xStart = usableStartX,
                xEnd = usableEndX,
                y = rodY - 45f,
                label = "Usable: ${config.unit.formatWithSymbol(config.unit.fromMm(result.usableSpanMm))}",
                textColor = secondaryTextColor,
                lineColor = IndustrialOrange.copy(alpha = 0.7f)
            )
        }

        // Draw Direction Arrow
        val arrowY = size.height - 10f
        val isReversed = config.direction == LayoutDirection.REVERSED
        drawContext.canvas.nativeCanvas.drawText(
            if (!isReversed) "Start (0) ➔ ➔ ➔ End" else "End ⬅ ⬅ ⬅ Start (0)",
            padLeft + (availableWidth / 2f),
            arrowY,
            android.graphics.Paint().apply {
                color = secondaryTextColor
                textSize = 22f
                isAntiAlias = true
                textAlign = android.graphics.Paint.Align.CENTER
            }
        )
    }
}

/**
 * Helper to draw a horizontal dimension line with arrowheads and centered text label.
 */
private fun DrawScope.drawDimensionLine(
    xStart: Float,
    xEnd: Float,
    y: Float,
    label: String,
    textColor: Int,
    lineColor: Color
) {
    if (xEnd <= xStart) return

    // Dimension line
    drawLine(
        color = lineColor,
        start = Offset(xStart, y),
        end = Offset(xEnd, y),
        strokeWidth = 1.5f
    )

    // Left arrow
    drawLine(color = lineColor, start = Offset(xStart, y), end = Offset(xStart + 8f, y - 5f), strokeWidth = 1.5f)
    drawLine(color = lineColor, start = Offset(xStart, y), end = Offset(xStart + 8f, y + 5f), strokeWidth = 1.5f)

    // Right arrow
    drawLine(color = lineColor, start = Offset(xEnd, y), end = Offset(xEnd - 8f, y - 5f), strokeWidth = 1.5f)
    drawLine(color = lineColor, start = Offset(xEnd, y), end = Offset(xEnd - 8f, y + 5f), strokeWidth = 1.5f)

    // End ticks
    drawLine(color = lineColor, start = Offset(xStart, y - 6f), end = Offset(xStart, y + 6f), strokeWidth = 1.5f)
    drawLine(color = lineColor, start = Offset(xEnd, y - 6f), end = Offset(xEnd, y + 6f), strokeWidth = 1.5f)

    // Text label
    val textPaint = android.graphics.Paint().apply {
        color = textColor
        textSize = 22f
        isAntiAlias = true
        textAlign = android.graphics.Paint.Align.CENTER
    }
    val midX = (xStart + xEnd) / 2f
    drawContext.canvas.nativeCanvas.drawText(label, midX, y + 18f, textPaint)
}

@Composable
fun SelectedMarkInfoBadge(
    mark: MarkItem,
    unit: MeasurementUnit,
    origin: MeasurementOrigin,
    onClose: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = IndustrialOrange.copy(alpha = 0.12f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(IndustrialOrange, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "#${mark.index}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Position: ${mark.formattedPosition(unit)}" +
                                (mark.fractionalInches(unit)?.let { " ($it)" } ?: ""),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "From Prev: ${mark.formattedDistanceFromPrev(unit)} | Center: ${mark.formattedPositionFromCenter(unit)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = "Close",
                style = MaterialTheme.typography.labelSmall,
                color = IndustrialOrange,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(4.dp)
                    .pointerInput(Unit) {
                        detectTapGestures { onClose() }
                    }
            )
        }
    }
}
