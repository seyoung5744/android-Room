package com.umpa2020.tracer.dataClass

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.umpa2020.tracer.gpx.WayPoint
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RouteGPX(
    var time:Long,
    var text:String,
    var wptList:MutableList<WayPoint>,
    var trkList:MutableList<WayPoint>
) : Parcelable