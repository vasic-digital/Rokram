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

## Code Coverage (Kover)
- **HTML Report**: `./gradlew koverHtmlReport` → View: `open build/reports/kover/html/index.html`
- **XML Report**: `./gradlew koverXmlReport` → Output: `build/reports/kover/report.xml`
- **Run Tests with Coverage**: `./gradlew test koverHtmlReport`
- **Current Baseline**: ~15% estimated (goal: >80%)
- **Note**: Reports are generated per module. Use `koverHtmlReport` for combined coverage.

## API Documentation (Dokka)
- **Generate Docs**: `./gradlew :shared:dokkaHtml` → Output: `shared/build/dokka/html/`
- **View Docs**: `open shared/build/dokka/html/index.html`
- **Publish to Website**: `mkdir -p docs/api && cp -r shared/build/dokka/html/* docs/api/`
- **Key Classes**: FormatRegistry, TextFormat, Document (all in `shared` module)
- **Note**: FormatRegistry and TextFormat already have comprehensive KDoc documentation

## Test Generation
- **Generate Tests**: `./scripts/generate_format_tests.sh <format-name> <extension>`
- **Example**: `./scripts/generate_format_tests.sh Markdown .md`
- **Dry Run**: `./scripts/generate_format_tests.sh "Org Mode" .org --dry-run`
- **Custom Options**: `--package <name>`, `--class <name>`, `--templates <list>`
- **Available Templates**: ParserTest, IntegrationTest, MockKExample, KotestPropertyTest
- **Output**: `shared/src/commonTest/kotlin/digital/vasic/yole/format/<package>/`
- **Documentation**: See `docs/TESTING_GUIDE.md` for complete testing documentation
- **Note**: Generated tests use placeholder content - customize with format-specific samples

## Code Style Guidelines
- **Language**: Kotlin with Java 8+ compatibility, platform-specific languages
- **Package structure**: `digital.vasic.yole.*` for app code, `net.gsantner.opoc.*` for shared utilities
- **Naming**: CamelCase for classes, lowerCamelCase for methods/variables, UPPER_SNAKE_CASE for constants
- **Imports**: Group standard libraries, third-party, then project imports
- **Error handling**: Use try-catch blocks, log errors appropriately, handle null checks
- **Testing**: Use JUnit 4/5 with AssertJ assertions, test classes end with `Tests` or `Test`
- **File headers**: Include SPDX license header and maintainer info (Apache 2.0, CC0-1.0, or Unlicense)
- **Version**: Use version catalog (libs.versions.toml) for dependency management