package io.github.komposeCharts.sample.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import io.github.komposeCharts.sample.component.KCListRow
import io.github.komposeCharts.sample.component.KCSectionLabel
import io.github.komposeCharts.sample.design.AppColors
import io.github.komposeCharts.sample.design.AppDimen
import io.github.komposeCharts.sample.design.AppTypography

@Composable
fun HomeScreen(
    statusBarDp: Int = 0,
    onChartSelected: (String) -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        // ── Top bar ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppColors.Surface)
                .drawBehind {
                    drawLine(
                        color = AppColors.BorderSubtle,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 1f,
                    )
                },
        ) {
            if (statusBarDp > 0) {
                Spacer(Modifier.height(statusBarDp.dp))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppDimen.Spacing_18dp, vertical = AppDimen.Spacing_14dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppDimen.Spacing_12dp),
            ) {
                // Logo icon — mini bar chart
                Box(
                    modifier = Modifier
                        .size(AppDimen.Spacing_32dp)
                        .clip(RoundedCornerShape(AppDimen.CornerSmallMedium))
                        .background(AppColors.AccentDefault)
                        .drawBehind {
                            val barW = 3f
                            val gap = 5f
                            val totalW = barW * 3 + gap * 2
                            val startX = (size.width - totalW) / 2f
                            val baseY = size.height - 8f
                            val heights = listOf(8f, 14f, 5f)
                            heights.forEachIndexed { i, h ->
                                drawRoundRect(
                                    color = Color.White,
                                    topLeft = Offset(startX + i * (barW + gap), baseY - h),
                                    size = Size(barW, h),
                                    cornerRadius = CornerRadius(1f),
                                )
                            }
                        },
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "KomposeCharts",
                        color = AppColors.TextPrimary,
                        fontSize = AppTypography.ListTitle,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = AppTypography.TitleTracking,
                    )
                    Text(
                        text = "sample \u00B7 v0.4.0",
                        color = AppColors.TextFaint,
                        fontSize = AppTypography.TableHeader,
                        fontFamily = FontFamily.Monospace,
                    )
                }
            }
        }

        // ── Content ──
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(AppDimen.Spacing_18dp),
            verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_12dp),
        ) {
            KCSectionLabel("Chart Catalog")
            Text(
                text = "Tap a chart to preview it live, toggle its options, and copy the Compose code.",
                color = AppColors.TextSecondary,
                fontSize = AppTypography.Description,
                lineHeight = AppTypography.descriptionLineHeight,
            )

            KCListRow(
                title = "Bar Chart",
                subtitle = "Compare values across categories.",
                tag = "BarChart",
                onClick = { onChartSelected("bar") },
            )

            KCListRow(
                title = "Line Chart",
                subtitle = "Track trends over a continuous axis.",
                tag = "LineChart",
                onClick = { onChartSelected("line") },
            )

            KCListRow(
                title = "Pie Chart",
                subtitle = "Show parts of a whole.",
                tag = "PieChart",
                onClick = { onChartSelected("pie") },
            )

            // ── Footer ──
            Text(
                text = "Kotlin Multiplatform \u00B7 Compose UI",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AppDimen.Spacing_18dp),
                color = AppColors.Disabled,
                fontSize = AppTypography.TableHeader,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
            )
        }
    }
}

private val Int.dp get() = androidx.compose.ui.unit.Dp(this.toFloat())
