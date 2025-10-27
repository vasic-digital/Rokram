/*#######################################################
 *
 *   Maintained 2017-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   Maintained 2025 by Milos Vasic
 *
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 #########################################################*/

import java.util.Date
import java.text.SimpleDateFormat

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

// Update minSdk for compatibility
rootProject.extra["version_minSdk"] = 21

android {
    namespace = "digital.vasic.yole"

    compileSdk = rootProject.extra["version_compileSdk"] as Int
    buildToolsVersion = rootProject.extra["version_buildTools"] as String

    defaultConfig {
        applicationId = "digital.vasic.yole"
        minSdk = rootProject.extra["version_minSdk"] as Int
        targetSdk = rootProject.extra["version_targetSdk"] as Int

        versionCode = 160
        versionName = "2.15.1"

        multiDexEnabled = true

        resValue("string", "manifest_package_id", "digital.vasic.yole")

        buildConfigField("boolean", "IS_TEST_BUILD", "false")
        buildConfigField("boolean", "IS_GPLAY_BUILD", "false")
        buildConfigField(
            "String[]",
            "DETECTED_ANDROID_LOCALES",
            rootProject.extra["findUsedAndroidLocales"] as () -> String
        )
        buildConfigField("String", "BUILD_DATE", "\"${rootProject.extra["getBuildDate"] as () -> String}\"")
        buildConfigField("String", "GITHASH", "\"${rootProject.extra["getGitHash"] as () -> String}\"")
        buildConfigField("String", "GITMSG", "\"${rootProject.extra["getGitLastCommitMessage"] as () -> String}\"")

        setProperty("archivesBaseName", "$applicationId-v$versionCode-$versionName")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    flavorDimensions += "default"
    productFlavors {
        create("flavorAtest") {
            dimension = "default"
            applicationIdSuffix = "_test"
            versionCode = SimpleDateFormat("yyMMdd").format(Date()).toInt()
            versionName = "${defaultConfig.versionName}-${SimpleDateFormat("HHmm").format(Date())}"
            buildConfigField("boolean", "IS_TEST_BUILD", "true")
        }
        create("flavorDefault") {
            dimension = "default"
        }
        create("flavorGplay") {
            dimension = "default"
            buildConfigField("boolean", "IS_GPLAY_BUILD", "true")
        }
    }

    sourceSets {
        getByName("main") {
            assets.srcDirs("src/main/assets")
            java.srcDirs("thirdparty/java")
            res.srcDirs("thirdparty/res")
            assets.srcDirs("thirdparty/assets")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }

    packaging {
        resources {
            excludes += listOf(
                "META-INF/LICENSE-LGPL-2.1.txt",
                "META-INF/LICENSE-LGPL-3.txt",
                "META-INF/LICENSE-W3C-TEST"
            )
        }
    }

    compileOptions {
        encoding = "UTF-8"
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.RequiresOptIn"
        )
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    lint {
        abortOnError = false
        disable += listOf(
            "MissingTranslation",
            "InvalidPackage",
            "ObsoleteLintCustomCheck",
            "DefaultLocale",
            "UnusedAttribute",
            "VectorRaster",
            "InflateParams",
            "IconLocation",
            "UnusedResources",
            "TypographyEllipsis"
        )
    }
}

dependencies {
    // Module dependencies
    implementation(project(":commons"))
    implementation(project(":core"))

    // Format modules (currently commented out in original, keep commented)
    // implementation(project(":format-markdown"))
    // implementation(project(":format-todotxt"))
    // ... etc

    // Kotlin
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.assertj.core)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)

    // AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.multidex)

    // Material Design
    implementation(libs.material)

    // Compose (for gradual migration)
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // UI libraries
    implementation(libs.gene.rate)
    implementation(libs.app.intro)
    implementation(libs.epub.parser)
    implementation(libs.color.picker)

    // Markdown converter (Flexmark)
    implementation(libs.bundles.flexmark)

    // CSV processing
    implementation(libs.opencsv)

    // Utilities
    implementation(libs.apache.commons.lang3)
    implementation(libs.commons.io)
    implementation(libs.gson)
}
