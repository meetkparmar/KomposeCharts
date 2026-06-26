package io.github.komposeCharts.sample.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.komposeCharts.sample.design.AppColors
import io.github.komposeCharts.sample.design.AppDimen
import io.github.komposeCharts.sample.design.AppTypography
import org.jetbrains.compose.ui.tooling.preview.Preview

enum class KCButtonVariant { PRIMARY, SECONDARY, GHOST }

@Composable
fun KCButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: KCButtonVariant = KCButtonVariant.PRIMARY,
    accent: Color = AppColors.AccentDefault,
    full: Boolean = false,
) {
    val shape = RoundedCornerShape(AppDimen.CornerMedium)

    val bgColor = when (variant) {
        KCButtonVariant.PRIMARY -> accent
        KCButtonVariant.SECONDARY -> AppColors.White
        KCButtonVariant.GHOST -> AppColors.Transparent
    }
    val textColor = when (variant) {
        KCButtonVariant.PRIMARY -> AppColors.White
        KCButtonVariant.SECONDARY -> AppColors.TextPrimary
        KCButtonVariant.GHOST -> accent
    }
    val borderColor = when (variant) {
        KCButtonVariant.PRIMARY -> accent
        KCButtonVariant.SECONDARY -> AppColors.Border
        KCButtonVariant.GHOST -> AppColors.Transparent
    }

    Row(
        modifier = modifier
            .then(if (full) Modifier.fillMaxWidth() else Modifier)
            .clip(shape)
            .border(AppDimen.BorderWidth, borderColor, shape)
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 11.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = AppTypography.Button,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
        )
    }
}

@Preview
@Composable
private fun KCButtonAllVariantsPreview() {
    KCPreviewContainer {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            KCButton(label = "Primary", onClick = {}, variant = KCButtonVariant.PRIMARY)
            KCButton(label = "Secondary", onClick = {}, variant = KCButtonVariant.SECONDARY)
            KCButton(label = "Ghost", onClick = {}, variant = KCButtonVariant.GHOST)
            KCButton(label = "Full width button", onClick = {}, full = true)
        }
    }
}
