package io.github.komposeCharts.sample.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.komposeCharts.sample.design.AppColors

@Composable
fun KCPreviewContainer(content: @Composable () -> Unit) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.Background)
            .padding(16.dp),
    ) {
        content()
    }
}
