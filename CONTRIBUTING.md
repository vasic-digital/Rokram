# Contributing to Yole

Thank you for your interest in contributing to Yole! This document provides guidelines for contributing to this cross-platform text editor project.

## Getting Started

### Prerequisites
- **Android Studio** latest stable version (for Android development)
- **Xcode** (for iOS development)
- **IntelliJ IDEA** or similar (for Desktop/Web development)
- **Git** for version control
- **Kotlin** 1.9+ (managed by Gradle)
- **Android SDK** 21-35 (API levels)
- **Java** 8+ compatibility

### Setup
1. Fork the repository on GitHub
2. Clone your fork locally:
    ```bash
    git clone https://github.com/yourusername/yole.git
    cd yole
    ```
3. Open the project in your preferred IDE
4. Let Gradle sync and download dependencies
5. Build the project to verify setup:
    ```bash
    ./gradlew assembleDebug  # For Android
    ./gradlew desktopApp:run  # For Desktop
    ./gradlew webApp:wasmJsBrowserRun  # For Web
    ```

## Development Workflow

### Branch Strategy
- `main`: Stable release branch
- `develop`: Development branch (if exists)
- `feature/*`: Feature-specific branches
- `bugfix/*`: Bug fix branches
- `hotfix/*`: Critical fixes for releases

### Code Style Guidelines

#### Kotlin Code Style
- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use **camelCase** for variables and functions
- Use **PascalCase** for classes and objects
- Use **UPPER_SNAKE_CASE** for constants
- Keep lines under 120 characters when possible

#### Import Organization
1. Standard Android imports
2. AndroidX imports  
3. Third-party library imports
4. Project imports (`digital.vasic.yole.*`, `net.gsantner.opoc.*`)

#### File Headers
All source files must include SPDX license header:
```kotlin
/*
 * SPDX-FileCopyrightText: 2017-2025 Gregor Santner <gsantner AT mailbox DOT org>
 * SPDX-FileCopyrightText: 2025 Your Name <your@email.com>
 * SPDX-License-Identifier: Apache 2.0
 */
```

### Testing

#### Running Tests
```bash
# Run all tests
./gradlew testFlavorDefaultDebugUnitTest

# Run specific test
./gradlew testFlavorDefaultDebugUnitTest --tests "digital.vasic.yole.format.todotxt.TodoTxtQuerySyntaxTests.ParseQuery"

# Run tests for specific module
./gradlew :format-markdown:testDebugUnitTest
```

#### Writing Tests
- Use **JUnit 4** with **AssertJ** assertions
- Test class names should end with `Tests` or `Test`
- Write unit tests for business logic in shared modules
- Write instrumented tests for Android-specific code
- Aim for high test coverage on core functionality

### Building and Linting

#### Build Commands
```bash
# Build debug APK
./gradlew assembleFlavorDefaultDebug

# Build release APK
./gradlew assembleFlavorDefaultRelease

# Run lint checks
./gradlew lintFlavorDefaultDebug

# Clean build
./gradlew clean
```

#### Before Submitting
1. **Run tests**: Ensure all tests pass
2. **Run lint**: Fix any lint warnings/errors
3. **Format code**: Use Android Studio's auto-reformat
4. **Build successfully**: Verify project builds without errors
5. **Test manually**: Test your changes on device/emulator

## Project Structure

### Cross-Platform Modules
```
├── app/                    # Legacy Android application
├── androidApp/             # Modern Android application
├── desktopApp/             # Desktop application (Windows/macOS/Linux)
├── iosApp/                 # iOS application
├── webApp/                 # Web application
├── format-*/               # Format-specific modules
│   ├── format-markdown/
│   ├── format-todotxt/
│   └── ... (all 18 formats)
├── commons/                # Shared utilities
└── core/                   # Core functionality
```
├── shared/                 # Shared KMP code
│   ├── src/
│   │   ├── commonMain/     # Common Kotlin code
│   │   ├── androidMain/    # Android-specific
│   │   ├── desktopMain/    # Desktop-specific
│   │   └── iosMain/        # iOS-specific
├── app/                    # Android application
├── androidApp/             # Android app (KMP)
├── desktopApp/             # Desktop application
├── iosApp/                 # iOS application
├── webApp/                 # Web application
├── format-*/               # Format-specific modules
│   ├── format-markdown/
│   ├── format-todotxt/
│   └── ...
├── commons/                # Shared utilities
└── core/                   # Core functionality
```

### Package Structure
- `digital.vasic.yole.*`: Main application code
- `net.gsantner.opoc.*`: Shared utility libraries
- `digital.vasic.yole.format.*`: Format-specific implementations

## Contributing Types

### Bug Reports
1. Search existing issues first
2. Use the bug report template
3. Include device info, Android version, and steps to reproduce
4. Provide screenshots if applicable

### Feature Requests
1. Search existing discussions and issues
2. Use the feature request template
3. Describe the use case and expected behavior
4. Consider if it fits the project's scope

### Code Contributions
1. Fork and create a feature branch
2. Write clean, tested code following guidelines
3. Update documentation if needed
4. Submit a pull request with clear description
5. Respond to code review feedback

### Documentation
- Improve existing documentation
- Add examples for new features
- Update README and guides
- Fix typos and clarify unclear sections

### Translations
- Contribute via [Crowdin](https://crowdin.com/project/markor)
- Report translation issues in GitHub issues
- Help maintain language quality

## Pull Request Process

### Before Submitting
1. **Rebase** your branch on latest `main`
2. **Test thoroughly** on multiple devices if possible
3. **Update documentation** for any user-facing changes
4. **Add tests** for new functionality
5. **Run full test suite** and fix any failures

### Pull Request Template
```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
- [ ] Unit tests pass
- [ ] Manual testing completed
- [ ] Lint checks pass

## Checklist
- [ ] Code follows project style guidelines
- [ ] Self-review completed
- [ ] Documentation updated
- [ ] Tests added/updated
```

### Code Review
- Be responsive to reviewer feedback
- Explain complex changes clearly
- Address all review comments
- Keep PRs focused and reasonably sized

## Community Guidelines

### Communication
- Be respectful and constructive
- Welcome newcomers and help them learn
- Focus on what is best for the project
- Show empathy towards other community members

### Code Review Etiquette
- Provide specific, actionable feedback
- Explain the reasoning behind suggestions
- Acknowledge good work and improvements
- Be patient with different perspectives

## Getting Help

### Resources
- [AGENTS.md](AGENTS.md): Development guide for agents
- [ARCHITECTURE.md](ARCHITECTURE.md): Project architecture
- [README.md](README.md): General project information
- [GitHub Discussions](https://github.com/gsantner/markor/discussions): Community discussions

### Contact
- Create a GitHub issue for bugs and feature requests
- Start a discussion for questions and ideas
- Check existing issues and discussions before posting

## License

By contributing to Yole, you agree that your contributions will be licensed under the same license as the project (Apache 2.0 for code, CC0 1.0 for translations and samples).

---

Thank you for contributing to Yole! Your help makes this project better for everyone. 🎉