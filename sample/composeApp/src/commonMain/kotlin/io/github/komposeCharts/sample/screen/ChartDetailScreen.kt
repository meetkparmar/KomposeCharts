package io.github.komposeCharts.sample.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.komposeCharts.charts.BarChart
import io.github.komposeCharts.charts.LineChart
import io.github.komposeCharts.charts.PieChart
import io.github.komposeCharts.core.data.ChartData
import io.github.komposeCharts.core.data.DataPoint
import io.github.komposeCharts.core.data.DataSeries
import io.github.komposeCharts.sample.design.AppDimen
import io.github.komposeCharts.style.AxisStyle
import io.github.komposeCharts.style.BarChartStyle
import io.github.komposeCharts.style.BarGrouping
import io.github.komposeCharts.style.CurveType
import io.github.komposeCharts.style.LegendStyle
import io.github.komposeCharts.style.LineChartStyle
import io.github.komposeCharts.style.PieChartStyle
import io.github.komposeCharts.style.SliceLabelType
import io.github.komposeCharts.style.TooltipStyle
import kotlin.math.round
import kotlin.random.Random

// ── Chart type navigation key ─────────────────────────────────────────────────
enum class ChartType(
    val displayTitle: String,
    val toggleOneLabel: String,
    val toggleOneSubtitle: String,
    val toggleTwoLabel: String,
    val toggleTwoSubtitle: String,
    val showThicknessSlider: Boolean,
) {
    LINE(
        displayTitle        = "Trend Analytics Line",
        toggleOneLabel      = "Point Markers",
        toggleOneSubtitle   = "Highlight data nodes",
        toggleTwoLabel      = "Grid Lines",
        toggleTwoSubtitle   = "Vertical & horizontal axis",
        showThicknessSlider = true,
    ),
    BAR(
        displayTitle        = "Bar Analytics",
        toggleOneLabel      = "Value Labels",
        toggleOneSubtitle   = "Show values above bars",
        toggleTwoLabel      = "Grid Lines",
        toggleTwoSubtitle   = "Vertical & horizontal axis",
        showThicknessSlider = false,
    ),
    PIE(
        displayTitle        = "Pie Distribution",
        toggleOneLabel      = "Slice Labels",
        toggleOneSubtitle   = "Show percentage labels",
        toggleTwoLabel      = "Show Legend",
        toggleTwoSubtitle   = "Display series legend",
        showThicknessSlider = false,
    ),
}

// ── Fixed dark palette matching Figma design ─────────────────────────────────
private val DetailBg        = Color(0xFF0C0F10)
private val CardBg          = Color(0xFF161A1C)
private val CardBgAlt       = Color(0xFF1B2022)
private val DetailPrimary   = Color(0xFF8DD0E9)
private val DetailOnPrimary = Color(0xFF004658)
private val DetailText      = Color(0xFFE1E6E9)
private val DetailTextMuted = Color(0xFFA6ACAE)
private val DetailAccent    = Color(0xFF06B6D4)
private val DetailDivider   = Color(0x1A43494B)

// ── Palette options ───────────────────────────────────────────────────────────
private enum class ChartPalette(
    val paletteName: String,
    val description: String,
    val swatchColors: List<Color>,
    val colors: List<Color>,
) {
    MATERIAL(
        paletteName  = "Material",
        description  = "Bold, structured shadows",
        swatchColors = listOf(Color(0xFF005B71), Color(0xFF00838F), Color(0xFF26A69A), Color(0xFF80CBC4)),
        colors       = listOf(Color(0xFF4FC3DC), Color(0xFF26A69A)),
    ),
    PASTEL(
        paletteName  = "Pastel",
        description  = "Soft, airy aesthetics",
        swatchColors = listOf(Color(0xFFFAD0C4), Color(0xFFFFD1FF), Color(0xFFD4FC79), Color(0xFF96E6A1)),
        colors       = listOf(Color(0xFFFAD0C4), Color(0xFFD4FC79)),
    ),
    VIBRANT(
        paletteName  = "Vibrant",
        description  = "High-energy gradients",
        swatchColors = listOf(Color(0xFFFF0080), Color(0xFF7928CA), Color(0xFF42275A), Color(0xFF734B6D)),
        colors       = listOf(Color(0xFFFF0080), Color(0xFF7928CA)),
    ),
}

private val detailMonths    = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun")
private val detailQuarters  = listOf("Q1", "Q2", "Q3", "Q4")
private val detailCategories = listOf("Design", "Eng", "Mktg", "Sales", "Support")

// ── Root composable ───────────────────────────────────────────────────────────
@Composable
fun ChartDetailScreen(
    chartType: ChartType = ChartType.LINE,
    onBack: () -> Unit   = {},
) {
    var selectedPalette by remember(chartType) { mutableStateOf(ChartPalette.MATERIAL) }
    var toggleOne       by remember(chartType) { mutableStateOf(true) }
    var toggleTwo       by remember(chartType) { mutableStateOf(false) }
    var lineThickness   by remember(chartType) { mutableStateOf(4f) }
    var dataRevision    by remember { mutableStateOf(0) }
    var lastTapped      by remember(chartType) { mutableStateOf<String?>(null) }

    // Per-type data
    val lineData = remember(dataRevision, chartType) {
        ChartData(series = listOf(
            DataSeries("Revenue", detailMonths.mapIndexed { i, m ->
                DataPoint(i.toFloat(), Random.nextFloat() * 80f + 20f, m) }),
            DataSeries("Expenses", detailMonths.mapIndexed { i, m ->
                DataPoint(i.toFloat(), Random.nextFloat() * 50f + 10f, m) }),
        ))
    }
    val barData = remember(dataRevision, chartType) {
        ChartData(series = listOf(
            DataSeries("2023", detailQuarters.mapIndexed { i, q ->
                DataPoint(i.toFloat(), Random.nextFloat() * 60f + 20f, q) }),
            DataSeries("2024", detailQuarters.mapIndexed { i, q ->
                DataPoint(i.toFloat(), Random.nextFloat() * 60f + 20f, q) }),
        ))
    }
    val pieData = remember(dataRevision, chartType) {
        ChartData(series = listOf(
            DataSeries("Budget", detailCategories.mapIndexed { i, cat ->
                DataPoint(i.toFloat(), Random.nextFloat() * 40f + 10f, cat) }),
        ))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DetailBg)
            .verticalScroll(rememberScrollState())
            // Ensures scrollable content never hides behind the gesture/button nav bar.
            // This screen is an overlay outside the Scaffold, so it must manage its
            // own bottom inset (the Scaffold's paddingValues do not apply here).
            .windowInsetsPadding(WindowInsets.navigationBars),
    ) {
        DetailTopBar(onBack = onBack)

        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppDimen.Spacing_24dp),
            verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_24dp),
        ) {
            Spacer(Modifier.height(AppDimen.Spacing_8dp))

            PreviewCanvasSection(
                chartType     = chartType,
                lineData      = lineData,
                barData       = barData,
                pieData       = pieData,
                palette       = selectedPalette,
                toggleOne     = toggleOne,
                toggleTwo     = toggleTwo,
                lineThickness = lineThickness,
                lastTapped    = lastTapped,
                onTapped      = { lastTapped = it },
            )

            ColorPaletteCard(
                selected   = selectedPalette,
                onSelected = { selectedPalette = it },
            )

            ChartFeaturesCard(
                chartType           = chartType,
                toggleOne           = toggleOne,
                onToggleOneChange   = { toggleOne = it },
                toggleTwo           = toggleTwo,
                onToggleTwoChange   = { toggleTwo = it },
                lineThickness       = lineThickness,
                onThicknessChange   = { lineThickness = it },
            )

            ProductionCtaCard()

            Spacer(Modifier.height(AppDimen.Spacing_48dp))
        }
    }
}

// ── Top app bar ───────────────────────────────────────────────────────────────
@Composable
private fun DetailTopBar(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color(0xE60F172A), Color(0x000F172A))))
            // Background fills into the status bar area; windowInsetsPadding then
            // nudges the icon/text row below the system status-bar icons.
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = AppDimen.Spacing_24dp, vertical = AppDimen.Spacing_16dp),
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment    = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppDimen.Spacing_12dp),
                verticalAlignment    = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(AppDimen.Spacing_32dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = DetailText, modifier = Modifier.size(18.dp))
                }
                Text("KomposeCharts", color = DetailAccent, fontSize = 20.sp, fontWeight = FontWeight.Bold, letterSpacing = (-1).sp)
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppDimen.Spacing_4dp),
                verticalAlignment    = Alignment.CenterVertically,
            ) {
                IconButton(onClick = {}, modifier = Modifier.size(AppDimen.Spacing_32dp)) {
                    Icon(Icons.Default.Search, "Search", tint = DetailText, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = {}, modifier = Modifier.size(AppDimen.Spacing_32dp)) {
                    Icon(Icons.Default.Settings, "Settings", tint = DetailText, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

// ── Preview canvas section ────────────────────────────────────────────────────
@Composable
private fun PreviewCanvasSection(
    chartType: ChartType,
    lineData: ChartData,
    barData: ChartData,
    pieData: ChartData,
    palette: ChartPalette,
    toggleOne: Boolean,
    toggleTwo: Boolean,
    lineThickness: Float,
    lastTapped: String?,
    onTapped: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_16dp)) {
        // Title block
        Column(verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_8dp)) {
            Text("INTERACTIVE PREVIEW", color = DetailPrimary, fontSize = 12.sp, letterSpacing = 2.4.sp)
            Text(chartType.displayTitle, color = DetailText, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.9).sp, lineHeight = 40.sp)
        }

        // Action buttons
        Row(horizontalArrangement = Arrangement.spacedBy(AppDimen.Spacing_12dp)) {
            Surface(shape = CircleShape, color = CardBgAlt) {
                Row(
                    modifier              = Modifier.padding(horizontal = AppDimen.Spacing_16dp, vertical = AppDimen.Spacing_8dp),
                    horizontalArrangement = Arrangement.spacedBy(AppDimen.Spacing_8dp),
                    verticalAlignment    = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Default.Download, null, tint = DetailText, modifier = Modifier.size(14.dp))
                    Text("Export JSON", color = DetailText, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }
            Surface(shape = CircleShape, color = DetailPrimary, shadowElevation = 6.dp) {
                Row(
                    modifier              = Modifier.padding(horizontal = AppDimen.Spacing_24dp, vertical = AppDimen.Spacing_8dp),
                    horizontalArrangement = Arrangement.spacedBy(AppDimen.Spacing_8dp),
                    verticalAlignment    = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Default.Bookmark, null, tint = DetailOnPrimary, modifier = Modifier.size(14.dp))
                    Text("Save Template", color = DetailOnPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Glassmorphism chart container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(AppDimen.Spacing_300dp)
                .clip(RoundedCornerShape(AppDimen.Spacing_32dp))
                .background(Color(0xFF101415))
                .border(1.dp, DetailDivider, RoundedCornerShape(AppDimen.Spacing_32dp)),
        ) {
            // Ambient glow blobs
            Box(modifier = Modifier.size(220.dp).align(Alignment.BottomStart).offset(x = 60.dp, y = 30.dp).background(DetailPrimary.copy(alpha = 0.12f), CircleShape))
            Box(modifier = Modifier.size(180.dp).align(Alignment.TopEnd).offset(x = 40.dp, y = (-20).dp).background(Color(0xFFCEDDFF).copy(alpha = 0.10f), CircleShape))

            when (chartType) {
                ChartType.LINE -> LineChart(
                    data     = lineData,
                    modifier = Modifier.fillMaxSize().padding(AppDimen.Spacing_8dp),
                    style    = LineChartStyle(
                        lineColors    = palette.colors,
                        lineThickness = lineThickness.dp,
                        curveType     = CurveType.CATMULL_ROM,
                        showMarkers   = toggleOne,
                        axisStyle     = AxisStyle(showGrid = toggleTwo),
                        tooltipStyle  = TooltipStyle(dismissAfterMs = 3000L),
                    ),
                    onDataPointClick = { sIdx, point ->
                        onTapped("${lineData.series[sIdx].label}: ${point.label} = ${round(point.y * 10.0) / 10.0}")
                    },
                )
                ChartType.BAR -> BarChart(
                    data     = barData,
                    modifier = Modifier.fillMaxSize().padding(AppDimen.Spacing_8dp),
                    style    = BarChartStyle(
                        barColors        = palette.colors,
                        grouping         = BarGrouping.GROUPED,
                        showValueLabels  = toggleOne,
                        axisStyle        = AxisStyle(showGrid = toggleTwo),
                        legendStyle      = LegendStyle(visible = false),
                        tooltipStyle     = TooltipStyle(dismissAfterMs = 3000L),
                    ),
                    onBarClick = { sIdx, point ->
                        onTapped("${barData.series[sIdx].label}: ${point.label} = ${point.y.toInt()}")
                    },
                )
                ChartType.PIE -> PieChart(
                    data     = pieData,
                    modifier = Modifier.fillMaxSize().padding(AppDimen.Spacing_8dp),
                    style    = PieChartStyle(
                        sliceColors      = palette.colors,
                        sliceLabelType   = if (toggleOne) SliceLabelType.PERCENT else SliceLabelType.NONE,
                        legendStyle      = LegendStyle(visible = toggleTwo),
                        tooltipStyle     = TooltipStyle(dismissAfterMs = 3000L),
                    ),
                    onSliceClick = { _, point ->
                        onTapped("${point.label}: ${round(point.y * 10.0) / 10.0}")
                    },
                )
            }
        }

        AnimatedVisibility(visible = lastTapped != null, enter = fadeIn(), exit = fadeOut()) {
            Text(text = lastTapped ?: "", color = DetailTextMuted, fontSize = 12.sp)
        }
    }
}

// ── Color palette card ────────────────────────────────────────────────────────
@Composable
private fun ColorPaletteCard(
    selected: ChartPalette,
    onSelected: (ChartPalette) -> Unit,
) {
    Surface(shape = RoundedCornerShape(AppDimen.Spacing_32dp), color = CardBg, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(AppDimen.Spacing_24dp), verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_24dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(AppDimen.Spacing_12dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Palette, null, tint = DetailPrimary, modifier = Modifier.size(20.dp))
                Text("Color Palette", color = DetailText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Column(verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_16dp)) {
                ChartPalette.entries.forEach { palette ->
                    PaletteOptionCard(palette = palette, selected = selected == palette, onClick = { onSelected(palette) })
                }
            }
        }
    }
}

@Composable
private fun PaletteOptionCard(
    palette: ChartPalette,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        onClick  = onClick,
        shape    = RoundedCornerShape(AppDimen.Spacing_24dp),
        color    = CardBgAlt,
        border   = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) DetailPrimary else DetailDivider),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_16dp)) {
            Row(modifier = Modifier.fillMaxWidth().height(32.dp).clip(RoundedCornerShape(6.dp))) {
                palette.swatchColors.forEach { color ->
                    Box(Modifier.weight(1f).fillMaxHeight().background(color))
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(palette.paletteName, color = DetailText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(palette.description, color = DetailTextMuted, fontSize = 12.sp)
            }
        }
    }
}

// ── Chart features card ───────────────────────────────────────────────────────
@Composable
private fun ChartFeaturesCard(
    chartType: ChartType,
    toggleOne: Boolean,
    onToggleOneChange: (Boolean) -> Unit,
    toggleTwo: Boolean,
    onToggleTwoChange: (Boolean) -> Unit,
    lineThickness: Float,
    onThicknessChange: (Float) -> Unit,
) {
    Surface(shape = RoundedCornerShape(AppDimen.Spacing_32dp), color = CardBg, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(AppDimen.Spacing_24dp), verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_24dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(AppDimen.Spacing_12dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Tune, null, tint = DetailPrimary, modifier = Modifier.size(22.dp))
                Text("Chart Features", color = DetailText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Column(verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_24dp)) {
                FeatureToggleRow(
                    title           = chartType.toggleOneLabel,
                    subtitle        = chartType.toggleOneSubtitle,
                    checked         = toggleOne,
                    onCheckedChange = onToggleOneChange,
                )
                FeatureToggleRow(
                    title           = chartType.toggleTwoLabel,
                    subtitle        = chartType.toggleTwoSubtitle,
                    checked         = toggleTwo,
                    onCheckedChange = onToggleTwoChange,
                )
            }
            if (chartType.showThicknessSlider) {
                Column(verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_4dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Line Thickness", color = DetailText, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Text("${lineThickness.toInt()}px", color = DetailPrimary, fontSize = 14.sp)
                    }
                    Slider(
                        value          = lineThickness,
                        onValueChange  = onThicknessChange,
                        valueRange     = 1f..10f,
                        modifier       = Modifier.fillMaxWidth(),
                        colors         = SliderDefaults.colors(
                            thumbColor         = DetailPrimary,
                            activeTrackColor   = DetailPrimary,
                            inactiveTrackColor = Color(0xFF212729),
                        ),
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("1px", color = DetailTextMuted, fontSize = 10.sp)
                        Text("10px", color = DetailTextMuted, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun FeatureToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, color = DetailText, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, color = DetailTextMuted, fontSize = 12.sp)
        }
        Switch(
            checked         = checked,
            onCheckedChange = onCheckedChange,
            colors          = SwitchDefaults.colors(
                checkedThumbColor    = DetailOnPrimary,
                checkedTrackColor    = DetailPrimary,
                uncheckedThumbColor  = Color(0xFF707679),
                uncheckedTrackColor  = Color(0xFF212729),
                uncheckedBorderColor = Color.Transparent,
            ),
        )
    }
}

// ── Production CTA ────────────────────────────────────────────────────────────
@Composable
private fun ProductionCtaCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppDimen.Spacing_48dp))
            .background(Color(0x33045D73))
            .border(1.dp, Color(0x1A8DD0E9), RoundedCornerShape(AppDimen.Spacing_48dp))
            .padding(horizontal = 33.dp, vertical = 41.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_24dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_8dp)) {
                Text("Ready for Production?", color = Color(0xFFA1E4FE), fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text(
                    "The current configuration is optimized for performance and accessibility. Sync directly with your codebase.",
                    color = DetailTextMuted, fontSize = 16.sp, textAlign = TextAlign.Center, lineHeight = 24.sp,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(AppDimen.Spacing_16dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = DetailPrimary) {
                    Text("Copy Config", modifier = Modifier.padding(horizontal = AppDimen.Spacing_32dp, vertical = AppDimen.Spacing_12dp), color = DetailOnPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Surface(shape = CircleShape, color = CardBgAlt) {
                    Icon(Icons.Default.Info, "Help", tint = DetailText, modifier = Modifier.padding(AppDimen.Spacing_12dp).size(20.dp))
                }
            }
        }
    }
}
