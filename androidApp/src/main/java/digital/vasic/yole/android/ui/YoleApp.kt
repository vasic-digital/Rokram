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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.Checkbox
import androidx.compose.ui.text.style.TextDecoration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import digital.vasic.yole.format.FormatRegistry
import java.io.File

enum class Screen {
    FILES,
    TODO,
    QUICKNOTE,
    MORE
}

enum class SubScreen {
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
    var currentScreen by remember { mutableStateOf(Screen.FILES) }
    var currentSubScreen by remember { mutableStateOf<SubScreen?>(null) }
    var selectedFile by remember { mutableStateOf<String?>(null) }
    var fileContent by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            when (currentSubScreen) {
                SubScreen.EDITOR -> EditorTopBar(
                    fileName = selectedFile ?: "Untitled",
                    onSaveClick = { /* TODO: Implement save */ },
                    onPreviewClick = { currentSubScreen = SubScreen.PREVIEW },
                    onBackClick = { currentSubScreen = null }
                )
                SubScreen.PREVIEW -> PreviewTopBar(
                    fileName = selectedFile ?: "Untitled",
                    onEditClick = { currentSubScreen = SubScreen.EDITOR },
                    onBackClick = { currentSubScreen = null }
                )
                SubScreen.SETTINGS -> SettingsTopBar(
                    onBackClick = { currentSubScreen = null }
                )
                null -> {
                    when (currentScreen) {
                        Screen.FILES -> FilesTopBar(
                            onSearchClick = { /* TODO: Implement search */ },
                            onSortClick = { /* TODO: Implement sort */ },
                            onMoreClick = { /* TODO: Implement more options */ }
                        )
                        Screen.TODO -> TodoTopBar(
                            onSearchClick = { /* TODO: Implement search */ },
                            onFilterClick = { /* TODO: Implement filter */ },
                            onMoreClick = { /* TODO: Implement more options */ }
                        )
                        Screen.QUICKNOTE -> QuickNoteTopBar(
                            onSaveClick = { /* TODO: Implement save */ },
                            onMoreClick = { /* TODO: Implement more options */ }
                        )
                        Screen.MORE -> MoreTopBar()
                    }
                }
                else -> {} // Exhaustive when
            }
        },
        bottomBar = {
            BottomNavigationBar(
                currentScreen = currentScreen,
                onScreenSelected = { currentScreen = it }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    when (currentScreen) {
                        Screen.FILES -> {
                            // Create new file
                            selectedFile = "untitled.txt"
                            fileContent = ""
                            currentSubScreen = SubScreen.EDITOR
                        }
                        Screen.TODO -> {
                            // Add new todo item
                            // TODO: Implement
                        }
                        Screen.QUICKNOTE -> {
                            // Quick note functionality
                            // TODO: Implement
                        }
                        Screen.MORE -> {
                            // More options
                            // TODO: Implement
                        }
                    }
                }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (currentSubScreen) {
                SubScreen.EDITOR -> EditorScreen(
                    fileName = selectedFile ?: "Untitled",
                    content = fileContent,
                    onContentChanged = { fileContent = it },
                    onBackClick = { currentSubScreen = null }
                )
                SubScreen.PREVIEW -> PreviewScreen(
                    fileName = selectedFile ?: "Untitled",
                    content = fileContent,
                    onBackClick = { currentSubScreen = null }
                )
                SubScreen.SETTINGS -> SettingsScreen(
                    onBackClick = { currentSubScreen = null }
                )
                null -> {
                    when (currentScreen) {
                        Screen.FILES -> FilesScreen(
                            onFileSelected = { file, content ->
                                selectedFile = file
                                fileContent = content
                                currentSubScreen = SubScreen.EDITOR
                            },
                            onSettingsClick = { currentSubScreen = SubScreen.SETTINGS }
                        )
                        Screen.TODO -> TodoScreen()
                        Screen.QUICKNOTE -> QuickNoteScreen()
                        Screen.MORE -> MoreScreen()
                    }
                }
                else -> {} // Exhaustive when
            }
        }
    }
}

@Composable
fun FileBrowserScreen(onFileSelected: (String, String) -> Unit, onSettingsClick: () -> Unit = {}) {
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
fun EditorScreen(fileName: String, content: String, onContentChanged: (String) -> Unit, onBackClick: () -> Unit = {}) {
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
fun PreviewScreen(fileName: String, content: String, onBackClick: () -> Unit = {}) {
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
fun SettingsScreen(onBackClick: () -> Unit = {}) {
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

// Bottom Navigation Bar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationBar(
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.List, contentDescription = "Files") },
            label = { Text("Files") },
            selected = currentScreen == Screen.FILES,
            onClick = { onScreenSelected(Screen.FILES) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.CheckCircle, contentDescription = "To-Do") },
            label = { Text("To-Do") },
            selected = currentScreen == Screen.TODO,
            onClick = { onScreenSelected(Screen.TODO) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Edit, contentDescription = "QuickNote") },
            label = { Text("QuickNote") },
            selected = currentScreen == Screen.QUICKNOTE,
            onClick = { onScreenSelected(Screen.QUICKNOTE) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Menu, contentDescription = "More") },
            label = { Text("More") },
            selected = currentScreen == Screen.MORE,
            onClick = { onScreenSelected(Screen.MORE) }
        )
    }
}

// Top Bars
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilesTopBar(
    onSearchClick: () -> Unit,
    onSortClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    TopAppBar(
        title = { Text("Files") },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Outlined.Search, contentDescription = "Search")
            }
            IconButton(onClick = onSortClick) {
                Icon(Icons.Filled.List, contentDescription = "Sort")
            }
            IconButton(onClick = onMoreClick) {
                Icon(Icons.Outlined.MoreVert, contentDescription = "More")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoTopBar(
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    TopAppBar(
        title = { Text("To-Do") },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Outlined.Search, contentDescription = "Search")
            }
            IconButton(onClick = onFilterClick) {
                Icon(Icons.Filled.Search, contentDescription = "Filter")
            }
            IconButton(onClick = onMoreClick) {
                Icon(Icons.Outlined.MoreVert, contentDescription = "More")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickNoteTopBar(
    onSaveClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    TopAppBar(
        title = { Text("QuickNote") },
        actions = {
            IconButton(onClick = onSaveClick) {
                Icon(Icons.Filled.Check, contentDescription = "Save")
            }
            IconButton(onClick = onMoreClick) {
                Icon(Icons.Outlined.MoreVert, contentDescription = "More")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreTopBar() {
    TopAppBar(
        title = { Text("More") }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorTopBar(
    fileName: String,
    onSaveClick: () -> Unit,
    onPreviewClick: () -> Unit,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = { Text(fileName, maxLines = 1) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(onClick = onSaveClick) {
                Icon(Icons.Filled.Check, contentDescription = "Save")
            }
            IconButton(onClick = onPreviewClick) {
                Icon(Icons.Filled.Info, contentDescription = "Preview")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewTopBar(
    fileName: String,
    onEditClick: () -> Unit,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = { Text("$fileName (Preview)", maxLines = 1) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(onClick = onEditClick) {
                Icon(Icons.Outlined.Edit, contentDescription = "Edit")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text("Settings") },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
            }
        }
    )
}

// Screen Composables
@Composable
fun FilesScreen(
    onFileSelected: (String, String) -> Unit,
    onSettingsClick: () -> Unit
) {
    FileBrowserScreen(
        onFileSelected = onFileSelected,
        onSettingsClick = onSettingsClick
    )
}

@Composable
fun TodoScreen() {
    var todoItems by remember { mutableStateOf(listOf<TodoItem>()) }
    var newTodoText by remember { mutableStateOf("") }
    var showCompleted by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Filter/Sort Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "To-Do List",
                style = MaterialTheme.typography.headlineMedium
            )

            Row {
                TextButton(onClick = { showCompleted = !showCompleted }) {
                    Text(if (showCompleted) "Hide Done" else "Show Done")
                }
            }
        }

        // Add new todo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newTodoText,
                onValueChange = { newTodoText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Add new todo...") },
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (newTodoText.isNotBlank()) {
                        val newItem = TodoItem(
                            id = System.currentTimeMillis().toString(),
                            text = newTodoText,
                            completed = false,
                            priority = null,
                            projects = emptyList(),
                            contexts = emptyList(),
                            dueDate = null
                        )
                        todoItems = todoItems + newItem
                        newTodoText = ""
                    }
                }
            ) {
                Text("Add")
            }
        }

        // Todo list
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(todoItems.filter { showCompleted || !it.completed }) { item ->
                TodoItemRow(
                    item = item,
                    onToggleComplete = { completed ->
                        todoItems = todoItems.map {
                            if (it.id == item.id) it.copy(completed = completed) else it
                        }
                    },
                    onDelete = {
                        todoItems = todoItems.filter { it.id != item.id }
                    }
                )
            }
        }
    }
}

data class TodoItem(
    val id: String,
    val text: String,
    val completed: Boolean,
    val priority: Char?,
    val projects: List<String>,
    val contexts: List<String>,
    val dueDate: String?
)

@Composable
fun TodoItemRow(
    item: TodoItem,
    onToggleComplete: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.completed,
                onCheckedChange = onToggleComplete
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.text,
                    style = if (item.completed) {
                        MaterialTheme.typography.bodyLarge.copy(
                            textDecoration = TextDecoration.LineThrough
                        )
                    } else {
                        MaterialTheme.typography.bodyLarge
                    },
                    color = if (item.completed) {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )

                // Show projects and contexts
                val tags = (item.projects.map { "+$it" } + item.contexts.map { "@$it" }).joinToString(" ")
                if (tags.isNotEmpty()) {
                    Text(
                        text = tags,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun QuickNoteScreen() {
    var noteContent by remember { mutableStateOf("") }
    var isPreviewMode by remember { mutableStateOf(false) }
    val isDarkTheme = isSystemInDarkTheme()

    Column(modifier = Modifier.fillMaxSize()) {
        // Quick actions toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "QuickNote",
                style = MaterialTheme.typography.headlineMedium
            )

            Row {
                TextButton(onClick = { isPreviewMode = !isPreviewMode }) {
                    Text(if (isPreviewMode) "Edit" else "Preview")
                }
                TextButton(onClick = { /* TODO: Save */ }) {
                    Text("Save")
                }
            }
        }

        if (isPreviewMode) {
            // Preview mode
            val format = remember { FormatRegistry.detectByFilename("quicknote.md") }
            val htmlContent = remember(noteContent, format, isDarkTheme) {
                generateHtmlPreview(noteContent, format, isDarkTheme)
            }

            Text(
                text = htmlContent,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            // Edit mode
            OutlinedTextField(
                value = noteContent,
                onValueChange = { noteContent = it },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("Start writing your quick note...") }
            )
        }
    }
}

@Composable
fun MoreScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "More Options",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Settings option
        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = { /* TODO: Navigate to settings */ }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Settings, contentDescription = "Settings")
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Settings", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        "Configure app preferences and behavior",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // File browser option
        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = { /* TODO: Open file browser */ }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.List, contentDescription = "File Browser")
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("File Browser", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        "Browse and manage your files",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Search option
        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = { /* TODO: Open search */ }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Search, contentDescription = "Search")
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Search", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        "Search through your notes and files",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Backup/Restore option
        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = { /* TODO: Open backup/restore */ }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Check, contentDescription = "Backup")
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Backup & Restore", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        "Backup your data or restore from backup",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // About option
        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = { /* TODO: Show about dialog */ }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Info, contentDescription = "About")
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("About Yole", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        "Version 2.15.1 - Text editor for Android, Desktop, iOS & Web",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewYoleApp() {
    YoleApp()
}