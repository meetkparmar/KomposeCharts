package io.github.komposeCharts.sample.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.komposeCharts.sample.design.AppColors
import io.github.komposeCharts.sample.design.AppTypography
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun KCTopBar(
    title: String,
    modifier: Modifier = Modifier,
    trailing: String = "",
    showBack: Boolean = true,
    onBack: () -> Unit = {},
) {
    val borderColor = AppColors.BorderSubtle

    Row(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color = borderColor,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx(),
                )
            }
            .padding(horizontal = 8.dp, vertical = 9.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showBack) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "\u2190", // ←
                    fontSize = 23.sp,
                    color = AppColors.TextPrimary,
                )
            }
        }

        Text(
            text = title,
            modifier = Modifier.weight(1f),
            color = AppColors.TextPrimary,
            fontSize = AppTypography.Title,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
        )

        if (trailing.isNotEmpty()) {
            Text(
                text = trailing,
                modifier = Modifier.padding(end = 12.dp),
                color = AppColors.TextFaint,
                fontSize = AppTypography.Mono,
                fontFamily = FontFamily.Monospace,
            )
        }
    }
}

@Preview
@Composable
private fun KCTopBarWithBackPreview() {
    KCPreviewContainer {
        KCTopBar(
            title = "Bar Chart",
            trailing = "BarChart()",
            showBack = true,
        )
    }
}

@Preview
@Composable
private fun KCTopBarWithoutBackPreview() {
    KCPreviewContainer {
        KCTopBar(
            title = "Settings",
            showBack = false,
        )
    }
}
