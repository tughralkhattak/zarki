package com.zarki.app.domain

/** Clean app-facing models (decoupled from the API DTOs). */

data class Manga(
    val id: String,
    val title: String,
    val coverUrl: String?,
    val description: String = "",
    val status: String = "",
    val year: Int? = null,
    val authors: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
)

data class Chapter(
    val id: String,
    val number: String,
    val title: String,
    val volume: String?,
    val pages: Int,
    val language: String?,
) {
    val display: String
        get() = buildString {
            append("Ch. ${number.ifBlank { "?" }}")
            if (!title.isNullOrBlank()) append(" — $title")
        }
}
