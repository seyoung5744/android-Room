package com.umpa2020.tracer.roomDatabase.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import javax.annotation.Nullable

@Entity(tableName = "gps")
data class GPSData (
  @PrimaryKey
  @Nullable
  var uid : Int,
  var lat: Double,
  var lng : Double
)
