package com.example.rodlayout.calculator

import com.example.rodlayout.model.CalculationResult
import com.example.rodlayout.model.MeasurementUnit
import com.example.rodlayout.model.RodLayoutConfig
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Generates formatted CSV, clipboard text, and workshop cut-list reports.
 */
object ExportFormatter {

    /**
     * Formats results as standard CSV.
     */
    fun toCsv(
        config: RodLayoutConfig,
        result: CalculationResult,
        decimals: Int = 2
    ): String {
        val unit = config.unit
        val sb = StringBuilder()
        sb.append("Mark #,Position from Start (${unit.symbol}),Distance from Previous (${unit.symbol}),Position from Center (${unit.symbol})")
        val isImperial = unit == MeasurementUnit.INCHES || unit == MeasurementUnit.FEET
        if (isImperial) {
            sb.append(",Imperial Fraction")
        }
        sb.append("\n")

        for (mark in result.marks) {
            sb.append(mark.index).append(",")
            sb.append(unit.format(mark.positionInUnit(unit), decimals)).append(",")
            sb.append(unit.format(mark.distanceFromPrevInUnit(unit), decimals)).append(",")
            sb.append(unit.format(mark.positionFromCenterInUnit(unit), decimals))
            if (isImperial) {
                sb.append(",\"").append(mark.fractionalInches(unit) ?: "").append("\"")
            }
            sb.append("\n")
        }

        return sb.toString()
    }

    /**
     * Formats all mark positions as a clean plain text list suitable for quick copying.
     */
    fun toClipboardText(
        config: RodLayoutConfig,
        result: CalculationResult,
        decimals: Int = 2
    ): String {
        val unit = config.unit
        val sb = StringBuilder()
        sb.appendLine("RodLayout Marking List")
        sb.appendLine("Total Length: ${unit.formatWithSymbol(config.totalLength, decimals)} | Marks: ${result.markCount} | Spacing: ${unit.formatWithSymbol(unit.fromMm(result.actualSpacingMm), decimals)}")
        sb.appendLine("----------------------------------------")
        for (mark in result.marks) {
            val fraction = mark.fractionalInches(unit)?.let { " ($it)" } ?: ""
            sb.appendLine("#${mark.index.toString().padStart(2, ' ')}: ${mark.formattedPosition(unit, decimals)}$fraction | Δ: ${mark.formattedDistanceFromPrev(unit, decimals)}")
        }
        return sb.toString()
    }

    /**
     * Formats a comprehensive workshop fabrication sheet.
     */
    fun toWorkshopReport(
        config: RodLayoutConfig,
        result: CalculationResult,
        decimals: Int = 2
    ): String {
        val unit = config.unit
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val dateStr = dateFormat.format(Date())

        val sb = StringBuilder()
        sb.appendLine("==================================================")
        sb.appendLine("              RODLAYOUT WORKSHOP REPORT           ")
        sb.appendLine("==================================================")
        sb.appendLine("Generated: $dateStr")
        sb.appendLine("Unit: ${unit.displayName} (${unit.symbol})")
        sb.appendLine("Mode: ${config.layoutMode.displayName}")
        sb.appendLine("Boundary Style: ${config.boundaryStyle.displayName}")
        sb.appendLine("Direction: ${config.direction.displayName}")
        sb.appendLine("Origin: ${config.origin.displayName}")
        sb.appendLine("--------------------------------------------------")
        sb.appendLine("SPECIFICATIONS:")
        sb.appendLine("  Total Rod Length : ${unit.formatWithSymbol(config.totalLength, decimals)}")
        sb.appendLine("  Starting Offset  : ${unit.formatWithSymbol(config.startOffset, decimals)}")
        sb.appendLine("  Ending Offset    : ${unit.formatWithSymbol(config.endOffset, decimals)}")
        if (config.endMargin > 0) {
            sb.appendLine("  End Margin       : ${unit.formatWithSymbol(config.endMargin, decimals)}")
        }
        sb.appendLine("  Mark Width (Kerf): ${unit.formatWithSymbol(config.markWidth, decimals)}")
        sb.appendLine("  Usable Marking   : ${unit.formatWithSymbol(unit.fromMm(result.usableSpanMm), decimals)}")
        sb.appendLine("  Total Marks      : ${result.markCount}")
        sb.appendLine("  Actual Spacing   : ${unit.formatWithSymbol(unit.fromMm(result.actualSpacingMm), decimals)}")
        result.firstMarkPosMm?.let {
            sb.appendLine("  First Mark Pos   : ${unit.formatWithSymbol(unit.fromMm(it), decimals)}")
        }
        result.lastMarkPosMm?.let {
            sb.appendLine("  Last Mark Pos    : ${unit.formatWithSymbol(unit.fromMm(it), decimals)}")
        }
        sb.appendLine("--------------------------------------------------")
        sb.appendLine("MARKING POSITIONS TABLE:")
        sb.appendLine(" #  | Distance from Start |  Delta (Prev)  | Center Ref")
        sb.appendLine("----+---------------------+----------------+-----------")

        for (mark in result.marks) {
            val idx = mark.index.toString().padStart(3, ' ')
            val pos = mark.formattedPosition(unit, decimals).padEnd(19, ' ')
            val prev = mark.formattedDistanceFromPrev(unit, decimals).padEnd(14, ' ')
            val center = mark.formattedPositionFromCenter(unit, decimals)
            sb.appendLine("$idx | $pos | $prev | $center")
        }

        sb.appendLine("==================================================")
        sb.appendLine("   RodLayout - Accurate Fabrication Marking App   ")
        sb.appendLine("==================================================")
        return sb.toString()
    }
}
