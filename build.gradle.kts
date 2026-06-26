plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.binary.compatibility.validator) apply false
}

subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")

    extensions.configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        config.setFrom(rootProject.files("detekt.yml"))
        buildUponDefaultConfig = true
        source.setFrom(
            "src/commonMain/kotlin",
            "src/androidMain/kotlin",
            "src/desktopMain/kotlin",
            "src/iosMain/kotlin",
            "src/wasmJsMain/kotlin",
            "src/main/kotlin",
            "src/main/java"
        )
    }
}
