package com.umpa2020.tracer.dataClass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RankingData(
  val makerNickname: String? = null,
  val challengerId: String?=null,
  val challengerNickname: String? = null,
  val challengerTime: Long? = null,
  var bestTime: Boolean? = null,
  val maxSpeed: String? = null,
  val averageSpeed: String? = null,
  var racerGPX:String?=null
) : Parcelable