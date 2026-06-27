package io.github.komposeCharts.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.komposeCharts.sample.design.AppColors
import io.github.komposeCharts.sample.design.AppTheme
import io.github.komposeCharts.sample.screen.BarChartScreen
import io.github.komposeCharts.sample.screen.HomeScreen

@Composable
fun App(statusBarDp: Int = 0) {
    AppTheme {
        var screen by remember { mutableStateOf("home") }

        Box(
            modifier = Modifier.fillMaxSize().background(AppColors.App),
        ) {
            when (screen) {
                "home" -> HomeScreen(
                    statusBarDp = statusBarDp,
                    onChartSelected = { screen = it },
                )

                "bar" -> BarChartScreen(
                    statusBarDp = statusBarDp,
                    onBack = { screen = "home" },
                )
            }
        }
    }
}
