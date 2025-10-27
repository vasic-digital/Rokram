/*#######################################################
 *
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * Main Compose UI for Yole Android App
 *
 *########################################################*/

package digital.vasic.yole.android.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import digital.vasic.yole.shared.format.FormatRegistry

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
    // TODO: Implement main screen with navigation
    // - File browser
    // - Document editor
    // - Preview panel
    // - Format selector
    
    // For now, show a simple welcome screen
    WelcomeScreen()
}

@Composable
fun WelcomeScreen() {
    androidx.compose.material3.Text(
        text = "Welcome to Yole!\n\nSupported formats: ${FormatRegistry.formats.size}",
        style = MaterialTheme.typography.headlineMedium
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewYoleApp() {
    YoleApp()
}