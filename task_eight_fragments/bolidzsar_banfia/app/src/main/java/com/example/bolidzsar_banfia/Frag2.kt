package com.example.bolidzsar_banfia


import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayItem



class Frag2 : androidx.fragment.app.Fragment(R.layout.frag2) {
    val viewModel : TestViewModel by activityViewModels()
    var lat = 0.0
    var lon = 0.0
    var finalLat = 0.0
    var finalLon = 0.0
    val viewModell : poiViewModel by viewModels()

    private val map1: MapView by lazy {   //  it will only be initialized the first time it is accessed.
        requireView().findViewById(R.id.map1)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val map1 = view.findViewById<MapView>(R.id.map1)
        map1.controller.setZoom(14.0)

        viewModel.livelat.observe(this.viewLifecycleOwner, Observer {newLat ->
            lat = newLat
            updateFinalLat()
            //Log.d("this is lat  from frag2","lat is :$lat")

        })
        viewModel.livelon.observe(this.viewLifecycleOwner, Observer {newLon ->
            lon = newLon
            updateFinalLat()
            //Log.d("this is lat  from frag2","lat is :$lon")

        })

        viewModell.getAllPoi().observe(this.viewLifecycleOwner, Observer {poilist ->

            var newlist = ArrayList<PoiEntity>()
            newlist.addAll(poilist)

            newlist.forEach { poi ->  // each of them working
                //Log.d("database", poi.toString())
                val marker = Marker(map1)

                marker.position = GeoPoint(poi.lattitude, poi.longitude)

                val markerBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(markerBitmap)
                val paint = Paint().apply {
                    color = Color.RED
                    style = Paint.Style.FILL
                }
                canvas.drawCircle(10f, 10f, 10f, paint)
                marker.icon = BitmapDrawable(resources, markerBitmap)

                marker.title = poi.name
                marker.snippet = "${poi.type}, ${poi.description}"
                // Adding the marker to the map
                map1.overlays.add(marker)
            }
            //Log.d("databse",)
        })

    }

    private fun updateFinalLat() {  // centering the map
        finalLat = lat
        finalLon = lon
        Log.d("finalLat","lat is :$finalLat")
        Log.d("finalLon","lat is :$finalLon")
        map1.controller.setCenter(GeoPoint(finalLat,finalLon))
    }

}
