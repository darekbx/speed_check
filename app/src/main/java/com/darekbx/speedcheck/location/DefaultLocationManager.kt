package com.darekbx.speedcheck.location

import android.annotation.SuppressLint
import android.location.LocationListener
import android.location.LocationManager

interface BaseLocationManager {
    fun isLocationEnabled(): Boolean
    fun removeUpdates(listener: LocationListener)

    fun requestLocationUpdates(
        provider: String,
        minTimeMs: Long,
        minDistanceM: Float,
        listener: LocationListener
    )
}

class DefaultLocationManager (
    private val locationManager: LocationManager
) : BaseLocationManager {

    override fun isLocationEnabled(): Boolean {
        return locationManager.isLocationEnabled
    }

    @SuppressLint("MissingPermission")
    override fun requestLocationUpdates(
        provider: String,
        minTimeMs: Long,
        minDistanceM: Float,
        listener: LocationListener
    ) {
        locationManager.requestLocationUpdates(provider, minTimeMs, minDistanceM, listener)
    }

    override fun removeUpdates(listener: LocationListener) {
        locationManager.removeUpdates(listener)
    }
}
