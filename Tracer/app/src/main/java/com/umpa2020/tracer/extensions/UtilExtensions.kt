package com.umpa2020.tracer.extensions

import android.graphics.Bitmap
import android.graphics.Canvas
import android.widget.Toast
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.umpa2020.tracer.App

fun String.show() {
  val text = this
  val duration = Toast.LENGTH_LONG
  val toast = Toast.makeText(App.instance.context(), text, duration)
  toast.show()

}

fun Int.toRank(): String {
  return when (this % 10) {
    1 -> "${this}st"
    2 -> "${this}nd"
    3 -> "${this}rd"
    else -> "${this}th"
  }
}

fun Int.makingIcon(): BitmapDescriptor {
  // 기본 마커 활용해서
  val circleDrawable = App.instance.context().getDrawable(this)
  val canvas = Canvas()
  val bitmap = Bitmap.createBitmap(
    circleDrawable!!.intrinsicWidth,
    circleDrawable.intrinsicHeight,
    Bitmap.Config.ARGB_8888
  )
  canvas.setBitmap(bitmap)
  circleDrawable.setBounds(
    0,
    0,
    circleDrawable.intrinsicWidth,
    circleDrawable.intrinsicHeight
  )
  circleDrawable.draw(canvas)
  return BitmapDescriptorFactory.fromBitmap(bitmap)
}


val Double.prettyDistance: String
  get() = if (this < 1000) {
    String.format("%.0f", this) + " m"
  } else {
    String.format("%.2f", this / 1000) + " km"
  }

val  Double.prettySpeed : String
  get() =  String.format("%.1f", this) + "km/h"

val Double.lockDistance: String
  get() = if (this < 1000) {
    String.format("%.0f", this)
  } else {
    String.format("%.2f", this / 1000)
  }

val Double.lockSpeed: String
  get() = String.format("%.1f", this)


fun Double.prettyDistance(): String {
  return if (this < 1000) {
    String.format("%.0f", this) + " m"
  } else {
    String.format("%.2f", this / 1000) + " km"
  }
}

fun Double.prettySpeed(): String {
  return String.format("%.1f", this) + "km/h"
}
