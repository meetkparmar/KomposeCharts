package io.github.komposeCharts.sample.screen

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
import androidx.compose.material.icons.filled.Palette
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
        displayTitle        = "Line Chart",
        toggleOneLabel      = "Point Markers",
        toggleOneSubtitle   = "Highlight data nodes",
        toggleTwoLabel      = "Grid Lines",
        toggleTwoSubtitle   = "Vertical & horizontal axis",
        showThicknessSlider = true,
    ),
    BAR(
        displayTitle        = "Bar Chart",
        toggleOneLabel      = "Value Labels",
        toggleOneSubtitle   = "Show values above bars",
        toggleTwoLabel      = "Grid Lines",
        toggleTwoSubtitle   = "Vertical & horizontal axis",
        showThicknessSlider = false,
    ),
    PIE(
        displayTitle        = "Pie Chart",
        toggleOneLabel      = "Slice Labels",
        toggleOneSubtitle   = "Show percentage labels",
        toggleTwoLabel      = "Show Legend",
        toggleTwoSubtitle   = "Display series legend",
        showThicknessSlider = false,
    ),
}

// ── Fixed dark palette ────────────────────────────────────────────────────────
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
        colors       = listOf(Color(0xFF4FC3DC), Color(0xFF26A69A), Color(0xFF005B71), Color(0xFF00838F), Color(0xFF80CBC4)),
    ),
    PASTEL(
        paletteName  = "Pastel",
        description  = "Soft, airy aesthetics",
        swatchColors = listOf(Color(0xFFFAD0C4), Color(0xFFFFD1FF), Color(0xFFD4FC79), Color(0xFF96E6A1)),
        colors       = listOf(Color(0xFFFAD0C4), Color(0xFFFFD1FF), Color(0xFFD4FC79), Color(0xFF96E6A1), Color(0xFFB2EBF2)),
    ),
    VIBRANT(
        paletteName  = "Vibrant",
        description  = "High-energy gradients",
        swatchColors = listOf(Color(0xFFFF0080), Color(0xFF7928CA), Color(0xFF42275A), Color(0xFF734B6D)),
        colors       = listOf(Color(0xFFFF0080), Color(0xFF7928CA), Color(0xFF42275A), Color(0xFF734B6D), Color(0xFFFF6B6B)),
    ),
}

private val detailMonths     = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun")
private val detailQuarters   = listOf("Q1", "Q2", "Q3", "Q4")
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

    val lineData = remember(chartType) {
        ChartData(series = listOf(
            DataSeries("Revenue", detailMonths.mapIndexed { i, m ->
                DataPoint(i.toFloat(), Random.nextFloat() * 80f + 20f, m) }),
            DataSeries("Expenses", detailMonths.mapIndexed { i, m ->
                DataPoint(i.toFloat(), Random.nextFloat() * 50f + 10f, m) }),
        ))
    }
    val barData = remember(chartType) {
        ChartData(series = listOf(
            DataSeries("2023", detailQuarters.mapIndexed { i, q ->
                DataPoint(i.toFloat(), Random.nextFloat() * 60f + 20f, q) }),
            DataSeries("2024", detailQuarters.mapIndexed { i, q ->
                DataPoint(i.toFloat(), Random.nextFloat() * 60f + 20f, q) }),
        ))
    }
    val pieData = remember(chartType) {
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
            .windowInsetsPadding(WindowInsets.navigationBars),
    ) {
        DetailTopBar(chartType = chartType, onBack = onBack)

        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppDimen.Spacing_24dp),
            verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_24dp),
        ) {
            Spacer(Modifier.height(AppDimen.Spacing_8dp))

            // Chart canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppDimen.Spacing_300dp)
                    .clip(RoundedCornerShape(AppDimen.Spacing_32dp))
                    .background(Color(0xFF101415))
                    .border(AppDimen.Spacing_1dp, DetailDivider, RoundedCornerShape(AppDimen.Spacing_32dp)),
            ) {
                Box(modifier = Modifier.size(AppDimen.Spacing_220dp).align(Alignment.BottomStart).offset(x = AppDimen.Spacing_60dp, y = AppDimen.Spacing_30dp).background(DetailPrimary.copy(alpha = 0.12f), CircleShape))
                Box(modifier = Modifier.size(AppDimen.Spacing_180dp).align(Alignment.TopEnd).offset(x = AppDimen.Spacing_40dp, y = -AppDimen.Spacing_20dp).background(Color(0xFFCEDDFF).copy(alpha = 0.10f), CircleShape))

                when (chartType) {
                    ChartType.LINE -> LineChart(
                        data     = lineData,
                        modifier = Modifier.fillMaxSize().padding(AppDimen.Spacing_8dp),
                        style    = LineChartStyle(
                            lineColors    = selectedPalette.colors,
                            lineThickness = lineThickness.dp,
                            curveType     = CurveType.CATMULL_ROM,
                            showMarkers   = toggleOne,
                            axisStyle     = AxisStyle(showGrid = toggleTwo),
                            tooltipStyle  = TooltipStyle(dismissAfterMs = 3000L),
                        ),
                    )
                    ChartType.BAR -> BarChart(
                        data     = barData,
                        modifier = Modifier.fillMaxSize().padding(AppDimen.Spacing_8dp),
                        style    = BarChartStyle(
                            barColors       = selectedPalette.colors,
                            grouping        = BarGrouping.GROUPED,
                            showValueLabels = toggleOne,
                            axisStyle       = AxisStyle(showGrid = toggleTwo),
                            legendStyle     = LegendStyle(visible = false),
                            tooltipStyle    = TooltipStyle(dismissAfterMs = 3000L),
                        ),
                    )
                    ChartType.PIE -> PieChart(
                        data     = pieData,
                        modifier = Modifier.fillMaxSize().padding(AppDimen.Spacing_8dp),
                        style    = PieChartStyle(
                            sliceColors    = selectedPalette.colors,
                            sliceLabelType = if (toggleOne) SliceLabelType.PERCENT else SliceLabelType.NONE,
                            legendStyle    = LegendStyle(visible = toggleTwo),
                            tooltipStyle   = TooltipStyle(dismissAfterMs = 3000L),
                        ),
                    )
                }
            }

            ColorPaletteCard(
                selected   = selectedPalette,
                onSelected = { selectedPalette = it },
            )

            ChartFeaturesCard(
                chartType         = chartType,
                toggleOne         = toggleOne,
                onToggleOneChange = { toggleOne = it },
                toggleTwo         = toggleTwo,
                onToggleTwoChange = { toggleTwo = it },
                lineThickness     = lineThickness,
                onThicknessChange = { lineThickness = it },
            )

            Spacer(Modifier.height(AppDimen.Spacing_48dp))
        }
    }
}

// ── Top app bar ───────────────────────────────────────────────────────────────
@Composable
private fun DetailTopBar(chartType: ChartType, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color(0xE60F172A), Color(0x000F172A))))
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = AppDimen.Spacing_24dp, vertical = AppDimen.Spacing_16dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppDimen.Spacing_12dp),
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(AppDimen.Spacing_32dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = DetailText, modifier = Modifier.size(AppDimen.Spacing_18dp))
            }
            Text(
                chartType.displayTitle,
                color      = DetailAccent,
                fontSize   = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp,
            )
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
                Icon(Icons.Default.Palette, null, tint = DetailPrimary, modifier = Modifier.size(AppDimen.Spacing_20dp))
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
        border   = BorderStroke(if (selected) AppDimen.Spacing_2dp else AppDimen.Spacing_1dp, if (selected) DetailPrimary else DetailDivider),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(AppDimen.Spacing_18dp), verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_16dp)) {
            Row(modifier = Modifier.fillMaxWidth().height(AppDimen.Spacing_32dp).clip(RoundedCornerShape(AppDimen.Spacing_6dp))) {
                palette.swatchColors.forEach { color ->
                    Box(Modifier.weight(1f).fillMaxHeight().background(color))
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_2dp)) {
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
                Icon(Icons.Default.Tune, null, tint = DetailPrimary, modifier = Modifier.size(AppDimen.Spacing_22dp))
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
                        value         = lineThickness,
                        onValueChange = onThicknessChange,
                        valueRange    = 1f..10f,
                        modifier      = Modifier.fillMaxWidth(),
                        colors        = SliderDefaults.colors(
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
        Column(verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_2dp)) {
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
