package com.zarki.app.data

import android.content.Context
import com.zarki.app.data.local.DownloadDao
import com.zarki.app.data.local.DownloadedChapter
import com.zarki.app.data.source.SourceManager
import com.zarki.app.domain.Chapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

/**
 * Downloads chapter pages to internal storage for offline reading and tracks
 * them in the database. Downloaded chapters are read from local files instead
 * of the network.
 */
class DownloadManager(
    private val context: Context,
    private val dao: DownloadDao,
    private val sources: SourceManager,
) {
    private val client = OkHttpClient()

    private val _active = MutableStateFlow<Set<String>>(emptySet())
    val active: StateFlow<Set<String>> = _active.asStateFlow()

    fun observeDownloads(): Flow<List<DownloadedChapter>> = dao.observeAll()
    fun downloadedIds(): Flow<List<String>> = dao.downloadedIds()

    suspend fun isDownloaded(chapterId: String): Boolean = dao.get(chapterId) != null

    /** Local file:// URLs for an offline chapter, or empty if not downloaded. */
    suspend fun localPages(chapterId: String): List<String> = withContext(Dispatchers.IO) {
        val d = dao.get(chapterId) ?: return@withContext emptyList()
        (0 until d.pageCount)
            .map { File(d.dir, "$it.jpg") }
            .filter { it.exists() }
            .map { "file://${it.absolutePath}" }
    }

    suspend fun download(chapter: Chapter, mangaId: String, mangaTitle: String, coverUrl: String?) {
        if (dao.get(chapter.id) != null || chapter.id in _active.value) return
        _active.value = _active.value + chapter.id
        try {
            withContext(Dispatchers.IO) {
                val urls = sources.current.pages(chapter.id)
                if (urls.isEmpty()) return@withContext
                val dir = File(context.filesDir, "downloads/${chapter.id}").apply { mkdirs() }
                urls.forEachIndexed { i, url ->
                    val req = Request.Builder().url(url)
                        .header("User-Agent", "Zarki/1.0 (Android manga reader)").build()
                    client.newCall(req).execute().use { resp ->
                        val body = resp.body ?: return@forEachIndexed
                        File(dir, "$i.jpg").outputStream().use { out -> body.byteStream().copyTo(out) }
                    }
                }
                dao.add(
                    DownloadedChapter(
                        chapterId = chapter.id,
                        mangaId = mangaId,
                        mangaTitle = mangaTitle,
                        coverUrl = coverUrl,
                        chapterNumber = chapter.number,
                        pageCount = urls.size,
                        dir = dir.absolutePath,
                        downloadedAt = System.currentTimeMillis(),
                    ),
                )
            }
        } catch (_: Exception) {
            // leave it un-downloaded; user can retry
        } finally {
            _active.value = _active.value - chapter.id
        }
    }

    suspend fun delete(chapterId: String) = withContext(Dispatchers.IO) {
        dao.get(chapterId)?.let { File(it.dir).deleteRecursively() }
        dao.remove(chapterId)
    }
}
