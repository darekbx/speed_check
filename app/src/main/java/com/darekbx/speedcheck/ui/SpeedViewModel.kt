package com.darekbx.speedcheck.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.darekbx.speedcheck.location.BaseLocationManager
import com.darekbx.speedcheck.location.LocationCollector

class SpeedViewModel(
    private val locationManager: BaseLocationManager,
    private val locationCollector: LocationCollector
) : ViewModel() {

    val locationFlow = locationCollector.locationFlow()

    fun isLocationEnabled() = mutableStateOf(locationManager.isLocationEnabled())
}
