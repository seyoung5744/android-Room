package com.umpa2020.tracer.dataClass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BannerData(
  val bannerId: String? = null,
  val bannerImagePath: String? = null
) : Parcelable