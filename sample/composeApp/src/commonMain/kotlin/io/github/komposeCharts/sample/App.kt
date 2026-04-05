package io.github.komposeCharts.sample

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
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
import io.github.komposeCharts.sample.design.AppTheme
import io.github.komposeCharts.sample.screen.ChartDetailScreen
import io.github.komposeCharts.sample.screen.ChartType
import io.github.komposeCharts.sample.screen.DocScreen
import io.github.komposeCharts.sample.screen.HomeScreen
import io.github.komposeCharts.sample.screen.SettingsScreen

private enum class Screen { HOME, DOCS, SETTINGS }

@Composable
fun App() {
    var darkTheme       by remember { mutableStateOf(true) }
    var showChartDetail by remember { mutableStateOf(false) }
    var detailChartType by remember { mutableStateOf(ChartType.LINE) }

    AppTheme(darkTheme = darkTheme) {
        var currentScreen by remember { mutableStateOf(Screen.HOME) }

        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                modifier              = Modifier.fillMaxSize(),
                // Disable Scaffold's automatic inset consumption so each screen
                // handles its own status-bar padding. The NavigationBar already
                // applies windowInsetsPadding(WindowInsets.navigationBars) internally,
                // so paddingValues.bottom still includes the gesture/button bar height.
                contentWindowInsets   = WindowInsets(0, 0, 0, 0),
                bottomBar             = {
                    NavigationBar {
                        NavigationBarItem(
                            selected = currentScreen == Screen.HOME,
                            onClick  = { currentScreen = Screen.HOME },
                            icon     = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label    = { Text("Home") },
                        )
                        NavigationBarItem(
                            selected = currentScreen == Screen.DOCS,
                            onClick  = { currentScreen = Screen.DOCS },
                            icon     = { Icon(Icons.Default.MenuBook, contentDescription = "Docs") },
                            label    = { Text("Docs") },
                        )
                        NavigationBarItem(
                            selected = currentScreen == Screen.SETTINGS,
                            onClick  = { currentScreen = Screen.SETTINGS },
                            icon     = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                            label    = { Text("Settings") },
                        )
                    }
                }
            ) { paddingValues ->
                // Apply only bottom padding — keeps content above the NavigationBar
                // (which already accounts for the gesture/button bar via its own insets).
                // Status-bar handling is left to each screen individually.
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                when (currentScreen) {
                    Screen.HOME     -> HomeScreen(
                        onChartSelected = { type ->
                            detailChartType = type
                            showChartDetail = true
                        }
                    )
                    Screen.DOCS     -> DocScreen()
                    Screen.SETTINGS -> SettingsScreen(
                        darkTheme         = darkTheme,
                        onDarkThemeChange = { darkTheme = it },
                    )
                }
                } // end inner Box
            }

            // Chart detail screen slides up over the scaffold
            AnimatedVisibility(
                visible = showChartDetail,
                enter   = slideInVertically(initialOffsetY = { it }),
                exit    = slideOutVertically(targetOffsetY = { it }),
            ) {
                ChartDetailScreen(
                    chartType = detailChartType,
                    onBack    = { showChartDetail = false },
                )
            }
        }
    }
}
