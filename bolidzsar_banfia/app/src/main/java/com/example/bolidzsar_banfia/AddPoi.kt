package com.example.bolidzsar_banfia

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.util.Log
import android.view.Menu
import android.widget.Button
import android.view.View
import android.widget.EditText


class AddPoi: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addpoi_layout)

        val latitude = intent.getDoubleExtra("latitude",0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)

        Log.d("AddPoi from add poi ", "lat: $latitude, long: $longitude")

        val buttonka = findViewById<Button>(R.id.submitButton)
        buttonka.setOnClickListener{
            val name = findViewById<EditText>(R.id.editText1)
            val type = findViewById<EditText>(R.id.editText2)
            val description = findViewById<EditText>(R.id.editText3)
            val nameText = name.text.toString()
            val typeText = type.text.toString()
            val descriptionText = description.text.toString()

            if (nameText.isEmpty()) {
                name.error = "Please enter a name"
                return@setOnClickListener
            }

            if (typeText.isEmpty()) {
                type.error = "Please enter a type"
                return@setOnClickListener
            }

            if (descriptionText.isEmpty()) {
                type.error = "Please enter a type"
                return@setOnClickListener
            }

            val resultIntent = Intent(this, MainActivity::class.java )

            resultIntent.putExtra("nameText", nameText)
            Log.d("nameText ", "name here: $nameText" )
            resultIntent.putExtra("type", typeText)
            resultIntent.putExtra("description", descriptionText)
            resultIntent.putExtra("latitude",latitude)
            resultIntent.putExtra("longitude",longitude)
            Log.d("result", "$resultIntent")
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }


    }
    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }


//    override fun onCreateOptionsMenu(menu: Menu) : Boolean {  // do I need it ?
//        // In Kotlin, we can just use 'menuInflater' instead
//        menuInflater.inflate(R.menu.menu, menu)
//        return true      // indication if succescful
//    }
}

