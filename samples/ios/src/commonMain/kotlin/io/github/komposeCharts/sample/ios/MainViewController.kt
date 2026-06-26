package io.github.komposeCharts.sample.ios

import androidx.compose.ui.window.ComposeUIViewController
import io.github.komposeCharts.sample.App

fun MainViewController() = ComposeUIViewController(
    configure = { enforceStrictPlistSanityCheck = false }
) { App() }
