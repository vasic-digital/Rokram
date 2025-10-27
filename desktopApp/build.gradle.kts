/*#######################################################
 *
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * Desktop Application Module for Yole
 * Cross-platform desktop apps for Windows, macOS, Linux
 *
 *########################################################*/

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.compose")
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    implementation(project(":shared"))
    
    // Compose Desktop
    implementation(compose.desktop.currentOs)
    
    // Kotlinx Coroutines
    implementation(libs.kotlinx.coroutines.core)
    
    // Testing
    testImplementation(libs.junit)
}

compose.desktop {
    application {
        mainClass = "digital.vasic.yole.desktop.MainKt"
        
        nativeDistributions {
            targetFormats(
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
            )
            
            packageName = "Yole"
            packageVersion = "1.0.0"
            
            description = "A versatile text editor supporting 18+ markup formats"
            
            // Windows configuration
            windows {
                menu = true
                upgradeUuid = "12345678-1234-1234-1234-123456789012"
                iconFile.set(project.file("src/main/resources/icons/icon.ico"))
            }
            
            // macOS configuration
            macOS {
                bundleID = "digital.vasic.yole"
                iconFile.set(project.file("src/main/resources/icons/icon.icns"))
            }
            
            // Linux configuration
            linux {
                iconFile.set(project.file("src/main/resources/icons/icon.png"))
            }
        }
    }
}