package com.example.rodlayout.model

import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Supported measurement units for RodLayout.
 * Base internal unit for all calculations is Millimeters (mm).
 */
enum class MeasurementUnit(
    val id: String,
    val displayName: String,
    val symbol: String,
    val mmFactor: Double
) {
    MILLIMETERS("mm", "Millimeters", "mm", 1.0),
    CENTIMETERS("cm", "Centimeters", "cm", 10.0),
    INCHES("in", "Inches", "in", 25.4),
    FEET("ft", "Feet", "ft", 304.8);

    /**
     * Converts a value in this unit to the base internal unit (mm).
     */
    fun toMm(value: Double): Double = value * mmFactor

    /**
     * Converts a value in base internal unit (mm) to this unit.
     */
    fun fromMm(mmValue: Double): Double = mmValue / mmFactor

    /**
     * Converts a value directly from this unit to a target unit.
     */
    fun convertTo(value: Double, targetUnit: MeasurementUnit): Double {
        if (this == targetUnit) return value
        val mm = toMm(value)
        return targetUnit.fromMm(mm)
    }

    /**
     * Format a value in this unit with given decimal precision.
     */
    fun format(value: Double, decimals: Int = 2): String {
        return "%.${decimals}f".format(value)
    }

    /**
     * Format with unit symbol e.g., "125.50 mm".
     */
    fun formatWithSymbol(value: Double, decimals: Int = 2): String {
        return "${format(value, decimals)} $symbol"
    }

    companion object {
        fun fromId(id: String): MeasurementUnit {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: MILLIMETERS
        }

        /**
         * Formats decimal inches to nearest fraction (e.g. 12 3/8").
         * denominator: 16, 32, or 64.
         */
        fun formatAsFractionalInches(inches: Double, maxDenominator: Int = 32): String {
            val sign = if (inches < 0) "-" else ""
            val absInches = abs(inches)
            var whole = absInches.toInt()
            val fractionalPart = absInches - whole

            var numerator = (fractionalPart * maxDenominator).roundToInt()
            var denominator = maxDenominator

            if (numerator == maxDenominator) {
                whole += 1
                numerator = 0
            }

            if (numerator == 0) {
                return "$sign$whole\""
            }

            // Reduce fraction
            while (numerator % 2 == 0 && denominator % 2 == 0) {
                numerator /= 2
                denominator /= 2
            }

            return if (whole == 0) {
                "$sign$numerator/$denominator\""
            } else {
                "$sign$whole $numerator/$denominator\""
            }
        }
    }
}
