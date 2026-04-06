package io.github.komposeCharts.sample.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import io.github.komposeCharts.sample.design.AppDimen

private enum class HomeCategory { ALL, ESSENTIAL }

// ── Root composable ───────────────────────────────────────────────────────────
@Composable
fun HomeScreen(onChartSelected: (ChartType) -> Unit = {}) {
    var selectedCategory by remember { mutableStateOf(HomeCategory.ALL) }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.statusBars)
                .navigationBarsPadding()
                .padding(top = AppDimen.Spacing_56dp),
            verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_0dp),
        ) {
            Spacer(Modifier.height(AppDimen.Spacing_32dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppDimen.Spacing_24dp),
                verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_40dp),
            ) {
                CategoryTabs(
                    selected = selectedCategory,
                    onSelect = { selectedCategory = it },
                )

                ChartCardsGrid(
                    onChartSelected = onChartSelected,
                )
            }

            Spacer(Modifier.height(AppDimen.Spacing_32dp))
        }

        // Fixed top app bar overlay
        HomeTopBar(modifier = Modifier.align(Alignment.TopStart))
    }
}

// ── Top app bar ───────────────────────────────────────────────────────────────
@Composable
private fun HomeTopBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.85f))
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = AppDimen.Spacing_24dp, vertical = AppDimen.Spacing_16dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppDimen.Spacing_12dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Default.ShowChart,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(AppDimen.Spacing_20dp),
            )
            Text(
                "KomposeCharts",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.6).sp,
            )
        }
    }
}

// ── Category tabs ─────────────────────────────────────────────────────────────
@Composable
private fun CategoryTabs(
    selected: HomeCategory,
    onSelect: (HomeCategory) -> Unit,
) {
    val tabs = listOf(
        HomeCategory.ALL to "All",
        HomeCategory.ESSENTIAL to "Essential",
    )
    Row(horizontalArrangement = Arrangement.spacedBy(AppDimen.Spacing_12dp)) {
        tabs.forEach { (cat, label) ->
            val isActive = cat == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(AppDimen.Spacing_32dp))
                    .background(
                        if (isActive) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .clickable { onSelect(cat) }
                    .padding(horizontal = AppDimen.Spacing_24dp, vertical = AppDimen.Spacing_8dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    label,
                    color = if (isActive) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

// ── Bento grid of chart cards ─────────────────────────────────────────────────
@Composable
private fun ChartCardsGrid(
    onChartSelected: (ChartType) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_24dp)) {
        LineChartCard(onClick = { onChartSelected(ChartType.LINE) })
        BarChartCard(onClick = { onChartSelected(ChartType.BAR) })
        PieChartCard(onClick = { onChartSelected(ChartType.PIE) })
    }
}

// ── Line Chart Card ───────────────────────────────────────────────────────────
@Composable
private fun LineChartCard(onClick: () -> Unit) {
    val cardColor = MaterialTheme.colorScheme.surfaceContainerLow
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppDimen.Spacing_32dp))
            .background(cardColor)
            .clickable(onClick = onClick),
    ) {
        Column {
            // Preview area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppDimen.Spacing_256dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.fillMaxSize().padding(AppDimen.Spacing_32dp)) {
                    drawLineChartPreview(this)
                }
                // Gradient fade at bottom
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(AppDimen.Spacing_100dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(listOf(Color.Transparent, cardColor))
                        ),
                )
                // ESSENTIAL badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = AppDimen.Spacing_24dp, y = -AppDimen.Spacing_14dp)
                        .clip(RoundedCornerShape(AppDimen.Spacing_32dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = AppDimen.Spacing_12dp, vertical = AppDimen.Spacing_2_5dp),
                ) {
                    Text(
                        "ESSENTIAL",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp,
                    )
                }
            }

            // Info area
            Spacer(Modifier.height(AppDimen.Spacing_32dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppDimen.Spacing_24dp)
                    .padding(bottom = AppDimen.Spacing_48dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_4dp),
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        "Line Chart",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 32.sp,
                    )
                    Text(
                        "Track trends and continuous data points with precision paths.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                    )
                }
                Spacer(Modifier.width(AppDimen.Spacing_8dp))
                Icon(
                    Icons.Default.ShowChart,
                    null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(AppDimen.Spacing_24dp),
                )
            }
        }
    }
}

// Canvas drawing of a curvy line chart preview
private fun drawLineChartPreview(scope: DrawScope) {
    val w = scope.size.width
    val h = scope.size.height

    // Revenue line (teal)
    val revPoints = listOf(0.65f, 0.50f, 0.70f, 0.35f, 0.55f, 0.25f)
    drawSmoothLine(scope, revPoints, w, h, Color(0xFF8DD0E9), strokeWidth = 3f)

    // Expenses line (muted)
    val expPoints = listOf(0.80f, 0.68f, 0.75f, 0.58f, 0.70f, 0.52f)
    drawSmoothLine(scope, expPoints, w, h, Color(0xFF4FC3DC).copy(alpha = 0.5f), strokeWidth = 2f)
}

private fun drawSmoothLine(
    scope: DrawScope,
    yFractions: List<Float>,
    w: Float,
    h: Float,
    color: Color,
    strokeWidth: Float,
) {
    if (yFractions.size < 2) return
    val step = w / (yFractions.size - 1)
    val path = Path()
    val pts = yFractions.mapIndexed { i, yf -> Offset(i * step, yf * h) }

    path.moveTo(pts[0].x, pts[0].y)
    for (i in 0 until pts.size - 1) {
        val cx = (pts[i].x + pts[i + 1].x) / 2
        path.cubicTo(cx, pts[i].y, cx, pts[i + 1].y, pts[i + 1].x, pts[i + 1].y)
    }
    scope.drawPath(path, color, style = Stroke(strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

// ── Bar Chart Card ────────────────────────────────────────────────────────────
@Composable
private fun BarChartCard(onClick: () -> Unit) {
    val cardColor = MaterialTheme.colorScheme.surfaceContainerLow
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppDimen.Spacing_32dp))
            .background(cardColor)
            .clickable(onClick = onClick),
    ) {
        Column {
            // Preview area — bars
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppDimen.Spacing_256dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.BottomCenter,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(AppDimen.Spacing_24dp),
                    horizontalArrangement = Arrangement.spacedBy(AppDimen.Spacing_8dp),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    val bars = listOf(
                        Pair(0.43f, 0.20f),
                        Pair(0.76f, 0.40f),
                        Pair(0.54f, 0.60f),
                        Pair(1.00f, 1.00f),
                    )
                    val primary = MaterialTheme.colorScheme.primary
                    bars.forEach { (heightFrac, alpha) ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(heightFrac)
                                .clip(RoundedCornerShape(topStart = AppDimen.Spacing_6dp, topEnd = AppDimen.Spacing_6dp))
                                .background(primary.copy(alpha = alpha)),
                        )
                    }
                }
                // Gradient fade at bottom
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(AppDimen.Spacing_80dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(listOf(Color.Transparent, cardColor))
                        ),
                )
            }

            // Info area
            Column(
                modifier = Modifier.padding(AppDimen.Spacing_24dp),
                verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_8dp),
            ) {
                Text(
                    "Bar Chart",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 28.sp,
                )
                Text(
                    "Categorical comparisons with dimensional depth and clarity.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                )
                Spacer(Modifier.height(AppDimen.Spacing_16dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "3D-OPTIMIZED",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp,
                    )
                    Icon(
                        Icons.Default.ShowChart,
                        null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(AppDimen.Spacing_16dp),
                    )
                }
            }
        }
    }
}

// ── Pie Chart Card ────────────────────────────────────────────────────────────
@Composable
private fun PieChartCard(onClick: () -> Unit) {
    val cardColor = MaterialTheme.colorScheme.surfaceContainerLow
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppDimen.Spacing_32dp))
            .background(cardColor)
            .clickable(onClick = onClick),
    ) {
        Column {
            // Preview area — donut
            val primaryColor = MaterialTheme.colorScheme.primary
            val primaryContainerColor = MaterialTheme.colorScheme.primaryContainer
            val onPrimaryContainerColor = MaterialTheme.colorScheme.onPrimaryContainer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppDimen.Spacing_256dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.size(AppDimen.Spacing_128dp)) {
                    val stroke = 48f
                    val radius = (size.minDimension - stroke) / 2
                    val center = Offset(size.width / 2, size.height / 2)

                    // Background ring
                    drawCircle(
                        color = primaryContainerColor,
                        radius = radius,
                        center = center,
                        style = Stroke(stroke),
                    )
                    // 64% arc (primary filled portion)
                    drawArc(
                        color = primaryColor,
                        startAngle = -90f,
                        sweepAngle = 360f * 0.64f,
                        useCenter = false,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(stroke, cap = StrokeCap.Butt),
                    )
                }
                Text(
                    "64%",
                    color = onPrimaryContainerColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                // Gradient fade at bottom
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(AppDimen.Spacing_80dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(listOf(Color.Transparent, cardColor))
                        ),
                )
            }

            // Info area
            Column(
                modifier = Modifier.padding(AppDimen.Spacing_24dp),
                verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_8dp),
            ) {
                Text(
                    "Pie Chart",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 28.sp,
                )
                Text(
                    "Elegant donut and radial distributions for proportional insight.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                )
            }
        }
    }
}
