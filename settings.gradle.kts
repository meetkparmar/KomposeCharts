pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        if (providers.gradleProperty("useRemote").isPresent) {
            maven("https://jitpack.io")
        }
    }
}

rootProject.name = "KomposeCharts-parent"

if (!providers.gradleProperty("useRemote").isPresent) {
    include(":core")
    include(":charts")
}

include(":samples:common")
include(":samples:android")
include(":samples:desktop")
include(":samples:ios")
include(":samples:web")
