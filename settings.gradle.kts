/*#######################################################
 *
 * SPDX-FileCopyrightText: 2017-2025 Gregor Santner <gsantner AT mailbox DOT org>
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Unlicense OR CC0-1.0
 *
 * Kotlin Multiplatform Settings
 *#########################################################*/

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://jitpack.io")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://jitpack.io")
    }
}

rootProject.name = "Yole"

// Enable Gradle version catalog
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// Existing Android modules
include(":app")
include(":core")
include(":commons")

// Format modules
include(":format-markdown")
include(":format-todotxt")
include(":format-csv")
include(":format-wikitext")
include(":format-keyvalue")
include(":format-asciidoc")
include(":format-orgmode")
include(":format-plaintext")
include(":format-latex")
include(":format-restructuredtext")
include(":format-taskpaper")
include(":format-textile")
include(":format-creole")
include(":format-tiddlywiki")
include(":format-jupyter")
include(":format-rmarkdown")

// Kotlin Multiplatform shared module (to be created)
// include(":shared")

// Platform-specific apps (to be created)
// include(":androidApp")
// include(":desktopApp")
// include(":webApp")
