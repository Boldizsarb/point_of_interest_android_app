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




class MainActivity : AppCompatActivity(), LocationListener {

    private lateinit var items: CustomItemizedIconOverlay
    private lateinit var markerGestureListener: ItemizedIconOverlay.OnItemGestureListener<OverlayItem>


    var poiList  = ArrayList<PoiEntity>()
    var netPoi  = ArrayList<PoiEntity>()

    private var poiName: String? = null
    private var poiType: String? = null
    private var poiDescription: String? = null
    private var poiLatitude: Double? = null
    private var poiLongitude: Double? = null

    private lateinit var mainMenu: Menu

    // initiating the two variables
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    lateinit var map1: MapView // this way the map1 is available everywhere!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Oncreate runs","Oncreate runs here ")
        // This line sets the user agent, a requirement to download OSM maps
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));



        setContentView(R.layout.activity_main)
        map1 = findViewById<MapView>(R.id.map1)
        map1.controller.setZoom(14.0)

        checkPermissions()
        map1.controller.setCenter(GeoPoint(latitude,longitude)) // setting it to the current address

        // MARKER
        markerGestureListener = object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
            override fun onItemLongPress(i: Int, item: OverlayItem): Boolean {
                Toast.makeText(this@MainActivity, item.snippet, Toast.LENGTH_SHORT).show()
                return true
            }

            override fun onItemSingleTapUp(i: Int, item: OverlayItem): Boolean {

                val poi =poiList[i]

                val message = "Name: ${poi.name}, Type: ${poi.type}, Description: ${poi.description}" // toast massage as well
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                // Inflate the custom layout
                val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val popupView = inflater.inflate(R.layout.popup_window, null)

                // Set the POI attributes
                popupView.findViewById<TextView>(R.id.poi_name).text = "Name: ${poi.name}"
                popupView.findViewById<TextView>(R.id.poi_type).text = "Type: ${poi.type}"
                popupView.findViewById<TextView>(R.id.poi_description).text = "Description: ${poi.description}"

                // Create the PopupWindow
                val popupWindow = PopupWindow(
                    popupView,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    true
                )

                // Show the PopupWindow
                //popupWindow.showAtLocation(map1, Gravity.CENTER, 0, 0)
                val markerPosition = map1.projection.toPixels(item.point, null)

                // Show the PopupWindow above the marker
                //popupWindow.showAsDropDown(map1, markerPosition.x, markerPosition.y - popupWindow.height)
                popupWindow.showAtLocation(map1, Gravity.TOP or Gravity.CENTER_HORIZONTAL, markerPosition.x, markerPosition.y - popupWindow.height)

                return true
            }
        }
        items = CustomItemizedIconOverlay(this, arrayListOf<OverlayItem>(), markerGestureListener)


        ///// click click
        map1.overlays.add(MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                // Check if the tap event occurred on a marker
                val tappedMarker = items.onSingleTap(p, map1)
                return if (tappedMarker) {
                    false // Let the CustomItemizedIconOverlay handle the tap event
                } else {
                    // Handle single tap on the map
                    //handleMapClick(p)
                    false // Return false to let the event propagate further
                }
            }

            override fun longPressHelper(p: GeoPoint): Boolean {
                // Check if the long press event occurred on a marker
                val tappedMarker = items.onSingleTap(p, map1)
                return if (tappedMarker) {
                    false // Let the CustomItemizedIconOverlay handle the long press event
                } else {
                    // Handle long press on the map
                    handleMapClick(p)
                    false // Return false to let the event propagate further
                }
            }
        }))

    }
    //val poiUpload =
//




    class CustomItemizedIconOverlay(
        context: Context,
        private val overlayItems: ArrayList<OverlayItem>,
        listener: OnItemGestureListener<OverlayItem>
    ) : ItemizedIconOverlay<OverlayItem>(context, overlayItems, listener) {

        fun onSingleTap(eventPos: GeoPoint, mapView: MapView): Boolean {
            val screenPoint = mapView.getProjection().toPixels(eventPos, null)
            val hitItems = ArrayList<OverlayItem>()

            for (item in overlayItems) {
                val itemPoint = mapView.getProjection().toPixels(item.point, null)
                val itemRect = getRect(itemPoint.x, itemPoint.y, mapView.resources.displayMetrics.density)
                if (itemRect.contains(screenPoint.x, screenPoint.y)) {
                    hitItems.add(item)
                }
            }

            return hitItems.isNotEmpty()
        }

        private fun getRect(centerX: Int, centerY: Int, density: Float): Rect {
            val iconWidth = (mDefaultMarker.intrinsicWidth * density).toInt()
            val iconHeight = (mDefaultMarker.intrinsicHeight * density).toInt()
            return Rect(centerX - iconWidth / 2, centerY - iconHeight / 2, centerX + iconWidth / 2, centerY + iconHeight / 2)
        }
    }
    //////// MARKER////////

    private fun handleMapClick(p: GeoPoint) { // Get the latitude and longitude of the clicked location

        val latitude = p.latitude   //// saving the click value before openeing the other side
        val longitude = p.longitude

        poiLatitude = p.latitude
        poiLongitude = p.longitude

        Log.d("AddPoi", "lat: $latitude, long: $longitude")

        val menuItem = mainMenu.findItem(R.id.addpoi)  /// imitating click
        onOptionsItemSelected(menuItem)

    }
//////////////// click click click

    val addPoitLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            // Retrieve the POI details from the intent and assign them to the corresponding variables
            poiName = data?.getStringExtra("nameText")
            poiType = data?.getStringExtra("type")
            poiDescription = data?.getStringExtra("description")
            // Log the POI details
            Log.d("from the launcher", "Name: $poiName, Type: $poiType, Description: $poiDescription, Latitude: $poiLatitude, Longitude: $poiLongitude")
            if (poiName != null && poiType != null && poiDescription != null && poiLatitude != null && poiLongitude != null) {
                val newPoi = PoiEntity(
                    0L,
                    poiName!!,
                    poiType!!,
                    poiDescription!!,
                    poiLatitude!!,
                    poiLongitude!!
                )
                poiList.add(newPoi)
                Log.d("list", "$newPoi")
            }else{
                Toast.makeText(this, "You need to fill up all the ", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext) //Accessing the preferences
       // val autodownload = prefs.getBoolean("autodownload", true) ?: true //example of retrieving the boolean
        val webUpload = prefs.getBoolean("web",false)?:false
        Log.d("web preference is:" ,"$webUpload") /// will need to make it conditional
        if (webUpload){
            Log.d("runs here","runingingingnig")
            if (poiName != null && poiType != null && poiDescription != null && poiLatitude != null && poiLongitude != null) {
                Log.d("values now","the values are not empty")
                // post request goes here
                val url = "http://10.0.2.2:3000/poi/create"
                val json = JSONObject()
                json.put("name", poiName)
                json.put("type", poiType)
                json.put("description", poiDescription)
                json.put("lat", poiLatitude)
                json.put("lon", poiLongitude)

// Send the POST request with the JSON object as the request body
                Fuel.post(url)
                    .header("Content-Type", "application/json")
                    .body(json.toString())
                    .response { request, response, result ->
                        result.fold(
                            { response ->
                                // Successful response
                                Toast.makeText(this@MainActivity, result.get().decodeToString(), Toast.LENGTH_LONG).show()
                            },
                            { error ->
                                // Error response
                                Log.e("POST request", "Error: ${error.message}")
                                Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_LONG).show()
                            }
                        )
                    }
            }/// end
        }

        if (poiName != null && poiType != null && poiDescription != null && poiLatitude != null && poiLongitude != null) {
            val item =
                OverlayItem("$poiName", "$poiType", GeoPoint(poiLatitude!!, poiLongitude!!))

            // add it to a list as well
            items.addItem(item)
            map1.overlays.add(items)
        }else{
            Toast.makeText(this, "fill up everything", Toast.LENGTH_SHORT).show()
        }

        //Log.d("it runs:", "it runs")
        // Check if there are any POI details to log
        if (poiName != null && poiType != null && poiDescription != null && poiLatitude != null && poiLongitude != null) {
            // Log the POI details
        Log.d("name", "name $poiName")
            Log.d("MainActivity2", "Name: $poiName, Type: $poiType, Description: $poiDescription, Latitude: $poiLatitude, Longitude: $poiLongitude")
            poiName = null
            poiName = null
            poiType = null
            poiDescription = null
            poiLatitude = null
            poiLongitude = null
        }
        Log.d("MainActivity2", "Name: $poiName, Type: $poiType, Description: $poiDescription, Latitude: $poiLatitude, Longitude: $poiLongitude")
    }



    override fun onCreateOptionsMenu(menu: Menu) : Boolean {  // inflating the menu
        // In Kotlin, we can just use 'menuInflater' instead
        menuInflater.inflate(R.menu.menu, menu)
        mainMenu = menu
        return true      // indication if succescful
    }

    override fun onOptionsItemSelected(item: MenuItem) : Boolean {
        when(item.itemId) {
            R.id.addpoi -> { val intent = Intent(this,AddPoi::class.java)
                addPoitLauncher.launch(intent)
                return true
            }
        }
        when(item.itemId) {
            R.id.save -> { //val intent = Intent(this,AddPoi::class.java) // not launching activity
                val db = PoiDatabase.getDatabase(application)
                for (tempoi in poiList){
                    val name = tempoi.name
                    val type = tempoi.type
                    val desc = tempoi.description
                    val lat = tempoi.lattitude
                    val long = tempoi.longitude
                    lifecycleScope.launch {
                        var insertId = 0L
                        withContext(Dispatchers.IO) {
                            val tempsavedpoi = PoiEntity(0,name,type,desc,lat,long)
                            insertId = db.PoiDAO().insert(tempsavedpoi)
                        }
                    }
                }
                Toast.makeText(this, "Upload successful!", Toast.LENGTH_SHORT).show()
                poiList.clear() // clearing it after that
                return true
            }
        }
        when(item.itemId){
            R.id.pref ->{
                val intent = Intent(this,Preferences::class.java)
                startActivity(intent)
                return true
            }
        }
        when(item.itemId) {
            R.id.allpoi -> { // task 5
                poiList.clear()
                items.removeAllItems()

                val db = PoiDatabase.getDatabase(application)
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        poiList.addAll(db.PoiDAO().getAllPoi())
                    }
                    for (poi in poiList) {
                        val item = OverlayItem(poi.name, poi.type, GeoPoint(poi.lattitude, poi.longitude))
                        items.addItem(item)
                    }
                    map1.overlays.add(items)
                    map1.invalidate() // Refresh the map to display the new markers

                }
                Log.d("allpois","$poiList")
                poiList.clear()

                // could make a toast with the confirmation of upload!
                return true
            }
        }
        when(item.itemId) {
            R.id.webpoi -> { // task 6
                var url = "http://10.0.2.2:3000/poi/all"
                poiList.clear()
                items.removeAllItems()

                Fuel.get(url).response {  request, response, result ->
                    result.fold(
                        success = { data ->
                            val responseJson = String(data)
                            val jsonArray = JSONArray(responseJson)
                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                val name = jsonObject.getString("name")
                                val type = jsonObject.getString("type")
                                val description = jsonObject.getString("description")
                                val latitude = jsonObject.getDouble("lat")
                                val longitude = jsonObject.getDouble("lon")
                                val poiEntity = PoiEntity(0, name, type, description, latitude, longitude)
                                poiList.add(poiEntity)
                            }
                            // Do something with the netPoi list
                            Log.d("net poi:", "$netPoi")
                            for (poi in poiList) {
                                val item = OverlayItem(poi.name, poi.type, GeoPoint(poi.lattitude, poi.longitude))
                                items.addItem(item)
                            }

                            map1.overlays.add(items)
                            map1.invalidate()
                            //poiList.clear()   // reset // can not othwervise it will throw an eror

                        },
                        failure = { error ->
                            Log.e("Fuel", "Error: $error")
                        }
                    )
                }
                return true
            }
        }
        return false
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
            0->{ // in case of multiple permission, the grantResults[0] is the first permission (Camera) grantResults[1] is the NFC
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
        map1.controller.setCenter(GeoPoint(latitude,longitude)) // had to declare it here to load the initial location
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