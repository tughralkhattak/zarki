package com.zarki.app.data.source

import com.zarki.app.data.MangaRepository
import com.zarki.app.domain.Chapter
import com.zarki.app.domain.Manga

/**
 * A content source for manga. This is the extension point: every provider
 * (MangaDex today, others later) implements this interface, so the rest of the
 * app never hardcodes where manga comes from.
 *
 * This is the same idea as Mihon's source/extension system — a neutral plugin
 * interface. Zarki ships with the legal MangaDex source; more legal sources can
 * be added behind this same interface.
 */
interface MangaSource {
    val id: String
    val name: String
    val lang: String
    val enabled: Boolean

    suspend fun popular(page: Int = 0): List<Manga>
    suspend fun latest(page: Int = 0): List<Manga>
    suspend fun search(query: String): List<Manga>
    suspend fun details(mangaId: String): Manga?
    suspend fun chapters(mangaId: String): List<Chapter>
    suspend fun pages(chapterId: String): List<String>
}

/** The official MangaDex source (legal, public API). */
class MangaDexSource(private val repo: MangaRepository = MangaRepository()) : MangaSource {
    override val id = "mangadex"
    override val name = "MangaDex"
    override val lang = "en"
    override val enabled = true

    override suspend fun popular(page: Int) = repo.popular(offset = page * 30)
    override suspend fun latest(page: Int) = repo.latest(offset = page * 30)
    override suspend fun search(query: String) = repo.search(query)
    override suspend fun details(mangaId: String) = repo.details(mangaId)
    override suspend fun chapters(mangaId: String) = repo.chapters(mangaId)
    override suspend fun pages(chapterId: String) = repo.pages(chapterId)
}

/**
 * Holds all installed sources and the currently selected one.
 * New sources register here; the UI lists them on the Sources screen.
 */
class SourceManager {
    val sources: List<MangaSource> = listOf(
        MangaDexSource(),
    )

    var current: MangaSource = sources.first()
        private set

    fun select(sourceId: String) {
        sources.firstOrNull { it.id == sourceId }?.let { current = it }
    }
}
