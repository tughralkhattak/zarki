package com.zarki.app.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data-transfer objects mapping the MangaDex API JSON.
 * Docs: https://api.mangadex.org/docs/
 * Unknown fields are ignored by the Json config, so these only declare what we use.
 */

@Serializable
data class MangaListResponse(
    val data: List<MangaData> = emptyList(),
    val limit: Int = 0,
    val offset: Int = 0,
    val total: Int = 0,
)

@Serializable
data class MangaSingleResponse(
    val data: MangaData? = null,
)

@Serializable
data class MangaData(
    val id: String,
    val attributes: MangaAttributes = MangaAttributes(),
    val relationships: List<Relationship> = emptyList(),
)

@Serializable
data class MangaAttributes(
    val title: Map<String, String> = emptyMap(),
    val altTitles: List<Map<String, String>> = emptyList(),
    val description: Map<String, String> = emptyMap(),
    val status: String? = null,
    val year: Int? = null,
    val contentRating: String? = null,
    val tags: List<Tag> = emptyList(),
)

@Serializable
data class Tag(
    val id: String,
    val attributes: TagAttributes = TagAttributes(),
)

@Serializable
data class TagAttributes(
    val name: Map<String, String> = emptyMap(),
)

@Serializable
data class Relationship(
    val id: String,
    val type: String,
    val attributes: RelationshipAttributes? = null,
)

@Serializable
data class RelationshipAttributes(
    // cover_art
    val fileName: String? = null,
    // author / artist
    val name: String? = null,
)

// ---------------- Chapters ----------------

@Serializable
data class ChapterListResponse(
    val data: List<ChapterData> = emptyList(),
    val limit: Int = 0,
    val offset: Int = 0,
    val total: Int = 0,
)

@Serializable
data class ChapterData(
    val id: String,
    val attributes: ChapterAttributes = ChapterAttributes(),
    val relationships: List<Relationship> = emptyList(),
)

@Serializable
data class ChapterAttributes(
    val volume: String? = null,
    val chapter: String? = null,
    val title: String? = null,
    val translatedLanguage: String? = null,
    val pages: Int = 0,
    val publishAt: String? = null,
)

// ---------------- Reader pages (at-home) ----------------

@Serializable
data class AtHomeResponse(
    val baseUrl: String = "",
    val chapter: AtHomeChapter = AtHomeChapter(),
)

@Serializable
data class AtHomeChapter(
    val hash: String = "",
    val data: List<String> = emptyList(),
    @SerialName("dataSaver") val dataSaver: List<String> = emptyList(),
)
