package com.example.rodlayout

import com.example.rodlayout.model.BoundaryStyle
import com.example.rodlayout.model.LayoutDirection
import com.example.rodlayout.model.LayoutMode
import com.example.rodlayout.model.MeasurementOrigin
import com.example.rodlayout.model.MeasurementUnit
import com.example.rodlayout.ui.viewmodel.AppTheme
import com.example.rodlayout.ui.viewmodel.RodViewModel
import com.example.rodlayout.ui.viewmodel.SettingsViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RodViewModelTest {

    @Test
    fun testInitialState() {
        val viewModel = RodViewModel()
        val state = viewModel.uiState.value

        assertEquals("1000", state.lengthInput)
        assertEquals("50", state.startOffsetInput)
        assertEquals("50", state.endOffsetInput)
        assertEquals(MeasurementUnit.MILLIMETERS, state.config.unit)
        assertTrue(state.result.isSuccess)
        assertTrue(state.result.markCount > 0)
    }

    @Test
    fun testUpdateInputsRecalculates() {
        val viewModel = RodViewModel()
        viewModel.updateTotalLength("1200")
        assertEquals(1200.0, viewModel.uiState.value.config.totalLength, 1e-4)

        viewModel.updateStartOffset("100")
        viewModel.updateEndOffset("100")
        viewModel.updateTargetSpacing("200")

        val res = viewModel.uiState.value.result
        assertTrue(res.isSuccess)
        // Usable = 1200 - 200 = 1000mm. Spacing = 200mm -> 6 marks (100, 300, 500, 700, 900, 1100)
        assertEquals(6, res.markCount)
    }

    @Test
    fun testUnitConversion() {
        val viewModel = RodViewModel()
        viewModel.updateTotalLength("1000") // 1000 mm
        viewModel.setMeasurementUnit(MeasurementUnit.CENTIMETERS, convertValues = true)

        val state = viewModel.uiState.value
        assertEquals(MeasurementUnit.CENTIMETERS, state.config.unit)
        assertEquals(100.0, state.config.totalLength, 1e-4)
        assertEquals("100", state.lengthInput)
    }

    @Test
    fun testLayoutModeSwitching() {
        val viewModel = RodViewModel()
        viewModel.setLayoutMode(LayoutMode.MARK_COUNT)
        assertEquals(LayoutMode.MARK_COUNT, viewModel.uiState.value.config.layoutMode)

        viewModel.updateMarkCount("7")
        val res = viewModel.uiState.value.result
        assertTrue(res.isSuccess)
        assertEquals(7, res.markCount)
    }

    @Test
    fun testDirectionToggle() {
        val viewModel = RodViewModel()
        assertEquals(LayoutDirection.NORMAL, viewModel.uiState.value.config.direction)
        viewModel.toggleDirection()
        assertEquals(LayoutDirection.REVERSED, viewModel.uiState.value.config.direction)
        viewModel.toggleDirection()
        assertEquals(LayoutDirection.NORMAL, viewModel.uiState.value.config.direction)
    }

    @Test
    fun testPresets() {
        val viewModel = RodViewModel()
        val balusterPreset = viewModel.presets.first { it.title.contains("Balusters") }
        viewModel.applyPreset(balusterPreset)

        val state = viewModel.uiState.value
        assertEquals(MeasurementUnit.INCHES, state.config.unit)
        assertEquals(LayoutMode.MARK_COUNT, state.config.layoutMode)
        assertEquals(BoundaryStyle.EQUAL_SPACES, state.config.boundaryStyle)
        assertTrue(state.result.isSuccess)
        assertEquals(16, state.result.markCount)
    }

    @Test
    fun testSteppers() {
        val viewModel = RodViewModel()
        viewModel.updateTotalLength("1000")
        viewModel.stepLength(50.0)
        assertEquals(1050.0, viewModel.uiState.value.config.totalLength, 1e-4)

        viewModel.stepLength(-100.0)
        assertEquals(950.0, viewModel.uiState.value.config.totalLength, 1e-4)

        viewModel.stepCount(2)
        assertEquals(12, viewModel.uiState.value.config.markCount)
    }

    @Test
    fun testSettingsViewModel() {
        val settingsVm = SettingsViewModel()
        assertEquals(AppTheme.SYSTEM, settingsVm.uiState.value.theme)
        assertEquals(2, settingsVm.uiState.value.decimalPrecision)
        assertTrue(settingsVm.uiState.value.showImperialFractions)

        settingsVm.setTheme(AppTheme.DARK)
        assertEquals(AppTheme.DARK, settingsVm.uiState.value.theme)

        settingsVm.setDecimalPrecision(4)
        assertEquals(4, settingsVm.uiState.value.decimalPrecision)

        settingsVm.setShowImperialFractions(false)
        assertFalse(settingsVm.uiState.value.showImperialFractions)
    }
}
