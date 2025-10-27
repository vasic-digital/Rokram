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
    
    Div({
        style {
            width(100.vw)
            height(100.vh)
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
            fontFamily("'Inter', 'Roboto', sans-serif")
        }
    }) {
        // Header
        Header({
            style {
                backgroundColor(Color("#1976d2"))
                color(Color.white)
                padding(16.px)
                boxShadow(0.px, 2.px, 4.px, Color("#00000033"))
            }
        }) {
            H1({
                style {
                    margin(0.px)
                    fontSize(24.px)
                    fontWeight("600")
                }
            }) {
                Text("Yole - Web Editor")
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
                    backgroundColor(Color("#f5f5f5"))
                    borderRight(1.px, LineStyle.Solid, Color("#e0e0e0"))
                    padding(16.px)
                    overflowY("auto")
                }
            }) {
                H2({
                    style {
                        fontSize(18.px)
                        marginTop(0.px)
                        marginBottom(16.px)
                        color(Color("#333"))
                    }
                }) {
                    Text("Document Formats")
                }
                
                // Format selection
                FormatList()
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
                        borderBottom(1.px, LineStyle.Solid, Color("#e0e0e0"))
                        backgroundColor(Color.white)
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
                            }
                            placeholder("Start writing your document...")
                        }
                    })
                    
                    // Preview (optional - could be toggle)
                    Div({
                        style {
                            flex(1)
                            padding(16.px)
                            borderLeft(1.px, LineStyle.Solid, Color("#e0e0e0"))
                            overflowY("auto")
                            backgroundColor(Color("#fafafa"))
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
fun FormatList() {
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
                    hover {
                        backgroundColor(Color("#e3f2fd"))
                    }
                }
            }) {
                Text("$name ($extension)")
            }
        }
    }
}