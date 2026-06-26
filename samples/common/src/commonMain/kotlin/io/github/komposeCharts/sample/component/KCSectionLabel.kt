package io.github.komposeCharts.sample.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import io.github.komposeCharts.sample.design.AppColors
import io.github.komposeCharts.sample.design.AppTypography
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun KCSectionLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text.uppercase(),
        modifier = modifier,
        color = AppColors.TextFaint,
        fontSize = AppTypography.Mono,
        fontFamily = FontFamily.Monospace,
        letterSpacing = AppTypography.LabelTracking,
        lineHeight = AppTypography.Mono,
    )
}

@Preview
@Composable
private fun KCSectionLabelPreview() {
    KCPreviewContainer {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            KCSectionLabel(text = "Options")
            KCSectionLabel(text = "Appearance")
            KCSectionLabel(text = "Data")
        }
    }
}
