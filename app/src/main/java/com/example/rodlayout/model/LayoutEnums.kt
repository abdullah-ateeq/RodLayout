package com.example.rodlayout.model

/**
 * Layout calculation mode.
 */
enum class LayoutMode(val displayName: String, val description: String) {
    FIXED_SPACING(
        displayName = "Fixed Spacing",
        description = "Specify spacing between marks; calculates how many marks fit."
    ),
    MARK_COUNT(
        displayName = "Specified Count",
        description = "Specify number of marks; distributes them evenly across the rod."
    )
}

/**
 * How marks relate to usable length boundaries.
 */
enum class BoundaryStyle(val displayName: String, val description: String) {
    AT_BOUNDARIES(
        displayName = "At Boundaries",
        description = "First and last marks sit exactly at the start and end offsets."
    ),
    EQUAL_SPACES(
        displayName = "Equal Margins (N+1 spaces)",
        description = "Usable length is divided into N+1 equal spaces; equal margin at both ends."
    ),
    CENTERED(
        displayName = "Centered Group",
        description = "Specified spacing maintained; remaining excess length split evenly at both ends."
    )
}

/**
 * Reference point from which mark positions are measured.
 */
enum class MeasurementOrigin(val displayName: String, val description: String) {
    FROM_START(
        displayName = "Starting Edge (0 at start)",
        description = "All marks measured from the starting edge of the rod."
    ),
    FROM_CENTER(
        displayName = "Rod Center (0 at midpoint)",
        description = "All marks measured relative to the rod's centerline."
    )
}

/**
 * Reading / fabrication direction along the rod.
 */
enum class LayoutDirection(val displayName: String, val symbol: String) {
    NORMAL(displayName = "Left to Right", symbol = "➔"),
    REVERSED(displayName = "Right to Left", symbol = "⬅")
}
