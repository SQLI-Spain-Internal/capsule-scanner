package com.sqli.capsulescanner

import android.app.Application
import android.content.Context
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {
    companion object {
        lateinit var instance: Context
            private set
    }
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this.applicationContext)
        instance = this
    }
}