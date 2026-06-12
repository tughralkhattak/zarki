package com.zarki.app

import android.app.Application
import androidx.room.Room
import com.zarki.app.data.local.ZarkiDatabase
import com.zarki.app.data.settings.SettingsStore
import com.zarki.app.data.source.SourceManager

/**
 * Holds app-wide singletons. A lightweight service-locator — enough without
 * pulling in a full DI framework.
 */
class ZarkiApplication : Application() {

    val database: ZarkiDatabase by lazy {
        Room.databaseBuilder(this, ZarkiDatabase::class.java, "zarki.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    val sources: SourceManager by lazy { SourceManager() }

    val settings: SettingsStore by lazy { SettingsStore(this) }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: ZarkiApplication
            private set
    }
}
