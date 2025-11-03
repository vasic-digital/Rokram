/*#######################################################
 *
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * Main Compose UI for Yole Android App
 *
 *########################################################*/

package digital.vasic.yole.android.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import digital.vasic.yole.format.FormatRegistry
import java.io.File

enum class Screen {
    FILE_BROWSER,
    EDITOR,
    PREVIEW,
    SETTINGS
}

@Composable
fun YoleApp() {
    val systemInDarkTheme = isSystemInDarkTheme()
    val colorScheme = if (systemInDarkTheme) darkColorScheme() else lightColorScheme()

    MaterialTheme(colorScheme = colorScheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            MainScreen()
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var currentScreen by remember { mutableStateOf(Screen.FILE_BROWSER) }
    var selectedFile by remember { mutableStateOf<String?>(null) }
    var fileContent by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Yole") },
                actions = {
                    // Navigation buttons with icons
                    TextButton(onClick = { currentScreen = Screen.FILE_BROWSER }) {
                        Text(if (currentScreen == Screen.FILE_BROWSER) "📁 Files" else "Files")
                    }
                    TextButton(onClick = { currentScreen = Screen.EDITOR }) {
                        Text(if (currentScreen == Screen.EDITOR) "✏️ Edit" else "Edit")
                    }
                    TextButton(onClick = { currentScreen = Screen.PREVIEW }) {
                        Text(if (currentScreen == Screen.PREVIEW) "👁️ Preview" else "Preview")
                    }
                    TextButton(onClick = { currentScreen = Screen.SETTINGS }) {
                        Text(if (currentScreen == Screen.SETTINGS) "⚙️ Settings" else "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (currentScreen) {
                Screen.FILE_BROWSER -> FileBrowserScreen(
                    onFileSelected = { file, content ->
                        selectedFile = file
                        fileContent = content
                        currentScreen = Screen.EDITOR
                    }
                )
                Screen.EDITOR -> EditorScreen(
                    fileName = selectedFile ?: "Untitled",
                    content = fileContent,
                    onContentChanged = { fileContent = it }
                )
                Screen.PREVIEW -> PreviewScreen(
                    fileName = selectedFile ?: "Untitled",
                    content = fileContent
                )
                Screen.SETTINGS -> SettingsScreen()
            }
        }
    }
}

@Composable
fun FileBrowserScreen(onFileSelected: (String, String) -> Unit) {
    val context = LocalContext.current
    var currentDirectory by remember { mutableStateOf<File?>(null) }
    var files by remember { mutableStateOf<List<File>>(emptyList()) }

    // Initialize with documents directory
    LaunchedEffect(Unit) {
        val docsDir = File(context.getExternalFilesDir(null)?.parentFile, "Documents")
        if (docsDir.exists()) {
            currentDirectory = docsDir
            files = docsDir.listFiles()?.toList() ?: emptyList()
        } else {
            // Fallback to app's private directory
            currentDirectory = context.filesDir
            files = context.filesDir.listFiles()?.toList() ?: emptyList()
        }
    }

    val directoryPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            val documentFile = DocumentFile.fromTreeUri(context, it)
            // For now, just show a message - full implementation would require more work
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "File Browser",
                style = MaterialTheme.typography.headlineMedium
            )

            TextButton(onClick = { directoryPicker.launch(null) }) {
                Text("📂 Open Folder")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Current directory path
        currentDirectory?.let { dir ->
            Text(
                text = "📁 ${dir.absolutePath}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // File list
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(files) { file ->
                val isDirectory = file.isDirectory
                val fileName = file.name
                val fileSize = if (file.isFile) "${file.length()} bytes" else ""

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    onClick = {
                        if (isDirectory) {
                            // Navigate into directory
                            currentDirectory = file
                            files = file.listFiles()?.toList() ?: emptyList()
                        } else {
                            // Try to read file content
                            try {
                                val content = file.readText()
                                onFileSelected(fileName, content)
                            } catch (e: Exception) {
                                // If reading fails, show empty content
                                onFileSelected(fileName, "")
                            }
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(horizontalArrangement = Arrangement.Start) {
                            Text(
                                text = if (isDirectory) "📁" else "📄",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = fileName,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        if (!isDirectory && fileSize.isNotEmpty()) {
                            Text(
                                text = fileSize,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick access buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedButton(
                onClick = {
                    // Go up one directory
                    currentDirectory?.parentFile?.let { parent ->
                        currentDirectory = parent
                        files = parent.listFiles()?.toList() ?: emptyList()
                    }
                },
                enabled = currentDirectory?.parentFile != null
            ) {
                Text("⬆️ Up")
            }

            OutlinedButton(onClick = {
                // Create new file
                onFileSelected("untitled.txt", "")
            }) {
                Text("➕ New File")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Supported formats: ${FormatRegistry.formats.size}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun EditorScreen(fileName: String, content: String, onContentChanged: (String) -> Unit) {
    var text by remember { mutableStateOf(content) }
    val format = remember(fileName) { FormatRegistry.detectByFilename(fileName) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Editing: $fileName",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Format: ${format.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row {
                TextButton(onClick = { /* TODO: Save */ }) {
                    Text("💾 Save")
                }
                TextButton(onClick = { /* TODO: Undo */ }) {
                    Text("↶ Undo")
                }
                TextButton(onClick = { /* TODO: Redo */ }) {
                    Text("↷ Redo")
                }
            }
        }

        // Action buttons for format-specific actions
        if (format.id == "markdown") {
            MarkdownActionButtons(
                onInsert = { action ->
                    val insertText = when (action) {
                        "bold" -> "**bold text**"
                        "italic" -> "*italic text*"
                        "header" -> "# Header"
                        "link" -> "[link text](url)"
                        "code" -> "`code`"
                        "list" -> "- List item"
                        else -> ""
                    }
                    text += insertText
                    onContentChanged(text)
                }
            )
        }

        // Editor
        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                onContentChanged(it)
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            placeholder = { Text("Start typing...") },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            )
        )
    }
}

@Composable
fun MarkdownActionButtons(onInsert: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ActionButton("B", "Bold") { onInsert("bold") }
        ActionButton("I", "Italic") { onInsert("italic") }
        ActionButton("H", "Header") { onInsert("header") }
        ActionButton("🔗", "Link") { onInsert("link") }
        ActionButton("</>", "Code") { onInsert("code") }
        ActionButton("•", "List") { onInsert("list") }
    }
}

@Composable
fun ActionButton(text: String, description: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.height(36.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun PreviewScreen(fileName: String, content: String) {
    val context = LocalContext.current
    val format = remember(fileName) { FormatRegistry.detectByFilename(fileName) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Preview: $fileName",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Format: ${format.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row {
                TextButton(onClick = { /* TODO: Export to PDF */ }) {
                    Text("📤 Export")
                }
            }
        }

        // Preview content
        val isDarkTheme = isSystemInDarkTheme()
        val htmlContent = remember(content, format, isDarkTheme) {
            generateHtmlPreview(content, format, isDarkTheme)
        }

        // For now, show as text. In a full implementation, this would be a WebView
        Text(
            text = htmlContent,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

fun generateHtmlPreview(content: String, format: digital.vasic.yole.format.TextFormat, isDark: Boolean): String {
    val themeClass = if (isDark) "dark-theme" else "light-theme"

    return """
    <!DOCTYPE html>
    <html>
    <head>
        <meta charset="utf-8">
        <style>
            body {
                font-family: 'Roboto', sans-serif;
                margin: 16px;
                line-height: 1.6;
            }
            .$themeClass {
                ${if (isDark) """
                    background-color: #121212;
                    color: #ffffff;
                """ else """
                    background-color: #ffffff;
                    color: #000000;
                """}
            }
            h1, h2, h3, h4, h5, h6 {
                color: ${if (isDark) "#bb86fc" else "#1976d2"};
                margin-top: 24px;
                margin-bottom: 16px;
            }
            code {
                background-color: ${if (isDark) "#333333" else "#f5f5f5"};
                padding: 2px 4px;
                border-radius: 4px;
                font-family: 'Courier New', monospace;
            }
            pre {
                background-color: ${if (isDark) "#333333" else "#f5f5f5"};
                padding: 16px;
                border-radius: 8px;
                overflow-x: auto;
            }
            blockquote {
                border-left: 4px solid ${if (isDark) "#bb86fc" else "#1976d2"};
                padding-left: 16px;
                margin-left: 0;
                color: ${if (isDark) "#cccccc" else "#666666"};
            }
        </style>
    </head>
    <body class="$themeClass">
        ${convertToHtml(content, format)}
    </body>
    </html>
    """.trimIndent()
}

fun convertToHtml(content: String, format: digital.vasic.yole.format.TextFormat): String {
    return when (format.id) {
        "markdown" -> convertMarkdownToHtml(content)
        "plaintext" -> "<pre>$content</pre>"
        "todotxt" -> convertTodoTxtToHtml(content)
        else -> "<pre>$content</pre>" // Fallback
    }
}

fun convertMarkdownToHtml(content: String): String {
    // Basic markdown conversion - in a real implementation, this would use a proper parser
    return content
        .replace(Regex("^### (.*)$", RegexOption.MULTILINE), "<h3>$1</h3>")
        .replace(Regex("^## (.*)$", RegexOption.MULTILINE), "<h2>$1</h2>")
        .replace(Regex("^# (.*)$", RegexOption.MULTILINE), "<h1>$1</h1>")
        .replace(Regex("\\*\\*(.*?)\\*\\*"), "<strong>$1</strong>")
        .replace(Regex("\\*(.*?)\\*"), "<em>$1</em>")
        .replace(Regex("`(.*?)`"), "<code>$1</code>")
        .replace(Regex("\n"), "<br>")
}

fun convertTodoTxtToHtml(content: String): String {
    val lines = content.lines()
    val html = lines.joinToString("<br>") { line ->
        if (line.trim().isEmpty()) {
            ""
        } else if (line.startsWith("x ")) {
            "<span style='text-decoration: line-through; color: #666;'>$line</span>"
        } else {
            "<span>$line</span>"
        }
    }
    return html
}

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    var themeMode by remember { mutableStateOf("system") }
    var showLineNumbers by remember { mutableStateOf(true) }
    var autoSave by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Theme settings
        Text(
            text = "Appearance",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = themeMode == "system",
                onClick = { themeMode = "system" }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("System theme (follows system setting)")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = themeMode == "light",
                onClick = { themeMode = "light" }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Light theme")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = themeMode == "dark",
                onClick = { themeMode = "dark" }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Dark theme")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Editor settings
        Text(
            text = "Editor",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Show line numbers")
            Switch(
                checked = showLineNumbers,
                onCheckedChange = { showLineNumbers = it }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Auto-save")
            Switch(
                checked = autoSave,
                onCheckedChange = { autoSave = it }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Format settings
        Text(
            text = "Formats",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Supported formats: ${FormatRegistry.formats.size}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        FormatRegistry.formats.take(5).forEach { format ->
            Text(
                text = "• ${format.name} (${format.extensions.joinToString(", ")})",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }

        if (FormatRegistry.formats.size > 5) {
            Text(
                text = "... and ${FormatRegistry.formats.size - 5} more",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // About
        Text(
            text = "About Yole",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Yole is a cross-platform text editor supporting 18+ markup formats including Markdown, todo.txt, CSV, and more.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Version: 2.15.1",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewYoleApp() {
    YoleApp()
}