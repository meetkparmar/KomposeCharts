package io.github.komposeCharts.sample.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import io.github.komposeCharts.charts.BarChart
import io.github.komposeCharts.charts.LineChart
import io.github.komposeCharts.charts.PieChart
import io.github.komposeCharts.core.data.ChartData
import io.github.komposeCharts.core.data.DataPoint
import io.github.komposeCharts.core.data.DataSeries
import io.github.komposeCharts.sample.design.AppDimen
import io.github.komposeCharts.sample.design.AppTypography
import io.github.komposeCharts.style.BarChartStyle
import io.github.komposeCharts.style.BarGrouping
import io.github.komposeCharts.style.CurveType
import io.github.komposeCharts.style.LineChartStyle
import io.github.komposeCharts.style.LegendStyle
import io.github.komposeCharts.style.PieChartStyle
import io.github.komposeCharts.style.SliceLabelType

// ── Syntax highlight colors ────────────────────────────────────────────────────
private data class SyntaxColors(
    val default: Color,
    val keyword: Color,
    val type: Color,
    val string: Color,
)

@Composable
private fun syntaxColors(): SyntaxColors = if (isSystemInDarkTheme()) SyntaxColors(
    default = Color(0xFFE1E6E9),
    keyword = Color(0xFFCEDDFF),
    type    = Color(0xFF7FC2DB),
    string  = Color(0xFF8DD0E9),
) else SyntaxColors(
    default = Color(0xFF1D2B2F),
    keyword = Color(0xFF0055AA),
    type    = Color(0xFF006B8A),
    string  = Color(0xFF005C45),
)

private val CodeBgDark  = Color(0xFF161A1C)
private val CodeBgLight = Color(0xFFF0F4F8)

// ── Static preview data ────────────────────────────────────────────────────────
private val linePreviewData = ChartData(series = listOf(
    DataSeries("Revenue", listOf(
        DataPoint(0f, 42f, "Jan"), DataPoint(1f, 68f, "Feb"),
        DataPoint(2f, 55f, "Mar"), DataPoint(3f, 78f, "Apr"),
        DataPoint(4f, 91f, "May"), DataPoint(5f, 83f, "Jun"),
    )),
    DataSeries("Expenses", listOf(
        DataPoint(0f, 28f, "Jan"), DataPoint(1f, 45f, "Feb"),
        DataPoint(2f, 38f, "Mar"), DataPoint(3f, 52f, "Apr"),
        DataPoint(4f, 61f, "May"), DataPoint(5f, 49f, "Jun"),
    )),
))

private val barPreviewData = ChartData(series = listOf(
    DataSeries("2023", listOf(
        DataPoint(0f, 45f, "Q1"), DataPoint(1f, 62f, "Q2"),
        DataPoint(2f, 58f, "Q3"), DataPoint(3f, 77f, "Q4"),
    )),
    DataSeries("2024", listOf(
        DataPoint(0f, 52f, "Q1"), DataPoint(1f, 70f, "Q2"),
        DataPoint(2f, 65f, "Q3"), DataPoint(3f, 84f, "Q4"),
    )),
))

private val piePreviewData = ChartData(series = listOf(
    DataSeries("Allocation", listOf(
        DataPoint(0f, 40f, "Design"),
        DataPoint(1f, 30f, "Engineering"),
        DataPoint(2f, 20f, "Marketing"),
        DataPoint(3f, 10f, "Other"),
    ))
))

// ── Screen ─────────────────────────────────────────────────────────────────────

@Composable
fun DocScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = AppDimen.Spacing_24dp)
                .windowInsetsPadding(WindowInsets.statusBars)
                .navigationBarsPadding()
                .padding(top = AppDimen.Spacing_48dp, bottom = AppDimen.Spacing_32dp),
            verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_48dp),
        ) {
            HeroSection()
            InstallationSection()
            CoreComponentsSection()
        }
    }
}

// ── Hero ───────────────────────────────────────────────────────────────────────

@Composable
private fun HeroSection() {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_24dp)) {
        // Version badge pill
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            border = BorderStroke(
                AppDimen.Spacing_1dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            ),
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = AppDimen.Spacing_12dp,
                    vertical = AppDimen.Spacing_4dp,
                ),
                horizontalArrangement = Arrangement.spacedBy(AppDimen.Spacing_8dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(AppDimen.Spacing_8dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                )
                Text(
                    text = "V 1.0.0 STABLE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        // Gradient headline
        Text(
            text = "KomposeCharts",
            style = MaterialTheme.typography.displayMedium.copy(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.onBackground,
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                ),
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-2.4).sp,
            ),
        )

        // Subtitle
        Text(
            text = "A Kotlin Multiplatform + Compose Multiplatform chart library — beautiful, animated, and fully customizable charts across Android, iOS, Desktop, and Web.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = AppTypography.subtitleLineHeight,
        )
    }
}

// ── Shared composables ─────────────────────────────────────────────────────────

@Composable
private fun SectionHeading(number: String, title: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(AppDimen.Spacing_12dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(AppDimen.Spacing_32dp)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = number,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun ComponentTitle(name: String, apiHint: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(AppDimen.Spacing_16dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = apiHint,
            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
            color = MaterialTheme.colorScheme.outline,
        )
    }
}

@Composable
private fun CodeBlock(filename: String, code: AnnotatedString) {
    val clipboard = LocalClipboardManager.current
    val codeBg = if (isSystemInDarkTheme()) CodeBgDark else CodeBgLight
    Surface(
        shape = RoundedCornerShape(AppDimen.Spacing_32dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(
            AppDimen.Spacing_1dp,
            MaterialTheme.colorScheme.outlineVariant,
        ),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppDimen.Spacing_16dp, vertical = AppDimen.Spacing_8dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = filename,
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                TextButton(
                    onClick = { clipboard.setText(AnnotatedString(code.text)) },
                    contentPadding = PaddingValues(
                        horizontal = AppDimen.Spacing_8dp,
                        vertical = AppDimen.Spacing_4dp,
                    ),
                ) {
                    Text(
                        text = "Copy",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                thickness = AppDimen.Spacing_1dp,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(codeBg)
                    .padding(AppDimen.Spacing_24dp),
            ) {
                Text(
                    text = code,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 22.sp,
                    ),
                )
            }
        }
    }
}

@Composable
private fun ChartPreviewCard(content: @Composable () -> Unit) {
    Surface(
        shape = RoundedCornerShape(AppDimen.Spacing_32dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(
            AppDimen.Spacing_1dp,
            MaterialTheme.colorScheme.outlineVariant,
        ),
    ) {
        Box(modifier = Modifier.padding(AppDimen.Spacing_16dp)) {
            content()
        }
    }
}

// ── Installation ───────────────────────────────────────────────────────────────

@Composable
private fun InstallationSection() {
    val c = syntaxColors()
    Column(verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_16dp)) {
        SectionHeading(number = "1", title = "Installation")
        Text(
            text = "Add the dependency to your module-level build.gradle.kts file.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        val code = buildAnnotatedString {
            withStyle(SpanStyle(color = c.default)) { append("dependencies {\n    ") }
            withStyle(SpanStyle(color = c.keyword)) { append("implementation") }
            withStyle(SpanStyle(color = c.default)) { append("(\n        ") }
            withStyle(SpanStyle(color = c.string)) {
                append("\"io.github.komposeCharts:charts:1.0.0\"")
            }
            withStyle(SpanStyle(color = c.default)) { append("\n    )\n}") }
        }
        CodeBlock(filename = "build.gradle.kts", code = code)
    }
}

// ── Core Components ────────────────────────────────────────────────────────────

@Composable
private fun CoreComponentsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_16dp)) {
        SectionHeading(number = "2", title = "Core Components")
        Text(
            text = "Each chart component is optimized for performance with a declarative API that matches the Compose mental model.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(AppDimen.Spacing_16dp))
        LineChartSection()

        Spacer(Modifier.height(AppDimen.Spacing_48dp))
        BarChartSection()

        Spacer(Modifier.height(AppDimen.Spacing_48dp))
        PieChartSection()
    }
}

@Composable
private fun LineChartSection() {
    val c = syntaxColors()
    Column(verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_16dp)) {
        ComponentTitle(name = "Line Charts", apiHint = "LineChart()")
        ChartPreviewCard {
            LineChart(
                data = linePreviewData,
                modifier = Modifier.fillMaxWidth().height(AppDimen.Spacing_260dp),
                style = LineChartStyle(
                    curveType = CurveType.CATMULL_ROM,
                    showArea = true,
                    legendStyle = LegendStyle(position = io.github.komposeCharts.style.LegendPosition.BOTTOM),
                ),
            )
        }
        val code = buildAnnotatedString {
            withStyle(SpanStyle(color = c.keyword)) { append("LineChart") }
            withStyle(SpanStyle(color = c.default)) { append("(\n") }
            withStyle(SpanStyle(color = c.default)) { append("    data = chartData,\n") }
            withStyle(SpanStyle(color = c.default)) { append("    style = ") }
            withStyle(SpanStyle(color = c.type))    { append("LineChartStyle") }
            withStyle(SpanStyle(color = c.default)) { append("(\n") }
            withStyle(SpanStyle(color = c.default)) { append("        curveType = ") }
            withStyle(SpanStyle(color = c.type))    { append("CurveType") }
            withStyle(SpanStyle(color = c.default)) { append(".CATMULL_ROM,\n") }
            withStyle(SpanStyle(color = c.default)) { append("        showArea = ") }
            withStyle(SpanStyle(color = c.type))    { append("true") }
            withStyle(SpanStyle(color = c.default)) { append(",\n        tooltipStyle = ") }
            withStyle(SpanStyle(color = c.type))    { append("TooltipStyle") }
            withStyle(SpanStyle(color = c.default)) { append("(),\n    ),\n)") }
        }
        CodeBlock(filename = "LineChart.kt", code = code)
    }
}

@Composable
private fun BarChartSection() {
    val c = syntaxColors()
    Column(verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_16dp)) {
        ComponentTitle(name = "Bar Charts", apiHint = "BarChart()")
        ChartPreviewCard {
            BarChart(
                data = barPreviewData,
                modifier = Modifier.fillMaxWidth().height(AppDimen.Spacing_260dp),
                style = BarChartStyle(
                    grouping = BarGrouping.GROUPED,
                    showValueLabels = true,
                ),
            )
        }
        val code = buildAnnotatedString {
            withStyle(SpanStyle(color = c.keyword)) { append("BarChart") }
            withStyle(SpanStyle(color = c.default)) { append("(\n") }
            withStyle(SpanStyle(color = c.default)) { append("    data = chartData,\n") }
            withStyle(SpanStyle(color = c.default)) { append("    style = ") }
            withStyle(SpanStyle(color = c.type))    { append("BarChartStyle") }
            withStyle(SpanStyle(color = c.default)) { append("(\n") }
            withStyle(SpanStyle(color = c.default)) { append("        grouping = ") }
            withStyle(SpanStyle(color = c.type))    { append("BarGrouping") }
            withStyle(SpanStyle(color = c.default)) { append(".GROUPED,\n") }
            withStyle(SpanStyle(color = c.default)) { append("        showValueLabels = ") }
            withStyle(SpanStyle(color = c.type))    { append("true") }
            withStyle(SpanStyle(color = c.default)) { append(",\n        tooltipStyle = ") }
            withStyle(SpanStyle(color = c.type))    { append("TooltipStyle") }
            withStyle(SpanStyle(color = c.default)) { append("(),\n    ),\n)") }
        }
        CodeBlock(filename = "BarChart.kt", code = code)
    }
}

@Composable
private fun PieChartSection() {
    val c = syntaxColors()
    Column(verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_16dp)) {
        ComponentTitle(name = "Pie & Donut", apiHint = "PieChart()")
        ChartPreviewCard {
            PieChart(
                data = piePreviewData,
                modifier = Modifier.fillMaxWidth().height(AppDimen.Spacing_300dp),
                style = PieChartStyle(
                    innerRadiusFraction = 0.5f,
                    centerLabel = "Allocation",
                    sliceLabelType = SliceLabelType.PERCENT,
                ),
            )
        }
        val code = buildAnnotatedString {
            withStyle(SpanStyle(color = c.keyword)) { append("PieChart") }
            withStyle(SpanStyle(color = c.default)) { append("(\n") }
            withStyle(SpanStyle(color = c.default)) { append("    data = chartData,\n") }
            withStyle(SpanStyle(color = c.default)) { append("    style = ") }
            withStyle(SpanStyle(color = c.type))    { append("PieChartStyle") }
            withStyle(SpanStyle(color = c.default)) { append("(\n") }
            withStyle(SpanStyle(color = c.default)) { append("        innerRadiusFraction = ") }
            withStyle(SpanStyle(color = c.type))    { append("0.5f") }
            withStyle(SpanStyle(color = c.default)) { append(",\n") }
            withStyle(SpanStyle(color = c.default)) { append("        centerLabel = ") }
            withStyle(SpanStyle(color = c.string))  { append("\"Total\"") }
            withStyle(SpanStyle(color = c.default)) { append(",\n") }
            withStyle(SpanStyle(color = c.default)) { append("        sliceLabelType = ") }
            withStyle(SpanStyle(color = c.type))    { append("SliceLabelType") }
            withStyle(SpanStyle(color = c.default)) { append(".PERCENT,\n    ),\n)") }
        }
        CodeBlock(filename = "PieChart.kt", code = code)
    }
}
