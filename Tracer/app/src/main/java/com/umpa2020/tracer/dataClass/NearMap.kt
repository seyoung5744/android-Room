package com.umpa2020.tracer.dataClass

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NearMap(
  var mapTitle: String,
  var latLng: LatLng,
  var distance: Double
) : Parcelable