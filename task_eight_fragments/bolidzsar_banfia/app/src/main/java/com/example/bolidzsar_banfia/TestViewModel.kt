package com.example.bolidzsar_banfia

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.osmdroid.views.MapView

class TestViewModel : ViewModel() {

    var currentlat = 0.0
        set(newValue) {
            field = newValue
            livelat.value = newValue
        }
    var livelat =  MutableLiveData<Double>()

    var currentlon = 0.0
        set(newValue) {
            field = newValue
            livelon.value = newValue
        }
    var livelon =  MutableLiveData<Double>()

}