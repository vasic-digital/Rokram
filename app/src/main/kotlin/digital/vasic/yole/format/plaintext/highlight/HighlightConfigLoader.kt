/*#######################################################
 *
 *   Maintained 2018-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 *   Maintained 2025 by Milos Vasic
 *   License of this file: Apache 2.0
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 *   SPDX-FileCopyrightText: 2025 Milos Vasic
 *   SPDX-License-Identifier: Apache-2.0
 *
 #########################################################*/
package digital.vasic.yole.format.plaintext.highlight

import android.content.Context
import com.google.gson.Gson
import java.io.IOException
import java.io.InputStreamReader
import java.util.Properties

/**
 * Loader for syntax highlighting configurations.
 *
 * Loads and caches:
 * - Language syntax definitions (from JSON files)
 * - Color themes (from JSON files)
 * - Language file extension mappings (from properties file)
 *
 * Uses LRU cache for syntax definitions to minimize memory usage.
 *
 * Asset structure:
 * - highlight/languages/map.properties - Extension to language mapping
 * - highlight/languages/[lang].json - Language syntax definitions
 * - highlight/themes/[theme].json - Color theme definitions
 */
class HighlightConfigLoader {
    private val gson = Gson()
    private val map = Properties()
    private val syntaxCache = SyntaxCache()
    private var codeTheme: CodeTheme? = null

    companion object {
        const val MAP_PATH = "highlight/languages/map.properties"
    }

    private fun <T> loadConfig(context: Context, path: String, classT: Class<T>): T {
        return try {
            context.assets.open(path).use { input ->
                gson.fromJson(InputStreamReader(input), classT)
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun loadSyntax(context: Context, lang: String) {
        val syntax = loadConfig(context, "highlight/languages/$lang.json", Syntax::class.java)
        syntaxCache.putSyntax(lang, syntax)
    }

    private fun loadTheme(context: Context, name: String) {
        codeTheme = loadConfig(context, "highlight/themes/$name.json", CodeTheme::class.java)
    }

    /**
     * Get language syntax definition for a file extension.
     *
     * @param context Android Context
     * @param lang Language name or file extension (e.g., "kt", "java", "py")
     * @return Language syntax definition, or null if not found
     */
    fun getSyntax(context: Context, lang: String): Syntax? {
        val normalizedLang = lang.replace("^\\.+".toRegex(), "").lowercase()

        if (map.isEmpty) {
            try {
                context.assets.open(MAP_PATH).use { input ->
                    map.load(input)
                }
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }

        val key = map.getProperty(normalizedLang) ?: return null
        var syntax = syntaxCache.getSyntax(key)

        if (syntax == null) {
            loadSyntax(context, key)
            syntax = syntaxCache.getSyntax(key)
        }

        return syntax
    }

    /**
     * Get color theme by name.
     *
     * @param context Android Context
     * @param name Theme name (e.g., "default", "dark")
     * @return Color theme
     */
    fun getTheme(context: Context, name: String): CodeTheme? {
        val currentTheme = codeTheme
        if (currentTheme == null || currentTheme.name != name) {
            loadTheme(context, name)
        }
        return codeTheme
    }

    /**
     * LRU cache for syntax definitions.
     *
     * Limits memory usage by caching only the most recently used syntax definitions.
     * Evicts least-used entry when cache size exceeds limit.
     */
    internal class SyntaxCache {
        private val syntaxMap = HashMap<String, Syntax>()
        private val usageMap = HashMap<String, Int>()

        companion object {
            const val CACHE_SIZE = 5
        }

        fun getSyntax(key: String?): Syntax? {
            if (key == null) return null

            val syntax = syntaxMap[key] ?: return null

            // Increment usage counter
            val usage = usageMap[key] ?: 0
            usageMap[key] = usage + 1

            return syntax
        }

        fun putSyntax(key: String, syntax: Syntax) {
            // Evict least-used entry if cache is full
            if (syntaxMap.size > CACHE_SIZE) {
                var entryKey: String? = null
                var min = Int.MAX_VALUE

                for ((k, value) in usageMap) {
                    if (value < min) {
                        min = value
                        entryKey = k
                    }
                }

                entryKey?.let {
                    syntaxMap.remove(it)
                    usageMap.remove(it)
                }
            }

            syntaxMap[key] = syntax
            usageMap[key] = 0
        }
    }
}
