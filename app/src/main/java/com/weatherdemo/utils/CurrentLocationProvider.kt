package com.weatherdemo.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender.SendIntentException
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.weatherdemo.interfaces.CurrentLocationResult
import com.weatherdemo.constants.MyConfig

class CurrentLocationProvider(var context: Activity, currentLocationResult: CurrentLocationResult) :
    ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
    var googleApiClient: GoogleApiClient?
    var locationManager: LocationManager? = null
    var isGPSEnabled = false
    var isNetworkEnabled = false
    var location : Location? = null
    var latitude = 0.0// latitude = 0.0
    var longitude = 0.0 // longitude = 0.0
    var currentLocationResult: CurrentLocationResult = currentLocationResult
    private val instance: GoogleApiClient
        get() = GoogleApiClient.Builder(context).addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this).addApi(LocationServices.API).build()

    private fun settingsRequest() {
        Log.e("settingsRequest", "Comes")
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 30 * 1000.toLong()
        locationRequest.fastestInterval = 5 * 1000.toLong()
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true) //this is the key ingredient
        val result =
            LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
        result.setResultCallback { result ->
            val status = result.status
            val state = result.locationSettingsStates
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> {
                }
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                    status.startResolutionForResult(
                        context,
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (e: SendIntentException) {
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Toast.makeText(
                    context,
                    "Location is Enabled",
                    Toast.LENGTH_SHORT
                ).show()
                LocationSettingsStatusCodes.CANCELED -> {
                }
            }
        }
    }

    override fun onConnected(@Nullable bundle: Bundle?) {}
    override fun onConnectionSuspended(i: Int) {}
    override fun onConnectionFailed(@NonNull connectionResult: ConnectionResult) {
        Toast.makeText(context, "Connection Failed", Toast.LENGTH_SHORT).show()
        Log.e(
            TAG,
            "" + connectionResult.toString() + " Error Message " + connectionResult.hashCode()
        )
    }

    // First get location from Network Provider

    // if GPS Enabled get lat/long using GPS Services
    @get:SuppressLint("MissingPermission")
    val currentLocation: Unit
        get() {
            try {
                val isPermission: Boolean = AndroidUtils.requestRuntimePermission(
                    context,
                    "Permission Require",
                    "Allow App to use GPS to get your current location.",
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MyConfig.REQUEST_CODE.LOCATION_REQUEST_CODE
                )
                if (isPermission) {
                    if (!canGetLocation()) {
                        settingsRequest()
                    } else {
                        // First get location from Network Provider
                        if (isNetworkEnabled) {
                            locationManager!!.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                0, 0f, this
                            )
                            Log.d("Network", "Network")
                            if (locationManager != null) {
                                location = locationManager!!
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                                if (location != null) {
                                    latitude = location!!.latitude
                                    longitude = location!!.longitude
                                }
                            }
                        }

                        // if GPS Enabled get lat/long using GPS Services
                        if (isGPSEnabled) {
                            if (location == null) {
                                locationManager!!.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    0, 0f, this
                                )
                                Log.d("GPS Enabled", "GPS Enabled")
                                if (locationManager != null) {
                                    location = locationManager!!
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER)
                                    if (location != null) {
                                        latitude = location!!.latitude
                                        longitude = location!!.longitude
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (location != null) {
                stopUsingGPS()
                currentLocationResult.gotCurrentLocation(location)
            }
        }

    private fun canGetLocation(): Boolean {
        locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // getting GPS status
        isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)

        // getting network status
        isNetworkEnabled = locationManager!!
            .isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        return isGPSEnabled || isNetworkEnabled
    }

    override fun onLocationChanged(location: Location) {
        currentLocationResult.gotCurrentLocation(location)
        stopUsingGPS()
        this.location = location
    }

    override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}
    override fun onProviderEnabled(s: String) {}
    override fun onProviderDisabled(s: String) {}
    fun stopUsingGPS() {
        if (locationManager != null) {
            locationManager!!.removeUpdates(this@CurrentLocationProvider)
        }
    }

    companion object {
        const val REQUEST_CHECK_SETTINGS = 0x1
        const val TAG = "StartLocationAlert"
    }

    init {
        googleApiClient = instance
        if (googleApiClient != null) {
            googleApiClient!!.connect()
            if (googleApiClient!!.isConnected) {
                if (googleApiClient!!.hasConnectedApi(LocationServices.API)) {
                    //Toast.makeText(context , "hasConnectedApi True", Toast.LENGTH_SHORT).show();
                }
            } else if (googleApiClient!!.isConnecting) {
                //Toast.makeText(context , "Please wait", Toast.LENGTH_SHORT).show();
            }
        }
    }
}