package com.zarki.app

import android.app.Application
import androidx.room.Room
import com.zarki.app.data.MangaRepository
import com.zarki.app.data.local.ZarkiDatabase

/**
 * Holds app-wide singletons. A lightweight service-locator — enough for v1
 * without pulling in a full DI framework.
 */
class ZarkiApplication : Application() {

    val database: ZarkiDatabase by lazy {
        Room.databaseBuilder(this, ZarkiDatabase::class.java, "zarki.db").build()
    }

    val repository: MangaRepository by lazy { MangaRepository() }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: ZarkiApplication
            private set
    }
}
