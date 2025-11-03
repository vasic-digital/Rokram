/*#######################################################
 *
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * Kotlin Multiplatform Shared Module
 * Contains platform-agnostic code shared across all platforms
 *
 *########################################################*/

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    kotlin("plugin.compose")
    id("com.android.library")
    id("org.jetbrains.compose")
}

kotlin {
    // Android target
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }

    // JVM Desktop target
    jvm("desktop") {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }

    // iOS targets - temporarily disabled for build stability
    // TODO: Re-enable iOS targets once basic compilation is working
    // listOf(
    //     iosX64(),
    //     iosArm64(),
    //     iosSimulatorArm64()
    // ).forEach { iosTarget ->
    //     iosTarget.binaries.framework {
    //         baseName = "shared"
    //         isStatic = true
    //     }
    // }

    // Web target (Wasm)
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "yole-shared"
        browser {
            commonWebpackConfig {
                outputFileName = "yole-shared.js"
            }
        }
    }

    sourceSets {
        // Common code for all platforms
        val commonMain by getting {
            dependencies {
                // Kotlin Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

                // Kotlinx Serialization
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

                // DateTime
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")

                // Okio for file system
                implementation("com.squareup.okio:okio:3.7.0")

                // Compose runtime (if using Compose Multiplatform)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                // kotlinx-coroutines-test doesn't have WASM variant
                // implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
            }
        }

        // Android-specific code
        val androidMain by getting {
            dependencies {
                implementation("androidx.core:core-ktx:1.12.0")
                implementation("androidx.appcompat:appcompat:1.6.1")
            }
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }

        // Desktop-specific code
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.common)
            }
        }

        val desktopTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }

        // iOS-specific code - temporarily disabled
        // val iosX64Main by getting
        // val iosArm64Main by getting
        // val iosSimulatorArm64Main by getting
        // val iosMain by creating {
        //     dependsOn(commonMain)
        //     iosX64Main.dependsOn(this)
        //     iosArm64Main.dependsOn(this)
        //     iosSimulatorArm64Main.dependsOn(this)
        //
        //     dependencies {
        //         implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
        //     }
        // }
        //
        // val iosX64Test by getting
        // val iosArm64Test by getting
        // val iosSimulatorArm64Test by getting
        // val iosTest by creating {
        //     dependsOn(commonTest)
        //     iosX64Test.dependsOn(this)
        //     iosArm64Test.dependsOn(this)
        //     iosSimulatorArm64Test.dependsOn(this)
        // }

        // Web-specific code (Wasm)
        val wasmJsMain by getting {
            dependencies {
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            }
        }

        val wasmJsTest by getting {
            dependencies {
                implementation(kotlin("test-wasm-js"))
                // kotlinx-coroutines-test doesn't have WASM variant
                // implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
            }
        }
    }
}

android {
    namespace = "digital.vasic.yole.shared"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
