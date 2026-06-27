package io.github.komposeCharts.style

/** How multiple series are arranged within a category group. */
enum class BarGrouping {
    /** Bars side-by-side within each category. */
    GROUPED,
    /** Bars stacked on top of each other. */
    STACKED,
    /** Stacked and normalized so each category totals 100%. */
    PERCENT_STACKED,
}
