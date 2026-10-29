package com.example.rodlayout

import com.example.rodlayout.calculator.ExportFormatter
import com.example.rodlayout.calculator.RodCalculator
import com.example.rodlayout.model.BoundaryStyle
import com.example.rodlayout.model.LayoutDirection
import com.example.rodlayout.model.LayoutMode
import com.example.rodlayout.model.MeasurementOrigin
import com.example.rodlayout.model.MeasurementUnit
import com.example.rodlayout.model.RodLayoutConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RodCalculatorTest {

    private val delta = 1e-4

    @Test
    fun testUnitConversions() {
        assertEquals(10.0, MeasurementUnit.CENTIMETERS.toMm(1.0), delta)
        assertEquals(25.4, MeasurementUnit.INCHES.toMm(1.0), delta)
        assertEquals(304.8, MeasurementUnit.FEET.toMm(1.0), delta)

        // Cross conversion
        val inchesInCm = MeasurementUnit.INCHES.convertTo(10.0, MeasurementUnit.CENTIMETERS)
        assertEquals(25.4, inchesInCm, delta)

        val mmInFeet = MeasurementUnit.MILLIMETERS.convertTo(304.8, MeasurementUnit.FEET)
        assertEquals(1.0, mmInFeet, delta)
    }

    @Test
    fun testFractionalInchesFormatting() {
        assertEquals("12\"", MeasurementUnit.formatAsFractionalInches(12.0))
        assertEquals("12 1/2\"", MeasurementUnit.formatAsFractionalInches(12.5))
        assertEquals("12 1/4\"", MeasurementUnit.formatAsFractionalInches(12.25))
        assertEquals("12 3/8\"", MeasurementUnit.formatAsFractionalInches(12.375))
        assertEquals("5/16\"", MeasurementUnit.formatAsFractionalInches(0.3125))
    }

    @Test
    fun testFixedSpacingAtBoundaries() {
        // Total: 1000mm, StartOffset: 100mm, EndOffset: 100mm
        // Usable span: 800mm, Spacing: 200mm -> 5 marks (100, 300, 500, 700, 900)
        val config = RodLayoutConfig(
            totalLength = 1000.0,
            startOffset = 100.0,
            endOffset = 100.0,
            unit = MeasurementUnit.MILLIMETERS,
            layoutMode = LayoutMode.FIXED_SPACING,
            targetSpacing = 200.0,
            boundaryStyle = BoundaryStyle.AT_BOUNDARIES
        )

        val result = RodCalculator.calculate(config)
        assertTrue(result.isSuccess)
        assertEquals(5, result.markCount)
        assertEquals(100.0, result.marks[0].positionFromStartMm, delta)
        assertEquals(300.0, result.marks[1].positionFromStartMm, delta)
        assertEquals(500.0, result.marks[2].positionFromStartMm, delta)
        assertEquals(700.0, result.marks[3].positionFromStartMm, delta)
        assertEquals(900.0, result.marks[4].positionFromStartMm, delta)

        // Spacing between marks
        assertEquals(200.0, result.marks[1].distanceFromPrevMm, delta)
        assertEquals(200.0, result.marks[4].distanceFromPrevMm, delta)
    }

    @Test
    fun testFixedSpacingCenteredGroup() {
        // Total: 1000mm, StartOffset: 50mm, EndOffset: 50mm -> usable: 900mm
        // Target Spacing: 200mm -> 4 spaces = 800mm span, 5 marks
        // leftover = 100mm -> 50mm extra margin on each side
        // first mark = 50 + 50 = 100mm, last mark = 900mm
        val config = RodLayoutConfig(
            totalLength = 1000.0,
            startOffset = 50.0,
            endOffset = 50.0,
            unit = MeasurementUnit.MILLIMETERS,
            layoutMode = LayoutMode.FIXED_SPACING,
            targetSpacing = 200.0,
            boundaryStyle = BoundaryStyle.CENTERED
        )

        val result = RodCalculator.calculate(config)
        assertTrue(result.isSuccess)
        assertEquals(5, result.markCount)
        assertEquals(100.0, result.marks[0].positionFromStartMm, delta)
        assertEquals(900.0, result.marks[4].positionFromStartMm, delta)
    }

    @Test
    fun testSpecifiedMarkCountAtBoundaries() {
        // Total: 1000mm, StartOffset: 100mm, EndOffset: 100mm -> usable: 800mm
        // Mark count: 5 -> 4 intervals -> spacing = 200mm
        val config = RodLayoutConfig(
            totalLength = 1000.0,
            startOffset = 100.0,
            endOffset = 100.0,
            unit = MeasurementUnit.MILLIMETERS,
            layoutMode = LayoutMode.MARK_COUNT,
            markCount = 5,
            boundaryStyle = BoundaryStyle.AT_BOUNDARIES
        )

        val result = RodCalculator.calculate(config)
        assertTrue(result.isSuccess)
        assertEquals(5, result.markCount)
        assertEquals(200.0, result.actualSpacingMm, delta)
        assertEquals(100.0, result.marks[0].positionFromStartMm, delta)
        assertEquals(900.0, result.marks[4].positionFromStartMm, delta)
    }

    @Test
    fun testSpecifiedMarkCountEqualSpaces() {
        // Baluster style: 3 marks across 800mm usable span -> 4 intervals -> spacing = 200mm
        // Start: 100mm. Marks at 300mm, 500mm, 700mm.
        val config = RodLayoutConfig(
            totalLength = 1000.0,
            startOffset = 100.0,
            endOffset = 100.0,
            unit = MeasurementUnit.MILLIMETERS,
            layoutMode = LayoutMode.MARK_COUNT,
            markCount = 3,
            boundaryStyle = BoundaryStyle.EQUAL_SPACES
        )

        val result = RodCalculator.calculate(config)
        assertTrue(result.isSuccess)
        assertEquals(3, result.markCount)
        assertEquals(200.0, result.actualSpacingMm, delta)
        assertEquals(300.0, result.marks[0].positionFromStartMm, delta)
        assertEquals(500.0, result.marks[1].positionFromStartMm, delta)
        assertEquals(700.0, result.marks[2].positionFromStartMm, delta)
    }

    @Test
    fun testZeroAndOneMarkCases() {
        // Zero marks
        val configZero = RodLayoutConfig(
            totalLength = 1000.0,
            layoutMode = LayoutMode.MARK_COUNT,
            markCount = 0
        )
        val resZero = RodCalculator.calculate(configZero)
        assertTrue(resZero.isSuccess)
        assertEquals(0, resZero.markCount)

        // One mark in 1000mm with 100mm offsets -> center is 500mm
        val configOne = RodLayoutConfig(
            totalLength = 1000.0,
            startOffset = 100.0,
            endOffset = 100.0,
            layoutMode = LayoutMode.MARK_COUNT,
            markCount = 1
        )
        val resOne = RodCalculator.calculate(configOne)
        assertTrue(resOne.isSuccess)
        assertEquals(1, resOne.markCount)
        assertEquals(500.0, resOne.marks[0].positionFromStartMm, delta)
    }

    @Test
    fun testMeasurementOriginCenter() {
        // Total: 1000mm. Center is 500mm.
        // Offsets 100mm each -> marks at 100, 500, 900
        val config = RodLayoutConfig(
            totalLength = 1000.0,
            startOffset = 100.0,
            endOffset = 100.0,
            layoutMode = LayoutMode.MARK_COUNT,
            markCount = 3,
            origin = MeasurementOrigin.FROM_CENTER
        )
        val result = RodCalculator.calculate(config)
        assertTrue(result.isSuccess)
        assertEquals(3, result.markCount)
        assertEquals(-400.0, result.marks[0].positionFromOriginMm, delta)
        assertEquals(0.0, result.marks[1].positionFromOriginMm, delta)
        assertEquals(400.0, result.marks[2].positionFromOriginMm, delta)
    }

    @Test
    fun testReversedDirection() {
        // Total: 1000mm, StartOffset: 100mm, EndOffset: 200mm
        // If reversed, the start offset becomes 200mm from right edge (800mm from left edge)
        val config = RodLayoutConfig(
            totalLength = 1000.0,
            startOffset = 100.0,
            endOffset = 200.0,
            layoutMode = LayoutMode.MARK_COUNT,
            markCount = 2,
            direction = LayoutDirection.REVERSED
        )
        val result = RodCalculator.calculate(config)
        assertTrue(result.isSuccess)
        assertEquals(2, result.markCount)
        // In reversed direction: First mark measured is at effective start (which was end offset 200mm)
        // Physical position from left is 800mm
        assertEquals(800.0, result.marks[0].positionFromStartMm, delta)
        // Second mark is at effective end (which was start offset 100mm), physical position 100mm
        assertEquals(100.0, result.marks[1].positionFromStartMm, delta)
    }

    @Test
    fun testValidationErrors() {
        // Negative length
        val badLength = RodLayoutConfig(totalLength = -50.0)
        assertFalse(RodCalculator.validate(badLength).isValid)

        // Offsets exceed length
        val badOffsets = RodLayoutConfig(totalLength = 100.0, startOffset = 60.0, endOffset = 60.0)
        assertFalse(RodCalculator.validate(badOffsets).isValid)

        // Negative target spacing
        val badSpacing = RodLayoutConfig(
            layoutMode = LayoutMode.FIXED_SPACING,
            targetSpacing = -10.0
        )
        assertFalse(RodCalculator.validate(badSpacing).isValid)
    }

    @Test
    fun testNoCumulativeRoundingErrors() {
        // 101 marks across 1000mm -> spacing = 10mm exactly
        val config = RodLayoutConfig(
            totalLength = 1000.0,
            startOffset = 0.0,
            endOffset = 0.0,
            layoutMode = LayoutMode.MARK_COUNT,
            markCount = 101
        )
        val result = RodCalculator.calculate(config)
        assertTrue(result.isSuccess)
        assertEquals(101, result.markCount)
        assertEquals(10.0, result.actualSpacingMm, delta)

        // Check mark 50 (index 49)
        assertEquals(490.0, result.marks[49].positionFromStartMm, delta)
        // Check last mark
        assertEquals(1000.0, result.marks[100].positionFromStartMm, delta)
    }

    @Test
    fun testExportCsvAndReport() {
        val config = RodLayoutConfig(
            totalLength = 100.0,
            startOffset = 10.0,
            endOffset = 10.0,
            unit = MeasurementUnit.CENTIMETERS,
            layoutMode = LayoutMode.MARK_COUNT,
            markCount = 5
        )
        val result = RodCalculator.calculate(config)
        val csv = ExportFormatter.toCsv(config, result)
        assertTrue(csv.contains("Mark #"))
        assertTrue(csv.contains("Position from Start (cm)"))

        val report = ExportFormatter.toWorkshopReport(config, result)
        assertTrue(report.contains("RODLAYOUT WORKSHOP REPORT"))
        assertTrue(report.contains("Total Rod Length : 100.00 cm"))
    }
}
