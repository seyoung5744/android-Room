package com.umpa2020.tracer.locationBackground

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.umpa2020.tracer.constant.Constants.Companion.LOCATION_INTERAL
import com.umpa2020.tracer.util.Logg

object LocationUpdatesComponent {
  private var iLocationProvider: ILocationProvider? = null
  /**
   * Provides access to the Fused Location Provider API.
   */
  @SuppressLint("StaticFieldLeak")
  private lateinit var fusedLocationClient: FusedLocationProviderClient   //위치정보 가져오는 인스턴스

  /**
   * Callback for changes in location.
   */
  private lateinit var locationCallback: LocationCallback


  private lateinit var locationRequest: LocationRequest

  /**
   *  현재 위치
   */
  private lateinit var currentLocation: Location

  /**
   *  이전 위치
   */
  var previousLocation: LatLng = LatLng(0.0, 0.0)          //이전위치
  /**
   * create first time to initialize the location components
   *
   * @param context
   */

  fun setILocationProvider(iLocationProvider: ILocationProvider){
    LocationUpdatesComponent.iLocationProvider = iLocationProvider
  }

  fun onCreate(context: Context) {
    Logg.i( "created...............")
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(context) // 위치 서비스 클라이언트 만들기

    // create location request
    createLocationRequest()
    // get last known location
    getLastLocation()

    locationCallback = object : LocationCallback() {
      override fun onLocationResult(locationResult: LocationResult?) {
        super.onLocationResult(locationResult)
        onNewLocation(locationResult!!.lastLocation)
      }
    }
  }
  /**
   * start location updates
   */
  fun onStart() {
    Logg.i( "onStart ")
    //hey request for location updates
    requestLocationUpdates()
  }

  /**
   * remove location updates
   */
  fun onStop() {
    Logg.i( "onStop....")
    removeLocationUpdates()
  }


  /**
   *  위치 요청 설정
   *  정확도: 위치 데이터의 정밀함. 일반적으로 정확도가 높을수록 배터리 소모가 큽니다.
   *  빈도: 위치 연산 빈도. 위치 연산 빈도가 높을수록 배터리를 더 많이 사용합니다.
   *  지연 시간: 위치 데이터의 제공 속도. 일반적으로 지연 시간이 적을수록 더 많은 배터리가 필요합니다.
   *  https://developer.android.com/guide/topics/location/battery?hl=ko#accuracy
   */

  private fun createLocationRequest() {
    locationRequest = LocationRequest.create() // 위치 요청
    locationRequest.run {
      priority = LocationRequest.PRIORITY_HIGH_ACCURACY
      interval = LOCATION_INTERAL.toLong() // 위치 받아오는 주기, setinterval() 메서드를 사용하여 앱을 위해 위치를 연산하는 간격을 지정합니다.
    }
  }

  /**
   *  정확도
   *  정확도 설정이 없으면 PRIORITY_BALANCED_POWER_ACCURACY이 기본 값(102)
   *  PRIORITY_HIGH_ACCURACY의 값은 100
   */
  fun setPriority(priority : Int){
    locationRequest.priority = priority
  }

  fun getLastLocat() : Location{
    return lastLocation!!
  }

  /**
   *  마지막으로 알려진 위치 가져오기
   *  위치 서비스 클라이언트를 만든 후 마지막으로 알려진 사용자 기기의 위치를 가져올 수 있습니다
   *  자료 : https://developer.android.com/training/location/retrieve-current?hl=ko#kotlin
   *
   *  마지막 위치를 가져와서 UI 설정. 시작 마커, 현재 자신의 위치 마커
   */
  var lastLocation : Location? = null
  private fun getLastLocation(){
    try{
      fusedLocationClient.lastLocation // 마지막으로 알려진 위치 가져오기
        .addOnCompleteListener{task ->
          if (task.isSuccessful && task.result != null) {
            currentLocation = task.result!!
            lastLocation = task.result!!
            Logg.i( "getLastLocation $currentLocation" )

            onNewLocation(currentLocation)
          } else {
            Logg.w( "Failed to get location.")
          }

        }
        .addOnSuccessListener { location ->
          if (location == null) {
            Logg.d("Location is null")
          } else {
            Logg.d("Success to get Init Location : $location" )
            previousLocation = LatLng(location.latitude, location.longitude) // 이전 위치
          }
        }
    }catch (e : Exception){
      Logg.e( "Lost location permission.$e")
    }

  }

  /**
   *  위치 업데이트 요청
   *  앱에서 위치 업데이트를 요청하기 전에 위치 서비스에 연결하고 위치를 요청해야 합니다.
   *  위치 설정 변경의 과정에서 이 방법을 보여줍니다. 위치 요청이 완료되면 requestLocationUpdates()를 호출하여 정기 업데이트를 시작할 수 있습니다.
   */
  private fun requestLocationUpdates() {
    Logg.i( "Requesting location updates")
    try {
      fusedLocationClient.requestLocationUpdates(
        locationRequest,
        locationCallback,
        Looper.getMainLooper()
      )
    }catch (e : Exception){
      Logg.e( "Lost location permission. Could not request updates. $e")
    }

  }

  /**
   *  위치 업데이트 중지
   *  사용자가 다른 앱 또는 동일한 앱의 다른 활동으로 전환하는 경우와 같이 더 이상 활동에 포커스가 없을 때 위치 업데이트를 중지
   *  백그라운드에서 실행 중일 때에도 앱이 정보를 수집할 필요가 없는 경우 위치 업데이트를 중지하면 전력 소모를 줄이는 데 도움이 될 수 있습니다.
   */
  private fun removeLocationUpdates() {
    Logg.i( "Removing location updates")
    try {
      fusedLocationClient.removeLocationUpdates(locationCallback)
    } catch (err: Exception) {
      //            Utils.setRequestingLocationUpdates(this, true);
      Logg.e( "Lost location permission. Could not remove updates. $err")
    }
  }


  private fun onNewLocation(location: Location?) {
    currentLocation = location!!
    iLocationProvider!!.onLocationUpdated(currentLocation)
  }

  /**
   * implements this interface to get call back of location changes
   */
  interface ILocationProvider {
    fun onLocationUpdated(location: Location?)
  }


  /**
   * The desired interval for location updates. Inexact. Updates may be more or less frequent.
   */
  private const val UPDATE_INTERVAL_IN_MILLISECONDS = (6 * 1000).toLong()

  /**
   * The fastest rate for active location updates. Updates will never be more frequent
   * than this value.
   */
  private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2

}