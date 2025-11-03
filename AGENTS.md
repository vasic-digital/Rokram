# Yole - Development Guide

## Build Commands
- **Android Build**: `./gradlew :androidApp:assembleDebug` or `make build`
- **Desktop Build**: `./gradlew :desktopApp:run` or `make desktop`
- **Web Build**: `./gradlew :webApp:wasmJsBrowserRun` or `make web`
- **iOS Build**: Open `iosApp/iosApp.xcodeproj` in Xcode
- **Lint**: `./gradlew lint` or `make lint`
- **Test**: `./gradlew test` or `make test`
- **Single test**: `./gradlew test --tests "digital.vasic.yole.format.todotxt.TodoTxtQuerySyntaxTests.ParseQuery"`
- **Clean**: `./gradlew clean` or `make clean`

## Code Style Guidelines
- **Language**: Kotlin with Java 8+ compatibility, platform-specific languages
- **Package structure**: `digital.vasic.yole.*` for app code, `net.gsantner.opoc.*` for shared utilities
- **Naming**: CamelCase for classes, lowerCamelCase for methods/variables, UPPER_SNAKE_CASE for constants
- **Imports**: Group standard libraries, third-party, then project imports
- **Error handling**: Use try-catch blocks, log errors appropriately, handle null checks
- **Testing**: Use JUnit 4/5 with AssertJ assertions, test classes end with `Tests` or `Test`
- **File headers**: Include SPDX license header and maintainer info (Apache 2.0, CC0-1.0, or Unlicense)
- **Version**: Use version catalog (libs.versions.toml) for dependency management