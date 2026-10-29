package com.example.rodlayout.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rodlayout.ui.theme.IndustrialOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "About RodLayout",
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
            // App Branding Header
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(IndustrialOrange),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Architecture,
                            contentDescription = null,
                            tint = androidx.compose.ui.graphics.Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "RodLayout",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "Version 1.0.0 • Professional Fabrication Utility",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(IndustrialOrange.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = IndustrialOrange,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = " 100% Offline • Zero Tracking • No Permissions",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = IndustrialOrange
                        )
                    }
                }
            }

            // Calculation Formulas & Engineering Logic
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Functions, contentDescription = null, tint = IndustrialOrange, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Calculation Engine & Formulas", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    }

                    FormulaItem(
                        title = "1. Usable Marking Span",
                        formula = "L_usable = L_total - S_offset - E_offset - (2 × Margin)",
                        explanation = "Delineates the active zone for drill holes or mark lines."
                    )

                    FormulaItem(
                        title = "2. Specified Mark Count (N marks)",
                        formula = "Spacing = L_usable / (N - 1)  [At Boundaries]\nSpacing = L_usable / (N + 1)  [Equal Spaces / Gaps]",
                        explanation = "At boundaries: endpoints sit at offsets. Equal spaces: equal margin gaps on both ends."
                    )

                    FormulaItem(
                        title = "3. Prevention of Cumulative Error",
                        formula = "Position(i) = S_offset + (i × Spacing)",
                        explanation = "Every position is calculated algebraically from the true physical origin, rounding only at display time."
                    )

                    FormulaItem(
                        title = "4. Centerline Reference (Midpoint)",
                        formula = "X_center = Position(i) - (L_total / 2)",
                        explanation = "Enables symmetrical layout reference for shafts, balance mounts, and centered brackets."
                    )
                }
            }

            // Applications Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Handyman, contentDescription = null, tint = IndustrialOrange, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Workshop & Fabrication Applications", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    }

                    ApplicationItem(
                        title = "Deck Railing & Baluster Spacing",
                        desc = "Ensures baluster spaces strictly adhere to building code (< 4 inches) with identical end gaps."
                    )
                    ApplicationItem(
                        title = "Woodworking & Cabinetry (32mm System)",
                        desc = "Drilling lines of 32mm shelf pin holes or dowels along cabinet gables with exact offset margins."
                    )
                    ApplicationItem(
                        title = "Metal Fabrication & Railings",
                        desc = "Laying out punch marks for drilling picket holes in square tubing, angle iron, and flat bar."
                    )
                    ApplicationItem(
                        title = "Pipe, Conduit & Strut Supports",
                        desc = "Marking strut channel attachment points and pipe hangers evenly along overhead runs."
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FormulaItem(
    title: String,
    formula: String,
    explanation: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = formula,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = IndustrialOrange
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = explanation, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
    }
}

@Composable
private fun ApplicationItem(
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = IndustrialOrange,
            modifier = Modifier
                .size(16.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(text = desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
        }
    }
}
