package io.github.komposeCharts.sample.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.komposeCharts.sample.design.AppColors
import io.github.komposeCharts.sample.design.AppDimen
import io.github.komposeCharts.sample.design.AppTypography
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun KCCodeBlock(
    code: String,
    modifier: Modifier = Modifier,
    label: String = "Kotlin",
    accent: Color = AppColors.AccentDefault,
) {
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    var copied by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        // Header row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 9.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label.uppercase(),
                color = AppColors.TextFaint,
                fontSize = AppTypography.Mono,
                fontFamily = FontFamily.Monospace,
                letterSpacing = AppTypography.MonoTracking,
            )

            Text(
                text = if (copied) "Copied \u2713" else "Copy",
                modifier = Modifier.clickable {
                    clipboardManager.setText(AnnotatedString(code))
                    copied = true
                    scope.launch {
                        delay(1600)
                        copied = false
                    }
                },
                color = accent,
                fontSize = AppTypography.Mono,
                fontFamily = FontFamily.Monospace,
            )
        }

        // Code block
        Text(
            text = code,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AppDimen.CornerMedium))
                .background(AppColors.CodeBackground)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 15.dp),
            color = AppColors.CodeText,
            fontSize = AppTypography.Code,
            fontFamily = FontFamily.Monospace,
            lineHeight = (AppTypography.Code.value * 1.65f).sp,
        )
    }
}

@Preview
@Composable
private fun KCCodeBlockPreview() {
    KCPreviewContainer {
        KCCodeBlock(
            code = """LineChart(
    data = chartData,
    style = LineChartStyle(
        lineColor = Color(0xFF4F46E5),
        showMarkers = true,
    ),
)""",
            label = "Kotlin",
        )
    }
}
