package com.umpa2020.tracer.locationBackground

import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.RemoteException
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.LocationRequest
import com.umpa2020.tracer.R
import com.umpa2020.tracer.main.start.running.RunningActivity
import com.umpa2020.tracer.util.Logg

/**
 *  IntentService : 오래걸리지만 메인스레드와 관련이 없는 작업을할 때 주로 이용한다.
 *  만약 메인 스레드와 관련된 작업을 해야 한다면 메인스레드 Handler나 Boradcast intent를 이용해야 한다.
 *  출처: https://fullstatck.tistory.com/23 [풀스택 엔지니어]
 */
class LocationBackgroundService : IntentService("LocationBackgroundService"), LocationUpdatesComponent.ILocationProvider{
  /**
   * Activity와 Service 통신
   *  1. Activity는 Service에 이벤트를 전달
   *  2. Service는 이벤트를 받고 어떤 동작을 수행
   *  3. Service는 Activity로 결과 이벤트를 전달
   */
  var notification: Notification? = null
  override fun onCreate() {
    super.onCreate()
    Logg.i( "onCreate ")

    /**
     *  위치 관련 생성.
     */



    Logg.d(this.toString())
    LocationUpdatesComponent.setILocationProvider(this)
    LocationUpdatesComponent.onCreate(this)
    LocationUpdatesComponent.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)


    notification = createNotification()
    startForeground(1, notification)
  }

  // this makes service running continuously,commenting this start command method service runs only once
  // action에 따른 service 실행 유무
  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    Logg.i( "onStartCommand Service started....")

    if (intent != null) {
      val action = intent.action
      Logg.i( "onStartCommand action $action")
      when (action) {
        ServiceStatus.START.name -> startService()
        ServiceStatus.STOP.name -> stopService()
      }
    }
    return START_STICKY
  }


  override fun onHandleIntent(intent: Intent?) {
    Logg.i( "onHandleIntent $intent")
  }

  /**
   * send message by using messenger
   *
   * @param messageID
   * 액티비티에 브로드캐스트를 이용해 message 보내기.
   */

  private fun sendMessage(location: Location) {
    try {
      val intent = Intent("custom-event-name")
      intent.putExtra("message", location)
      Logg.d( location.toString())
      LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

    } catch (e: RemoteException) {
      Logg.e( "Error passing service object back to activity.")
    }
  }


  /**
   *  LocationComponent에서 locatoin결과 값 받아옴.
   *  => 이걸 이제 액티비티 or 프래그먼트에 전달해주는 것.
   */
  //it의 경우는 함수의 변수가 한 개여야 만 허용.
  override fun onLocationUpdated(location: Location?) {
    location?.let { sendMessage(it) }
  }


  /**
   *  foreground에 알림창 만들기.
   */
  private fun createNotification(): Notification {
    val notificationChannelId = LocationBackgroundService::class.java.simpleName

    // depending on the Android API that we're dealing with we will have
    // to use a specific method to create the notification
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      val channel = NotificationChannel(
        notificationChannelId,
        "Notifications Channel",
        NotificationManager.IMPORTANCE_HIGH
      ).let {
        it.description = "Background location service is getting location..."
        it
      }
      notificationManager.createNotificationChannel(channel)
    }

    val pendingIntent: PendingIntent = Intent(this, RunningActivity::class.java).let { notificationIntent ->
      PendingIntent.getActivity(this, 0, notificationIntent, 0)
    }

    val builder: Notification.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(
      this,
      notificationChannelId
    ) else Notification.Builder(this)

    return builder
      .setContentTitle(getString(R.string.gps_activation))
      //  .setContentText("Background location service is getting location...")
      .setContentIntent(pendingIntent)
      .setSmallIcon(R.mipmap.ic_launcher_tracer_final)
      .setTicker("Ticker text")
      .build()
  }

  private fun startService() {
    //hey request for location updates
    LocationUpdatesComponent.onStart()
  }

  private fun stopService() {
    LocationUpdatesComponent.onStop()
    stopForeground(true)
    stopSelf() // 이건 뭐지??
  }

  companion object {
    private val TAG = "LocationBackground"
    const val LOCATION_MESSAGE = 999
  }

}