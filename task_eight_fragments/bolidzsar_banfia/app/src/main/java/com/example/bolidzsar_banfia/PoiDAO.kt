package com.example.bolidzsar_banfia

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PoiDAO {

    @Query("SELECT * FROM poi")
    //fun getAllPoi(): List<PoiEntity>
    fun getAllPoi(): LiveData<List<PoiEntity>>

    @Insert
    fun insert(poi:PoiEntity) : Long

}