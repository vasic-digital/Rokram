# Markor Modular Architecture

## Overview

Markor has been refactored from a monolithic Android application into a modular architecture supporting 18 different text formats. This document describes the new architecture, module structure, and implementation details.

## Module Structure

### Core Modules

#### `commons` - Shared Utilities
- **Purpose**: Contains shared utilities, base classes, and interfaces used across all modules
- **Key Components**:
  - `GsFileUtils` - File operations and utilities
  - `GsContextUtils` - Android context utilities
  - `GsCollectionUtils` - Collection manipulation utilities
  - Base classes for format implementations

#### `core` - Core Functionality
- **Purpose**: Contains reusable editors, syntax highlighters, and text converters
- **Key Components**:
  - `FormatRegistry` - Central registry for all supported formats
  - `TextConverterBase` - Base class for text format converters
  - `SyntaxHighlighterBase` - Base class for syntax highlighters
  - `ActionButtonBase` - Base class for format-specific action buttons

#### `app` - Main Application
- **Purpose**: Contains screens, activities, and UI components
- **Key Components**:
  - `MainActivity` - Main application screen
  - `DocumentActivity` - Document editing/viewing
  - `SettingsActivity` - Application settings
  - All Android-specific UI components

### Format Modules

Each format is implemented as a separate module with the following structure:

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

All formats are registered in `FormatRegistry.java` with:

```java
public static final List<Format> FORMATS = Arrays.asList(
    new Format(FORMAT_LATEX, R.string.latex, ".tex", CONVERTER_LATEX),
    new Format(FORMAT_RESTRUCTUREDTEXT, R.string.restructuredtext, ".rst", CONVERTER_RESTRUCTUREDTEXT),
    // ... other formats
);
```

### Text Conversion Pipeline

1. **File Detection**: `TextConverter.isFileOutOfThisFormat()`
2. **Markup Parsing**: `TextConverter.convertMarkupToHtml()`
3. **HTML Rendering**: WebView with format-specific CSS
4. **Syntax Highlighting**: `SyntaxHighlighter.applySyntaxHighlighting()`

### Testing Strategy

#### Unit Tests
- Format detection accuracy
- Markup conversion correctness
- Syntax highlighting patterns
- Action button functionality

#### Integration Tests
- Module interaction
- Format conversion pipelines
- UI component integration

#### E2E Tests
- Full application workflows
- Real device/emulator testing
- AI-powered QA validation

## Build System

### Gradle Configuration
- Multi-module project with 20+ modules
- Shared build logic in root `build.gradle`
- Module-specific dependencies
- Automated testing pipeline

### Dependencies
- **Core**: AndroidX, Material Design
- **Markdown**: Flexmark library
- **CSV**: OpenCSV
- **Math**: KaTeX (via WebView)
- **Testing**: JUnit, AssertJ, Espresso

## Migration Guide

### From Monolithic to Modular

1. **Code Organization**:
   - Move shared code to `commons`
   - Move format implementations to separate modules
   - Keep UI code in `app` module

2. **Dependency Management**:
   - Update all imports to use new package structure
   - Add module dependencies in `build.gradle` files
   - Ensure proper dependency flow (commons → core → format modules → app)

3. **Testing**:
   - Run `./run_all_tests.sh` for comprehensive testing
   - Ensure 100% test success rate
   - Use AI QA for E2E validation

## Future Enhancements

### Planned Features
- Plugin system for third-party formats
- Cloud synchronization for all formats
- Advanced format conversion between types
- Collaborative editing support

### Performance Optimizations
- Lazy loading of format modules
- Caching of converted content
- Background processing for large files

## Contributing

When adding new formats:

1. Create new `format-[name]` module
2. Implement the three required classes
3. Add to `FormatRegistry.FORMATS`
4. Update string resources
5. Add comprehensive tests
6. Update documentation

See `format-latex` as a reference implementation.