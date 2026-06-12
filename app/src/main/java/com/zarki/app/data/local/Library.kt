package com.zarki.app.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

/** A manga the user has saved to their library. */
@Entity(tableName = "library")
data class LibraryManga(
    @PrimaryKey val id: String,
    val title: String,
    val coverUrl: String?,
    val addedAt: Long,
)

/** Per-chapter reading progress — powers read-state, "continue", and history. */
@Entity(tableName = "progress")
data class ReadProgress(
    @PrimaryKey val chapterId: String,
    val mangaId: String,
    val mangaTitle: String,
    val coverUrl: String?,
    val chapterNumber: String,
    val lastPage: Int,
    val totalPages: Int,
    val read: Boolean,
    val updatedAt: Long,
)

@Dao
interface LibraryDao {
    @Query("SELECT * FROM library ORDER BY addedAt DESC")
    fun observeAll(): Flow<List<LibraryManga>>

    @Query("SELECT EXISTS(SELECT 1 FROM library WHERE id = :id)")
    fun isSaved(id: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(manga: LibraryManga)

    @Query("DELETE FROM library WHERE id = :id")
    suspend fun remove(id: String)
}

@Dao
interface ProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(progress: ReadProgress)

    @Query("SELECT chapterId FROM progress WHERE mangaId = :mangaId AND read = 1")
    fun readChapterIds(mangaId: String): Flow<List<String>>

    @Query("SELECT * FROM progress WHERE mangaId = :mangaId ORDER BY updatedAt DESC LIMIT 1")
    suspend fun lastRead(mangaId: String): ReadProgress?

    @Query("SELECT * FROM progress ORDER BY updatedAt DESC LIMIT 100")
    fun history(): Flow<List<ReadProgress>>

    @Query("DELETE FROM progress")
    suspend fun clearHistory()
}

@Database(
    entities = [LibraryManga::class, ReadProgress::class],
    version = 2,
    exportSchema = false,
)
abstract class ZarkiDatabase : RoomDatabase() {
    abstract fun libraryDao(): LibraryDao
    abstract fun progressDao(): ProgressDao
}
