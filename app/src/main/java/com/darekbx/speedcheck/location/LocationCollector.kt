package com.darekbx.speedcheck.location

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn

class LocationCollector(private val locationManager: BaseLocationManager) {

    fun locationFlow(): Flow<Location> {
        return _locationUpdates
    }

    @SuppressLint("MissingPermission")
    private val _locationUpdates = callbackFlow {

        val locationListener = LocationListener { location ->
            trySend(location)
        }

        Log.v(TAG, "Request location updates")
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            UPDATE_INTERVAL,
            MIN_DISTANCE,
            locationListener
        )

        awaitClose {
            Log.v(TAG, "Stop location updates")
            locationManager.removeUpdates(locationListener)
        }
    }.flowOn(Dispatchers.Main)

    companion object {
        private const val TAG = "LocationCollector"
        private const val MIN_DISTANCE = 1F // One meter
        private const val UPDATE_INTERVAL = 1000L // One second
    }
}
