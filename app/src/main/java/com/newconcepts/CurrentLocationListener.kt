package com.newconcepts

import android.annotation.SuppressLint
import android.arch.lifecycle.LiveData
import android.content.Context
import android.location.Location
import com.google.android.gms.location.*

class CurrentLocationListener private constructor(context: Context) : LiveData<Location>() {

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLocationRequest: LocationRequest? = null
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            value = result?.lastLocation
        }
    }

    init {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        mFusedLocationClient?.lastLocation?.addOnSuccessListener { value = it }
        createLocationRequest()
    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    @SuppressLint("MissingPermission")
    override fun onActive() {
        super.onActive()
        mFusedLocationClient?.requestLocationUpdates(mLocationRequest, mLocationCallback, null)
    }

    override fun onInactive() {
        super.onInactive()
        mFusedLocationClient?.removeLocationUpdates(mLocationCallback)
    }


    companion object {

        @Volatile
        private var INSTANCE: CurrentLocationListener? = null

        fun getInstance(context: Context): CurrentLocationListener =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildLocationUpdateListener(context).also { INSTANCE = it }
                }

        private fun buildLocationUpdateListener(context: Context) =
                CurrentLocationListener(context)
    }
}