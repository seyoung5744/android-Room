package com.umpa2020.tracer.roomDatabase.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "map_record")
data class MapRecordData (
  @PrimaryKey
  var uid : Int,
  var distance : String,
  var speed : String,
  var time : Long,
  var timeControl : Boolean,
  var timeWhenStop : Long,
  var timeText : String
)