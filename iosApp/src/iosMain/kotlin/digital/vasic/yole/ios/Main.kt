/*#######################################################
 *
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * Yole iOS Application
 * Native iOS app with Compose Multiplatform
 *
 *########################################################*/

package digital.vasic.yole.ios

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import digital.vasic.yole.model.Document
import digital.vasic.yole.format.FormatRegistry
import kotlinx.coroutines.*

/**
 * Main entry point for Yole iOS Application
 */
@Composable
fun YoleIOSApp() {
    MaterialTheme {
        YoleAppContent()
    }
}

/**
 * Main Yole iOS App Content
 */
@Composable
fun YoleAppContent() {
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Documents) }
    var documents by remember { mutableStateOf(emptyList<Document>()) }
    
    Scaffold(
        topBar = {
            YoleTopAppBar(
                currentScreen = currentScreen,
                onScreenChange = { screen -> currentScreen = screen }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (currentScreen) {
                AppScreen.Documents -> DocumentsScreen(
                    documents = documents,
                    onDocumentSelected = { /* TODO */ },
                    onCreateDocument = { /* TODO */ }
                )
                AppScreen.Editor -> EditorScreen()
                AppScreen.Settings -> SettingsScreen()
            }
        }
    }
}

/**
 * Top App Bar for Yole iOS
 */
@Composable
fun YoleTopAppBar(
    currentScreen: AppScreen,
    onScreenChange: (AppScreen) -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Yole",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        actions = {
            Row {
                IconButton(
                    onClick = { onScreenChange(AppScreen.Documents) },
                    modifier = Modifier
                ) {
                    Icon(
                        Icons.Default.Folder,
                        contentDescription = "Documents",
                        tint = if (currentScreen == AppScreen.Documents) 
                            MaterialTheme.colorScheme.onPrimary 
                        else 
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    )
                }
                
                IconButton(
                    onClick = { onScreenChange(AppScreen.Editor) },
                    modifier = Modifier
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editor",
                        tint = if (currentScreen == AppScreen.Editor) 
                            MaterialTheme.colorScheme.onPrimary 
                        else 
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    )
                }
                
                IconButton(
                    onClick = { onScreenChange(AppScreen.Settings) },
                    modifier = Modifier
                ) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = if (currentScreen == AppScreen.Settings) 
                            MaterialTheme.colorScheme.onPrimary 
                        else 
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    )
                }
            }
        }
    )
}

/**
 * Documents Screen
 */
@Composable
fun DocumentsScreen(
    documents: List<Document>,
    onDocumentSelected: (Document) -> Unit,
    onCreateDocument: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Text(
            text = "Documents",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )
        
        // Create New Document Button
        Button(
            onClick = onCreateDocument,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(Icons.Default.Add, contentDescription = "Create")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create New Document")
        }
        
        // Documents List
        if (documents.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.FolderOpen,
                    contentDescription = "No Documents",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No documents yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                Text(
                    text = "Create your first document to get started",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(documents) { document ->
                    DocumentItem(
                        document = document,
                        onClick = { onDocumentSelected(document) }
                    )
                }
            }
        }
    }
}

/**
 * Document List Item
 */
@Composable
fun DocumentItem(
    document: Document,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = document.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${document.format.name} • ${document.modifiedAt}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
    }
}

/**
 * Editor Screen
 */
@Composable
fun EditorScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Editor",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Document editor will be implemented here",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

/**
 * Settings Screen
 */
@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Application settings will be implemented here",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

/**
 * App Screens
 */
sealed class AppScreen {
    object Documents : AppScreen()
    object Editor : AppScreen()
    object Settings : AppScreen()
}