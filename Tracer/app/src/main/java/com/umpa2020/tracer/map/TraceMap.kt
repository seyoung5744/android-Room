package com.umpa2020.tracer.map

import android.graphics.Color
import android.location.Location
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.umpa2020.tracer.R
import com.umpa2020.tracer.extensions.bounds
import com.umpa2020.tracer.extensions.makingIcon
import com.umpa2020.tracer.extensions.toLatLng
import com.umpa2020.tracer.gpx.WayPoint
import com.umpa2020.tracer.gpx.WayPointType.*
import com.umpa2020.tracer.util.Logg

class TraceMap(val mMap: GoogleMap) {

  fun captureMapScreen(callback: GoogleMap.SnapshotReadyCallback) {
    mMap.snapshot(callback)
  }

  lateinit var loadTrack: Polyline
  val passedIcon = R.drawable.ic_passed_circle.makingIcon()
  val racerIcons = arrayOf(
    R.drawable.ic_racer0.makingIcon(),
    R.drawable.ic_racer1.makingIcon(),
    R.drawable.ic_racer2.makingIcon(),
    R.drawable.ic_racer3.makingIcon(),
    R.drawable.ic_racer4.makingIcon()
  )
  var markerList = mutableListOf<Marker>()
  var turningPointList = mutableListOf<Marker>()
  fun drawRoute(
    trkList: List<WayPoint>,
    wptList: List<WayPoint>
  ): Pair<MutableList<Marker>, MutableList<Marker>> {
    Logg.d("Map is draw")
    val track = trkList.map { it.toLatLng() }
    loadTrack =
      mMap.addPolyline(
        PolylineOptions()
          .addAll(track)
          .color(Color.RED)
          .startCap(RoundCap() as Cap)
          .endCap(RoundCap())
      )        //경로를 그릴 폴리라인 집합
    val unPassedIcon = R.drawable.ic_unpassed_circle.makingIcon()
    wptList.forEachIndexed { i, it ->
      when (it.type) {
        START_POINT -> {
          markerList.add(
            mMap.addMarker(
              MarkerOptions()
                .position(it.toLatLng())
                .title(it.name)
                .icon(R.drawable.ic_start_point.makingIcon())
            )
          )

        }
        FINISH_POINT -> {
          markerList.add(
            mMap.addMarker(
              MarkerOptions()
                .position(it.toLatLng())
                .title(it.name)
                .icon(R.drawable.ic_finish_point.makingIcon())
            )
          )
        }
        DISTANCE_POINT -> {
          markerList.add(
            mMap.addMarker(
              MarkerOptions()
                .position(it.toLatLng())
                .title(it.name)
                .icon(unPassedIcon)
                .anchor(0f, 0.5f)
            )
          )
        }
        TURNING_LEFT_POINT -> {
          turningPointList.add(
            mMap.addMarker(
              MarkerOptions()
                .position(it.toLatLng())
                .title(it.desc)
                .icon(R.drawable.ic_turn_left.makingIcon())
            )
          )
        }
        TURNING_RIGHT_POINT -> {
          turningPointList.add(
            mMap.addMarker(
              MarkerOptions()
                .position(it.toLatLng())
                .title(it.desc)
                .icon(R.drawable.ic_turn_right.makingIcon())
            )
          )
        }
        else -> {
        }
      }
    }
    val trackBounds = track.toMutableList().bounds()
    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(trackBounds, 1080, 300, 100))
    return Pair(markerList, turningPointList)
  }

  var polyFlag = true
  lateinit var poly: Polyline
  fun drawPolyLine(preLoc: LatLng, curLoc: LatLng) {
    Logg.d("making polyline $preLoc $curLoc")

    if (polyFlag) {
      //polyline 그리기
      poly = mMap.addPolyline(
        PolylineOptions().add(
          preLoc,
          curLoc
        )
      )
      polyFlag = false
    } else {
      val a = poly.points
      a.add(curLoc)
      poly.points = a
      Logg.d("add new point $curLoc")
    }
  }

  fun moveCamera(curLoc: Location) {
    Logg.d("move camera $curLoc")
    mMap.moveCamera(
      CameraUpdateFactory.newCameraPosition(
        CameraPosition(curLoc.toLatLng(), 17F, 0F, curLoc.bearing)
      )
    )
  }

  fun moveCamera(latlng: LatLng) {
    mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng))
  }

  fun changeMarkerIcon(nextWP: Int) {
    markerList[nextWP].setIcon(passedIcon)
  }

  fun initCamera(latlng: LatLng) {
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17F))
  }

  var racerList = mutableListOf<Marker>()

  suspend fun updateMarker(i: Int, latlng: LatLng) {
    racerList[i].position = latlng
  }

  suspend fun addRacer(latlng: LatLng, name: String, racerNo: Int) {
    racerList.add(
      mMap.addMarker(
        MarkerOptions()
          .position(latlng)
          .title(name)
          .icon(racerIcons[racerNo])
          .draggable(true)
      )
    )
  }

  fun removeRacer(racerNo: Int) {
    racerList[racerNo].remove()
  }

}
