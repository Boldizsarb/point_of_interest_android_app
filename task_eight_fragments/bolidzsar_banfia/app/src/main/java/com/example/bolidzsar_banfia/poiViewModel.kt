package com.example.bolidzsar_banfia

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData


class poiViewModel (app: Application): AndroidViewModel(app){
    val db = PoiDatabase.getDatabase(app)

    var pois : LiveData<List<PoiEntity>>

    init {
        pois = db.PoiDAO().getAllPoi()
    }
    fun getAllPoi(): LiveData<List<PoiEntity>>{
        return pois
    }
}