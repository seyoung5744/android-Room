package com.umpa2020.tracer.main.start.racing

import android.content.Intent
import android.content.pm.ActivityInfo
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import com.umpa2020.tracer.R
import com.umpa2020.tracer.constant.Constants
import com.umpa2020.tracer.constant.Constants.Companion.ARRIVE_BOUNDARY
import com.umpa2020.tracer.constant.Constants.Companion.DEVIATION_COUNT
import com.umpa2020.tracer.constant.Privacy
import com.umpa2020.tracer.constant.UserState
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.RacerData
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.extensions.prettyDistance
import com.umpa2020.tracer.extensions.toLatLng
import com.umpa2020.tracer.extensions.toWayPoint
import com.umpa2020.tracer.gpx.WayPoint
import com.umpa2020.tracer.gpx.WayPointType
import com.umpa2020.tracer.main.start.BaseRunningActivity
import com.umpa2020.tracer.network.FBMapRepository
import com.umpa2020.tracer.network.FBRacingRepository
import com.umpa2020.tracer.network.RacingListener
import com.umpa2020.tracer.util.ChoicePopup
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.TTS
import kotlinx.android.synthetic.main.activity_ranking_recode_racing.*
import kotlinx.coroutines.*
import com.umpa2020.tracer.gpx.WayPointType.*
import java.util.concurrent.TimeUnit

class RacingActivity : BaseRunningActivity() {
  companion object {
    const val ROUTE_GPX = "RouteGPX"
  }

  lateinit var mapRouteGPX: RouteGPX
  lateinit var mapTitle: String
  var racingResult = true
  var deviationCount = 0

  lateinit var turningPointList: MutableList<Marker>
  lateinit var markerList: MutableList<Marker>
  var track: MutableList<LatLng> = mutableListOf()
  var nextWP: Int = 1
  var nextTP: Int = 0
  lateinit var racerList: Array<RacerData>
  var racerGPXList: Array<RouteGPX>? = null
  lateinit var startPoint: WayPoint

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    setContentView(R.layout.activity_ranking_recode_racing)
    mapRouteGPX = intent.getParcelableExtra(ROUTE_GPX) as RouteGPX
    mapTitle = intent.getStringExtra("mapTitle")!!
    racerList = intent.getSerializableExtra("RacerList") as Array<RacerData>

    //TODO:: racerList로 racingGPX 가져오기 FireBase
    Logg.d(racerList.joinToString())
    FBRacingRepository().listRacingGPX(
      mapTitle,
      racerList.map { it.racerId!! }.toTypedArray(),
      object : RacingListener {
        override fun racingList(gpxList: Array<RouteGPX>) {
          racerGPXList = gpxList
          Logg.d("${racerGPXList!!.size}")
        }
      })
    init()

    // 시작 포인트로 이동
    TTS.speech(getString(R.string.goToStartPoint))

    racingStartButton.setOnClickListener(this)
    racingStopButton.setOnClickListener(this)
    racingPauseButton.setOnClickListener(this)
  }

  override fun onMapReady(googleMap: GoogleMap) {
    super.onMapReady(googleMap)
    traceMap.drawRoute(mapRouteGPX.trkList, mapRouteGPX.wptList).run {
      markerList = this.first
      turningPointList = this.second
    }
  }


  override fun init() {
    loadRoute()
    val smf = supportFragmentManager.findFragmentById(R.id.map_viewer) as SupportMapFragment
    smf.getMapAsync(this)

    startButton = racingStartButton
    stopButton = racingStopButton
    pauseButton = racingPauseButton
    chronometer = racingTimerTextView
    notificationTextView = racingNotificationTextView
    pauseNotificationTextView = racingPauseNotificationTextView
    drawerHandle = racingHandle
    drawer = racingDrawer
    speedTextView = racingSpeedTextView
    distanceTextView = racingDistanceTextView
    stopButton.setOnLongClickListener {
      noticePopup = ChoicePopup(this, getString(R.string.please_select),
        getString(R.string.cannot_save),
        getString(R.string.yes), getString(R.string.no),
        View.OnClickListener {
          noticePopup.dismiss()
          finish()
        },
        View.OnClickListener {
          noticePopup.dismiss()
        })
      noticePopup.show()
      true
    }
  }

  fun loadRoute() {
    mapRouteGPX.trkList.forEach {
      track.add(it.toLatLng())
    }
    mapRouteGPX.wptList.let { startPoint = it.first() }
  }

  override fun onSingleClick(v: View?) {
    when (v!!.id) {
      R.id.racingStartButton -> {
        when (userState) {
          UserState.NORMAL -> {
            Toast.makeText(
              this,
              getString(R.string.startpoint) + ARRIVE_BOUNDARY + getString(R.string.only_start),
              Toast.LENGTH_LONG
            ).show()
            Logg.d("NORMAL")
          }
          UserState.READYTORACING -> {
            Logg.d("READYTORACING")
            notificationTextView.visibility = View.GONE
            start()
          }
          else -> {
          }
        }
      }
      R.id.racingStopButton -> {
        //"종료를 원하시면 길게 눌러주세요".show()
        // Toast.makeText(this, "종료를 원하시면 길게 눌러주세요", Toast.LENGTH_LONG).show()

      }
      R.id.racingPauseButton -> {
        if (privacy == Privacy.RACING) {
          showPausePopup(
            getString(R.string.pause_notsave)
          )
        } else {
          if (userState == UserState.PAUSED)
            restart()
          else
            pause()
        }
      }
    }
  }


  override fun updateLocation(curLoc: Location) {
    super.updateLocation(curLoc)
    when (userState) {
      UserState.NORMAL -> {
        Logg.d("NORMAL")
        checkIsReady()
      }

      UserState.READYTORACING -> {
        Logg.d("READYTORACING")
        checkIsReadyToRacing()
      }
      UserState.RUNNING -> {
        Logg.d("RUNNING")
        checkMarker()
        checkTurningPoint()
        checkDeviation()
      }
      else -> {

      }
    }
  }


  override fun start() {
    super.start()
    FBMapRepository().updateExecute(mapTitle)
    // 레이싱 시작 TTS
    TTS.speech(getString(R.string.startRacing))
    wpList.add(currentLocation.toWayPoint(START_POINT)
    )

    if (!racerGPXList.isNullOrEmpty()) {
      Logg.d("Start Virtual Racing")
      virtualRacing()
    }
  }

  /**
   * 가상레이싱 코루틴 함수
   * 다른 사람의 GPX를 읽어서 자동으로 각 체크포인트의 이동시간을 계산하여 마커를 이동시킨다
   */
  private fun virtualRacing() {
    val checkPoints = arrayOf(DISTANCE_POINT, START_POINT, FINISH_POINT)
    racerGPXList?.forEachIndexed { racerNo, racingGPX ->
      GlobalScope.launch {
        val wpts = racingGPX.wptList.filter { checkPoints.contains(it.type) }
        var tempIndex = 1
        val wptIndices = mutableListOf<Int>()
        wptIndices.add(0)
        wptIndices.addAll(racingGPX.trkList.mapIndexed { i, trk ->
          if (trk.toLatLng() == wpts[tempIndex].toLatLng()) {
            tempIndex++
            i
          } else null
        }.filterNotNull())

        withContext(Dispatchers.Main) {
          traceMap.addRacer(wpts[0].toLatLng(), racerList[racerNo].racerName!!, racerNo)
          run loop@{
            wpts.forEachIndexed { index, it ->
              if (index + 2 == wpts.size) return@loop
              val duration =  TimeUnit.MILLISECONDS.toSeconds((wpts[index + 1].time!! - wpts[index].time!!))
              val unitDuration= duration/ (wptIndices[index + 1] - wptIndices[index])
              
              Logg.d("기간 $unitDuration  1 : ${wpts[index + 1].time}   2: ${wpts[index].time}")
              (wptIndices[index]..wptIndices[index + 1]).forEach {
                delay(unitDuration)
                traceMap.updateMarker(racerNo, racingGPX.trkList[it].toLatLng())
              }
            }
          }
          TTS.speech("${racerList[racerNo].racerName} is arrive")
          traceMap.removeRacer(racerNo)
        }
      }
    }
  }

  override fun stop() {
    super.stop()
    //TODO: FINISH POINT 변경
    wpList.add(currentLocation.toWayPoint(FINISH_POINT)
    )

    // 레이싱 끝 TTS
    TTS.speech(getString(R.string.finishRacing))

    val infoData = InfoData()
    infoData.time = SystemClock.elapsedRealtime() - chronometer.base
    infoData.mapTitle = mapTitle
    infoData.distance = distance
    //infoData.distance = calcLeftDistance()
    val routeGPX = RouteGPX(infoData.time!!, "", wpList, trkList)

    val newIntent = Intent(this, RacingFinishActivity::class.java)
    newIntent.putExtra("Result", racingResult)
    newIntent.putExtra("InfoData", infoData)
    newIntent.putExtra("MapRouteGPX", mapRouteGPX)
    newIntent.putExtra("RouteGPX", routeGPX)

    startActivity(newIntent)
    finish()
  }

  private fun checkTurningPoint() {
    if (nextTP >= turningPointList.size) return

    if (SphericalUtil.computeDistanceBetween(
        currentLatLng,
        turningPointList[nextTP].position
      ) < ARRIVE_BOUNDARY
    ) {
      TTS.speech(turningPointList[nextTP].title)
      nextTP++
    }
  }

  private fun checkMarker() {
    if (nextWP == markerList.size) return

    if (SphericalUtil.computeDistanceBetween(
        currentLatLng,
        markerList[nextWP].position
      ) < ARRIVE_BOUNDARY
    ) {
      traceMap.changeMarkerIcon(nextWP)
      nextWP++
      wpList.add(currentLocation.toWayPoint(DISTANCE_POINT)      )
      if (nextWP == markerList.size) {
        stop()
      }
    }
  }

  private fun checkDeviation() {
    Logg.d(
      PolyUtil.isLocationOnPath(
        currentLatLng,
        track,
        false,
        Constants.DEVIATION_DISTANCE
      ).toString()
    )
    //경로이탈검사
    if (PolyUtil.isLocationOnPath(
        currentLatLng,
        track,
        false,
        Constants.DEVIATION_DISTANCE
      )
    ) {
      deviationCount = 0
      notificationTextView.visibility = View.GONE
    } else {
      deviationCount++
      notice(
        getString(R.string.out_of_route) + deviationCount.toString() + getString(R.string.route_deviates) + DEVIATION_COUNT + getString(
          R.string.resgisration
        )
      )
      if (deviationCount > DEVIATION_COUNT) {
        racingResult = false
        stop()
      }
    }
  }


  private fun checkIsReadyToRacing() {
    if (SphericalUtil.computeDistanceBetween(
        currentLatLng, mapRouteGPX.wptList[0].toLatLng()
      ) > ARRIVE_BOUNDARY
    ) {
      userState = UserState.NORMAL
      notice(
        getString(R.string.move_startpoint)
          + (SphericalUtil.computeDistanceBetween(
          currentLatLng,
          markerList[0].position
        )).prettyDistance
      )
    }
  }

  //시작포인트에 10m이내로 들어오면 준비상태로 변경
  private fun checkIsReady() {
    if (SphericalUtil.computeDistanceBetween(
        currentLatLng, mapRouteGPX.wptList[0].toLatLng()
      ) <= ARRIVE_BOUNDARY
    ) {
      notice(getString(R.string.want_start))
      //traceMap.changeMarkerColor(0, BitmapDescriptorFactory.HUE_BLUE)
      userState = UserState.READYTORACING
    } else {
      notice(
        getString(R.string.move_startpoint)
          + (SphericalUtil.computeDistanceBetween(
          currentLatLng,
          mapRouteGPX.wptList[0].toLatLng()
        )).prettyDistance
      )
    }
  }


}
