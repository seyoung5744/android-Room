package com.umpa2020.tracer.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.SystemClock
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.umpa2020.tracer.LocationViewModel
import com.umpa2020.tracer.extensions.toLatLng
import com.umpa2020.tracer.main.start.BaseRunningActivity

/**
 *  브로드 케스트 리시버 클래스
 *  serevice로부터 신호(GPS)를 받아 인자값으로 들어오는 map에 전달.
 */
class LocationBroadcastReceiver(val activity: BaseRunningActivity) : BroadcastReceiver() {
  var previousLatLng = LatLng(0.0, 0.0)          //이전위
  var currentLatLng = LatLng(0.0, 0.0)

  var previousTime = 0L
  var currentTime = 0L

  var flag = true
  var currentLocation : Location? = null

  private lateinit var model: LocationViewModel

  override fun onReceive(context: Context?, intent: Intent?) {
    val message = intent?.getParcelableExtra<Location>("message")
    currentLocation = message as Location


    currentLatLng = currentLocation!!.toLatLng()
    currentTime = SystemClock.elapsedRealtime()

    if (flag) { // 맨 처음엔 이전 위치가 없으므로
      Logg.d("맨 처음 위치 업데이트")
      activity.updateLocation(currentLocation!!) // currentLocation : Location
      previousLatLng = currentLatLng
      previousTime = currentTime
      flag = false
    } else {
      if ((((currentTime - previousTime) / 1000) + 1) * 10
          > SphericalUtil.computeDistanceBetween(previousLatLng, currentLatLng)
      ) {
        activity.updateLocation(currentLocation!!)
        previousLatLng = currentLatLng
        previousTime = currentTime
        Logg.d("GPS 예상 안")
      } else {
        Logg.d("GPS 튐")
      }
    }
  }
}
