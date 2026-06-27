package io.github.komposeCharts.sample.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import io.github.komposeCharts.sample.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val rootView = window.decorView
            val insets = ViewCompat.getRootWindowInsets(rootView)
            val statusBarHeight = insets
                ?.getInsets(WindowInsetsCompat.Type.statusBars())
                ?.top ?: 0
            val density = resources.displayMetrics.density
            val statusBarDp = (statusBarHeight / density).toInt()

            App(statusBarDp = statusBarDp)
        }
    }
}
