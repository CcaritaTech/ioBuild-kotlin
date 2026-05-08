package com.example.iobuild_kt

import android.app.Application
import com.example.iobuild_kt.core.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class IoBuildApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@IoBuildApp)
            modules(appModule)
        }
    }
}
