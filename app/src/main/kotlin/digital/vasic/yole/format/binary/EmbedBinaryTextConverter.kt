/*#######################################################
 *
 * SPDX-FileCopyrightText: 2022-2025 Gregor Santner <gsantner AT mailbox DOT org>
 * SPDX-License-Identifier: Unlicense OR CC0-1.0
 *
 *########################################################*/
package digital.vasic.yole.format.binary

import android.app.Activity
import android.content.Context
import digital.vasic.opoc.format.GsSimplePlaylistParser
import digital.vasic.opoc.util.GsFileUtils
import digital.vasic.opoc.util.GsStorageUtils
import digital.vasic.opoc.util.GsUiUtils
import digital.vasic.yole.R
import digital.vasic.yole.format.TextConverterBase
import digital.vasic.yole.format.markdown.MarkdownTextConverter
import java.io.File

/**
 * Binary file embedder text converter.
 *
 * Embeds binary files (images, audio, video, playlists) into HTML for preview.
 */
@Suppress("WeakerAccess")
class EmbedBinaryTextConverter : TextConverterBase() {

    @Suppress("ConstantConditions")
    override fun convertMarkup(markup: String, context: Context, lightMode: Boolean, enableLineNumbers: Boolean, file: File): String {
        var converted: String
        var onLoadJs = ""
        val head = CSS_EMBED_STYLE + CSS_EMBED_TABLE_LIMITS + CSS_EMBED_STICKY_STYLE

        converted = HTML100_BODY_BEGIN
        val extWithDot = GsFileUtils.getFilenameExtension(file.name)

        // Sticky header with content depending on type
        run {
            converted += "\n<div class='sticky sticky-blackbox'>\n"
            when {
                EXT_IMAGE.contains(extWithDot) || extWithDot.matches(EXT_IMAGE_TEXTUAL.toRegex()) -> {
                    converted += "<img class='' src='$TOKEN_FILEURI_VIEWED_FILE' alt='Your Android device does not support the file format.'/>"
                }
                EXT_VIDEO.contains(extWithDot) || extWithDot.matches(EXT_MATCHES_M3U_PLAYLIST.toRegex()) -> {
                    converted += "<video class='htmlav' autoplay controls loop style='max-height: 45vh; width: 100%; max-width: 100%;' srcx='$TOKEN_FILEURI_VIEWED_FILE'/>Your Android device does not support the video tag or the file format.</video>"
                }
                EXT_AUDIO.contains(extWithDot) -> {
                    converted += " <audio class='htmlav' title='${file.name}' autoplay loop controls loop='0' style='width: 100%;'><source srcx='$TOKEN_FILEURI_VIEWED_FILE'>Your Android device does not support the audio tag or the file format.</audio>"
                }
            }
            converted += "<span class='clear'></span><div style='margin-left: 12px; margin-right: 8px;'>"
            if (converted.contains("htmlav")) {
                converted += "<button type='button floatl' class='fa' onclick=\"javascript:document.avSetPlaylistPos(null, -1);\"/>⏮️️</button>"
                converted += "<button type='button floatl' class='fa' onclick=\"javascript:document.avSeek(-30);\"/>⏪</button>"
                converted += "<button type='button floatl' class='fa' onclick=\"javascript:document.avPause();\"/>⏯️</button>"
                converted += "<button type='button floatl' class='fa' onclick=\"javascript:document.avSeek(30);\"/>⏩</button>"
                converted += "<button type='button floatl' class='fa' onclick=\"javascript:document.avSetPlaylistPos(null, +1);\"/>⏭️</button>"
                converted += "<button type='button floatl' class='fa' onclick=\"javascript:document.avLoopToggle();\"/>&#128257;</button>"
                converted += "<p id='avCurrentPlayedTitleP' style='margin: 0px; margin-bottom: 2px; white-space:nowrap; overflow:hidden;" +
                    (if (extWithDot.matches(EXT_MATCHES_M3U_PLAYLIST.toRegex())) "" else "display:none;") + "'></p>"

                // Audio/Video playback & playlist js functions
                onLoadJs += "document.playlist = []; document.playlistTitles = []; document.playlistIndex = -1;"
                onLoadJs += "document.av               = function(){ return document.getElementsByClassName('htmlav')[0]; };"
                onLoadJs += "document.avLoopToggle     = function(){ var av=document.av(); av.loop = !av.loop; };"
                onLoadJs += "document.avPause          = function(){ var av=document.av(); if(av.paused){av.play();} else{av.pause();}; };"
                onLoadJs += "document.avSetUrl         = function(u){ var av=document.av(); av.src = u; av.play(); };"
                onLoadJs += "document.avAddToPlaylist  = function(t, u){ var av=document.av(); document.playlist.push(u); document.playlistTitles.push(t); if (document.playlistIndex < 0){ document.avSetPlaylistPos(null, 1);} };"
                onLoadJs += "document.avSeek           = function(delta){ var av=document.av(); av.currentTime +=delta; av.play(); };"
                onLoadJs += "document.av().addEventListener('ended', ()=>{ console.error('ended'); document.avSetPlaylistPos(null, +1); });"
                onLoadJs += "document.avSetPlaylistPos = function(i, delta, byUserSelection) { " +
                        "        i = i!=null ? i : document.playlistIndex; delta = delta!=null ? delta : 0; byUserSelection = byUserSelection!=null ? byUserSelection : false;" +
                        "        document.playlistIndex = (i+delta)%document.playlist.length;" +
                        "        document.avSetUrl(document.playlist[document.playlistIndex]);" +
                        "        document.getElementById('avCurrentPlayedTitleP').innerText = document.playlistTitles[document.playlistIndex].substring(0, 120);" +
                        "        if (!byUserSelection) { document.location.hash = 'playlistbtn'+(document.playlistIndex-6);}" +
                        "    };"

                // Add file itself as first item to playlist
                onLoadJs += "document.avAddToPlaylist('$TOKEN_FILEURI_VIEWED_FILE', '$TOKEN_FILEURI_VIEWED_FILE');"

                // Don't turn screen automatically off during playback
                if (context is Activity) {
                    GsUiUtils.setKeepScreenOn(context, true)
                }
            }

            // Rotation sticky button
            if (converted.contains("rotatable")) {
                converted += "<button type='button floatl' class='fa' onclick=\"javascript:document.rotate();\"/>&#128260️</button>"
                onLoadJs += "document.rotation = 0;"
                onLoadJs += "document.rotate       = function()      { var o=document.getElementsByClassName('rotatable')[0]; document.rotation+=90; o.style.transform = 'rotate(xdeg)'.replace('x', document.rotation); o.style.height = Math.min(o.offsetHeight, window.screen.width*0.9)+'px'; };"
            }
            converted += "\n</div></div>\n" // button-margin, sticky
        }

        // content area with side margins
        run {
            converted += "\n\n<div style='margin: 16px; margin-top: 0px;'><br/>\n"
            // Add file info table below content
            val table = StringBuilder()
            table.append(String.format("%s | %s\n-----|-----\n", context.getString(R.string.type), context.getString(R.string.info)))
            for (metaPair in GsStorageUtils.extractFileMetadata(context, file, true)) {
                table.append(String.format("%s | %s\n", metaPair.first.replace("|", "/"), metaPair.second.replace("|", "/")))
            }
            converted += MarkdownTextConverter.flexmarkRenderer.render(MarkdownTextConverter.flexmarkParser.parse(table.toString()))

            // m3u/m3u8: buttons for playlist entries
            if (extWithDot.matches(EXT_MATCHES_M3U_PLAYLIST.toRegex())) {
                table.setLength(0)
                table.append(String.format("%s | %s\n-----|-----\n", context.getString(R.string.name), context.getString(R.string.info)))

                var i = 0
                for (playlistItem in GsSimplePlaylistParser().parse(GsFileUtils.readTextFileFast(file).first)) {
                    onLoadJs += "\ndocument.avAddToPlaylist('${playlistItem.name}', '${playlistItem.url}');"
                    table.append(playlistItem.getName(80)).append(" | ")
                    table.append(String.format(PLAYLIST_BUTTON_TEMPLATE, i, i + 1)).append("\n")
                    i++
                }
                if (i > 0) {
                    onLoadJs += "document.avLoopToggle(); document.avSetPlaylistPos(1, 0);"
                    converted += "\n<br/>" + MarkdownTextConverter.flexmarkRenderer.render(MarkdownTextConverter.flexmarkParser.parse(table.toString()))
                }
            }

            // end div with side margins
            converted += "</div>"
        }

        converted += HTML101_BODY_END
        return putContentIntoTemplate(context, converted, lightMode, file, onLoadJs, head)
    }

    override fun getContentType(): String {
        return CONTENT_TYPE_HTML
    }

    override fun isFileOutOfThisFormat(file: File, name: String, ext: String): Boolean {
        return ext in EXT
    }

    companion object {
        private val EXT_AUDIO = listOf(".mp3", ".ogg", ".flac", ".opus", ".oga", ".wma", ".m4a", ".aac", ".wav", ".amr", ".mid", ".midi", ".pcm")
        private val EXT_IMAGE = listOf(".jpg", ".jpeg", ".png", ".bmp", ".gif", ".webp", ".heic", ".heif", ".avif")
        private val EXT_VIDEO = listOf(".webm", ".mp4", ".mpeg4", ".mpeg", ".mpg", ".mkv", ".3gp", ".ts", ".m4v")
        private val EXT = buildList {
            addAll(EXT_IMAGE)
            addAll(EXT_VIDEO)
            addAll(EXT_AUDIO)
        }

        const val EXT_MATCHES_M3U_PLAYLIST = "(?i).m3u8?"
        const val EXT_IMAGE_TEXTUAL = "(?i).svg"

        private const val HTML100_BODY_BEGIN = "<div>\n  "
        private const val HTML101_BODY_END = "\n\n</div>"
        private const val CSS_EMBED_STYLE = "$CSS_S html,body{padding: 0px; margin:0px;}$CSS_E"
        private const val CSS_EMBED_TABLE_LIMITS = "$CSS_S table {word-break: break-word;} thead tr th:first-child, tbody tr td:first-child {word-break:keep-all; min-width: 100px;} thead {display:none;}  table tr:nth-child(odd) td{ background: $TOKEN_COLOR_GREY_OF_THEME; color: $TOKEN_BW_INVERSE_OF_THEME; }$CSS_E"
        private const val CSS_EMBED_STICKY_STYLE = "$CSS_S .sticky-blackbox{top: 0px; border-top-width:0.1px; width: 100%; max-width: 100%; background: black; color: lightgrey; word-break: break-word;}$CSS_E"
        private const val PLAYLIST_BUTTON_TEMPLATE = "<button type='button' id='playlistbtn%d' onclick=\"javascript:document.avSetPlaylistPos(%d, 0, true);\"/>&#10132;</button>"
    }
}
