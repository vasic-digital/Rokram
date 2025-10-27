# Complete Yole Kotlin Migration Plan

**Date**: 2025-10-26
**Scope**: Migrate ALL remaining Java code to Kotlin
**Target**: 100% Kotlin codebase
**Strategy**: Systematic, module-by-module approach

---

## 📋 Executive Summary

**Total Scope**: 18 format modules + app module core files
**Estimated Lines**: ~50,000+ lines of Java code
**Current Progress**: 21/21 commons files (100%), core analysis complete
**Remaining**: 18 format modules + app infrastructure

---

## 🎯 Migration Strategy

### Phase 1: Format Modules (Priority)
Migrate all 18 format modules from Java to Kotlin while maintaining Java interoperability.

### Phase 2: App Module Infrastructure (Optional)
In-place Kotlin migration of app base classes and utilities.

### Phase 3: Verification & Testing
Comprehensive testing, build verification, documentation.

---

## 📊 Format Module Inventory

### Tier 1: Simple Formats (3-4 files each, ~500-1000 lines)
**Priority**: HIGH - Quick wins, build confidence

1. **format-plaintext** (~600 lines)
   - PlaintextTextConverter.java
   - PlaintextSyntaxHighlighter.java
   - PlaintextActionButtons.java

2. **format-csv** (~400 lines)
   - CsvTextConverter.java
   - CsvSyntaxHighlighter.java

3. **format-keyvalue** (~500 lines)
   - KeyValueTextConverter.java
   - KeyValueSyntaxHighlighter.java

**Subtotal**: ~1,500 lines across 3 formats

---

### Tier 2: Medium Formats (3-4 files each, ~1000-2000 lines)
**Priority**: MEDIUM - Standard complexity

4. **format-todotxt** (~1,500 lines)
   - TodoTxtTextConverter.java
   - TodoTxtSyntaxHighlighter.java
   - TodoTxtActionButtons.java
   - TodoTxtAutoTextFormatter.java

5. **format-wikitext** (~1,200 lines)
   - WikitextTextConverter.java
   - WikitextSyntaxHighlighter.java
   - WikitextActionButtons.java
   - WikitextReplacePatternGenerator.java

6. **format-asciidoc** (~1,100 lines)
   - AsciidocTextConverter.java
   - AsciidocSyntaxHighlighter.java
   - AsciidocActionButtons.java

7. **format-orgmode** (~1,300 lines)
   - OrgmodeTextConverter.java
   - OrgmodeSyntaxHighlighter.java
   - OrgmodeActionButtons.java
   - OrgmodeReplacePatternGenerator.java

**Subtotal**: ~5,100 lines across 4 formats

---

### Tier 3: Complex Formats (4+ files, 2000+ lines)
**Priority**: LOW - Highest complexity

8. **format-markdown** (~2,500 lines)
   - MarkdownTextConverter.java (30,428 bytes - largest!)
   - MarkdownSyntaxHighlighter.java
   - MarkdownActionButtons.java
   - MarkdownReplacePatternGenerator.java

9. **format-latex** (~1,800 lines)
   - LaTeXTextConverter.java
   - LaTeXSyntaxHighlighter.java
   - LaTeXActionButtons.java

10. **format-restructuredtext** (~1,500 lines)
    - ReStructuredTextTextConverter.java
    - ReStructuredTextSyntaxHighlighter.java
    - ReStructuredTextActionButtons.java

**Subtotal**: ~5,800 lines across 3 formats

---

### Tier 4: Specialized Formats (Various complexity)
**Priority**: MEDIUM - Niche but important

11. **format-taskpaper** (~800 lines)
    - TaskpaperTextConverter.java
    - TaskpaperSyntaxHighlighter.java
    - TaskpaperActionButtons.java

12. **format-textile** (~900 lines)
    - TextileTextConverter.java
    - TextileSyntaxHighlighter.java

13. **format-creole** (~700 lines)
    - CreoleTextConverter.java
    - CreoleSyntaxHighlighter.java

14. **format-tiddlywiki** (~800 lines)
    - TiddlyWikiTextConverter.java
    - TiddlyWikiSyntaxHighlighter.java

15. **format-jupyter** (~1,000 lines)
    - JupyterTextConverter.java
    - JupyterSyntaxHighlighter.java

16. **format-rmarkdown** (~900 lines)
    - RMarkdownTextConverter.java
    - RMarkdownSyntaxHighlighter.java

**Subtotal**: ~5,100 lines across 6 formats

---

### Special Cases

17. **format-binary** (EmbedBinary)
    - Located in app/src/.../format/binary/
    - EmbedBinaryTextConverter.java

18. **format-general** (shared utilities)
    - Located in app/src/.../format/general/
    - ColorUnderlineSpan.java
    - Various helpers

---

## 📈 Format Module Statistics

| Tier | Formats | Estimated Lines | Complexity | Priority |
|------|---------|-----------------|------------|----------|
| Tier 1 | 3 | ~1,500 | Low | HIGH ✓ |
| Tier 2 | 4 | ~5,100 | Medium | MEDIUM |
| Tier 3 | 3 | ~5,800 | High | LOW |
| Tier 4 | 6 | ~5,100 | Mixed | MEDIUM |
| Special | 2 | ~500 | Varies | LOW |
| **TOTAL** | **18** | **~18,000** | - | - |

---

## 🏗️ App Module Inventory

### Base Classes (in app module)
1. **FormatRegistry.java** (245 lines) - Format registration
2. **TextConverterBase.java** (213 lines) - HTML conversion base
3. **ActionButtonBase.java** (1,037 lines) - Action button base
4. **SyntaxHighlighterBase.java** (683 lines) - Highlighting base
5. **Document.java** (391 lines) - Document model
6. **AppSettings.java** (1,115 lines) - Settings manager

**Subtotal**: ~3,684 lines

### Activities & Fragments (~5,000 lines)
- MainActivity.java
- DocumentActivity.java
- DocumentEditAndViewFragment.java
- DocumentShareIntoFragment.java
- SettingsActivity.java

### Utilities & Helpers (~3,000 lines)
- YoleContextUtils.java
- TextViewUtils.java
- Various dialog classes
- File browser components

### Frontend Components (~4,000 lines)
- HighlightingEditor.java
- AutoTextFormatter.java
- ListHandler.java
- Search components

**App Module Total**: ~15,000+ lines

---

## 🎯 Migration Order (Recommended)

### Week 1: Quick Wins (Tier 1)
**Days 1-2**: format-plaintext, format-csv, format-keyvalue
**Goal**: Build momentum, establish patterns
**Lines**: ~1,500

### Week 2: Standard Complexity (Tier 2, Part 1)
**Days 3-4**: format-todotxt, format-wikitext
**Goal**: Handle medium complexity
**Lines**: ~2,700

### Week 3: Standard Complexity (Tier 2, Part 2)
**Days 5-6**: format-asciidoc, format-orgmode
**Goal**: Complete tier 2
**Lines**: ~2,400

### Week 4: Specialized Formats (Tier 4)
**Days 7-9**: format-taskpaper, format-textile, format-creole, format-tiddlywiki, format-jupyter, format-rmarkdown
**Goal**: Knock out niche formats
**Lines**: ~5,100

### Week 5: Complex Formats (Tier 3)
**Days 10-12**: format-markdown, format-latex, format-restructuredtext
**Goal**: Tackle largest formats
**Lines**: ~5,800

### Week 6-7: App Module (Optional)
**Days 13-20**: App base classes, utilities, activities
**Goal**: Complete 100% Kotlin migration
**Lines**: ~15,000

---

## 📋 Per-Format Migration Checklist

For each format module:

### Pre-Migration
- [ ] Read all Java files in format
- [ ] Identify dependencies
- [ ] Check for special patterns (regex, HTML templates)
- [ ] Review existing tests

### Migration
- [ ] Configure Kotlin in format module build.gradle
- [ ] Migrate TextConverter (HTML generation)
- [ ] Migrate SyntaxHighlighter (regex patterns)
- [ ] Migrate ActionButtons (toolbar actions)
- [ ] Migrate helper classes (if any)

### Post-Migration
- [ ] Add @JvmStatic for Java interoperability
- [ ] Convert to idiomatic Kotlin (null safety, data classes)
- [ ] Update imports in dependent files
- [ ] Build format module successfully
- [ ] Run unit tests
- [ ] Test format in app (manual verification)
- [ ] Commit changes

### Verification
- [ ] ./gradlew :format-[name]:build succeeds
- [ ] ./gradlew :format-[name]:test passes
- [ ] ./gradlew assembleFlavorDefaultDebug succeeds
- [ ] App launches and format works

---

## 🛠️ Build Configuration Pattern

Each format module needs:

```gradle
apply plugin: 'com.android.library'
apply plugin: 'kotlin-android'

android {
    // ... existing config ...

    kotlinOptions {
        jvmTarget = '11'
        freeCompilerArgs += [
            '-Xjvm-default=all',
            '-opt-in=kotlin.RequiresOptIn'
        ]
    }
}

dependencies {
    // Add Kotlin stdlib
    implementation "org.jetbrains.kotlin:kotlin-stdlib:${version_plugin_kotlin}"

    // ... existing dependencies ...
}
```

---

## 🎯 Success Criteria

### Format Modules
- ✅ All 18 format modules migrated to Kotlin
- ✅ All modules build successfully
- ✅ All unit tests passing
- ✅ App builds and runs with all formats
- ✅ Manual testing confirms all formats work

### App Module
- ✅ Base classes migrated to Kotlin (in-place)
- ✅ Key utilities migrated
- ✅ Activities/fragments migrated
- ✅ Full app functionality maintained
- ✅ All tests passing

### Overall
- ✅ 100% Kotlin codebase achieved
- ✅ Zero breaking changes
- ✅ Documentation updated
- ✅ Clean git history with logical commits

---

## 📊 Progress Tracking

| Module | Status | Files | Lines | Completion |
|--------|--------|-------|-------|------------|
| commons | ✅ Complete | 21 | ~8,400 | 100% |
| core | ✅ Analyzed | 0 | 0 | N/A |
| format-plaintext | ⏳ Pending | 3 | ~600 | 0% |
| format-csv | ⏳ Pending | 2 | ~400 | 0% |
| format-keyvalue | ⏳ Pending | 2 | ~500 | 0% |
| format-todotxt | ⏳ Pending | 4 | ~1,500 | 0% |
| format-wikitext | ⏳ Pending | 4 | ~1,200 | 0% |
| format-asciidoc | ⏳ Pending | 3 | ~1,100 | 0% |
| format-orgmode | ⏳ Pending | 4 | ~1,300 | 0% |
| format-markdown | ⏳ Pending | 4 | ~2,500 | 0% |
| format-latex | ⏳ Pending | 3 | ~1,800 | 0% |
| format-restructuredtext | ⏳ Pending | 3 | ~1,500 | 0% |
| format-taskpaper | ⏳ Pending | 3 | ~800 | 0% |
| format-textile | ⏳ Pending | 2 | ~900 | 0% |
| format-creole | ⏳ Pending | 2 | ~700 | 0% |
| format-tiddlywiki | ⏳ Pending | 2 | ~800 | 0% |
| format-jupyter | ⏳ Pending | 2 | ~1,000 | 0% |
| format-rmarkdown | ⏳ Pending | 2 | ~900 | 0% |
| app (base classes) | ⏳ Pending | 6 | ~3,684 | 0% |
| app (activities) | ⏳ Pending | ~10 | ~5,000 | 0% |
| app (utilities) | ⏳ Pending | ~15 | ~3,000 | 0% |
| app (frontend) | ⏳ Pending | ~8 | ~4,000 | 0% |
| **TOTAL** | - | **~100+** | **~35,000+** | **~20%** |

---

## 🚀 Let's Begin!

**Starting with**: Tier 1 formats (quick wins)
**First target**: format-plaintext (simplest format)
**Expected duration**: 15-20 minutes per simple format
**Total estimated time**: 40-60 hours for complete migration

---

**Maintained By**: Claude (AI Assistant) + Milos Vasic
**Status**: Ready to execute
**Next Step**: Begin format-plaintext migration
