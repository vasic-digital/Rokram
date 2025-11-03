/*#######################################################
 *
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * Web Application Module for Yole
 * Progressive Web App with Kotlin/Wasm
 *
 *########################################################*/

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    wasmJs {
        moduleName = "yole-web"
        browser {
            commonWebpackConfig {
                outputFileName = "yole-web.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        val wasmJsMain by getting {
            dependencies {
                implementation(project(":shared"))
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                
                // Web-specific dependencies
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
            }
        }

        val wasmJsTest by getting {
            dependencies {
                implementation(kotlin("test-wasm-js"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
            }
        }
    }
}

compose.experimental {
    web.application {}
}

// Dev server configuration
kotlin {
    wasmJs {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
    }
}

// Configure PWA features
tasks.register<Copy>("copyWebResources") {
    from("src/wasmJsMain/resources")
    into("build/distributions")
}

tasks.named("wasmJsBrowserDistribution") {
    dependsOn("copyWebResources")
}

// Copy Logo.png to resources
tasks.register<Copy>("copyLogo") {
    from("../../Assets/Logo.png")
    into("src/wasmJsMain/resources")
}

tasks.named("compileKotlinWasmJs") {
    dependsOn("copyLogo")
}