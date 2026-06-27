package io.github.komposeCharts.style

/** Where value labels are positioned relative to bars. */
enum class ValueLabelPosition {
    /** No value labels. */
    NONE,
    /** Above the bar (outside). */
    TOP,
    /** Centered inside the bar. */
    CENTER,
    /** Inside the bar near the baseline. */
    INSIDE_BOTTOM,
    /** Above the bar with extra offset. */
    OUTSIDE,
}
