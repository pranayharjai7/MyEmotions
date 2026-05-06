package com.pranayharjai7.myemotions.ui.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.pranayharjai7.myemotions.domain.repository.LocationRepository
import kotlinx.coroutines.tasks.await

suspend fun getCurrentLocation(context: Context): com.google.android.gms.maps.model.LatLng? {
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        return null
    }

    return try {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val location = fusedLocationClient.lastLocation.await()
        location?.let { com.google.android.gms.maps.model.LatLng(it.latitude, it.longitude) }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

suspend fun fetchAndUpdateLocation(context: Context, locationRepository: LocationRepository) {
    getCurrentLocation(context)?.let { latLng ->
        locationRepository.updateLocation(latLng.latitude, latLng.longitude)
    }
}
