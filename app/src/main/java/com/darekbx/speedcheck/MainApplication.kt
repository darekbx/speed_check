package com.darekbx.speedcheck

import android.app.Application
import com.darekbx.speedcheck.di.appModule
import com.darekbx.speedcheck.di.modulesRuntime
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(modulesRuntime + appModule)
        }
    }
}
