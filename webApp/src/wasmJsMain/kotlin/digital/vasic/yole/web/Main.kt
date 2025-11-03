/*#######################################################
 *
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * Yole Web Application
 * Progressive Web App with Kotlin/Wasm
 *
 *########################################################*/

package digital.vasic.yole.web

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import digital.vasic.yole.model.Document
import digital.vasic.yole.format.TextFormat
import digital.vasic.yole.format.FormatRegistry
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposable

/**
 * Main entry point for Yole Web Application
 */
fun main() {
    renderComposable(rootElementId = "root") {
        YoleWebApp()
    }
}

/**
 * Yole Web Application UI
 */
@Composable
fun YoleWebApp() {
    var currentDocument by remember { mutableStateOf<Document?>(null) }
    var documentContent by remember { mutableStateOf("# Welcome to Yole Web\n\nStart writing your document...") }
    var isDarkTheme by remember { mutableStateOf(false) }

    // Theme colors
    val headerBg = if (isDarkTheme) Color("#1a1a1a") else Color("#1976d2")
    val headerText = Color.white
    val logoBg = if (isDarkTheme) Color("#333") else Color.white
    val sidebarBg = if (isDarkTheme) Color("#2a2a2a") else Color("#f5f5f5")
    val sidebarBorder = if (isDarkTheme) Color("#444") else Color("#e0e0e0")
    val sidebarText = if (isDarkTheme) Color.white else Color("#333")
    val editorBg = if (isDarkTheme) Color("#1e1e1e") else Color.white
    val editorBorder = if (isDarkTheme) Color("#444") else Color("#e0e0e0")
    val previewBg = if (isDarkTheme) Color("#252525") else Color("#fafafa")

    Div({
        style {
            width(100.vw)
            height(100.vh)
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
            fontFamily("'Inter', 'Roboto', sans-serif")
            backgroundColor(if (isDarkTheme) Color("#121212") else Color.white)
            color(if (isDarkTheme) Color.white else Color("#333"))
        }
    }) {
        // Header
        Header({
            style {
                backgroundColor(headerBg)
                color(headerText)
                padding(16.px)
                boxShadow(0.px, 2.px, 4.px, if (isDarkTheme) Color("#00000066") else Color("#00000033"))
                display(DisplayStyle.Flex)
                alignItems(AlignItems.Center)
            }
        }) {
            // Logo with background
            Div({
                style {
                    backgroundColor(logoBg)
                    padding(8.px)
                    borderRadius(8.px)
                    marginRight(16.px)
                    boxShadow(0.px, 1.px, 3.px, if (isDarkTheme) Color("#ffffff1a") else Color("#0000001a"))
                }
            }) {
                Img(src = "Logo.png", alt = "Yole Logo", attrs = {
                    style {
                        width(32.px)
                        height(32.px)
                        display(DisplayStyle.Block)
                    }
                })
            }

            H1({
                style {
                    margin(0.px)
                    fontSize(24.px)
                    fontWeight("600")
                    flex(1)
                }
            }) {
                Text("Yole - Web Editor")
            }

            // Theme toggle button
            Button(attrs = {
                onClick { isDarkTheme = !isDarkTheme }
                style {
                    backgroundColor(Color.transparent)
                    border(1.px, LineStyle.Solid, headerText)
                    color(headerText)
                    padding(8.px, 12.px)
                    borderRadius(4.px)
                    cursor("pointer")
                    fontSize(14.px)
                }
            }) {
                Text(if (isDarkTheme) "☀️ Light" else "🌙 Dark")
            }
        }
        
        // Main content area
        Main({
            style {
                flex(1)
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Row)
                overflow("hidden")
            }
        }) {
            // Sidebar
            Aside({
                style {
                    width(250.px)
                    backgroundColor(sidebarBg)
                    borderRight(1.px, LineStyle.Solid, sidebarBorder)
                    padding(16.px)
                    overflowY("auto")
                }
            }) {
                H2({
                    style {
                        fontSize(18.px)
                        marginTop(0.px)
                        marginBottom(16.px)
                        color(sidebarText)
                    }
                }) {
                    Text("Document Formats")
                }
                
                // Format selection
                FormatList(isDarkTheme)
            }
            
            // Editor area
            Section({
                style {
                    flex(1)
                    display(DisplayStyle.Flex)
                    flexDirection(FlexDirection.Column)
                }
            }) {
                // Editor toolbar
                Div({
                    style {
                        padding(16.px)
                        borderBottom(1.px, LineStyle.Solid, editorBorder)
                        backgroundColor(editorBg)
                    }
                }) {
                    Button(attrs = {
                        onClick { 
                            // TODO: Implement new document
                            documentContent = "# New Document\n\nStart writing..."
                        }
                        style {
                            backgroundColor(Color("#1976d2"))
                            color(Color.white)
                            border(0.px)
                            padding(8.px, 16.px)
                            borderRadius(4.px)
                            cursor("pointer")
                            marginRight(8.px)
                        }
                    }) {
                        Text("New Document")
                    }
                    
                    Button(attrs = {
                        onClick { 
                            // TODO: Implement save functionality
                            println("Save document: $documentContent")
                        }
                        style {
                            backgroundColor(Color("#388e3c"))
                            color(Color.white)
                            border(0.px)
                            padding(8.px, 16.px)
                            borderRadius(4.px)
                            cursor("pointer")
                        }
                    }) {
                        Text("Save")
                    }
                }
                
                // Editor
                Div({
                    style {
                        flex(1)
                        display(DisplayStyle.Flex)
                        flexDirection(FlexDirection.Row)
                        overflow("hidden")
                    }
                }) {
                    // Text editor
                    Textarea({
                        value(documentContent)
                        onInput { event ->
                            documentContent = event.value
                        }
                        attrs {
                            style {
                                flex(1)
                                border(0.px)
                                padding(16.px)
                                fontFamily("'JetBrains Mono', 'Courier New', monospace")
                                fontSize(14.px)
                                lineHeight("1.5")
                                resize("none")
                                outline("none")
                                backgroundColor(editorBg)
                                color(if (isDarkTheme) Color.white else Color("#333"))
                            }
                            placeholder("Start writing your document...")
                        }
                    })
                    
                    // Preview (optional - could be toggle)
                    Div({
                        style {
                            flex(1)
                            padding(16.px)
                            borderLeft(1.px, LineStyle.Solid, editorBorder)
                            overflowY("auto")
                            backgroundColor(previewBg)
                            color(if (isDarkTheme) Color.white else Color("#333"))
                        }
                    }) {
                        // TODO: Implement HTML preview
                        P { Text("Preview will appear here") }
                    }
                }
            }
        }
    }
}

/**
 * Format selection list
 */
@Composable
fun FormatList(isDarkTheme: Boolean) {
    val sidebarText = if (isDarkTheme) Color.white else Color("#333")
    val hoverBg = if (isDarkTheme) Color("#333") else Color("#e3f2fd")

    val formats = listOf(
        "Markdown" to ".md",
        "Plain Text" to ".txt",
        "Todo.txt" to ".txt",
        "CSV" to ".csv",
        "LaTeX" to ".tex",
        "Org Mode" to ".org",
        "AsciiDoc" to ".adoc",
        "WikiText" to ".wiki"
    )

    Ul({
        style {
            listStyle("none")
            padding(0.px)
            margin(0.px)
        }
    }) {
        formats.forEach { (name, extension) ->
            Li({
                style {
                    padding(8.px, 12.px)
                    marginBottom(4.px)
                    borderRadius(4.px)
                    cursor("pointer")
                    color(sidebarText)
                    hover {
                        backgroundColor(hoverBg)
                    }
                }
            }) {
                Text("$name ($extension)")
            }
        }
    }
}