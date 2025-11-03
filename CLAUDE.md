# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Yole** is a fork of Yole - a versatile Android text editor supporting 18+ markup formats including Markdown, todo.txt, CSV, WikiText, LaTeX, reStructuredText, and more. The app has been refactored from a monolithic structure into a modular architecture with separate modules for each format.

**Key Characteristics:**
- Android application (Java 11, minSdk 18, targetSdk 35)
- Modular multi-module Gradle project (20+ modules)
- Offline-first, no internet required
- Format-agnostic text editing with syntax highlighting and HTML preview
- Package namespace: `digital.vasic.yole.*`

## Build & Development Commands

### Essential Commands

Build the application:
```bash
./gradlew assembleFlavorDefaultDebug
# or
make build
```

Run tests:
```bash
./gradlew testFlavorDefaultDebugUnitTest
# or
make test
```

Run all tests (comprehensive suite):
```bash
./run_all_tests.sh
```

Run a specific test class:
```bash
./gradlew testFlavorDefaultDebugUnitTest --tests "digital.vasic.yole.format.todotxt.TodoTxtQuerySyntaxTests.ParseQuery"
```

Lint:
```bash
./gradlew lintFlavorDefaultDebug
# or
make lint
```

Clean:
```bash
./gradlew clean
# or
make clean
```

Build, install, and run on device:
```bash
make all install run
```

Verify modular architecture:
```bash
./verify_build.sh
```

### Build Variants/Flavors

- **flavorDefault**: Main build variant
- **flavorAtest**: Testing build with dynamic version codes
- **flavorGplay**: Google Play Store variant

## Architecture

### Module Structure

**Core Modules:**

1. **commons** - Shared utilities and base classes
   - `GsFileUtils` - File operations
   - `GsContextUtils` - Android context utilities
   - `GsCollectionUtils` - Collection helpers
   - Provides foundation for all other modules

2. **core** - Format system infrastructure
   - `FormatRegistry` - Central registry for all 18 formats
   - `TextConverterBase` - Base class for HTML conversion
   - `SyntaxHighlighterBase` - Base class for syntax highlighting
   - `ActionButtonBase` - Base class for toolbar actions
   - `AppSettings`, `Document` - Core models

3. **app** - Main application
   - Activities: `MainActivity`, `DocumentActivity`, `SettingsActivity`
   - Fragments: `DocumentEditAndViewFragment`, etc.
   - All UI and Android-specific code

**Format Modules:**

Each format is a separate module with this structure:
```
format-[name]/
├── src/main/java/digital/vasic/yole/format/[name]/
│   ├── [Name]TextConverter.java      # HTML conversion
│   ├── [Name]SyntaxHighlighter.java  # Editor highlighting
│   └── [Name]ActionButtons.java      # Toolbar actions
├── src/test/java/                    # Unit tests
└── build.gradle                      # Module dependencies
```

**Supported Formats:**
- markdown, todotxt, csv, wikitext, keyvalue, asciidoc, orgmode, plaintext
- latex, restructuredtext, taskpaper, textile, creole, tiddlywiki, jupyter, rmarkdown

### Dependency Flow

```
commons → core → format modules → app
```

- App module depends on core and all format modules
- Format modules depend on core (and transitively on commons)
- Core depends on commons
- **Important:** Never create circular dependencies

### Format Registration

All formats must be registered in `core/src/main/java/digital/vasic/yole/format/FormatRegistry.java`:

```java
public static final List<Format> FORMATS = Arrays.asList(
    new Format(FORMAT_LATEX, R.string.latex, ".tex", CONVERTER_LATEX),
    // ... other formats
);
```

### Text Conversion Pipeline

1. **File Detection** → `TextConverter.isFileOutOfThisFormat()`
2. **Markup Parsing** → `TextConverter.convertMarkupToHtml()`
3. **HTML Rendering** → WebView with format-specific CSS
4. **Syntax Highlighting** → `SyntaxHighlighter.applySyntaxHighlighting()`

## Adding New Formats

When implementing a new format module:

1. Create module directory: `format-[name]/`
2. Add to `settings.gradle`: `include ':format-[name]'`
3. Create `build.gradle` with dependencies on `core`
4. Implement three required classes:
   - `[Name]TextConverter extends TextConverterBase`
   - `[Name]SyntaxHighlighter extends SyntaxHighlighterBase`
   - `[Name]ActionButtons extends ActionButtonBase`
5. Register in `FormatRegistry.FORMATS`
6. Add string resources for format name
7. Write comprehensive unit tests
8. Reference `format-latex` as a working example

## Code Style & Conventions

- **Language:** Java 11 (source/target compatibility)
- **Package Structure:**
  - App code: `digital.vasic.yole.*`
  - Shared utilities: `net.gsantner.opoc.*` (legacy OPOC library)
- **Naming:**
  - Classes: `CamelCase`
  - Methods/variables: `lowerCamelCase`
  - Constants: `UPPER_SNAKE_CASE`
- **Code Style:** AOSP Java Code Style
- **File Headers:** Include SPDX license header and maintainer info
- **Error Handling:** Use try-catch blocks, log errors with `Log.e()`, null-check rigorously
- **Testing:** JUnit 4 with AssertJ assertions, test classes end with `Tests` or `Test`

## Testing Strategy

### Test Types

1. **Unit Tests** (`src/test/java/`)
   - Format detection accuracy
   - Markup conversion correctness
   - Syntax highlighting patterns
   - Action button functionality

2. **Integration Tests**
   - Module interaction
   - Format conversion pipelines

3. **Android Tests** (`src/androidTest/java/`)
   - UI component integration
   - Full device/emulator testing

### Running Tests

- All tests must pass (100% success rate required)
- Use `./run_all_tests.sh` for comprehensive testing
- Test individual modules: `./gradlew :format-markdown:test`

## Key Dependencies

- **Android:** AndroidX, Material Design
- **Markdown:** Flexmark library (v0.42.14)
- **CSV:** OpenCSV
- **Math Rendering:** KaTeX (via WebView)
- **Testing:** JUnit 4, AssertJ, Espresso

## Important Implementation Notes

### Format Module Implementation

When working on format modules:
- Each format must handle file detection via file extension matching
- HTML conversion should be self-contained within `TextConverter`
- Syntax highlighting patterns use regex-based matching
- Action buttons provide quick-insert UI for format-specific syntax

### File Locations

- **App code:** `app/src/main/java/digital/vasic/yole/`
- **Core code:** `core/src/main/java/digital/vasic/yole/`
- **Commons:** `commons/src/main/java/net/gsantner/opoc/`
- **Format implementations:** `format-[name]/src/main/java/digital/vasic/yole/format/[name]/`
- **Resources:** `app/src/main/res/`
- **Assets:** `app/src/main/assets/`

### Build Output

- APKs: `dist/*.apk` (via Makefile) or `app/build/outputs/apk/`
- Logs: `dist/log/`
- Test results: `dist/tests/`
- Lint reports: `dist/lint/`

## Special Files & Scripts

- `ARCHITECTURE.md` - Detailed architecture documentation
- `AGENTS.md` - Build commands and code style quick reference
- `run_all_tests.sh` - Comprehensive test runner
- `verify_build.sh` - Architecture verification script
- `Makefile` - Build automation with common targets
- `settings.gradle` - Multi-module project configuration

## Application Features

- **Notebook:** Root folder for all documents (user-configurable)
- **QuickNote:** Fast-access Markdown file
- **ToDo:** Main todo.txt file for task management
- **File Encryption:** AES256 encryption support for text files
- **Syntax Highlighting:** Real-time highlighting for all supported formats
- **HTML Preview:** WebView-based rendering for supported formats
- **PDF Export:** Convert documents to PDF
- **Cross-Platform:** Files are plaintext, compatible with any editor

## Debugging & Troubleshooting

- Use Android Studio's built-in debugger
- Check `dist/log/gradle.log` for build errors
- Lint warnings configured to not abort builds (see `app/build.gradle` lint section)
- Test failures require investigation before proceeding
- Format-specific issues: Check `[Format]TextConverter` and `[Format]SyntaxHighlighter`
