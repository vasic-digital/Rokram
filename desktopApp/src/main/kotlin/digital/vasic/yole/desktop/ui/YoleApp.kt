/*#######################################################
 *
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * Main Compose UI for Yole Desktop App
 *
 *########################################################*/

package digital.vasic.yole.desktop.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import digital.vasic.yole.format.FormatRegistry

enum class Screen {
    FILE_BROWSER,
    EDITOR,
    PREVIEW
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YoleApp() {
    MaterialTheme {
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
                title = { Text("Yole Desktop") },
                actions = {
                    // Navigation buttons
                    TextButton(onClick = { currentScreen = Screen.FILE_BROWSER }) {
                        Text(if (currentScreen == Screen.FILE_BROWSER) "📁 Files" else "Files")
                    }
                    TextButton(onClick = { currentScreen = Screen.EDITOR }) {
                        Text(if (currentScreen == Screen.EDITOR) "✏️ Edit" else "Edit")
                    }
                    TextButton(onClick = { currentScreen = Screen.PREVIEW }) {
                        Text(if (currentScreen == Screen.PREVIEW) "👁️ Preview" else "Preview")
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
            }
        }
    }
}

@Composable
fun FileBrowserScreen(onFileSelected: (String, String) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "File Browser",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Sample files for demonstration - will be replaced with actual file browsing
        val sampleFiles = listOf(
            "sample.md" to "# Sample Markdown\n\nThis is a sample document.",
            "todo.txt" to "x 2018-01-01 Complete task @work\n2018-01-02 New task +project @home",
            "notes.txt" to "Plain text notes\n\n- Item 1\n- Item 2"
        )

        sampleFiles.forEach { (fileName, content) ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                onClick = { onFileSelected(fileName, content) }
            ) {
                Text(
                    text = fileName,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Supported formats: ${FormatRegistry.formats.size}",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Note: File system access will be implemented next",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun EditorScreen(fileName: String, content: String, onContentChanged: (String) -> Unit) {
    var text by remember { mutableStateOf(content) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Editing: $fileName",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                onContentChanged(it)
            },
            modifier = Modifier.fillMaxSize(),
            placeholder = { Text("Start typing...") }
        )
    }
}

@Composable
fun PreviewScreen(fileName: String, content: String) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Preview: $fileName",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Use format parsers for preview
        val previewContent = remember(content, fileName) {
            try {
                val format = FormatRegistry.detectByFilename(fileName)
                // For now, just show the content with format info
                "Format: ${format.name}\n\n$content"
            } catch (e: Exception) {
                content // Fallback to raw content
            }
        }

        Text(
            text = previewContent,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}