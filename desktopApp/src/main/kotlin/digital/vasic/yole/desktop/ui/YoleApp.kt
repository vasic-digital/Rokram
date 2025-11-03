/*#######################################################
 *
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * Main Compose UI for Yole Desktop App
 *
 *########################################################*/

package digital.vasic.yole.desktop.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
// import androidx.compose.ui.tooling.preview.Preview // Not available for desktop
import digital.vasic.yole.format.FormatRegistry

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

@Composable
fun MainScreen() {
    // TODO: Implement main screen with desktop-specific features
    // - Menu bar
    // - File dialogs
    // - Multi-window support
    // - Keyboard shortcuts
    
    // For now, show a simple welcome screen
    WelcomeScreen()
}

@Composable
fun WelcomeScreen() {
    Text(
        text = "Welcome to Yole Desktop!\n\nSupported formats: ${FormatRegistry.formats.size}",
        style = MaterialTheme.typography.headlineMedium
    )
}

// @Preview - not available for desktop
// @Composable
// fun PreviewYoleApp() {
//     YoleApp()
// }