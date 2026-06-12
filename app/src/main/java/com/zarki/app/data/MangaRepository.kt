package com.zarki.app.data

import com.zarki.app.data.remote.MangaData
import com.zarki.app.data.remote.Network
import com.zarki.app.domain.Chapter
import com.zarki.app.domain.Manga

/**
 * Turns raw MangaDex API responses into clean domain models.
 */
class MangaRepository {

    private val api = Network.api

    suspend fun popular(offset: Int = 0): List<Manga> =
        api.listManga(offset = offset).data.map { it.toManga() }

    suspend fun latest(offset: Int = 0): List<Manga> =
        api.latestManga(offset = offset).data.map { it.toManga() }

    suspend fun search(query: String): List<Manga> =
        api.searchManga(title = query).data.map { it.toManga() }

    suspend fun details(id: String): Manga? =
        api.getManga(id).data?.toManga()

    suspend fun chapters(id: String): List<Chapter> {
        // Page through the whole feed (MangaDex returns at most 500 per call) so
        // long series aren't cut off.
        val collected = mutableListOf<com.zarki.app.data.remote.ChapterData>()
        var offset = 0
        while (true) {
            val resp = api.getChapters(id = id, limit = 500, offset = offset)
            collected += resp.data
            offset += resp.data.size
            if (resp.data.isEmpty() || offset >= resp.total || offset > 10000) break
        }
        val mapped = collected.map { c ->
            Chapter(
                id = c.id,
                number = c.attributes.chapter ?: "",
                title = c.attributes.title ?: "",
                volume = c.attributes.volume,
                pages = c.attributes.pages,
                language = c.attributes.translatedLanguage,
            )
        }
        // Keep readable chapters (MangaDex-hosted) and collapse duplicate chapter
        // numbers uploaded by different scanlation groups into one entry.
        val readable = mapped.filter { it.pages > 0 }
        val list = readable.ifEmpty { mapped }
        return list.distinctBy { it.number.ifBlank { it.id } }
    }

    /** Returns the full image URLs for every page of a chapter. */
    suspend fun pages(chapterId: String): List<String> {
        val res = api.getPages(chapterId)
        val base = res.baseUrl
        val hash = res.chapter.hash
        return res.chapter.data.map { file -> "$base/data/$hash/$file" }
    }

    private fun MangaData.toManga(): Manga {
        val attrs = attributes
        val title = attrs.title.values.firstOrNull()
            ?: attrs.altTitles.firstNotNullOfOrNull { it.values.firstOrNull() }
            ?: "Untitled"
        val cover = relationships.firstOrNull { it.type == "cover_art" }
            ?.attributes?.fileName
            ?.let { Network.coverUrl(id, it) }
        val authors = relationships
            .filter { it.type == "author" || it.type == "artist" }
            .mapNotNull { it.attributes?.name }
            .distinct()
        return Manga(
            id = id,
            title = title,
            coverUrl = cover,
            description = attrs.description["en"] ?: attrs.description.values.firstOrNull() ?: "",
            status = attrs.status?.replaceFirstChar { it.uppercase() } ?: "",
            year = attrs.year,
            authors = authors,
            tags = attrs.tags.mapNotNull { it.attributes.name["en"] }.take(8),
        )
    }
}
