pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}

dependencyResolutionManagement {
        // Work around JitPack dropping classifiers from Gradle module metadata.
        components {
            all {
                if (id.group == "com.github.meetkparmar.KomposeCharts") {
                    val module = id
                    val sourcesVariants = listOf(
                        "metadataSourcesElements", "releaseSourcesElements-published",
                        "desktopSourcesElements-published", "iosArm64SourcesElements-published",
                        "iosSimulatorArm64SourcesElements-published", "iosX64SourcesElements-published",
                    )
                    sourcesVariants.filter { variant ->
                        when {
                            module.name.endsWith("-android") -> variant == "releaseSourcesElements-published"
                            module.name.endsWith("-desktop") -> variant == "desktopSourcesElements-published"
                            module.name.endsWith("-iosarm64") -> variant == "iosArm64SourcesElements-published"
                            module.name.endsWith("-iossimulatorarm64") -> variant == "iosSimulatorArm64SourcesElements-published"
                            module.name.endsWith("-iosx64") -> variant == "iosX64SourcesElements-published"
                            else -> variant == "metadataSourcesElements"
                        }
                    }.forEach { variant ->
                        withVariant(variant) {
                            withFiles {
                                removeAllFiles()
                                addFile("${module.name}-${module.version}-sources.jar")
                            }
                        }
                    }
                    listOf("iosArm64", "iosSimulatorArm64", "iosX64").forEach { target ->
                        if (module.name == "charts-${target.lowercase()}") {
                            withVariant("${target}ResourcesElements-published") {
                                withFiles {
                                    removeAllFiles()
                                    addFile("${module.name}-${module.version}-kotlin_resources.kotlin_resources.zip")
                                }
                            }
                        }
                        if (module.name.endsWith("-${target.lowercase()}")) withVariant("${target}MetadataElements-published") {
                            withFiles {
                                removeAllFiles()
                                addFile("${module.name}-${module.version}-metadata.jar")
                            }
                        }
                    }
                }
            }
        }

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
