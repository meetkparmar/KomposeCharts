# Install KomposeCharts from JitPack

Add `google()`, `mavenCentral()`, and `maven("https://jitpack.io")` inside `dependencyResolutionManagement.repositories` in your `settings.gradle.kts`.

## Required metadata workaround

JitPack rewrites classified artifact filenames in Gradle metadata. Without this rule, shared Kotlin metadata compilation fails on missing iOS metadata JARs, and IDE source downloads can point to the wrong files. Add the following **inside `dependencyResolutionManagement`**, alongside `repositories`:

```kotlin
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

```

This rule affects only `com.github.meetkparmar.KomposeCharts`, preserves normal platform selection, and restores the actual source, native metadata, and Compose resource filenames. It does not replace SDK code or bypass checksum validation. Keep it for versions published through this JitPack pipeline.

## Shared dependency

In a Compose Multiplatform module's `commonMain.dependencies`, add:

```kotlin
implementation("com.github.meetkparmar.KomposeCharts:charts:0.2.0-rc.2")
```

This is a candidate version until stable verification finishes. `core` is included transitively. Kotlin/Compose versions and platform requirements are listed in the [README](README.md).

For a repository checkout, `-PuseRemote -PremoteLibraryVersion=<version>` enables the same installation rule and excludes local SDK projects. Do not add `mavenLocal()` when verifying a release.
