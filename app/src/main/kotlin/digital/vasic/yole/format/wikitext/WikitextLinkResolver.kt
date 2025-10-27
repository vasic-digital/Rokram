package digital.vasic.yole.format.wikitext

import digital.vasic.opoc.format.GsTextUtils
import digital.vasic.opoc.util.GsFileUtils
import org.apache.commons.io.FilenameUtils
import java.io.File
import java.io.IOException
import java.util.regex.Pattern

/**
 * Resolves wikitext links and converts them to file paths if necessary.
 * The logic follows the specification of wikitext links as stated here: https://zim-wiki.org/manual/Help/Links.html
 * If the link contains an additional description, it is parsed as well.
 */
class WikitextLinkResolver private constructor(
    private var _notebookRootDir: File?,
    private val _currentPage: File,
    private val _shouldDynamicallyDetermineRoot: Boolean
) {
    private var _wikitextPath: String? = null
    private var _resolvedLink: String? = null
    private var _linkDescription: String? = null
    private var _isWebLink = false

    enum class Patterns(val pattern: Pattern) {
        LINK(Pattern.compile("\\[\\[(?!\\[)((.+?)(\\|(.+?))?\\]*)]\\]")),
        SUBPAGE_PATH(Pattern.compile("\\+(.*)")),
        TOPLEVEL_PATH(Pattern.compile(":(.*)")),
        RELATIVE_PATH(Pattern.compile("[^#/]+")), // no weblinks, no inner page references
        PATH_WITH_INNER_PAGE_REFERENCE(Pattern.compile("(.*)#(.+)")),
        WEBLINK(Pattern.compile("^[a-z]+://.+"))
        // TODO: external file links
        // TODO: interwiki links
    }

    private fun resolve(wikitextLink: String): WikitextLinkResolver {
        val m = Patterns.LINK.pattern.matcher(wikitextLink)
        if (m.matches()) {
            _wikitextPath = m.group(2)
            _linkDescription = m.group(4)
            _resolvedLink = resolveWikitextPath(_wikitextPath!!)
        }
        return this
    }

    private fun resolveWikitextPath(wikitextPath: String): String? {
        val webLinkMatcher = Patterns.WEBLINK.pattern.matcher(wikitextPath)
        if (webLinkMatcher.matches()) {
            _isWebLink = true
            return wikitextPath
        } else {
            _isWebLink = false
        }

        // inner page references are not yet supported - for compatibility just get the page
        val pathWithoutInnerRef = stripInnerPageReference(wikitextPath)
        if (GsTextUtils.isNullOrEmpty(pathWithoutInnerRef)) {
            return null
        }

        val subpageMatcher = Patterns.SUBPAGE_PATH.pattern.matcher(pathWithoutInnerRef)
        if (subpageMatcher.matches()) {
            val folderForSubpagesOfCurrentPage = _currentPage.path.replace(".txt", "")
            val wikitextPagePath = subpageMatcher.group(1)
            return FilenameUtils.concat(folderForSubpagesOfCurrentPage, wikitextPagePathToRelativeFilePath(wikitextPagePath!!))
        }

        // the link types below need knowledge of the notebook root dir
        if (_shouldDynamicallyDetermineRoot) {
            _notebookRootDir = findNotebookRootDir(_currentPage)
            if (_notebookRootDir == null) {
                // try the current directory as a possible notebook root dir
                _notebookRootDir = _currentPage.parentFile
            }
        }

        val toplevelMatcher = Patterns.TOPLEVEL_PATH.pattern.matcher(pathWithoutInnerRef)
        if (toplevelMatcher.matches()) {
            val wikitextPagePath = toplevelMatcher.group(1)
            return FilenameUtils.concat(_notebookRootDir!!.path, wikitextPagePathToRelativeFilePath(wikitextPagePath!!))
        }

        val relativeMatcher = Patterns.RELATIVE_PATH.pattern.matcher(pathWithoutInnerRef)
        if (relativeMatcher.matches()) {
            val relativeWikitextPagePath = relativeMatcher.group()
            val relativeLinkToCheck = wikitextPagePathToRelativeFilePath(relativeWikitextPagePath)
            return findFirstPageTraversingUpToRoot(_currentPage, relativeLinkToCheck)
        }

        // Try to resolve the path as relative to the wiki page's attachment directory.
        // Return an absolute path, or the original path in case it cannot be resolved,
        // it might be a URL.
        return _notebookRootDir?.let {
            resolveAttachmentPath(pathWithoutInnerRef, it, _currentPage, _shouldDynamicallyDetermineRoot)
        } ?: pathWithoutInnerRef
    }

    private fun stripInnerPageReference(wikitextPath: String): String {
        val pathWithInnerPageReferenceMatcher = Patterns.PATH_WITH_INNER_PAGE_REFERENCE.pattern.matcher(wikitextPath)
        if (pathWithInnerPageReferenceMatcher.matches()) {
            val pagePath = pathWithInnerPageReferenceMatcher.group(1)
            return pagePath!!
        }
        return wikitextPath
    }

    private fun findFirstPageTraversingUpToRoot(currentPage: File?, relativeLinkToCheck: String): String? {
        // if the notebook directory is set incorrectly/cannot be reached,
        // dynamic traversal can go up to the root of the filesystem - thus the null-check
        if (currentPage == null || currentPage == _notebookRootDir) {
            return null
        }

        val parentFolder = currentPage.parentFile ?: return null
        val candidateFile = GsFileUtils.join(parentFolder, relativeLinkToCheck)
        return if (candidateFile.exists()) {
            candidateFile.toString()
        } else {
            findFirstPageTraversingUpToRoot(parentFolder, relativeLinkToCheck)
        }
    }

    /**
     * @param wikitextPagePath the page path (separator: double colons)
     * @return
     */
    private fun wikitextPagePathToRelativeFilePath(wikitextPagePath: String): String {
        var result = wikitextPagePath.replace(":", File.separator)
        result = result.replace(" ", "_")
        result = "$result.txt"
        return result
    }

    val wikitextPath: String?
        get() = _wikitextPath

    val resolvedLink: String?
        get() = _resolvedLink

    val linkDescription: String?
        get() = _linkDescription

    fun isWebLink(): Boolean = _isWebLink

    val notebookRootDir: File?
        get() = _notebookRootDir

    companion object {
        @JvmStatic
        fun resolve(wikitextLink: String, notebookRootDir: File?, currentPage: File, shouldDynamicallyDetermineRoot: Boolean): WikitextLinkResolver {
            return WikitextLinkResolver(notebookRootDir, currentPage, shouldDynamicallyDetermineRoot).resolve(wikitextLink)
        }

        private fun findNotebookRootDir(currentPage: File?): File? {
            if (currentPage != null && currentPage.exists()) {
                return if (GsFileUtils.join(currentPage, "notebook.zim").exists()) {
                    currentPage
                } else {
                    findNotebookRootDir(currentPage.parentFile)
                }
            }
            return null
        }

        /**
         * Return a wiki file's Attachment Directory.<p></p>
         *
         * <p> By design, a Zim wiki file's Attachment Directory should
         * have the same path of the wiki file itself, without the .txt
         * suffix.</p><p></p>
         *
         * <p>Here, a difference from Zim is that any file can derive a
         * properly named Attachment Directory, as long as that file is
         * with a suffix.</p>
         *
         * @param currentPage Current wiki file's path.
         * @return {@code currentPage}'s path without suffix.
         * @see GsFileUtils.getFilenameWithoutExtension
         */
        @JvmStatic
        fun findAttachmentDir(currentPage: File): File {
            return GsFileUtils.join(currentPage.parentFile ?: currentPage, GsFileUtils.getFilenameWithoutExtension(currentPage))
        }

        /**
         * Return a Notebook's Root Directory.<p></p>
         *
         * <p> By design ({@code shouldDynamicallyDetermineRoot = true}),
         * a Zim Notebook's Root Directory is the closest ancestor of the
         * {@code currentPage} with a notebook.zim file in it.</p><p></p>
         *
         * <p> Here, a difference from Zim is that if a notebook.zim file
         * can't be located, a {@code currentPage}'s current directory is
         * taken as Root Directory.</p><p></p>
         *
         * <p> WARNING: Removing a notebook.zim file from the Notebook or
         * changing the value of {@code notebookRootDir}, may switch to a
         * Root Directory different than where the Notebook was organized
         * originally.</p>
         *
         * @param notebookRootDir                Root Directory when {@code shouldDynamicallyDetermineRoot == false}.
         * @param currentPage                    Current wiki file's path used to determine the current directory.
         * @param shouldDynamicallyDetermineRoot If {@code true}, return the closest ancestor of the
         *                                       {@code currentPage} with a notebook.zim file in it, or fall back to the current directory
         *                                       on failure.  If {@code false}, return {@code notebookRootDir}.
         * @return Identified Notebook's Root Directory.
         * @see WikitextLinkResolver.findNotebookRootDir
         */
        @JvmStatic
        fun findNotebookRootDir(notebookRootDir: File?, currentPage: File, shouldDynamicallyDetermineRoot: Boolean): File? {
            var rootDir = notebookRootDir
            if (shouldDynamicallyDetermineRoot) {
                rootDir = findNotebookRootDir(currentPage)
                if (rootDir == null) {
                    rootDir = currentPage.parentFile
                }
            }
            return rootDir
        }

        /**
         * Return a system file's path as wiki attachment's path.<p></p>
         *
         * <p> If both {@code file} and {@code currentPage} are children
         * of the same Notebook's Root Directory, return a path relative
         * to the {@code currentPage}'s Attachment Directory, otherwise,
         * return the original {@code file}'s path.</p><p></p>
         *
         * <p> Here, a difference from Zim is to always consider as Root
         * Directory the {@code currentPage}'s directory before anything
         * else.</p>
         *
         * @param file                           System file's path to resolve as wiki attachment's path.
         * @param notebookRootDir                Root Directory when {@code shouldDynamicallyDetermineRoot == false}.
         * @param currentPage                    Current wiki file's path used to determine the current Attachment Directory.
         * @param shouldDynamicallyDetermineRoot If {@code true}, the Root Directory is the closest ancestor
         *                                       of the {@code currentPage} with a notebook.zim file in it, or the {@code currentPage}'s directory
         *                                       on failure.  If {@code false}, the Root Directory is {@code notebookRootDir}.  In either cases, a
         *                                       {@code currentPage}'s directory is always considered as Root Directory before anything else.
         * @return {@code file}'s path relative to the {@code currentPage}'s Attachment Directory, when both
         * {@code file} and {@code currentPage} are children of the identified Notebook's Root Directory, or
         * the original {@code file}'s path otherwise.
         * @see WikitextLinkResolver.findAttachmentDir
         * @see WikitextLinkResolver.findNotebookRootDir
         */
        @JvmStatic
        fun resolveSystemFilePath(file: File, notebookRootDir: File?, currentPage: File, shouldDynamicallyDetermineRoot: Boolean): String {
            val currentDir = currentPage.parentFile ?: return file.toString()
            val rootDir = findNotebookRootDir(notebookRootDir, currentPage, shouldDynamicallyDetermineRoot)

            if (GsFileUtils.isChild(currentDir, file) ||
                (rootDir != null && GsFileUtils.isChild(rootDir, file) && GsFileUtils.isChild(rootDir, currentPage))) {
                val attachmentDir = findAttachmentDir(currentPage)
                var path = GsFileUtils.relativePath(attachmentDir, file)

                // Zim prefixes also children of the Attachment Directory.
                if (file.toString().endsWith("/$path")) {
                    path = "./$path"
                }

                return path
            }

            return file.toString()
        }

        /**
         * Return a wiki attachment's path as system absolute path.<p></p>
         *
         * <p> If {@code path} can be resolved as a relative path to the
         * {@code currentPage}'s Attachment Directory, return the result
         * of {@code path} as a system absolute path.  Otherwise, return
         * the original {@code path}.</p><p></p>
         *
         * <p> Here, a difference from Zim is to always consider as Root
         * Directory the {@code currentPage}'s directory before anything
         * else.</p>
         *
         * @param path                           Path that might be relative to the {@code currentPage}'s Attachment Directory.
         * @param notebookRootDir                Root Directory when {@code shouldDynamicallyDetermineRoot == false}.
         * @param currentPage                    Current wiki file's path used to determine the current Attachment Directory.
         * @param shouldDynamicallyDetermineRoot If {@code true}, the Root Directory is the closest ancestor
         *                                       of the {@code currentPage} with a notebook.zim file in it, or the {@code currentPage}'s directory
         *                                       on failure.  If {@code false}, the Root Directory is {@code notebookRootDir}.  In either cases, a
         *                                       {@code currentPage}'s directory is always considered as Root Directory before anything else.
         * @return {@code path} as a system absolute path, if it can be resolved as a relative path to the
         * {@code currentPage}'s Attachment Directory, or the original {@code path} otherwise.
         * @see WikitextLinkResolver.findAttachmentDir
         * @see WikitextLinkResolver.resolveSystemFilePath
         */
        @JvmStatic
        fun resolveAttachmentPath(path: String, notebookRootDir: File?, currentPage: File, shouldDynamicallyDetermineRoot: Boolean): String {
            if (path.startsWith("./") || path.startsWith("../")) {
                val attachmentDir = findAttachmentDir(currentPage)
                val file = File(attachmentDir, path).absoluteFile
                val resolved = resolveSystemFilePath(file, notebookRootDir, currentPage, shouldDynamicallyDetermineRoot)

                if (path == resolved) {
                    return try {
                        file.canonicalPath
                    } catch (e: IOException) {
                        file.toString()
                    }
                }
            }

            return path
        }
    }
}
