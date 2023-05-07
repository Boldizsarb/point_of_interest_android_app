package com.example.bolidzsar_banfia
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "poi")

data class PoiEntity (

    @PrimaryKey(autoGenerate = true)
            val id: Long,
            val name: String,
            val type:String,
            val description:String,
            val lattitude : Double,
            val longitude : Double
)