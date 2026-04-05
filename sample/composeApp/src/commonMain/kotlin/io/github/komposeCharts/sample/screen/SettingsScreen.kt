package io.github.komposeCharts.sample.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import io.github.komposeCharts.sample.design.AppDimen
import io.github.komposeCharts.sample.design.AppTypography

@Composable
fun SettingsScreen(
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
) {
    var highContrast by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AppDimen.Spacing_24dp)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(top = AppDimen.Spacing_48dp, bottom = AppDimen.Spacing_128dp),
        verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_48dp),
    ) {
        // Hero Header
        Column(verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_8dp)) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = AppTypography.headingLetterSpacing,
            )
            Text(
                text = "Manage your workspace preferences\nand application data.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = AppTypography.subtitleLineHeight,
            )
        }

        // Settings Sections
        Column(verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_32dp)) {

            // General Section
            Column(verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_16dp)) {
                SectionLabel(title = "GENERAL")
                Surface(
                    shape = RoundedCornerShape(AppDimen.Spacing_32dp),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(AppDimen.Spacing_24dp),
                        verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_24dp),
                    ) {
                        SettingsToggleRow(
                            title = "Dark Mode",
                            description = "Adjust the appearance of the workspace.",
                            checked = darkTheme,
                            onCheckedChange = onDarkThemeChange,
                        )
                        SettingsToggleRow(
                            title = "High Contrast",
                            description = "Increase legibility for chart elements.",
                            checked = highContrast,
                            onCheckedChange = { highContrast = it },
                        )
                    }
                }
            }

            // App Section
            Column(verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_16dp)) {
                SectionLabel(title = "APP")

                Surface(
                    shape = RoundedCornerShape(AppDimen.Spacing_32dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column {
                        // Version Info row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = AppDimen.Spacing_20dp, vertical = AppDimen.Spacing_20dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Version Info",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Surface(
                                shape = RoundedCornerShape(AppDimen.Spacing_16dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                            ) {
                                Text(
                                    text = "v1.0.0",
                                    modifier = Modifier.padding(
                                        horizontal = AppDimen.Spacing_8dp,
                                        vertical = AppDimen.Spacing_4dp,
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                            }
                        }

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant,
                            thickness = AppDimen.Spacing_1dp,
                        )

                        // Licenses row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = AppDimen.Spacing_20dp, vertical = AppDimen.Spacing_20dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Licenses",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = "›",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant,
                            thickness = AppDimen.Spacing_1dp,
                        )

                        // Clear Cache row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = AppDimen.Spacing_20dp, vertical = AppDimen.Spacing_20dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Clear Cache",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }

                // System Integrity card
                Surface(
                    shape = RoundedCornerShape(AppDimen.Spacing_32dp),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(AppDimen.Spacing_128dp),
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(AppDimen.Spacing_16dp),
                            verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_2dp),
                        ) {
                            Text(
                                text = "SYSTEM INTEGRITY",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = AppTypography.statusLabelLetterSpacing,
                            )
                            Text(
                                text = "All systems operational",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        letterSpacing = AppTypography.sectionLabelLetterSpacing,
        modifier = Modifier.padding(horizontal = AppDimen.Spacing_4dp),
    )
}

@Composable
private fun SettingsToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = AppDimen.Spacing_16dp),
            verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_2dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = AppTypography.descriptionLineHeight,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}
