package com.example.rodlayout.model

/**
 * Represents a single mark position along the rod.
 * Stores exact internal mm values to avoid cumulative rounding errors,
 * with utility methods for display in any unit.
 */
data class MarkItem(
    val index: Int, // 1-based index
    val positionFromStartMm: Double,
    val distanceFromPrevMm: Double,
    val positionFromCenterMm: Double,
    val positionFromOriginMm: Double
) {
    fun positionInUnit(unit: MeasurementUnit): Double = unit.fromMm(positionFromStartMm)
    fun distanceFromPrevInUnit(unit: MeasurementUnit): Double = unit.fromMm(distanceFromPrevMm)
    fun positionFromCenterInUnit(unit: MeasurementUnit): Double = unit.fromMm(positionFromCenterMm)
    fun positionFromOriginInUnit(unit: MeasurementUnit): Double = unit.fromMm(positionFromOriginMm)

    fun formattedPosition(unit: MeasurementUnit, decimals: Int = 2): String =
        unit.formatWithSymbol(positionInUnit(unit), decimals)

    fun formattedDistanceFromPrev(unit: MeasurementUnit, decimals: Int = 2): String =
        unit.formatWithSymbol(distanceFromPrevInUnit(unit), decimals)

    fun formattedPositionFromCenter(unit: MeasurementUnit, decimals: Int = 2): String {
        val value = positionFromCenterInUnit(unit)
        val prefix = if (value > 0) "+" else ""
        return "$prefix${unit.formatWithSymbol(value, decimals)}"
    }

    fun formattedPositionFromOrigin(unit: MeasurementUnit, decimals: Int = 2): String {
        return unit.formatWithSymbol(positionFromOriginInUnit(unit), decimals)
    }

    fun fractionalInches(unit: MeasurementUnit, maxDenominator: Int = 32): String? {
        if (unit != MeasurementUnit.INCHES && unit != MeasurementUnit.FEET) return null
        val inches = MeasurementUnit.INCHES.fromMm(positionFromStartMm)
        return MeasurementUnit.formatAsFractionalInches(inches, maxDenominator)
    }
}
