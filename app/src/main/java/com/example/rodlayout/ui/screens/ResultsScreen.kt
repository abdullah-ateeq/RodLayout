package com.example.rodlayout.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rodlayout.calculator.ExportFormatter
import com.example.rodlayout.model.MeasurementOrigin
import com.example.rodlayout.model.MeasurementUnit
import com.example.rodlayout.ui.theme.IndustrialOrange
import com.example.rodlayout.ui.theme.IndustrialOrangeDark
import com.example.rodlayout.ui.viewmodel.RodViewModel
import com.example.rodlayout.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    rodViewModel: RodViewModel,
    settingsViewModel: SettingsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by rodViewModel.uiState.collectAsState()
    val settingsState by settingsViewModel.uiState.collectAsState()
    val config = uiState.config
    val result = uiState.result
    val context = LocalContext.current

    var searchQuery by remember { mutableStateOf("") }

    val isImperial = config.unit == MeasurementUnit.INCHES || config.unit == MeasurementUnit.FEET
    val showFractions = isImperial && settingsState.showImperialFractions

    val filteredMarks = remember(result.marks, searchQuery) {
        if (searchQuery.isBlank()) {
            result.marks
        } else {
            result.marks.filter { mark ->
                mark.index.toString().contains(searchQuery) ||
                        mark.formattedPosition(config.unit).contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Marking Results",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "${result.markCount} total marks • Spacing: ${config.unit.formatWithSymbol(config.unit.fromMm(result.actualSpacingMm))}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Copy all button
                    IconButton(
                        onClick = {
                            val text = ExportFormatter.toClipboardText(config, result, settingsState.decimalPrecision)
                            copyToClipboard(context, "RodLayout Marks", text)
                        }
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy All Marks")
                    }

                    // Share button
                    IconButton(
                        onClick = {
                            val report = ExportFormatter.toWorkshopReport(config, result, settingsState.decimalPrecision)
                            shareText(context, "RodLayout Workshop Report", report)
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share Report")
                    }
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
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Quick Summary Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total Length", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(config.unit.formatWithSymbol(config.totalLength), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Column {
                        Text("Usable Span", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(config.unit.formatWithSymbol(config.unit.fromMm(result.usableSpanMm)), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Column {
                        Text("Pitch / Spacing", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(config.unit.formatWithSymbol(config.unit.fromMm(result.actualSpacingMm)), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = IndustrialOrange)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search filter bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Filter by mark number or position...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(18.dp))
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = IndustrialOrange,
                    focusedLabelColor = IndustrialOrange
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.width(36.dp)
                )
                Text(
                    text = "Pos from Start",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1.3f)
                )
                Text(
                    text = "Δ Prev",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1.0f)
                )
                Text(
                    text = if (config.origin == MeasurementOrigin.FROM_CENTER) "Center Ref" else "Fraction",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1.1f)
                )
            }

            // Results List
            if (filteredMarks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (result.marks.isEmpty()) "No marks calculated. Adjust rod length or spacing." else "No marks match '$searchQuery'",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
                ) {
                    itemsIndexed(filteredMarks) { index, mark ->
                        val isEven = index % 2 == 0
                        val rowBg = if (isEven) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(rowBg)
                                .clickable {
                                    val itemText = "Mark #${mark.index}: ${mark.formattedPosition(config.unit, settingsState.decimalPrecision)}"
                                    copyToClipboard(context, "Mark Position", itemText)
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Badge with Mark Number
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(IndustrialOrange.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${mark.index}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = IndustrialOrange
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Distance from start
                            Text(
                                text = mark.formattedPosition(config.unit, settingsState.decimalPrecision),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1.3f)
                            )

                            // Delta from previous
                            Text(
                                text = mark.formattedDistanceFromPrev(config.unit, settingsState.decimalPrecision),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1.0f)
                            )

                            // Fraction or Center
                            val trailingText = if (config.origin == MeasurementOrigin.FROM_CENTER) {
                                mark.formattedPositionFromCenter(config.unit, settingsState.decimalPrecision)
                            } else if (showFractions) {
                                mark.fractionalInches(config.unit, settingsState.imperialMaxDenominator) ?: "-"
                            } else {
                                mark.formattedPositionFromCenter(config.unit, settingsState.decimalPrecision)
                            }

                            Text(
                                text = trailingText,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (showFractions) FontWeight.Bold else FontWeight.Normal,
                                color = if (showFractions) IndustrialOrange else MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.End,
                                modifier = Modifier.weight(1.1f)
                            )
                        }

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                            thickness = 0.5.dp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons Row (CSV export, Text report export)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val csv = ExportFormatter.toCsv(config, result, settingsState.decimalPrecision)
                        shareText(context, "rod_marks_${config.totalLength}${config.unit.symbol}.csv", csv, mimeType = "text/csv")
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export CSV", fontSize = 13.sp)
                }

                Button(
                    onClick = {
                        val report = ExportFormatter.toWorkshopReport(config, result, settingsState.decimalPrecision)
                        shareText(context, "RodLayout Workshop Report", report)
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IndustrialOrange),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Workshop Sheet", fontSize = 13.sp)
                }
            }
        }
    }
}

private fun copyToClipboard(context: Context, label: String, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
}

private fun shareText(context: Context, title: String, text: String, mimeType: String = "text/plain") {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share via"))
}
