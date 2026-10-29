package com.example.rodlayout.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.rodlayout.calculator.RodCalculator
import com.example.rodlayout.model.BoundaryStyle
import com.example.rodlayout.model.CalculationResult
import com.example.rodlayout.model.LayoutDirection
import com.example.rodlayout.model.LayoutMode
import com.example.rodlayout.model.MarkItem
import com.example.rodlayout.model.MeasurementOrigin
import com.example.rodlayout.model.MeasurementUnit
import com.example.rodlayout.model.RodLayoutConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.max

data class PresetTemplate(
    val title: String,
    val description: String,
    val unit: MeasurementUnit,
    val totalLength: Double,
    val startOffset: Double,
    val endOffset: Double,
    val targetSpacing: Double,
    val markCount: Int,
    val layoutMode: LayoutMode,
    val boundaryStyle: BoundaryStyle
)

data class RodUiState(
    val config: RodLayoutConfig = RodLayoutConfig(),
    val result: CalculationResult = CalculationResult(isSuccess = true),
    val selectedMark: MarkItem? = null,
    // String representations for text field inputs
    val lengthInput: String = "1000",
    val startOffsetInput: String = "50",
    val endOffsetInput: String = "50",
    val spacingInput: String = "100",
    val countInput: String = "10",
    val markWidthInput: String = "3",
    val endMarginInput: String = "0",
    val isAdvancedSettingsExpanded: Boolean = false
)

class RodViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RodUiState())
    val uiState: StateFlow<RodUiState> = _uiState.asStateFlow()

    init {
        recalculate()
    }

    val presets = listOf(
        PresetTemplate(
            title = "1-Meter Rod (100mm Spacing)",
            description = "Standard 1000mm rail with 50mm offsets and 100mm fixed pitch.",
            unit = MeasurementUnit.MILLIMETERS,
            totalLength = 1000.0,
            startOffset = 50.0,
            endOffset = 50.0,
            targetSpacing = 100.0,
            markCount = 10,
            layoutMode = LayoutMode.FIXED_SPACING,
            boundaryStyle = BoundaryStyle.AT_BOUNDARIES
        ),
        PresetTemplate(
            title = "Deck Balusters (4\" Max Gap)",
            description = "6 ft rail evenly divided into equal spaces complying with 4\" code.",
            unit = MeasurementUnit.INCHES,
            totalLength = 72.0,
            startOffset = 3.5,
            endOffset = 3.5,
            targetSpacing = 4.0,
            markCount = 16,
            layoutMode = LayoutMode.MARK_COUNT,
            boundaryStyle = BoundaryStyle.EQUAL_SPACES
        ),
        PresetTemplate(
            title = "32mm System Shelf Pins",
            description = "Euro 32mm hole line for cabinetry with 37mm front offset.",
            unit = MeasurementUnit.MILLIMETERS,
            totalLength = 768.0,
            startOffset = 32.0,
            endOffset = 32.0,
            targetSpacing = 32.0,
            markCount = 23,
            layoutMode = LayoutMode.FIXED_SPACING,
            boundaryStyle = BoundaryStyle.AT_BOUNDARIES
        ),
        PresetTemplate(
            title = "Pipe Hanger Anchors (4 ft spacing)",
            description = "20 ft conduit/pipe run with supports every 4 feet.",
            unit = MeasurementUnit.FEET,
            totalLength = 20.0,
            startOffset = 1.0,
            endOffset = 1.0,
            targetSpacing = 4.0,
            markCount = 5,
            layoutMode = LayoutMode.FIXED_SPACING,
            boundaryStyle = BoundaryStyle.AT_BOUNDARIES
        )
    )

    fun updateTotalLength(input: String) {
        val parsed = input.toDoubleOrNull() ?: 0.0
        _uiState.update {
            it.copy(
                lengthInput = input,
                config = it.config.copy(totalLength = parsed)
            )
        }
        recalculate()
    }

    fun updateStartOffset(input: String) {
        val parsed = input.toDoubleOrNull() ?: 0.0
        _uiState.update {
            it.copy(
                startOffsetInput = input,
                config = it.config.copy(startOffset = parsed)
            )
        }
        recalculate()
    }

    fun updateEndOffset(input: String) {
        val parsed = input.toDoubleOrNull() ?: 0.0
        _uiState.update {
            it.copy(
                endOffsetInput = input,
                config = it.config.copy(endOffset = parsed)
            )
        }
        recalculate()
    }

    fun updateTargetSpacing(input: String) {
        val parsed = input.toDoubleOrNull() ?: 0.0
        _uiState.update {
            it.copy(
                spacingInput = input,
                config = it.config.copy(targetSpacing = parsed)
            )
        }
        recalculate()
    }

    fun updateMarkCount(input: String) {
        val parsed = input.toIntOrNull() ?: 0
        _uiState.update {
            it.copy(
                countInput = input,
                config = it.config.copy(markCount = parsed)
            )
        }
        recalculate()
    }

    fun updateMarkWidth(input: String) {
        val parsed = input.toDoubleOrNull() ?: 0.0
        _uiState.update {
            it.copy(
                markWidthInput = input,
                config = it.config.copy(markWidth = parsed)
            )
        }
        recalculate()
    }

    fun updateEndMargin(input: String) {
        val parsed = input.toDoubleOrNull() ?: 0.0
        _uiState.update {
            it.copy(
                endMarginInput = input,
                config = it.config.copy(endMargin = parsed)
            )
        }
        recalculate()
    }

    fun setMeasurementUnit(newUnit: MeasurementUnit, convertValues: Boolean = true) {
        val current = _uiState.value
        val oldUnit = current.config.unit
        if (oldUnit == newUnit) return

        if (!convertValues) {
            _uiState.update {
                it.copy(config = it.config.copy(unit = newUnit))
            }
            recalculate()
            return
        }

        // Convert all existing values to the new unit
        val newLength = oldUnit.convertTo(current.config.totalLength, newUnit)
        val newStartOff = oldUnit.convertTo(current.config.startOffset, newUnit)
        val newEndOff = oldUnit.convertTo(current.config.endOffset, newUnit)
        val newSpacing = oldUnit.convertTo(current.config.targetSpacing, newUnit)
        val newMarkWidth = oldUnit.convertTo(current.config.markWidth, newUnit)
        val newEndMargin = oldUnit.convertTo(current.config.endMargin, newUnit)

        fun fmt(d: Double): String {
            val rounded = (d * 1000).toInt() / 1000.0
            return if (rounded % 1.0 == 0.0) rounded.toInt().toString() else "%.2f".format(rounded)
        }

        val updatedConfig = current.config.copy(
            unit = newUnit,
            totalLength = newLength,
            startOffset = newStartOff,
            endOffset = newEndOff,
            targetSpacing = newSpacing,
            markWidth = newMarkWidth,
            endMargin = newEndMargin
        )

        _uiState.update {
            it.copy(
                config = updatedConfig,
                lengthInput = fmt(newLength),
                startOffsetInput = fmt(newStartOff),
                endOffsetInput = fmt(newEndOff),
                spacingInput = fmt(newSpacing),
                markWidthInput = fmt(newMarkWidth),
                endMarginInput = fmt(newEndMargin)
            )
        }
        recalculate()
    }

    fun setLayoutMode(mode: LayoutMode) {
        _uiState.update {
            it.copy(config = it.config.copy(layoutMode = mode))
        }
        recalculate()
    }

    fun setBoundaryStyle(style: BoundaryStyle) {
        _uiState.update {
            it.copy(config = it.config.copy(boundaryStyle = style))
        }
        recalculate()
    }

    fun setMeasurementOrigin(origin: MeasurementOrigin) {
        _uiState.update {
            it.copy(config = it.config.copy(origin = origin))
        }
        recalculate()
    }

    fun toggleDirection() {
        val currentDir = _uiState.value.config.direction
        val newDir = if (currentDir == LayoutDirection.NORMAL) LayoutDirection.REVERSED else LayoutDirection.NORMAL
        _uiState.update {
            it.copy(config = it.config.copy(direction = newDir))
        }
        recalculate()
    }

    fun toggleAdvancedSettings() {
        _uiState.update {
            it.copy(isAdvancedSettingsExpanded = !it.isAdvancedSettingsExpanded)
        }
    }

    fun selectMark(mark: MarkItem?) {
        _uiState.update { it.copy(selectedMark = mark) }
    }

    fun resetToDefaults() {
        val defaultConfig = RodLayoutConfig()
        _uiState.update {
            RodUiState(
                config = defaultConfig,
                lengthInput = "1000",
                startOffsetInput = "50",
                endOffsetInput = "50",
                spacingInput = "100",
                countInput = "10",
                markWidthInput = "3",
                endMarginInput = "0"
            )
        }
        recalculate()
    }

    fun applyPreset(preset: PresetTemplate) {
        fun fmt(d: Double): String {
            return if (d % 1.0 == 0.0) d.toInt().toString() else "%.2f".format(d)
        }

        val newConfig = _uiState.value.config.copy(
            unit = preset.unit,
            totalLength = preset.totalLength,
            startOffset = preset.startOffset,
            endOffset = preset.endOffset,
            targetSpacing = preset.targetSpacing,
            markCount = preset.markCount,
            layoutMode = preset.layoutMode,
            boundaryStyle = preset.boundaryStyle
        )

        _uiState.update {
            it.copy(
                config = newConfig,
                lengthInput = fmt(preset.totalLength),
                startOffsetInput = fmt(preset.startOffset),
                endOffsetInput = fmt(preset.endOffset),
                spacingInput = fmt(preset.targetSpacing),
                countInput = preset.markCount.toString()
            )
        }
        recalculate()
    }

    fun stepLength(delta: Double) {
        val cur = _uiState.value.config.totalLength
        val next = max(1.0, cur + delta)
        updateTotalLength(if (next % 1.0 == 0.0) next.toInt().toString() else "%.1f".format(next))
    }

    fun stepSpacing(delta: Double) {
        val cur = _uiState.value.config.targetSpacing
        val next = max(0.5, cur + delta)
        updateTargetSpacing(if (next % 1.0 == 0.0) next.toInt().toString() else "%.1f".format(next))
    }

    fun stepCount(delta: Int) {
        val cur = _uiState.value.config.markCount
        val next = max(0, cur + delta)
        updateMarkCount(next.toString())
    }

    private fun recalculate() {
        val currentConfig = _uiState.value.config
        val result = RodCalculator.calculate(currentConfig)
        _uiState.update {
            it.copy(
                result = result,
                // If previously selected mark is no longer in range, clear or update
                selectedMark = if (it.selectedMark != null) {
                    result.marks.firstOrNull { m -> m.index == it.selectedMark.index }
                } else null
            )
        }
    }
}
