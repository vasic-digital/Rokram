# Markor - Agent Development Guide

## Build Commands
- **Build**: `./gradlew assembleFlavorDefaultDebug` or `make build`
- **Lint**: `./gradlew lintFlavorDefaultDebug` or `make lint`
- **Test**: `./gradlew testFlavorDefaultDebugUnitTest` or `make test`
- **Single test**: `./gradlew testFlavorDefaultDebugUnitTest --tests "digital.vasic.yole.format.todotxt.TodoTxtQuerySyntaxTests.ParseQuery"`
- **Clean**: `./gradlew clean` or `make clean`

## Code Style Guidelines
- **Language**: Java 8 (source/target compatibility)
- **Package structure**: `digital.vasic.yole.*` for app code, `net.gsantner.opoc.*` for shared utilities
- **Naming**: CamelCase for classes, lowerCamelCase for methods/variables, UPPER_SNAKE_CASE for constants
- **Imports**: Group standard Android, androidx, third-party, then project imports
- **Error handling**: Use try-catch blocks, log errors with `Log.e()`, handle null checks
- **Testing**: Use JUnit 4 with AssertJ assertions, test classes end with `Tests` or `Test`
- **File headers**: Include SPDX license header and maintainer info
- **Lint**: Configure in build.gradle with disabled rules for MissingTranslation, InvalidPackage, etc.
- **Build variants**: flavorAtest (testing), flavorDefault (main), flavorGplay (Play Store)