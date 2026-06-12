package com.zarki.app.data.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

/**
 * Builds the Retrofit client for MangaDex. A polite User-Agent is required.
 */
object Network {

    private const val BASE_URL = "https://api.mangadex.org/"
    const val COVERS_URL = "https://uploads.mangadex.org/covers"

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    private val userAgent = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .header("User-Agent", "Zarki/1.0 (Android manga reader)")
            .build()
        chain.proceed(request)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(userAgent)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val api: MangaDexApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(MangaDexApi::class.java)
    }

    /** Build a (thumbnail) cover URL from a manga id and cover file name. */
    fun coverUrl(mangaId: String, fileName: String, thumb: Boolean = true): String {
        val suffix = if (thumb) ".512.jpg" else ""
        return "$COVERS_URL/$mangaId/$fileName$suffix"
    }
}
