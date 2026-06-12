package com.zarki.app.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface for the MangaDex API (the legal, official source).
 */
interface MangaDexApi {

    @GET("manga")
    suspend fun listManga(
        @Query("limit") limit: Int = 30,
        @Query("offset") offset: Int = 0,
        @Query("order[followedCount]") orderByFollows: String = "desc",
        @Query("includes[]") includes: List<String> = listOf("cover_art"),
        @Query("contentRating[]") contentRating: List<String> = listOf("safe", "suggestive"),
        @Query("hasAvailableChapters") hasChapters: String = "true",
    ): MangaListResponse

    @GET("manga")
    suspend fun latestManga(
        @Query("limit") limit: Int = 30,
        @Query("offset") offset: Int = 0,
        @Query("order[latestUploadedChapter]") orderByLatest: String = "desc",
        @Query("includes[]") includes: List<String> = listOf("cover_art"),
        @Query("contentRating[]") contentRating: List<String> = listOf("safe", "suggestive"),
        @Query("hasAvailableChapters") hasChapters: String = "true",
    ): MangaListResponse

    @GET("manga")
    suspend fun searchManga(
        @Query("title") title: String,
        @Query("limit") limit: Int = 30,
        @Query("includes[]") includes: List<String> = listOf("cover_art"),
        @Query("contentRating[]") contentRating: List<String> = listOf("safe", "suggestive", "erotica"),
    ): MangaListResponse

    @GET("manga/{id}")
    suspend fun getManga(
        @Path("id") id: String,
        @Query("includes[]") includes: List<String> = listOf("cover_art", "author", "artist"),
    ): MangaSingleResponse

    @GET("manga/{id}/feed")
    suspend fun getChapters(
        @Path("id") id: String,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0,
        @Query("translatedLanguage[]") language: List<String> = listOf("en"),
        @Query("order[chapter]") orderByChapter: String = "desc",
        @Query("includes[]") includes: List<String> = listOf("scanlation_group"),
        @Query("contentRating[]") contentRating: List<String> = listOf("safe", "suggestive", "erotica"),
    ): ChapterListResponse

    @GET("at-home/server/{chapterId}")
    suspend fun getPages(
        @Path("chapterId") chapterId: String,
    ): AtHomeResponse
}
