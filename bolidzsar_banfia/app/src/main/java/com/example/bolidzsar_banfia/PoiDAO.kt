package com.example.bolidzsar_banfia

import androidx.room.*

@Dao
interface PoiDAO {

    @Query("SELECT * FROM poi")
    fun getAllPoi(): List<PoiEntity>

    @Insert
    fun insert(poi:PoiEntity) : Long

}