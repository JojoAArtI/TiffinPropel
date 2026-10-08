package com.propel.tiffin

import android.app.Application
import com.propel.tiffin.di.AppContainer

class TiffinApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
