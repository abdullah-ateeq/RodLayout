package com.example.rodlayout.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rodlayout.ui.theme.IndustrialOrange

@Composable
fun NumericInputField(
    valueText: String,
    onValueChange: (String) -> Unit,
    label: String,
    unitSuffix: String? = null,
    isIntegerOnly: Boolean = false,
    errorMessage: String? = null,
    onIncrement: (() -> Unit)? = null,
    onDecrement: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = valueText,
                onValueChange = { newStr ->
                    // Filter input: allow numbers and at most one decimal if !isIntegerOnly
                    if (isIntegerOnly) {
                        if (newStr.isEmpty() || newStr.all { it.isDigit() }) {
                            onValueChange(newStr)
                        }
                    } else {
                        val validChars = newStr.all { it.isDigit() || it == '.' }
                        val dotCount = newStr.count { it == '.' }
                        if (validChars && dotCount <= 1) {
                            onValueChange(newStr)
                        }
                    }
                },
                label = { Text(label, style = MaterialTheme.typography.bodyMedium) },
                suffix = unitSuffix?.let {
                    {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = IndustrialOrange
                        )
                    }
                },
                trailingIcon = {
                    if (valueText.isNotEmpty()) {
                        IconButton(
                            onClick = { onValueChange("") },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                isError = errorMessage != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (isIntegerOnly) KeyboardType.Number else KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = IndustrialOrange,
                    focusedLabelColor = IndustrialOrange,
                    cursorColor = IndustrialOrange
                ),
                modifier = Modifier.weight(1f)
            )

            // Optional stepper buttons (+ / -) for fine increments
            if (onIncrement != null && onDecrement != null) {
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                    onClick = onDecrement,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrement",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(
                    onClick = onIncrement,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increment",
                        tint = IndustrialOrange
                    )
                }
            }
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 8.dp, top = 2.dp)
            )
        }
    }
}
