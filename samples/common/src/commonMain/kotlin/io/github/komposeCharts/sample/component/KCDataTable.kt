package io.github.komposeCharts.sample.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import io.github.komposeCharts.sample.design.AppColors
import io.github.komposeCharts.sample.design.AppDimen
import io.github.komposeCharts.sample.design.AppTypography
import org.jetbrains.compose.ui.tooling.preview.Preview

data class KCTableRow(val key: String, val value: String)

@Composable
fun KCDataTable(
    rows: List<KCTableRow>,
    modifier: Modifier = Modifier,
    head0: String = "Month",
    head1: String = "Revenue",
) {
    val shape = RoundedCornerShape(AppDimen.CornerMedium)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .border(AppDimen.BorderWidth, AppColors.BorderSubtle, shape)
            .background(AppColors.Surface),
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppColors.SurfaceMuted)
                .drawBehind {
                    drawLine(
                        color = AppColors.BorderMuted,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 1.dp.toPx(),
                    )
                }
                .padding(horizontal = 14.dp, vertical = 9.dp),
        ) {
            Text(
                text = head0.uppercase(),
                modifier = Modifier.weight(1f),
                color = AppColors.TextFaint,
                fontSize = AppTypography.TableHeader,
                fontFamily = FontFamily.Monospace,
                letterSpacing = AppTypography.HeaderTracking,
            )
            Text(
                text = head1.uppercase(),
                color = AppColors.TextFaint,
                fontSize = AppTypography.TableHeader,
                fontFamily = FontFamily.Monospace,
                letterSpacing = AppTypography.HeaderTracking,
            )
        }

        // Data rows
        rows.forEachIndexed { index, row ->
            val borderColor = AppColors.BorderFaint
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (index > 0) Modifier.drawBehind {
                            drawLine(
                                color = borderColor,
                                start = Offset(0f, 0f),
                                end = Offset(size.width, 0f),
                                strokeWidth = 1.dp.toPx(),
                            )
                        } else Modifier
                    )
                    .padding(horizontal = 14.dp, vertical = 11.dp),
            ) {
                Text(
                    text = row.key,
                    modifier = Modifier.weight(1f),
                    color = AppColors.TextPrimary,
                    fontSize = AppTypography.TableKey,
                )
                Text(
                    text = row.value,
                    color = AppColors.TextPrimary,
                    fontSize = AppTypography.TableValue,
                    fontFamily = FontFamily.Monospace,
                )
            }
        }
    }
}

@Preview
@Composable
private fun KCDataTablePreview() {
    KCPreviewContainer {
        KCDataTable(
            head0 = "Month",
            head1 = "Revenue",
            rows = listOf(
                KCTableRow("January", "\$42,000"),
                KCTableRow("February", "\$58,000"),
                KCTableRow("March", "\$35,000"),
                KCTableRow("April", "\$64,000"),
            ),
        )
    }
}
