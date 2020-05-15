package com.umpa2020.tracer.gpx

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.security.Timestamp

@Parcelize
class WayPoint (
  var lat:Double,
  var lon:Double,
  var alt:Double,
  var speed:Double?,
  var name:String?,
  var desc:String?,
  var time:Long?,
  var type:Enum<WayPointType>?
):Parcelable