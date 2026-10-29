package com.example.rodlayout.calculator

import com.example.rodlayout.model.BoundaryStyle
import com.example.rodlayout.model.CalculationResult
import com.example.rodlayout.model.LayoutDirection
import com.example.rodlayout.model.LayoutMode
import com.example.rodlayout.model.MarkItem
import com.example.rodlayout.model.MeasurementOrigin
import com.example.rodlayout.model.RodLayoutConfig
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max

/**
 * Pure calculation engine for RodLayout.
 * Computes marking positions along a rod/rail/pipe with high precision,
 * avoiding cumulative rounding errors by evaluating positions algebraically.
 */
object RodCalculator {

    private const val EPSILON = 1e-7

    /**
     * Calculates the mark positions and layout metrics based on [config].
     */
    fun calculate(config: RodLayoutConfig): CalculationResult {
        // Validation checks
        val validation = validate(config)
        if (!validation.isValid) {
            return CalculationResult(
                isSuccess = false,
                error = validation.error,
                warnings = validation.warnings,
                totalLengthMm = config.totalLengthMm,
                startOffsetMm = config.startOffsetMm,
                endOffsetMm = config.endOffsetMm
            )
        }

        val totalLengthMm = config.totalLengthMm
        // Account for layout direction (if reversed, start and end offsets swap roles)
        val isReversed = config.direction == LayoutDirection.REVERSED
        val effectiveStartOffsetMm = if (!isReversed) config.startOffsetMm else config.endOffsetMm
        val effectiveEndOffsetMm = if (!isReversed) config.endOffsetMm else config.startOffsetMm

        val endMarginMm = config.endMarginMm
        val usableSpanMm = totalLengthMm - effectiveStartOffsetMm - effectiveEndOffsetMm - (2 * endMarginMm)

        val rawPositionsMm = mutableListOf<Double>()
        var actualSpacingMm = 0.0
        var actualStartMarginMm = effectiveStartOffsetMm + endMarginMm
        var actualEndMarginMm = effectiveEndOffsetMm + endMarginMm

        when (config.layoutMode) {
            LayoutMode.FIXED_SPACING -> {
                val targetSpacingMm = config.targetSpacingMm
                if (usableSpanMm < -EPSILON) {
                    return CalculationResult(
                        isSuccess = false,
                        error = "Usable span is negative. Check offsets and margins.",
                        totalLengthMm = totalLengthMm
                    )
                }

                when (config.boundaryStyle) {
                    BoundaryStyle.AT_BOUNDARIES -> {
                        // Place marks starting at start boundary, stepping by targetSpacing
                        var pos = effectiveStartOffsetMm + endMarginMm
                        val maxPos = totalLengthMm - effectiveEndOffsetMm - endMarginMm
                        while (pos <= maxPos + EPSILON) {
                            rawPositionsMm.add(pos)
                            pos += targetSpacingMm
                        }
                        actualSpacingMm = targetSpacingMm
                    }

                    BoundaryStyle.CENTERED -> {
                        // Determine how many marks fit with targetSpacing
                        val count = if (usableSpanMm >= 0) {
                            floor(usableSpanMm / targetSpacingMm).toInt() + 1
                        } else 0

                        if (count <= 1) {
                            // Single mark centered in usable span
                            val centerPos = effectiveStartOffsetMm + endMarginMm + (usableSpanMm / 2.0)
                            rawPositionsMm.add(centerPos)
                            actualSpacingMm = 0.0
                        } else {
                            val markArraySpan = (count - 1) * targetSpacingMm
                            val leftover = usableSpanMm - markArraySpan
                            val extraMargin = leftover / 2.0
                            val firstPos = effectiveStartOffsetMm + endMarginMm + extraMargin
                            actualStartMarginMm += extraMargin
                            actualEndMarginMm += extraMargin
                            actualSpacingMm = targetSpacingMm

                            for (i in 0 until count) {
                                rawPositionsMm.add(firstPos + (i * targetSpacingMm))
                            }
                        }
                    }

                    BoundaryStyle.EQUAL_SPACES -> {
                        // Fit N marks such that they divide usable span into N+1 equal spaces
                        // Choose N closest to usableSpan / targetSpacing
                        val n = max(1, (usableSpanMm / targetSpacingMm).toInt())
                        val spacing = usableSpanMm / (n + 1)
                        actualSpacingMm = spacing
                        val firstPos = effectiveStartOffsetMm + endMarginMm + spacing
                        for (i in 0 until n) {
                            rawPositionsMm.add(firstPos + (i * spacing))
                        }
                    }
                }
            }

            LayoutMode.MARK_COUNT -> {
                val count = config.markCount
                if (count == 0) {
                    // Zero marks case
                    return CalculationResult(
                        isSuccess = true,
                        marks = emptyList(),
                        totalLengthMm = totalLengthMm,
                        startOffsetMm = effectiveStartOffsetMm,
                        endOffsetMm = effectiveEndOffsetMm,
                        usableSpanMm = usableSpanMm,
                        actualSpacingMm = 0.0,
                        warnings = validation.warnings
                    )
                } else if (count == 1) {
                    // Single mark centered in usable span
                    val mid = effectiveStartOffsetMm + endMarginMm + (usableSpanMm / 2.0)
                    rawPositionsMm.add(mid)
                    actualSpacingMm = 0.0
                } else {
                    when (config.boundaryStyle) {
                        BoundaryStyle.AT_BOUNDARIES, BoundaryStyle.CENTERED -> {
                            // Marks at both ends of the usable span (N-1 intervals)
                            val spacing = usableSpanMm / (count - 1)
                            actualSpacingMm = spacing
                            val firstPos = effectiveStartOffsetMm + endMarginMm
                            for (i in 0 until count) {
                                // Direct calculation avoids cumulative rounding
                                rawPositionsMm.add(firstPos + (i * spacing))
                            }
                        }

                        BoundaryStyle.EQUAL_SPACES -> {
                            // N marks divide the usable span into N+1 spaces
                            val spacing = usableSpanMm / (count + 1)
                            actualSpacingMm = spacing
                            val firstPos = effectiveStartOffsetMm + endMarginMm + spacing
                            for (i in 0 until count) {
                                rawPositionsMm.add(firstPos + (i * spacing))
                            }
                        }
                    }
                }
            }
        }

        // If direction is reversed, sort positions from the actual physical left or flip indices?
        // In fabrication, if direction is REVERSED, the operator measures from right to left (0 at right end).
        // For physical diagram drawing, the physical position from start (left edge) is:
        // If reversed, the position along the rod from left edge is totalLengthMm - pos.
        val finalMarks = rawPositionsMm.mapIndexed { idx, rawPos ->
            val positionFromStartMm = if (!isReversed) rawPos else (totalLengthMm - rawPos)
            val prevRawPos = if (idx > 0) rawPositionsMm[idx - 1] else null
            val distanceFromPrevMm = if (prevRawPos != null) abs(rawPos - prevRawPos) else 0.0

            val positionFromCenterMm = positionFromStartMm - (totalLengthMm / 2.0)

            val positionFromOriginMm = when (config.origin) {
                MeasurementOrigin.FROM_START -> {
                    // If normal, rawPos is distance from start edge.
                    // If reversed, rawPos is distance from end edge (acting as new start origin).
                    rawPos
                }
                MeasurementOrigin.FROM_CENTER -> {
                    // Relative to center: positive in travel direction, negative behind center
                    if (!isReversed) positionFromCenterMm else -positionFromCenterMm
                }
            }

            MarkItem(
                index = idx + 1,
                positionFromStartMm = positionFromStartMm,
                distanceFromPrevMm = distanceFromPrevMm,
                positionFromCenterMm = positionFromCenterMm,
                positionFromOriginMm = positionFromOriginMm
            )
        }

        // Calculate overlap warning if mark width is larger than spacing (and there is >1 mark)
        val finalWarnings = validation.warnings.toMutableList()
        if (finalMarks.size > 1 && config.markWidthMm > 0 && config.markWidthMm > actualSpacingMm) {
            finalWarnings.add(
                "Mark width (${config.unit.formatWithSymbol(config.markWidth)}) exceeds spacing " +
                        "(${config.unit.formatWithSymbol(config.unit.fromMm(actualSpacingMm))}). Marks will overlap."
            )
        }

        val firstMark = finalMarks.firstOrNull()?.positionFromStartMm
        val lastMark = finalMarks.lastOrNull()?.positionFromStartMm

        return CalculationResult(
            isSuccess = true,
            marks = finalMarks,
            totalLengthMm = totalLengthMm,
            startOffsetMm = effectiveStartOffsetMm,
            endOffsetMm = effectiveEndOffsetMm,
            usableSpanMm = usableSpanMm,
            actualSpacingMm = actualSpacingMm,
            actualStartMarginMm = actualStartMarginMm,
            actualEndMarginMm = actualEndMarginMm,
            firstMarkPosMm = firstMark,
            lastMarkPosMm = lastMark,
            warnings = finalWarnings
        )
    }

    /**
     * Validates input dimensions and configurations.
     */
    fun validate(config: RodLayoutConfig): ValidationInfo {
        val warnings = mutableListOf<String>()

        if (config.totalLength <= 0) {
            return ValidationInfo(false, "Total rod length must be greater than zero.", warnings)
        }

        if (config.startOffset < 0 || config.endOffset < 0) {
            return ValidationInfo(false, "Offsets cannot be negative.", warnings)
        }

        if (config.endMargin < 0) {
            return ValidationInfo(false, "End margin cannot be negative.", warnings)
        }

        val totalOffsetsMm = config.startOffsetMm + config.endOffsetMm + (2 * config.endMarginMm)
        if (totalOffsetsMm >= config.totalLengthMm) {
            return ValidationInfo(
                false,
                "Offsets and margins (${config.unit.format(config.unit.fromMm(totalOffsetsMm))} ${config.unit.symbol}) " +
                        "equal or exceed total rod length (${config.unit.formatWithSymbol(config.totalLength)}).",
                warnings
            )
        }

        when (config.layoutMode) {
            LayoutMode.FIXED_SPACING -> {
                if (config.targetSpacing <= 0) {
                    return ValidationInfo(false, "Spacing must be greater than zero.", warnings)
                }
                val usableSpanMm = config.totalLengthMm - totalOffsetsMm
                if (config.targetSpacingMm > usableSpanMm) {
                    warnings.add("Target spacing exceeds usable span. Only one mark will fit.")
                }
            }

            LayoutMode.MARK_COUNT -> {
                if (config.markCount < 0) {
                    return ValidationInfo(false, "Mark count cannot be negative.", warnings)
                }
                if (config.markCount == 0) {
                    warnings.add("Mark count is 0; no marks will be generated.")
                }
            }
        }

        return ValidationInfo(true, null, warnings)
    }
}

data class ValidationInfo(
    val isValid: Boolean,
    val error: String? = null,
    val warnings: List<String> = emptyList()
)
