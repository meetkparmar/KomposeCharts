package io.github.komposeCharts.sample.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.komposeCharts.sample.design.AppDimen

// ── Fixed dark palette ────────────────────────────────────────────────────────
private val HomeBg       = Color(0xFF0C0F10)
private val HomeCard     = Color(0xFF000000)
private val HomeCardInner = Color(0xFF101415)
private val HomeAccent   = Color(0xFF22D3EE)
private val HomePrimary  = Color(0xFF8DD0E9)
private val HomeText     = Color(0xFFE1E6E9)
private val HomeTextMuted = Color(0xFFA6ACAE)
private val HomeTagBg    = Color(0xFF045D73)
private val HomeTagText  = Color(0xFFC4EEFF)

private enum class HomeCategory { ALL, ESSENTIAL, ADVANCED }

// ── Root composable ───────────────────────────────────────────────────────────
@Composable
fun HomeScreen(onChartSelected: (ChartType) -> Unit = {}) {
    var selectedCategory by remember { mutableStateOf(HomeCategory.ALL) }

    Box(modifier = Modifier.fillMaxSize().background(HomeBg)) {

        // Scrollable main content — starts below the top bar.
        // windowInsetsPadding(statusBars) dynamically accounts for the extra
        // height the HomeTopBar gains from its own status-bar inset handling.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(top = 56.dp),                // clears fixed top bar content area
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            Spacer(Modifier.height(AppDimen.Spacing_32dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppDimen.Spacing_24dp),
                verticalArrangement = Arrangement.spacedBy(40.dp),
            ) {
                HeroSection()

                CategoryTabs(
                    selected  = selectedCategory,
                    onSelect  = { selectedCategory = it },
                )

                ChartCardsGrid(
                    category        = selectedCategory,
                    onChartSelected = onChartSelected,
                )
            }

            Spacer(Modifier.height(AppDimen.Spacing_128dp))
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
            .background(Color(0xCC020617))
            // Background covers the status bar area; windowInsetsPadding then
            // pushes the icons/text below the status-bar system icons.
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = AppDimen.Spacing_24dp, vertical = AppDimen.Spacing_16dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment    = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppDimen.Spacing_12dp),
            verticalAlignment    = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Default.ShowChart,
                contentDescription = null,
                tint     = HomeAccent,
                modifier = Modifier.size(20.dp),
            )
            Text(
                "KomposeCharts",
                color         = HomeAccent,
                fontSize      = 24.sp,
                fontWeight    = FontWeight.ExtraBold,
                letterSpacing = (-0.6).sp,
            )
        }
        IconButton(onClick = {}, modifier = Modifier.size(AppDimen.Spacing_32dp)) {
            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = HomeText, modifier = Modifier.size(20.dp))
        }
    }
}

// ── Hero section ──────────────────────────────────────────────────────────────
@Composable
private fun HeroSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppDimen.Spacing_32dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF161A1C), Color(0xFF101415)),
                    start  = Offset(0f, 0f),
                    end    = Offset(1000f, 1000f),
                )
            )
            .border(1.dp, Color(0x0A43494B), RoundedCornerShape(AppDimen.Spacing_32dp))
    ) {
        // Radial glow on the right
        Box(
            modifier = Modifier
                .size(220.dp)
                .align(Alignment.CenterEnd)
                .offset(x = 60.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(HomePrimary.copy(alpha = 0.3f), Color.Transparent)
                    ),
                    CircleShape,
                ),
        )

        Column(
            modifier            = Modifier.padding(41.dp),
            verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_16dp),
        ) {
            Text(
                "The\nAnalytical\nGallery",
                color         = HomeText,
                fontSize      = 48.sp,
                fontWeight    = FontWeight.ExtraBold,
                letterSpacing = (-1.2).sp,
                lineHeight    = 48.sp,
            )
            Text(
                "Transforming complex data into precise architectural visualizations. Our gallery houses minimalist, high-performance charting components for the modern web.",
                color      = HomeTextMuted,
                fontSize   = 18.sp,
                lineHeight = (18 * 1.625f).sp,
            )
            Spacer(Modifier.height(AppDimen.Spacing_16dp))
            Surface(
                shape  = RoundedCornerShape(AppDimen.Spacing_48dp),
                color  = HomePrimary,
                shadowElevation = 8.dp,
            ) {
                Text(
                    "Get Started",
                    modifier   = Modifier.padding(horizontal = AppDimen.Spacing_32dp, vertical = AppDimen.Spacing_12dp),
                    color      = Color(0xFF004658),
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
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
        HomeCategory.ALL      to "All",
        HomeCategory.ESSENTIAL to "Essential",
        HomeCategory.ADVANCED  to "Advanced",
    )
    Row(horizontalArrangement = Arrangement.spacedBy(AppDimen.Spacing_12dp)) {
        tabs.forEach { (cat, label) ->
            val isActive = cat == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (isActive) HomePrimary else Color(0xFFC1D8E2))
                    .clickable { onSelect(cat) }
                    .padding(horizontal = AppDimen.Spacing_24dp, vertical = AppDimen.Spacing_8dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    label,
                    color      = if (isActive) Color(0xFF004658) else Color(0xFF495F68),
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

// ── Bento grid of chart cards ─────────────────────────────────────────────────
@Composable
private fun ChartCardsGrid(
    category: HomeCategory,
    onChartSelected: (ChartType) -> Unit,
) {
    val showLine    = category == HomeCategory.ALL || category == HomeCategory.ESSENTIAL
    val showBar     = category == HomeCategory.ALL || category == HomeCategory.ESSENTIAL
    val showPie     = category == HomeCategory.ALL || category == HomeCategory.ESSENTIAL
    val showScatter = category == HomeCategory.ALL || category == HomeCategory.ADVANCED

    Column(verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_24dp)) {
        if (showLine)    LineChartCard(onClick = { onChartSelected(ChartType.LINE) })
        if (showBar)     BarChartCard(onClick = { onChartSelected(ChartType.BAR) })
        if (showPie)     PieChartCard(onClick = { onChartSelected(ChartType.PIE) })
        if (showScatter) ScatterPlotCard()
    }
}

// ── Line Chart Card ───────────────────────────────────────────────────────────
@Composable
private fun LineChartCard(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppDimen.Spacing_32dp))
            .background(HomeCard)
            .clickable(onClick = onClick),
    ) {
        Column {
            // Preview area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(256.dp)
                    .background(HomeCardInner),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.fillMaxSize().padding(AppDimen.Spacing_32dp)) {
                    drawLineChartPreview(this)
                }
                // Gradient fade to black at bottom
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(listOf(Color.Transparent, HomeCard))
                        ),
                )
                // ESSENTIAL badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = AppDimen.Spacing_24dp, y = (-14).dp)
                        .clip(CircleShape)
                        .background(HomeTagBg)
                        .padding(horizontal = AppDimen.Spacing_12dp, vertical = 2.5.dp),
                ) {
                    Text(
                        "ESSENTIAL",
                        color         = HomeTagText,
                        fontSize      = 10.sp,
                        fontWeight    = FontWeight.SemiBold,
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
                verticalAlignment    = Alignment.Top,
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_4dp),
                    modifier            = Modifier.weight(1f),
                ) {
                    Text("Line Chart", color = HomeText, fontSize = 24.sp, fontWeight = FontWeight.Bold, lineHeight = 32.sp)
                    Text(
                        "Track trends and continuous data points with precision paths.",
                        color    = HomeTextMuted,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                    )
                }
                Spacer(Modifier.width(AppDimen.Spacing_8dp))
                Icon(Icons.Default.ShowChart, null, tint = HomeTextMuted, modifier = Modifier.size(24.dp))
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppDimen.Spacing_32dp))
            .background(HomeCard)
            .clickable(onClick = onClick),
    ) {
        Column {
            // Preview area — bars
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(256.dp)
                    .background(HomeCardInner)
                    .padding(AppDimen.Spacing_24dp),
                contentAlignment = Alignment.BottomCenter,
            ) {
                Row(
                    modifier              = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(AppDimen.Spacing_8dp),
                    verticalAlignment    = Alignment.Bottom,
                ) {
                    // 4 bars with increasing opacity/height
                    val bars = listOf(
                        Pair(0.43f, 0.20f),
                        Pair(0.76f, 0.40f),
                        Pair(0.54f, 0.60f),
                        Pair(1.00f, 1.00f),
                    )
                    bars.forEach { (heightFrac, alpha) ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(heightFrac)
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(HomePrimary.copy(alpha = alpha)),
                        )
                    }
                }
            }

            // Info area
            Column(
                modifier            = Modifier.padding(AppDimen.Spacing_24dp),
                verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_8dp),
            ) {
                Text("Bar Chart", color = HomeText, fontSize = 20.sp, fontWeight = FontWeight.Bold, lineHeight = 28.sp)
                Text(
                    "Categorical comparisons with dimensional depth and clarity.",
                    color    = HomeTextMuted,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                )
                Spacer(Modifier.height(AppDimen.Spacing_16dp))
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment    = Alignment.CenterVertically,
                ) {
                    Text(
                        "3D-OPTIMIZED",
                        color         = HomeTextMuted,
                        fontSize      = 10.sp,
                        fontWeight    = FontWeight.SemiBold,
                        letterSpacing = 1.sp,
                    )
                    Icon(Icons.Default.ShowChart, null, tint = HomeTextMuted, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

// ── Pie Chart Card ────────────────────────────────────────────────────────────
@Composable
private fun PieChartCard(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppDimen.Spacing_32dp))
            .background(HomeCard)
            .clickable(onClick = onClick),
    ) {
        Column {
            // Preview area — donut
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(256.dp)
                    .background(HomeCardInner),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.size(128.dp)) {
                    val stroke = 48f
                    val radius = (size.minDimension - stroke) / 2
                    val center = Offset(size.width / 2, size.height / 2)

                    // Background ring
                    drawCircle(
                        color  = Color(0xFF045D73),
                        radius = radius,
                        center = center,
                        style  = Stroke(stroke),
                    )
                    // 64% arc (teal filled portion)
                    drawArc(
                        color      = HomePrimary,
                        startAngle = -90f,
                        sweepAngle = 360f * 0.64f,
                        useCenter  = false,
                        topLeft    = Offset(center.x - radius, center.y - radius),
                        size       = Size(radius * 2, radius * 2),
                        style      = Stroke(stroke, cap = StrokeCap.Butt),
                    )
                }
                Text(
                    "64%",
                    color      = HomeTagText,
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            // Info area
            Column(
                modifier            = Modifier.padding(AppDimen.Spacing_24dp),
                verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_8dp),
            ) {
                Text("Pie Chart", color = HomeText, fontSize = 20.sp, fontWeight = FontWeight.Bold, lineHeight = 28.sp)
                Text(
                    "Elegant donut and radial distributions for proportional insight.",
                    color    = HomeTextMuted,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                )
            }
        }
    }
}

// ── Scatter Plot Card (Advanced / Coming Soon) ────────────────────────────────
@Composable
private fun ScatterPlotCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppDimen.Spacing_32dp))
            .background(HomeCard)
            .border(1.dp, Color(0x0D8DD0E9), RoundedCornerShape(AppDimen.Spacing_32dp)),
    ) {
        Column {
            // Preview area — scatter dots
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(256.dp)
                    .background(Color(0x80101415)),
            ) {
                val dots = listOf(
                    Triple(0.30f, 0.40f, 12.dp),
                    Triple(0.46f, 0.40f, 20.dp),
                    Triple(0.62f, 0.40f,  8.dp),
                    Triple(0.78f, 0.40f, 24.dp),
                    Triple(0.95f, 0.40f, 16.dp),
                    Triple(0.38f, 0.62f, 12.dp),
                    Triple(0.54f, 0.62f, 20.dp),
                )
                val alphas = listOf(1.0f, 0.4f, 0.6f, 0.2f, 1.0f, 0.8f, 1.0f)
                dots.forEachIndexed { i, (xf, yf, size) ->
                    val color = if (i == 6) Color(0xFF045D73) else HomePrimary.copy(alpha = alphas[i])
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset(
                                x = (xf * 300).dp - (size / 2),
                                y = (yf * 220).dp - (size / 2),
                            )
                            .size(size)
                            .background(color, CircleShape),
                    )
                }
            }

            // Info area
            Column(
                modifier = Modifier.padding(AppDimen.Spacing_32dp),
                verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_8dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppDimen.Spacing_8dp),
                    verticalAlignment    = Alignment.CenterVertically,
                ) {
                    Text("Scatter Plot", color = HomeText, fontSize = 24.sp, fontWeight = FontWeight.Bold, lineHeight = 32.sp)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFBBCFFA))
                            .padding(horizontal = AppDimen.Spacing_8dp, vertical = 2.dp),
                    ) {
                        Text(
                            "ADVANCED",
                            color         = Color(0xFF324669),
                            fontSize      = 10.sp,
                            fontWeight    = FontWeight.SemiBold,
                            letterSpacing = (-0.5).sp,
                        )
                    }
                }
                Text(
                    "Identify correlations and distributions within dense multivariate datasets.",
                    color    = HomeTextMuted,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                )
                Spacer(Modifier.height(AppDimen.Spacing_16dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(AppDimen.Spacing_32dp))
                        .background(Color(0xFF1B2022))
                        .border(1.dp, Color(0x33434B4B), RoundedCornerShape(AppDimen.Spacing_32dp))
                        .padding(vertical = 11.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "Coming Soon",
                        color    = HomeTextMuted,
                        fontSize = 16.sp,
                    )
                }
            }
        }
    }
}
