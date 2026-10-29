package com.example.rodlayout.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rodlayout.ui.theme.IndustrialOrange
import com.example.rodlayout.ui.theme.IndustrialOrangeBright
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(0.7f) }
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 600, easing = LinearOutSlowInEasing)
        )
        progress.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 700, easing = LinearOutSlowInEasing)
        )
        delay(400)
        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clickable { onSplashFinished() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Animated Rod & Caliper Graphic
            Box(
                modifier = Modifier
                    .size(width = 240.dp, height = 80.dp)
                    .scale(scale.value),
                contentAlignment = Alignment.Center
            ) {
                val currentProgress = progress.value
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val rodH = 26f
                    val rodY = (h - rodH) / 2f

                    // Rod base
                    drawRoundRect(
                        color = Color(0xFF2C3440),
                        topLeft = Offset(10f, rodY),
                        size = Size(w - 20f, rodH),
                        cornerRadius = CornerRadius(6f, 6f)
                    )
                    drawRoundRect(
                        color = Color(0xFF5A6B82),
                        topLeft = Offset(10f, rodY),
                        size = Size(w - 20f, rodH),
                        cornerRadius = CornerRadius(6f, 6f),
                        style = Stroke(width = 2f)
                    )

                    // Draw ruler ticks
                    val markCount = 7
                    val step = (w - 40f) / (markCount - 1)
                    val activeMarks = (markCount * currentProgress).toInt()

                    for (i in 0 until activeMarks) {
                        val markX = 20f + (i * step)
                        val isCenter = i == markCount / 2
                        val tickColor = if (isCenter) IndustrialOrangeBright else IndustrialOrange

                        drawLine(
                            color = tickColor,
                            start = Offset(markX, rodY - 6f),
                            end = Offset(markX, rodY + rodH + 6f),
                            strokeWidth = if (isCenter) 4f else 2.5f
                        )

                        // Drill point center
                        drawCircle(
                            color = Color.White,
                            radius = 4f,
                            center = Offset(markX, rodY + (rodH / 2f))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App Title
            Text(
                text = "RODLAYOUT",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Accent Bar
            Box(
                modifier = Modifier
                    .height(4.dp)
                    .fillMaxWidth(0.25f)
                    .background(IndustrialOrange, RoundedCornerShape(2.dp))
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Subtitle
            Text(
                text = "Precision Linear Marking & Layout Utility",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Offline Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        IndustrialOrange.copy(alpha = 0.12f),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = IndustrialOrange,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = " 100% Fully Offline • Workshop Ready",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = IndustrialOrange
                )
            }
        }
    }
}
