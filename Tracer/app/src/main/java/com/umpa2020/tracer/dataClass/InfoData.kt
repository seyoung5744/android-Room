package com.umpa2020.tracer.dataClass

import android.os.Parcelable
import com.umpa2020.tracer.constant.Privacy
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InfoData(
  var makersNickname: String? = null,
  var makersUserNumber: String? = null,
  var mapTitle: String? = null,
  var mapExplanation: String? = null,
  var mapImage: String? = null,
  var routeGPXPath: String? = null,
  var distance: Double? = null,
  var time: Long? = null,
  var execute: Int? = null,
  var likes: Int? = null,
  var privacy: Privacy = Privacy.PUBLIC,
  var startLatitude: Double? = null,
  var startLongitude: Double? = null,
  var myLiked: Boolean = false,
  var played: Boolean = false
) : Parcelable
