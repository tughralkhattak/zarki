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

    suspend fun search(query: String): List<Manga> =
        api.searchManga(title = query).data.map { it.toManga() }

    suspend fun details(id: String): Manga? =
        api.getManga(id).data?.toManga()

    suspend fun chapters(id: String): List<Chapter> =
        api.getChapters(id).data.mapNotNull { c ->
            Chapter(
                id = c.id,
                number = c.attributes.chapter ?: "",
                title = c.attributes.title ?: "",
                volume = c.attributes.volume,
                pages = c.attributes.pages,
                language = c.attributes.translatedLanguage,
            )
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
