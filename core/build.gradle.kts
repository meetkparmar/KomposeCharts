import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.dokka)
    id("maven-publish")
}

group = "io.github.nicosnicolass"
version = property("libraryVersion") as String

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
        publishLibraryVariants("release")
    }

    jvm("desktop") {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "KomposeChartsCore"
            isStatic = true
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            // Pure Kotlin — no Compose dependency
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "io.github.komposeCharts.core"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

publishing {
    publications.withType<MavenPublication> {
        pom {
            name.set("KomposeCharts Core")
            description.set("Core data models and utilities for KomposeCharts - a Compose Multiplatform charting library")
            url.set("https://github.com/nicosnicolass/KomposeCharts")
            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }
            developers {
                developer {
                    id.set("nicosnicolass")
                    name.set("Meet Parmar")
                }
            }
            scm {
                connection.set("scm:git:git://github.com/nicosnicolass/KomposeCharts.git")
                developerConnection.set("scm:git:ssh://github.com/nicosnicolass/KomposeCharts.git")
                url.set("https://github.com/nicosnicolass/KomposeCharts")
            }
        }
    }
}
