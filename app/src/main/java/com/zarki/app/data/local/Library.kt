package com.zarki.app.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
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

@Database(entities = [LibraryManga::class], version = 1, exportSchema = false)
abstract class ZarkiDatabase : RoomDatabase() {
    abstract fun libraryDao(): LibraryDao
}
