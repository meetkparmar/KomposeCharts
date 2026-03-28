package io.github.komposeCharts.sample

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.komposeCharts.sample.screen.BarChartScreen
import io.github.komposeCharts.sample.screen.LineChartScreen
import io.github.komposeCharts.sample.screen.PieChartScreen
import io.github.komposeCharts.theme.ChartDefaults
import io.github.komposeCharts.theme.ChartTheme
import io.github.komposeCharts.theme.chartThemeFromMaterial3

private enum class Screen { LINE, BAR, PIE }

@Composable
fun App() {
    MaterialTheme {
        var currentScreen by remember { mutableStateOf(Screen.LINE) }
        var useMaterial3Theme by remember { mutableStateOf(false) }

        val chartTheme = if (useMaterial3Theme) chartThemeFromMaterial3() else ChartDefaults.theme()

        ChartTheme(theme = chartTheme) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(
                            selected = currentScreen == Screen.LINE,
                            onClick = { currentScreen = Screen.LINE },
                            icon = { Icon(Icons.Default.ShowChart, contentDescription = "Line") },
                            label = { Text("Line") },
                        )
                        NavigationBarItem(
                            selected = currentScreen == Screen.BAR,
                            onClick = { currentScreen = Screen.BAR },
                            icon = { Icon(Icons.Default.BarChart, contentDescription = "Bar") },
                            label = { Text("Bar") },
                        )
                        NavigationBarItem(
                            selected = currentScreen == Screen.PIE,
                            onClick = { currentScreen = Screen.PIE },
                            icon = { Icon(Icons.Default.PieChart, contentDescription = "Pie") },
                            label = { Text("Pie") },
                        )
                    }
                }
            ) { _ ->
                when (currentScreen) {
                    Screen.LINE -> LineChartScreen()
                    Screen.BAR -> BarChartScreen()
                    Screen.PIE -> PieChartScreen()
                }
            }
        }
    }
}
