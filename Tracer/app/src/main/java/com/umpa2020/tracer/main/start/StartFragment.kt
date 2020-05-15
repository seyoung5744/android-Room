package com.umpa2020.tracer.main.start

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.storage.FirebaseStorage
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.constant.Constants.Companion.TIMESTAMP_LENGTH
import com.umpa2020.tracer.dataClass.NearMap
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.extensions.*
import com.umpa2020.tracer.gpx.WayPoint
import com.umpa2020.tracer.gpx.WayPointType
import com.umpa2020.tracer.main.challenge.ChallengeDataSettingActivity
import com.umpa2020.tracer.main.ranking.RankingMapDetailActivity
import com.umpa2020.tracer.main.start.racing.RacingActivity
import com.umpa2020.tracer.main.start.running.RunningActivity
import com.umpa2020.tracer.map.TraceMap
import com.umpa2020.tracer.network.FBMapRepository
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.MyProgressBar
import com.umpa2020.tracer.util.OnSingleClickListener
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.fragment_start.*
import kotlinx.android.synthetic.main.fragment_start.view.*
import java.io.File


class StartFragment : Fragment(), OnMapReadyCallback, OnSingleClickListener {
  val TAG = "StartFragment"

  var traceMap: TraceMap? = null
  var currentLocation: Location? = null


  var routeMarkers = mutableListOf<Marker>()

  // 처음 화면 시작에서 주변 route 마커 찍어주기 위함
  val STRAT_FRAGMENT_NEARMAP = 30
  val NEARMAPFALSE = 41
  var nearMaps: ArrayList<NearMap> = arrayListOf()
  var wedgedCamera = true
  val progressBar = MyProgressBar()
  var firstFlag = true

  override fun onSingleClick(v: View?) {
    when (v!!.id) {
      R.id.mainStartRunning -> {
        val newIntent = Intent(activity, RunningActivity::class.java)
        startActivity(newIntent)
      }
      R.id.mainStartSearchAreaButton -> {
        searchThisArea()
      }
      R.id.mainStartSearchButton -> {
        if (mainStartLogoTextView.visibility == View.VISIBLE) {
          mainStartLogoTextView.visibility = View.GONE
          mainStartSearchLayout.visibility = View.VISIBLE
        } else {
          search()
          wedgedCamera = false
        }
      }
      R.id.mainStartBackButton -> { // 검색창에서 뒤로가기 버튼
        mainStartLogoTextView.visibility = View.VISIBLE
        mainStartSearchLayout.visibility = View.GONE
        // 키보드 숨기기
        (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
          .hideSoftInputFromWindow(mainStartSearchTextView.windowToken, 0)

        // EditText 초기화
        mainStartSearchTextView.setText("")
      }
      R.id.upload -> {
        val intent = Intent(context, ChallengeDataSettingActivity::class.java)
        startActivity(intent)
      }
      R.id.gpxTest -> {
        /* val path = "/data/data/com.umpa2020.tracer/files/originGPX/2020korea50k_10kfinal.gpx"
         val gpx = path.gpxToClass()
         gpx.addCheckPoint()
         gpx.addDirectionSign()
         gpx.wptList.forEachIndexed{i,it-> Logg.d("${it.type} $i")

      }
      */
        val saveFolder = File(requireContext().filesDir, "routeGPX") // 저장 경로
        if (!saveFolder.exists()) {       //폴더 없으면 생성
          saveFolder.mkdir()
        }
        val wptList = mutableListOf<WayPoint>()
        wptList.add(WayPoint(1.0, 1.0, 1.0, 1.0, "1", "1", 0, WayPointType.START_POINT))
        wptList.add(WayPoint(2.0, 2.0, 2.0, 2.0, "2", "2", 0, WayPointType.DISTANCE_POINT))
        wptList.add(WayPoint(3.0, 3.0, 3.0, 3.0, "3", "3", 0, WayPointType.DISTANCE_POINT))
        wptList.add(WayPoint(4.0, 4.0, 4.0, 4.0, "4", "4", 0, WayPointType.FINISH_POINT))


        val trkList = mutableListOf<WayPoint>()
        trkList.add(WayPoint(1.0, 1.0, 1.0, 1.0, "1", "1", 0, WayPointType.TRACK_POINT))
        trkList.add(WayPoint(2.0, 2.0, 2.0, 2.0, "2", "2", 0, WayPointType.TRACK_POINT))
        trkList.add(WayPoint(3.0, 3.0, 3.0, 3.0, "3", "3", 0, WayPointType.TRACK_POINT))
        trkList.add(WayPoint(4.0, 4.0, 4.0, 4.0, "4", "4", 0, WayPointType.TRACK_POINT))

        val routeGPX = RouteGPX(0, "0", wptList, trkList)
        routeGPX.addDirectionSign()
        val routeGpxFile = routeGPX.classToGpx(saveFolder.path)
      }
    }
  }


  /**
   *  현재 맵 보이는 범위로 루트 검색
   */
  private fun searchThisArea() {
    progressBar.show()
    val bound = traceMap!!.mMap.projection.visibleRegion.latLngBounds

    val mHandler = object : Handler(Looper.getMainLooper()) {
      override fun handleMessage(msg: Message) {
        when (msg.what) {
          STRAT_FRAGMENT_NEARMAP -> {
            mainStartSearchAreaButton.visibility = View.GONE

            nearMaps = msg.obj as ArrayList<NearMap>
            val icon =
              BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_ROSE)

            routeMarkers.forEach {
              it.remove()
            }
            routeMarkers.clear()
            nearMaps.forEach {
              val cutted = it.mapTitle.subSequence(0, it.mapTitle.length - TIMESTAMP_LENGTH)

              //데이터 바인딩
              routeMarkers.add(
                traceMap!!.mMap.addMarker(
                  MarkerOptions()
                    .position(it.latLng)
                    .title(cutted.toString())
                    .snippet(it.distance.prettyDistance)
                    .icon(icon)
                )
              )

              //TODO: 윈도우 커스터마이즈
              routeMarkers.last().tag = it.mapTitle

              traceMap!!.mMap.setOnInfoWindowClickListener { it2 ->
                val intent = Intent(activity, RankingMapDetailActivity::class.java)
                intent.putExtra("MapTitle", it2.tag.toString())
                startActivity(intent)
              }
            }
            progressBar.dismiss()
          }
          NEARMAPFALSE -> {
            getString(R.string.not_search).show()
            progressBar.dismiss()
          }
        }
      }
    }
    FBMapRepository().listNearMap(bound.southwest, bound.northeast, mHandler)
  }

  /**
   *  검색 창에 입력한 장소 주소로 반환 및 검색창에 설정.
   */
  private fun search() {
    val geocoder = Geocoder(context)
    if (mainStartSearchTextView.text.isEmpty()) {
      getString(R.string.enter_address).show()
      return
    }

    val addressList =
      geocoder.getFromLocationName(mainStartSearchTextView.text.toString(), 10)

    Logg.i(addressList.size.toString())
    for (i in 0 until addressList.size)
      Logg.i(addressList[i].toString())

    // 최대 검색 결과 개수
    if (addressList.size == 0) {
      getString(R.string.cannot_find).show()
    } else {
      // mainStartSearchTextView.setText(addressList[0].getAddressLine(0))
      traceMap!!.moveCamera(LatLng(addressList[0].latitude, addressList[0].longitude))
      searchThisArea()
    }

    // 키보드 숨기기
    (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
      .hideSoftInputFromWindow(mainStartSearchTextView.windowToken, 0)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {

    Logg.d("onCreateView()")
    val view = inflater.inflate(R.layout.fragment_start, container, false)
    view.test.setOnClickListener {
      val storage = FirebaseStorage.getInstance()
      val routeRef = storage.reference.child("mapRoute").child("asdasdqwe1587633430060")
      val localFile = File.createTempFile("routeGpx", "xml")
      routeRef.getFile(Uri.fromFile(localFile)).addOnSuccessListener {
        val routeGPX = localFile.path.gpxToClass()
        val intent = Intent(App.instance.context(), RacingActivity::class.java)
        intent.putExtra("RouteGPX", routeGPX)
        val racingGPXs = ArrayList<RouteGPX>()
        racingGPXs.add(routeGPX)
        racingGPXs.add(routeGPX)
        racingGPXs.add(routeGPX)

        intent.putExtra("RacingGPXs", racingGPXs)
        intent.putExtra("mapTitle", "asdasdqwe1587633430060")
        startActivity(intent)
      }
    }

    // 검색 창 키보드에서 엔터키 리스너
    view.mainStartSearchTextView.setOnEditorActionListener(
      object : TextView.OnEditorActionListener {
        override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {

          Logg.i("엔터키 클릭")
          search()
          return true
        }
      }
    )

    val smf = childFragmentManager.findFragmentById(R.id.map_viewer_start) as SupportMapFragment
    smf.getMapAsync(this)

    return view
  }

  override fun onMapReady(googleMap: GoogleMap) {
    Logg.d("onMapReady")
    traceMap = TraceMap(googleMap) //구글맵
    traceMap!!.mMap.isMyLocationEnabled = true // 이 값을 true로 하면 구글 기본 제공 파란 위치표시 사용가능.

    // Shared의 값을 받아와서 초기 카메라 위치 설정.
    Logg.d("${UserInfo.lat} , ${UserInfo.lng}")
    val latlng = LatLng(UserInfo.lat.toDouble(), UserInfo.lng.toDouble())
    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlng, 17f)
    traceMap!!.mMap.moveCamera(cameraUpdate)

    wedgedCamera = true
    traceMap!!.mMap.setOnCameraMoveCanceledListener {
      wedgedCamera = false
      mainStartSearchAreaButton.visibility = View.VISIBLE
    }
    traceMap!!.mMap.setOnMyLocationButtonClickListener {
      wedgedCamera = true
      true
    }
    traceMap!!.mMap.uiSettings.isCompassEnabled = true
    traceMap!!.mMap.uiSettings.isZoomControlsEnabled = true
  }

  override fun onResume() {
    super.onResume()
    // 브로드 캐스트 등록 - 전역 context로 수정해야함
    LocalBroadcastManager.getInstance(this.requireContext())
      .registerReceiver(locationBroadcastReceiver, IntentFilter("custom-event-name"))

    // Shared의 마지막 위치였던거 확인.
    Logg.d("${UserInfo.lat} , ${UserInfo.lng}")
  }

  fun getVersionInfo() {
    val info: PackageInfo =
      requireContext().packageManager.getPackageInfo(App.instance.packageName, 0)
    val version = info.versionName
    Logg.d(version.toString())
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    Logg.d("onViewCreated()")

    view.mainStartRunning.setOnClickListener(this)
    view.mainStartSearchAreaButton.setOnClickListener(this)
    view.mainStartSearchButton.setOnClickListener(this)
    view.mainStartBackButton.setOnClickListener(this)

    view.gpxTest.setOnClickListener(this)
    view.upload.setOnClickListener(this)
  }

  override fun onPause() {
    super.onPause()
    // 브로드 캐스트 해제
    LocalBroadcastManager.getInstance(this.requireContext())
      .unregisterReceiver(locationBroadcastReceiver)

    //Shared로 마지막 위치 업데이트
    if (currentLocation != null) {
      Logg.d("마지막 위치 업데이트")
      UserInfo.lat = currentLocation!!.latitude.toFloat()
      UserInfo.lng = currentLocation!!.longitude.toFloat()
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    Logg.d("onDestroy()")
  }

  // 초기에 위치 받아오면 카메라 설정.
  var locationBroadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      val message = intent?.getParcelableExtra<Location>("message")
      currentLocation = message as Location
      traceMap?.let {
        if (wedgedCamera) it.moveCamera(currentLocation!!)
        if (firstFlag) {
          searchThisArea()
          firstFlag = false
          it.initCamera(currentLocation!!.toLatLng())
        }
        Logg.d("${currentLocation}")
      }
    }
  }

  private fun setDefaultLocation() {


    //디폴트 위치, Seoul
    val DEFAULT_LOCATION = LatLng(37.621664, 127.0561576)


    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15f)
    traceMap!!.mMap.moveCamera(cameraUpdate)

  }
}
