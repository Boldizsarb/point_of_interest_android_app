package com.example.bolidzsar_banfia

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.location.LocationListener
import androidx.appcompat.app.AlertDialog
import android.location.LocationManager
import android.location.Location
import android.util.Log
import android.view.*
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.OverlayItem
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import androidx.fragment.app.activityViewModels


class MainActivity : AppCompatActivity(), LocationListener {


    private var poiLatitude: Double? = null
    private var poiLongitude: Double? = null

    // initiating the two variables
    var latitude: Double = 0.0
    var longitude: Double = 0.0

     lateinit var map1: MapView //


    val viewModel: TestViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Oncreate runs","Oncreate runs here ")
        // This line sets the user agent, a requirement to download OSM maps
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map1 = MapView(this)

        setContentView(R.layout.activity_main)

        map1.controller.setZoom(14.0)

        checkPermissions()
        //map1.controller.setCenter(GeoPoint(latitude,longitude)) // setting it to the current address
        Log.d("lat from main","$latitude")
        Log.d("lat from main","$poiLatitude")
    }



    override fun onResume() {
        super.onResume()
        map1.controller.setZoom(14.0)
        map1.controller.setCenter(GeoPoint(latitude,longitude))
        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext) //Accessing the preferences

    }

    fun checkPermissions(){ // this is my own function


        if(ContextCompat.checkSelfPermission(this,  Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED){
            requestLocation()

            // 2.if the permission was not granted yet, request it from the user
        }else{
            // an array of permissions, note that you can request multiple at once
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),0)
        }
    }
    // 3. after the user granted or denied permisssion, the program calls this
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults) // part of the android API
        when(requestCode){ //  results 0 = Granted, results 1 = Denied
            0->{
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    requestLocation()
                }else{ // informing the user about the decesion
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun requestLocation(){
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500000, 0f, this)
        }
    }

    override fun onLocationChanged(location: Location){
        latitude = location.latitude
        longitude = location.longitude
        poiLatitude = location.latitude
        poiLongitude = location.longitude
        Toast.makeText(this, "Location: ${location.latitude}, ${location.longitude}", Toast.LENGTH_LONG).show()
        //map1.controller.setCenter(GeoPoint(latitude,longitude)) // had to declare it here to load the initial location
        Log.d("lat from mainchanged","$latitude")
        viewModel.currentlat = latitude   // centering the map
        viewModel.currentlon = longitude
    }

    override fun onProviderDisabled(provider: String) {
        Toast.makeText(this, "Provider disabled", Toast.LENGTH_LONG).show()
    }

    override fun onProviderEnabled(provider: String) {
        Toast.makeText(this, "Provider enabled", Toast.LENGTH_LONG).show()
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        // Do nothing
    }




}