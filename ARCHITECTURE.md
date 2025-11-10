# Yole Cross-Platform Architecture

## Overview

Yole is a cross-platform text editor supporting Android, Desktop, iOS, and Web platforms with 18+ text formats. This document describes the architecture, module structure, and implementation details.

## Current Platform Status (November 2025)

| Platform | Status | Notes |
|----------|--------|-------|
| **Android** | ✅ Production Ready | Fully functional with comprehensive test coverage |
| **Desktop** | ⚠️ Beta (30% complete) | Basic implementation, needs completion and testing |
| **iOS** | 🚧 Disabled | iOS targets temporarily disabled in `shared/build.gradle.kts:41` due to compilation issues. Requires resolution before re-enabling |
| **Web** | 🚧 Stub (0% complete) | Build configuration complete, but no source implementation. Infrastructure in place for future development |

**Critical Issues:**
- **iOS**: All iOS targets (`iosX64`, `iosArm64`, `iosSimulatorArm64`) commented out. TODO at line 41 of `shared/build.gradle.kts`: "Re-enable iOS targets once basic compilation is working"
- **Web**: `webApp/src/main/` has no source code despite complete build configuration
- **Core Module**: Investigation needed - main source directory structure unclear

## Module Structure

### Platform-Specific Applications

#### `androidApp` - Android Application
- **Purpose**: Native Android application with modern Android development practices
- **Key Components**:
  - Native Android UI components
  - Android-specific file system access
  - Platform-optimized performance
- **Technologies**: AndroidX, Kotlin, modern Android architecture

#### `desktopApp` - Desktop Application
- **Purpose**: Native desktop application for Windows, macOS, and Linux
- **Key Components**:
  - Desktop-specific UI framework
  - Native file system integration
  - Cross-platform desktop support
- **Technologies**: Compose Desktop, Kotlin

#### `iosApp` - iOS Application
- **Purpose**: Native iOS application
- **Key Components**:
  - iOS-specific UI components
  - iOS file system integration
  - Apple ecosystem integration
- **Technologies**: SwiftUI or UIKit, platform-specific development

#### `webApp` - Web Application
- **Purpose**: Progressive Web App with modern web technologies
- **Key Components**:
  - Web-specific UI framework
  - Browser-based file handling
  - PWA capabilities
- **Technologies**: WebAssembly, modern web development

### Legacy Android Modules

#### `commons` - Shared Utilities
- **Purpose**: Contains shared utilities, base classes, and interfaces used across Android modules
- **Key Components**:
  - `GsFileUtils` - File operations and utilities
  - `GsContextUtils` - Android context utilities
  - `GsCollectionUtils` - Collection manipulation utilities
  - Base classes for format implementations

#### `core` - Core Functionality
- **Purpose**: Contains reusable editors, syntax highlighters, and text converters
- **Key Components**:
  - Android-specific implementations
  - `TextConverterBase` - Base class for text format converters
  - `SyntaxHighlighterBase` - Base class for syntax highlighters
  - `ActionButtonBase` - Base class for format-specific action buttons

#### `app` - Legacy Android Application
- **Purpose**: Legacy Android application (being phased out)
- **Key Components**:
  - `MainActivity` - Main application screen
  - `DocumentActivity` - Document editing/viewing
  - `SettingsActivity` - Application settings
  - Legacy Android UI components

### Format Modules

#### Platform-Specific Format Implementations
Each format is implemented natively for each platform:

```
format-[name]/
├── src/main/java/digital/vasic/yole/format/[name]/  # Android implementation
│   ├── [Name]TextConverter.java      # HTML conversion
│   ├── [Name]SyntaxHighlighter.java  # Editor highlighting
│   └── [Name]ActionButtons.java      # Toolbar actions
├── src/test/java/                    # Unit tests
└── src/androidTest/java/             # Integration tests
```

Platform-specific implementations are maintained separately for optimal performance and native integration.
shared/src/commonMain/kotlin/digital/vasic/yole/format/[name]/
├── [Name]Parser.kt                   # Common parsing logic
└── Platform-specific implementations
    ├── androidMain/[Name]Parser.android.kt
    ├── desktopMain/[Name]Parser.desktop.kt
    └── iosMain/[Name]Parser.ios.kt
```

#### Legacy Android Format Modules
Each format also has a legacy Android module for UI-specific components:

```
format-[name]/
├── src/main/java/digital/vasic/yole/format/[name]/
│   ├── [Name]TextConverter.java      # HTML conversion
│   ├── [Name]SyntaxHighlighter.java  # Editor highlighting
│   └── [Name]ActionButtons.java      # Toolbar actions
├── src/test/java/                    # Unit tests
└── src/androidTest/java/             # Integration tests
```

## Supported Formats

### 1. Markdown (`format-markdown`)
- **Extensions**: `.md`
- **Features**: Full CommonMark support, KaTeX math rendering, tables, code blocks
- **Dependencies**: Flexmark library

### 2. Todo.txt (`format-todotxt`)
- **Extensions**: `.todo.txt`
- **Features**: Task management, priorities, contexts, projects, due dates
- **Special Features**: Advanced search and filtering

### 3. CSV (`format-csv`)
- **Extensions**: `.csv`
- **Features**: Table preview, column-based syntax highlighting, HTML export
- **Dependencies**: OpenCSV library

### 4. WikiText (`format-wikitext`)
- **Extensions**: `.txt`
- **Features**: Zim wiki format, link resolution, transclusion
- **Special Features**: Attachment directory handling

### 5. Key-Value (`format-keyvalue`)
- **Extensions**: `.json`, `.yml`, `.yaml`, `.toml`, `.vcf`, `.ics`, `.ini`
- **Features**: Structured data editing with syntax highlighting
- **Supported Formats**: JSON, YAML, TOML, INI, VCF (vCard), ICS (iCalendar)

### 6. AsciiDoc (`format-asciidoc`)
- **Extensions**: `.adoc`
- **Features**: Technical documentation format
- **Dependencies**: AsciiDoctor.js

### 7. Org-mode (`format-orgmode`)
- **Extensions**: `.org`
- **Features**: Emacs org-mode format, headings, TODO states, timestamps
- **Dependencies**: Org-mode JavaScript library

### 8. Plaintext (`format-plaintext`)
- **Extensions**: `.txt`
- **Features**: Basic text editing with code syntax highlighting
- **Special Features**: Automatic language detection

### 9. LaTeX (`format-latex`) - NEW
- **Extensions**: `.tex`, `.latex`
- **Features**:
  - LaTeX command highlighting
  - Math expression conversion to KaTeX
  - Basic document structure conversion
  - Comment highlighting
- **Action Buttons**: Common LaTeX commands, math delimiters, lists

### 10. reStructuredText (`format-restructuredtext`) - NEW
- **Extensions**: `.rst`, `.rest`
- **Features**:
  - Header detection and conversion
  - Inline markup (`*bold*`, `**italic**`, ```code```)
  - Code blocks
  - Basic list support
- **Action Buttons**: RST markup shortcuts

### 11. TaskPaper (`format-taskpaper`) - NEW
- **Extensions**: `.taskpaper`
- **Features**:
  - Project-based task organization
  - Tag support
  - Note attachments
- **Action Buttons**: Task creation, project management

### 12. Textile (`format-textile`) - NEW
- **Extensions**: `.textile`
- **Features**:
  - Lightweight markup similar to Markdown
  - Textile-specific syntax highlighting
  - HTML conversion
- **Action Buttons**: Textile formatting shortcuts

### 13. Creole (`format-creole`) - NEW
- **Extensions**: `.creole`
- **Features**:
  - Wiki markup standard
  - Cross-wiki compatibility
  - Link and formatting support
- **Action Buttons**: Creole wiki markup

### 14. TiddlyWiki (`format-tiddlywiki`) - NEW
- **Extensions**: `.tid`
- **Features**:
  - Personal wiki format
  - Structured data support
  - Link handling
- **Action Buttons**: Tiddler creation, linking

### 15. Jupyter (`format-jupyter`) - NEW
- **Extensions**: `.ipynb`
- **Features**:
  - JSON-based notebook format
  - Code cell highlighting
  - Markdown cell support
- **Action Buttons**: Cell creation, notebook structure

### 16. R Markdown (`format-rmarkdown`) - NEW
- **Extensions**: `.Rmd`
- **Features**:
  - R code integration
  - Markdown with R chunks
  - Output formatting
- **Action Buttons**: Code chunk insertion, R integration

## Implementation Details

### Format Registration

Formats are registered in platform-specific registries:

```kotlin
object FormatRegistry {
    private val formats = mutableMapOf<String, TextFormat>()

    fun registerFormat(format: TextFormat) {
        formats[format.name] = format
    }

    fun getFormat(name: String): TextFormat? = formats[name]
}
```

### Text Parsing Pipeline

1. **File Detection**: `TextFormat.detectFormat(content: String, extension: String)`
2. **Markup Parsing**: `TextParser.parse(content: String): Document`
3. **Platform Rendering**: Platform-specific converters to HTML/other formats
4. **Syntax Highlighting**: Platform-specific syntax highlighters

### Android Pipeline

1. **File Detection**: `TextConverter.isFileOutOfThisFormat()`
2. **Markup Parsing**: `TextConverter.convertMarkupToHtml()`
3. **HTML Rendering**: WebView with format-specific CSS
4. **Syntax Highlighting**: `SyntaxHighlighter.applySyntaxHighlighting()`

### Testing Strategy

#### Platform-Specific Tests
- `androidApp/src/test/`: Android unit tests
- `desktopApp/src/test/`: Desktop unit tests
- `iosApp/src/test/`: iOS unit tests
- `webApp/src/test/`: Web unit tests

#### Legacy Android Tests
- Format detection accuracy
- Markup conversion correctness
- Syntax highlighting patterns
- Action button functionality
- UI component integration

#### E2E Tests
- Full application workflows
- Real device/emulator testing
- Cross-platform compatibility testing

## Build System

### Gradle Configuration
- Multi-platform project with 25+ modules
- Version catalog for dependency management (`libs.versions.toml`)
- Shared build logic in root `build.gradle.kts`
- Module-specific dependencies
- Automated testing pipeline

### Platform Dependencies
- **Android**: AndroidX, Material Design, Kotlin
- **Desktop**: Compose Desktop, Kotlin
- **iOS**: Platform-specific frameworks
- **Web**: WebAssembly, modern web technologies

### Legacy Android Dependencies
- **Core**: AndroidX, Material Design
- **Markdown**: Flexmark library
- **CSV**: OpenCSV
- **Math**: KaTeX (via WebView)
- **Testing**: JUnit, AssertJ, Espresso

## Development Guide

### Platform Development

1. **Android Development**:
    - Use `androidApp/` module for modern Android development
    - Follow Android development best practices
    - Test on various Android devices and versions

2. **Desktop Development**:
    - Use `desktopApp/` module for cross-platform desktop
    - Test on Windows, macOS, and Linux
    - Ensure native look and feel

3. **iOS Development**:
    - Use `iosApp/` module for iOS development
    - Follow iOS development guidelines
    - Test on various iOS devices

4. **Web Development**:
    - Use `webApp/` module for PWA development
    - Ensure responsive design
    - Test across modern browsers

### Code Organization
- Keep platform-specific code in respective app modules
- Share common utilities through `commons/` module
- Use `core/` for shared Android functionality
- Maintain format modules for extensibility

## Future Enhancements

### Planned Features
- Plugin system for third-party formats
- Cloud synchronization for all platforms
- Advanced format conversion between types
- Collaborative editing support
- AI-powered features

### Performance Optimizations
- Lazy loading of format modules
- Caching of converted content
- Background processing for large files
- Platform-specific optimizations

## Contributing

When adding new formats:

### Platform-Specific Implementation
1. Create new `format-[name]` module
2. Implement platform-specific parsers and converters
3. Add to platform-specific `FormatRegistry`
4. Update string resources for each platform
5. Add comprehensive tests for each platform
6. Update documentation

### Cross-Platform Considerations
- Ensure consistent behavior across platforms
- Handle platform-specific file system differences
- Test on all target platforms
- Maintain performance optimizations

See existing format modules as reference implementations.