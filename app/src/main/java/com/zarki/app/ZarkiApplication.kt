package com.zarki.app

import android.app.Application
import androidx.room.Room
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import com.zarki.app.data.BackupManager
import com.zarki.app.data.DownloadManager
import com.zarki.app.data.local.ZarkiDatabase
import com.zarki.app.data.settings.SettingsStore
import com.zarki.app.data.source.SourceManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient

/**
 * Holds app-wide singletons. A lightweight service-locator — enough without
 * pulling in a full DI framework. Also configures Coil's image loader so manga
 * page images (served by external CDNs) load reliably with a real User-Agent.
 */
class ZarkiApplication : Application(), ImageLoaderFactory {

    val database: ZarkiDatabase by lazy {
        Room.databaseBuilder(this, ZarkiDatabase::class.java, "zarki.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    val sources: SourceManager by lazy { SourceManager() }

    val settings: SettingsStore by lazy { SettingsStore(this) }

    val downloads: DownloadManager by lazy {
        DownloadManager(this, database.downloadDao(), sources)
    }

    val backup: BackupManager by lazy {
        BackupManager(database.libraryDao(), database.progressDao())
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override fun newImageLoader(): ImageLoader {
        val userAgent = Interceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .header("User-Agent", "Zarki/1.0 (Android manga reader)")
                    .build(),
            )
        }
        return ImageLoader.Builder(this)
            .okHttpClient {
                OkHttpClient.Builder().addInterceptor(userAgent).build()
            }
            .crossfade(true)
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.05)
                    .build()
            }
            .build()
    }

    companion object {
        lateinit var instance: ZarkiApplication
            private set
    }
}
