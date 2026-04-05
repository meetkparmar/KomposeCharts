package io.github.komposeCharts.sample.design

import androidx.compose.ui.unit.sp

internal object AppTypography {

    // ── Letter spacing ─────────────────────────────────────────────────────────
    /** Tight tracking for large hero headings. */
    val headingLetterSpacing = (-0.9).sp
    /** Wide tracking for ALL-CAPS section labels. */
    val sectionLabelLetterSpacing = 1.4.sp
    /** Wide tracking for monospace-style status labels. */
    val statusLabelLetterSpacing = 1.sp

    // ── Line height ────────────────────────────────────────────────────────────
    /** Relaxed line height for subtitle / description text. */
    val subtitleLineHeight = 28.sp
    /** Compact line height for multi-line helper text. */
    val descriptionLineHeight = 20.sp
}
