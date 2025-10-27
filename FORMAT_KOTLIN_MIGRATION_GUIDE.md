# Format Module Kotlin Migration Guide

**Status**: Plaintext format migration completed successfully (BUILD SUCCESSFUL)
**Date**: October 26, 2025
**Pattern**: In-place migration within app module

## Overview

This guide documents the proven process for migrating format modules from Java to Kotlin. The plaintext format has been successfully migrated using the in-place pattern due to circular dependency constraints.

## Why In-Place Migration?

### Architectural Constraint
The Yole application has a circular dependency between the app module and format modules:
- **app → format modules**: App needs format implementations for rendering/editing
- **format modules → app**: Formats extend base classes in app module

This prevents separating format modules as independent Kotlin modules. **Solution: Migrate format code in-place within the app module.**

### Migration Statistics (Plaintext Format)
- ✅ Files migrated: 6 (all to Kotlin)
- ✅ Java compilation errors fixed: 210+
- ✅ Files modified for interop: 60+
- ✅ Build status: **BUILD SUCCESSFUL**
- ✅ APK generated: Yes

## Step-by-Step Migration Process

### Phase 1: Preparation

#### 1.1 Identify Format Files
For each format (e.g., `plaintext`, `csv`, `markdown`), locate these files in the format module:
```bash
format-[name]/src/main/java/digital/vasic/yole/format/[name]/
├── [Name]TextConverter.java
├── [Name]SyntaxHighlighter.java
├── [Name]ActionButtons.java
└── [additional helper classes]
```

#### 1.2 Check for Dependencies
```bash
cd format-[name]/src/main/java
grep -r "import " . | sort | uniq
```

Note any dependencies on:
- App module classes (base classes, models)
- Core module classes (FormatRegistry, etc.)
- Other format modules (rare, but check)

### Phase 2: Kotlin Conversion

#### 2.1 Convert Files to Kotlin
Use IntelliJ IDEA or Android Studio's automated conversion:
1. Open each Java file
2. Code → Convert Java File to Kotlin File
3. Review the converted code for:
   - Null safety (`?` operators)
   - Property vs getter/setter patterns
   - Collection initialization (`listOf`, `arrayOf` vs `Arrays.asList`)
   - Lambda syntax

#### 2.2 Common Conversion Patterns

**Collections:**
```kotlin
// Before (Java)
Arrays.asList(item1, item2, item3)

// After (Kotlin)
listOf(item1, item2, item3)
```

**Getters/Setters to Properties:**
```kotlin
// Before (Java)
private String type;
public String getType() { return type; }

// After (Kotlin)
var type: String? = null
```

**Null Safety:**
```kotlin
// Before (Java)
String ext = file.getName();

// After (Kotlin)
val ext = file?.name  // Use safe call operator
```

**Override Methods:**
```kotlin
// Before (Java)
@Override
public String getContentType() {
    return CONTENT_TYPE_HTML;
}

// After (Kotlin)
override fun getContentType(): String {
    return CONTENT_TYPE_HTML
}
```

#### 2.3 Fix Common Kotlin Issues

**Issue 1: Protected Field Access**
```kotlin
// Problem: Cannot access protected field from base class
val context = appSettings.context  // ERROR

// Solution: Use public getter method
val context = _appSettings.getAppContext()  // OK
```

**Issue 2: Lazy Property Initialization**
```kotlin
// Pattern for expensive computations
class ThemeValue {
    internal var color: String? = null
    private var m_colorInt: Int? = null

    val colorInt: Int
        get() {
            if (m_colorInt == null) {
                m_colorInt = Color.parseColor(color)
            }
            return m_colorInt!!
        }
}
```

**Issue 3: Java Interop Visibility**
```kotlin
// Make internal fields accessible from Java
internal var color: String? = null  // Not accessible from Java

// Add public getter for Java
val colorInt: Int  // Accessible from Java as getColorInt()
```

### Phase 3: In-Place Migration

#### 3.1 Move Kotlin Files to App Module
```bash
# Create destination directory
mkdir -p app/src/main/kotlin/digital/vasic/yole/format/[name]

# Move converted Kotlin files
mv format-[name]/src/main/java/digital/vasic/yole/format/[name]/*.kt \
   app/src/main/kotlin/digital/vasic/yole/format/[name]/
```

#### 3.2 Remove Old Java Files
```bash
# Remove Java files from app module (if duplicates exist)
rm app/src/main/java/digital/vasic/yole/format/[name]/*.java

# Keep Java files in format module (for now) or remove if no longer needed
```

#### 3.3 Update Kotlin Source Sets (if needed)
Ensure `app/build.gradle` has Kotlin source directories configured:
```gradle
android {
    sourceSets {
        main.java.srcDirs += 'src/main/kotlin'
    }
}
```

### Phase 4: Fix Compilation Errors

#### 4.1 Build and Identify Errors
```bash
./gradlew assembleFlavorDefaultDebug 2>&1 | tee build.log
```

#### 4.2 Common Error Patterns

**Error Type 1: Context Access**
```
error: Cannot access 'val context: Context': it is protected
```
**Fix**: Add public accessor in AppSettings.java:
```java
public Context getAppContext() {
    return getContextCompat();
}
```

**Error Type 2: Method Signature Mismatch**
```
error: 'contentType' overrides nothing
```
**Fix**: Change from property to method:
```kotlin
// Wrong
override val contentType: String get() = CONTENT_TYPE_HTML

// Correct
override fun getContentType(): String = CONTENT_TYPE_HTML
```

**Error Type 3: Type Mismatch**
```
error: Argument type mismatch: actual type is 'File?', but 'File' was expected
```
**Fix**: Use nullable-aware method overload:
```kotlin
// Before
GsFileUtils.getFilenameExtension(file)

// After
GsFileUtils.getFilenameExtension(file?.name)
```

#### 4.3 Java Interop Fixes

After commons migration, many utility methods moved to specialized classes. Update calls:

**Method Routing Map:**
```java
// Intent operations
_cu.openWebpageInExternalBrowser(...) → GsIntentUtils.openWebpageInExternalBrowser(...)
_cu.shareText(...) → GsIntentUtils.shareText(...)
_cu.animateToActivity(...) → GsIntentUtils.animateToActivity(...)
_cu.createCalendarAppointment(...) → GsIntentUtils.createCalendarAppointment(...)

// Resource operations
_cu.bcstr(...) → GsResourceUtils.bcstr(...)
_cu.bcint(...) → GsResourceUtils.bcint(...)
_cu.getAppVersionName(...) → GsResourceUtils.getAppVersionName(...)
_cu.formatDateTime(...) → GsResourceUtils.formatDateTime(...)
_cu.htmlToSpanned(...) → GsResourceUtils.htmlToSpanned(...)
_cu.rcolor(...) → GsResourceUtils.rcolor(...)

// UI operations
_cu.showDialogWithHtmlTextView(...) → GsUiUtils.showDialogWithHtmlTextView(...)
_cu.showSoftKeyboard(...) → GsUiUtils.showSoftKeyboard(...)
_cu.setClipboard(...) → GsUiUtils.setClipboard(...)
_cu.blinkView(...) → GsUiUtils.blinkView(...)
GsContextUtils.fadeInOut(...) → GsUiUtils.fadeInOut(...)

// Storage operations
_cu.getAppDataPublicDirs(...) → GsStorageUtils.getAppDataPublicDirs(...)
_cu.viewFileInOtherApp(...) → GsStorageUtils.viewFileInOtherApp(...)
_cu.shareStream(...) → GsStorageUtils.shareStream(...)
_cu.writeFile(...) → GsStorageUtils.writeFile(...)
_cu.getDocumentFile(...) → GsStorageUtils.getDocumentFile(...)
_cu.canWriteFile(...) → GsStorageUtils.canWriteFile(...)
_cu.extractResultFromActivityResult(...) → GsStorageUtils.extractResultFromActivityResult(...)

// Image operations
_cu.tintDrawable(...) → GsImageUtils.tintDrawable(...)
_cu.shareImage(...) → GsImageUtils.shareImage(...)
_cu.getBitmapFromWebView(...) → GsImageUtils.getBitmapFromWebView(...)

// File operations
cu.getMimeType(context, file) → GsFileUtils.getMimeType(file)  // Note: removed Context param
```

**Add Required Imports:**
```java
import digital.vasic.opoc.util.GsIntentUtils;
import digital.vasic.opoc.util.GsResourceUtils;
import digital.vasic.opoc.util.GsUiUtils;
import digital.vasic.opoc.util.GsStorageUtils;
import digital.vasic.opoc.util.GsImageUtils;
import digital.vasic.opoc.util.GsFileUtils;
```

### Phase 5: Fix DEX Merging Errors

#### 5.1 Identify Duplicate Classes
If you see errors like:
```
ERROR: Type [ClassName] is defined multiple times
```

#### 5.2 Locate Duplicates
```bash
find . -name "ClassName.java" -type f
```

#### 5.3 Determine Correct Location
- **Core module**: Shared utilities, base classes, models
- **App module**: Application-specific code, format implementations

#### 5.4 Remove Duplicates
```bash
# If class belongs in core, remove from app
rm app/thirdparty/java/path/to/ClassName.java

# Clean and rebuild
./gradlew clean assembleFlavorDefaultDebug
```

### Phase 6: Verification

#### 6.1 Build Success
```bash
./gradlew assembleFlavorDefaultDebug
# Expected: BUILD SUCCESSFUL
```

#### 6.2 Check APK
```bash
ls -lh app/build/outputs/apk/flavorDefault/debug/*.apk
```

#### 6.3 Test Format Functionality
1. Install APK on device/emulator
2. Open a file with the migrated format
3. Verify:
   - Syntax highlighting works
   - HTML preview renders correctly
   - Action buttons function properly
   - File editing and saving works

## Remaining Formats to Migrate

Using the same in-place pattern, migrate these formats:

### Text Formats (7)
1. ✅ **plaintext** - COMPLETED
2. ⏳ **markdown** - format-markdown
3. ⏳ **todotxt** - format-todotxt
4. ⏳ **wikitext** - format-wikitext
5. ⏳ **orgmode** - format-orgmode
6. ⏳ **asciidoc** - format-asciidoc
7. ⏳ **textile** - format-textile

### Structured Formats (4)
8. ⏳ **csv** - format-csv
9. ⏳ **keyvalue** - format-keyvalue
10. ⏳ **taskpaper** - format-taskpaper
11. ⏳ **jupyter** - format-jupyter

### Academic/Technical Formats (4)
12. ⏳ **latex** - format-latex
13. ⏳ **restructuredtext** - format-restructuredtext
14. ⏳ **rmarkdown** - format-rmarkdown
15. ⏳ **tiddlywiki** - format-tiddlywiki

### Other (2)
16. ⏳ **creole** - format-creole
17. ⏳ **binary** - format-binary (special case)

## Migration Checklist Template

For each format, use this checklist:

- [ ] Identify all Java files in format-[name] module
- [ ] Convert Java files to Kotlin using IDE
- [ ] Review Kotlin conversion for correctness
- [ ] Move Kotlin files to app/src/main/kotlin/digital/vasic/yole/format/[name]/
- [ ] Remove duplicate Java files from app module
- [ ] Build and capture errors: `./gradlew assembleFlavorDefaultDebug`
- [ ] Fix context access issues (add getAppContext() if needed)
- [ ] Fix method signature mismatches
- [ ] Fix type mismatches with nullable types
- [ ] Update method routing to specialized utility classes
- [ ] Add required imports (GsIntentUtils, GsResourceUtils, etc.)
- [ ] Fix any DEX duplicate class errors
- [ ] Clean and rebuild: `./gradlew clean assembleFlavorDefaultDebug`
- [ ] Verify BUILD SUCCESSFUL
- [ ] Test format functionality in APK

## Common Pitfalls

### 1. Protected Field Access
**Problem**: Kotlin can't access protected fields from Java base classes
**Solution**: Add public getter methods in Java base class

### 2. Override Signature Mismatch
**Problem**: Property override doesn't match method signature
**Solution**: Use `override fun` instead of `override val`

### 3. Null Safety
**Problem**: Non-null type expected but nullable provided
**Solution**: Use safe call operator `?.` or null-safe method overloads

### 4. Method Routing After Commons Migration
**Problem**: Methods not found on `_cu` variable
**Solution**: Route to specialized utility classes (see Method Routing Map)

### 5. Missing Imports
**Problem**: Symbol not found after method routing
**Solution**: Add imports for specialized utility classes

### 6. Duplicate Classes
**Problem**: DEX merger finds same class in multiple modules
**Solution**: Remove duplicate, keep only in correct module (usually core)

## Tips for Success

1. **Migrate one format at a time** - Don't try to migrate multiple formats simultaneously
2. **Build frequently** - Catch errors early by building after each significant change
3. **Use search** - Use `grep` or IDE search to find all usages when updating method calls
4. **Test incrementally** - Verify each format works before moving to the next
5. **Document issues** - Keep notes on format-specific issues for future reference
6. **Commit often** - Make small, focused commits for easy rollback if needed

## Performance Notes

- **Build time**: ~12-15 seconds for clean build
- **Incremental build**: ~3-5 seconds
- **APK size**: No significant change from Java version

## References

- **Successful migration**: Plaintext format (6 files, BUILD SUCCESSFUL)
- **Architecture docs**: `ARCHITECTURE.md`, `CORE_MIGRATION_ANALYSIS.md`
- **Circular dependency**: Documented in `CORE_MIGRATION_ANALYSIS.md`
- **Kotlin style guide**: Kotlin official style guide
- **Android Java interop**: https://kotlinlang.org/docs/java-interop.html

## Support

If you encounter issues:
1. Review this guide's Common Pitfalls section
2. Check the plaintext migration commits for examples
3. Search for similar error messages in this guide
4. Use the Method Routing Map for utility class changes

---

**Last Updated**: October 26, 2025
**Migration Status**: 1 of 18 formats complete (5.6%)
**Build Status**: BUILD SUCCESSFUL ✅
