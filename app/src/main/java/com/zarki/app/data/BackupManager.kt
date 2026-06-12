package com.zarki.app.data

import com.zarki.app.data.local.LibraryDao
import com.zarki.app.data.local.LibraryManga
import com.zarki.app.data.local.ProgressDao
import com.zarki.app.data.local.ReadProgress
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/** The shape of a Zarki backup file. */
@Serializable
data class BackupData(
    val version: Int = 1,
    val app: String = "Zarki",
    val library: List<LibraryManga> = emptyList(),
    val history: List<ReadProgress> = emptyList(),
)

/**
 * Exports the user's library + reading history to a JSON string they can save,
 * and restores it back. Lets users move their data between devices or keep a
 * safe backup.
 */
class BackupManager(
    private val library: LibraryDao,
    private val progress: ProgressDao,
) {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    suspend fun exportJson(): String {
        val data = BackupData(
            library = library.allOnce(),
            history = progress.allProgress(),
        )
        return json.encodeToString(data)
    }

    /** Restores a backup; returns the number of items imported. */
    suspend fun importJson(text: String): Int {
        val data = json.decodeFromString<BackupData>(text)
        data.library.forEach { library.add(it) }
        data.history.forEach { progress.upsert(it) }
        return data.library.size + data.history.size
    }
}
