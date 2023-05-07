package com.example.bolidzsar_banfia

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Frag1 : androidx.fragment.app.Fragment(R.layout.frag1) {

    var name = ""   // global variable
    var typeText = ""
    var descText = ""
    var lat = 0.0
    var lon = 0.0
    val viewModel : TestViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val etName = view.findViewById<EditText>(R.id.etName)
        val type = view.findViewById<EditText>(R.id.type)
        val desc = view.findViewById<EditText>(R.id.description)

        //// retrieving the lat and lon
        viewModel.livelon.observe(this.viewLifecycleOwner, Observer {newLon ->
            lon = newLon
        })

        viewModel.livelat.observe(this.viewLifecycleOwner, Observer {newLat ->
            lat = newLat
        })

        view.findViewById<Button>(R.id.btn1).setOnClickListener {

            lon
            lat
            name  = etName.text.toString()
            typeText = type.text.toString()
            descText = desc.text.toString()

            if (name.isEmpty()) {
                etName.error = "Please enter a name"
                return@setOnClickListener
            }
            if (typeText.isEmpty()) {
                type.error = "Please enter the type"
                return@setOnClickListener
            }
            if (descText.isEmpty()) {
                desc.error = "Please enter the type"
                return@setOnClickListener
            }
           // Log.d("attributes", "name: $name type: $typeText desc: $descText lat: $lat lon:$lon")

            val db = PoiDatabase.getDatabase(requireContext())

            lifecycleScope.launch {
                var insertId = 0L
                withContext(Dispatchers.IO) {
                    val tempsavedpoi = PoiEntity(0,name,typeText,descText,lat,lon)
                    insertId = db.PoiDAO().insert(tempsavedpoi)
                    //Log.d("POI inserted with ID", insertId.toString())
                    etName.setText("")
                    type.setText("")
                    desc.setText("")

                }
            }
        }
    }
}

