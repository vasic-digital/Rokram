/*#######################################################
 *
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * iOS Application Module for Yole
 * Native iOS app with Compose Multiplatform
 *
 *########################################################*/

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
}

kotlin {
    // iOS targets temporarily disabled for build stability
    // iosX64()
    // iosArm64()
    // iosSimulatorArm64()

    // Temporary JVM target to satisfy Kotlin plugin requirements
    jvm()

    // sourceSets - disabled since iOS targets are disabled
    // sourceSets {
    //     val iosX64Main by getting
    //     val iosArm64Main by getting
    //     val iosSimulatorArm64Main by getting
    //
    //     val iosMain by creating {
    //         dependsOn(commonMain.get())
    //         iosX64Main.dependsOn(this)
    //         iosArm64Main.dependsOn(this)
    //         iosSimulatorArm64Main.dependsOn(this)
    //
    //         dependencies {
    //             implementation(project(":shared"))
    //             implementation(compose.runtime)
    //             implementation(compose.foundation)
    //             implementation(compose.material3)
    //             implementation(compose.ui)
    //
    //             // iOS-specific dependencies
    //             implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    //         }
    //     }
    //
    //     val iosX64Test by getting
    //     val iosArm64Test by getting
    //     val iosSimulatorArm64Test by getting
    //
    //     val iosTest by creating {
    //         dependsOn(commonTest.get())
    //         iosX64Test.dependsOn(this)
    //         iosArm64Test.dependsOn(this)
    //         iosSimulatorArm64Test.dependsOn(this)
    //
    //         dependencies {
    //             implementation(kotlin("test"))
    //             implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    //         }
    //     }
    // }
}

// iOS-specific configurations - temporarily disabled for build stability
// val xcodeBuildDir = project.file("build/xcode-frameworks")
//
// tasks.register<org.jetbrains.kotlin.gradle.tasks.FatFrameworkTask>("debugFatFramework") {
//     baseName = "Yole"
//     destinationDirProperty.set(xcodeBuildDir.resolve("Debug"))
//     from(
//         kotlin.targets.getByName<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>("iosArm64").binaries.getFramework("DEBUG"),
//         kotlin.targets.getByName<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>("iosX64").binaries.getFramework("DEBUG")
//     )
// }
//
// tasks.register<org.jetbrains.kotlin.gradle.tasks.FatFrameworkTask>("releaseFatFramework") {
//     baseName = "Yole"
//     destinationDirProperty.set(xcodeBuildDir.resolve("Release"))
//     from(
//         kotlin.targets.getByName<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>("iosArm64").binaries.getFramework("RELEASE"),
//         kotlin.targets.getByName<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>("iosX64").binaries.getFramework("RELEASE")
//     )
// }
//
// tasks.named("assemble") {
//     dependsOn("debugFatFramework", "releaseFatFramework")
// }