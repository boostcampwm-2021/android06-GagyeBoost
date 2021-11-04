package com.example.gagyeboost.common

import android.app.Application
import com.example.gagyeboost.di.appModule
import com.example.gagyeboost.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(appModule, viewModelModule)
        }
    }
}
