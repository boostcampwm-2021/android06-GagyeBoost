package com.example.gagyeboost.common

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import androidx.core.app.ActivityCompat
import java.util.*

class GPSUtils(private val context: Context) : LocationListener {

    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    fun getUserLocation(): Address = if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        getAddress(DEFAULT_LAT, DEFAULT_LNG)
    } else {
        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let {
            getAddress(it.latitude, it.longitude)
        } ?: getAddress(DEFAULT_LAT, DEFAULT_LNG)
    }


    private fun getAddress(lat: Double, lng: Double): Address {
        return Geocoder(context, Locale.getDefault()).getFromLocation(lat, lng, 10)[0]
    }

    override fun onLocationChanged(location: Location) {
    }
}
