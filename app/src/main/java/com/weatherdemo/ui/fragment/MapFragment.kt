package com.weatherdemo.ui.fragment

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.weatherdemo.R
import com.weatherdemo.autocompletelocation.AutoCompleteAdapter
import com.weatherdemo.base.BaseFragment
import com.weatherdemo.constants.MyConfig
import com.weatherdemo.entity.CityMaster
import com.weatherdemo.interfaces.CurrentLocationResult
import com.weatherdemo.room.AppDatabase
import com.weatherdemo.ui.activity.MainActivity
import com.weatherdemo.utils.AndroidUtils
import com.weatherdemo.utils.CurrentLocationProvider
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.toolbar_center.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.StringBuilder
import java.util.*

class MapFragment : BaseFragment(), OnMapReadyCallback, CurrentLocationResult {

    private var googleMap: GoogleMap? = null

    private var currentLocationProvider: CurrentLocationProvider? = null
    private var isLocationPermissionGranted = true
    private var isCurrentLocationClicked = false

    private var currentLatitude = 0.0
    private var currentLongitude = 0.0
    private var city = ""

    private var origin: LatLng? = null

    private lateinit var placesClient: PlacesClient
    private lateinit var mAdapter: AutoCompleteAdapter

    override fun getLayoutId(): Int {
        return R.layout.fragment_map
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle()
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this@MapFragment)

        setUpPlaceAPI()
        setUpAutoCompleteTextView()

        ivMenu.setOnClickListener {
            (activity as MainActivity).openCloseDrawer()
        }

        btnNext.setOnClickListener {

            CoroutineScope(IO).launch {
                insertDataInCityMaster(CityMaster(city, currentLatitude, currentLongitude), activity!!.application)
            }
            Toast.makeText(activity!!, R.string.city_added_successfully, Toast.LENGTH_SHORT).show()
        }

        llDirection.setOnClickListener {
            isCurrentLocationClicked = true
            callLocationService()
        }

        ivClear.setOnClickListener {
            autocompleteTextView.setText("")

        }

        autocompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (char.toString() == "") {
                    ivClear.visibility = View.GONE
                } else {
                    ivClear.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun setTitle() {
        tvTitle.text = getString(R.string.map)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    private fun insertDataInCityMaster(cityMaster: CityMaster, application: Application) {
        AppDatabase.get(application).getCityMasterDao().insertCityMaster(cityMaster)
    }

    override fun onMapReady(gMap: GoogleMap) {
        this.googleMap = gMap
        if (googleMap != null) {
            googleMap!!.uiSettings.isZoomControlsEnabled = false
            googleMap!!.uiSettings.isZoomGesturesEnabled = true
            googleMap!!.uiSettings.isMapToolbarEnabled = false
            callLocationService()

            googleMap!!.setOnMapClickListener {
                currentLatitude = it.latitude
                currentLongitude = it.longitude
                setMapPin()
            }
        }
    }

    private fun setUpPlaceAPI() {

        val apiKey = getString(R.string.google_place_api_key)

        if (!Places.isInitialized()) {
            Places.initialize(activity!!, apiKey)
        }
        placesClient = Places.createClient(activity!!)
    }

    private fun setUpAutoCompleteTextView() {
        autocompleteTextView.threshold = 1
        autocompleteTextView.onItemClickListener = autocompleteClickListener
        mAdapter = AutoCompleteAdapter(activity!!, placesClient)
        autocompleteTextView.setAdapter(mAdapter)
    }

    private var autocompleteClickListener: AdapterView.OnItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val item: AutocompletePrediction? = mAdapter.getItem(position)
                val placeID = item?.placeId
                val placeFields: List<Place.Field> = listOf(
                        Place.Field.ID,
                        Place.Field.NAME,
                        Place.Field.ADDRESS,
                        Place.Field.LAT_LNG
                )
                var request: FetchPlaceRequest? = null
                if (placeID != null) {
                    request = FetchPlaceRequest.builder(placeID, placeFields).build()
                }

                if (request != null) {
                    placesClient.fetchPlace(request).addOnSuccessListener {
                        val place = it.place
                        val stringBuilder = StringBuilder()
                        stringBuilder.append("Name: ${place.name}\n")
                        val queriedLocation: LatLng? = place.latLng
                        stringBuilder.append("Latitude: ${queriedLocation?.latitude}\n")
                        stringBuilder.append("Longitude: ${queriedLocation?.longitude}\n")
                        stringBuilder.append("Address: ${place.address}\n")
                        origin = place.latLng
                        currentLatitude = queriedLocation?.latitude!!
                        currentLongitude = queriedLocation.longitude

                        addMarker()
                        getCityFromLatLongAndSetInTextView()


                    }.addOnFailureListener {
                        it.printStackTrace()
                    }
                }
                AndroidUtils.hideSoftKeyboard(activity!!)
            }

    override fun gotCurrentLocation(location: Location?) {
        if (location != null) {

            isCurrentLocationClicked = false
            currentLatitude = location.latitude
            currentLongitude = location.longitude
            Log.e("currentLatitude", currentLatitude.toString() + "")
            Log.e("currentLongitude", currentLongitude.toString() + "")

            setMapPin()
        }
    }

    private fun setMapPin() {
        if (currentLatitude != 0.0 && currentLongitude != 0.0) {
            googleMap!!.clear()
            origin = LatLng(currentLatitude, currentLongitude)
            addMarker()
            getCityFromLatLongAndSetInTextView()
        }
    }

    private fun addMarker() {
        val markerOptions = MarkerOptions()
        markerOptions.position(origin!!)
        markerOptions.title(origin!!.latitude.toString() + " : " + origin!!.longitude)

        googleMap!!.clear()
        googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 14f))
        googleMap!!.addMarker(markerOptions)
    }

    private fun getCityFromLatLongAndSetInTextView() {
        if (currentLatitude != 0.0 && currentLongitude != 0.0) {

            var addresses: List<Address>? = null
            val geoCoder = Geocoder(activity, Locale.getDefault())
            try {
                addresses = geoCoder.getFromLocation(currentLatitude, currentLongitude, 1)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    if (addresses[0].locality != null) {
                        city = addresses[0].locality
                        Log.e("CITY", city)
                    } else if (addresses[0].subAdminArea != null) {
                        city = addresses[0].subAdminArea
                        Log.e("CITY", city)
                    }
                    autocompleteTextView.setText(city)
                } else {
                    autocompleteTextView.setText("")
                }
                autocompleteTextView.setSelection(autocompleteTextView.text.length)
                autocompleteTextView.dismissDropDown()
            }
        }
    }

    private fun callLocationService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity!!.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                    && activity!!.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
            ) {
                currentLocationProvider = CurrentLocationProvider(activity!!, this)
                currentLocationProvider!!.currentLocation
            } else {
                checkForLocationPermission()
            }
        } else {
            currentLocationProvider = CurrentLocationProvider(activity!!, this)
            currentLocationProvider!!.currentLocation
        }
    }

    private fun checkForLocationPermission() {
        isLocationPermissionGranted = true
        var hasAccessFineLocation = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hasAccessFineLocation =
                    activity!!.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        var hasAccessCoarseLocation = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hasAccessCoarseLocation =
                    activity!!.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        val permissions: MutableList<String> =
                ArrayList()
        if (hasAccessFineLocation != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (hasAccessCoarseLocation != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (permissions.isNotEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        permissions.toTypedArray(),
                        MyConfig.REQUEST_CODE.REQUEST_CODE_PERMISSION
                )
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray
    ) {
        when (requestCode) {
            MyConfig.REQUEST_CODE.REQUEST_CODE_PERMISSION -> {
                var i = 0
                while (i < permissions.size) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.d("Permissions", "Permission Granted: " + permissions[i])
                        if (i == permissions.size - 1) {
                            if (isLocationPermissionGranted) {
                                currentLocationProvider = CurrentLocationProvider(activity!!, this@MapFragment)
                                currentLocationProvider!!.currentLocation
                            }
                        }
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        Log.e("Permissions", "Permission Denied: " + permissions[i])
                        isLocationPermissionGranted = false
                    }
                    i++
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            MyConfig.REQUEST_CODE.REQUEST_CHECK_SETTINGS -> when (resultCode) {
                Activity.RESULT_OK -> {
                    val currentLocationProvider =
                            CurrentLocationProvider(activity!!, this@MapFragment)
                    currentLocationProvider.currentLocation
                }
                Activity.RESULT_CANCELED -> Toast.makeText(activity!!, R.string.enable_gps, Toast.LENGTH_SHORT).show()
            }
        }
    }
}