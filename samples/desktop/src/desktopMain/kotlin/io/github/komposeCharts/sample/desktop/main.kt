package io.github.komposeCharts.sample.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.komposeCharts.sample.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "KomposeCharts Sample",
    ) {
        App()
    }
}
