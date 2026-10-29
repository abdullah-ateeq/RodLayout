package com.example.rodlayout.model

/**
 * User inputs and configuration for the rod layout calculation.
 * Input values are stored in the selected [unit].
 */
data class RodLayoutConfig(
    val totalLength: Double = 1000.0,
    val startOffset: Double = 50.0,
    val endOffset: Double = 50.0,
    val markWidth: Double = 3.0,
    val endMargin: Double = 0.0,
    val unit: MeasurementUnit = MeasurementUnit.MILLIMETERS,
    val layoutMode: LayoutMode = LayoutMode.FIXED_SPACING,
    val targetSpacing: Double = 100.0,
    val markCount: Int = 10,
    val boundaryStyle: BoundaryStyle = BoundaryStyle.AT_BOUNDARIES,
    val origin: MeasurementOrigin = MeasurementOrigin.FROM_START,
    val direction: LayoutDirection = LayoutDirection.NORMAL
) {
    /**
     * Converts this configuration to mm values for pure calculations.
     */
    val totalLengthMm: Double get() = unit.toMm(totalLength)
    val startOffsetMm: Double get() = unit.toMm(startOffset)
    val endOffsetMm: Double get() = unit.toMm(endOffset)
    val markWidthMm: Double get() = unit.toMm(markWidth)
    val endMarginMm: Double get() = unit.toMm(endMargin)
    val targetSpacingMm: Double get() = unit.toMm(targetSpacing)
}

/**
 * Result of the layout calculation.
 */
data class CalculationResult(
    val isSuccess: Boolean,
    val marks: List<MarkItem> = emptyList(),
    val totalLengthMm: Double = 0.0,
    val startOffsetMm: Double = 0.0,
    val endOffsetMm: Double = 0.0,
    val usableSpanMm: Double = 0.0,
    val actualSpacingMm: Double = 0.0,
    val actualStartMarginMm: Double = 0.0,
    val actualEndMarginMm: Double = 0.0,
    val firstMarkPosMm: Double? = null,
    val lastMarkPosMm: Double? = null,
    val error: String? = null,
    val warnings: List<String> = emptyList()
) {
    val markCount: Int get() = marks.size
}
