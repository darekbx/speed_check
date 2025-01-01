package com.darekbx.speedcheck.di

import android.content.Context
import android.location.LocationListener
import android.location.LocationManager
import com.darekbx.speedcheck.location.BaseLocationManager
import com.darekbx.speedcheck.location.DefaultLocationManager
import com.darekbx.speedcheck.location.LocationCollector
import com.darekbx.speedcheck.ui.SpeedViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val modulesPreview = module {
    single<BaseLocationManager> {
        object : BaseLocationManager {
            override fun isLocationEnabled(): Boolean = true

            override fun removeUpdates(listener: LocationListener) { }

            override fun requestLocationUpdates(
                provider: String,
                minTimeMs: Long,
                minDistanceM: Float,
                listener: LocationListener
            ) { }
        }
    }
}

val modulesRuntime = module {
    single<BaseLocationManager> { DefaultLocationManager(get()) }
}

val appModule = module {
    single { androidContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager }
    single { LocationCollector(get()) }

    viewModel { SpeedViewModel(get(), get()) }
}
