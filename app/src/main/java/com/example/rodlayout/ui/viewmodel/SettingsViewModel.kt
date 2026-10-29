package com.example.rodlayout.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class AppTheme {
    SYSTEM, LIGHT, DARK
}

data class SettingsUiState(
    val theme: AppTheme = AppTheme.SYSTEM,
    val decimalPrecision: Int = 2,
    val showImperialFractions: Boolean = true,
    val imperialMaxDenominator: Int = 32,
    val hapticFeedbackEnabled: Boolean = true
)

class SettingsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun setTheme(theme: AppTheme) {
        _uiState.update { it.copy(theme = theme) }
    }

    fun setDecimalPrecision(precision: Int) {
        val clamped = precision.coerceIn(1, 4)
        _uiState.update { it.copy(decimalPrecision = clamped) }
    }

    fun setShowImperialFractions(show: Boolean) {
        _uiState.update { it.copy(showImperialFractions = show) }
    }

    fun setImperialMaxDenominator(denom: Int) {
        _uiState.update { it.copy(imperialMaxDenominator = denom) }
    }

    fun setHapticFeedbackEnabled(enabled: Boolean) {
        _uiState.update { it.copy(hapticFeedbackEnabled = enabled) }
    }
}
