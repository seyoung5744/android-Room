package com.umpa2020.tracer

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle

class App : Application() {
  companion object {
    lateinit var instance: App
      private set

    fun applicationContext() : Context? {
      return instance.applicationContext
    }
    const val notificationId: Int = 1 // 잠금화면 알림창 Id
  }

  private var activityCount = 0
  private val activityLifecycleCallbacks = ActivityLifecycleCallbacks()

  override fun onCreate() {
    super.onCreate()
    instance = this@App

    registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
  }

  fun context(): Context = applicationContext

  fun currentActivity(): Activity? = activityLifecycleCallbacks.currentActivity

  inner class ActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {
    var currentActivity: Activity? = null

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
      currentActivity = activity
      activityCount++
    }

    override fun onActivityStarted(activity: Activity?) {}
    override fun onActivityResumed(activity: Activity?) {
      currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity?) {}
    override fun onActivityStopped(activity: Activity?) {}
    override fun onActivityDestroyed(activity: Activity?) {
      activityCount--

      // 앱을 완전히 종료할 때 구독 끊기
      if (activityCount == 0) {
        destroyAllRepository()
      }
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {}
  }

  fun destroyAllRepository() {
    /* AreaRepository.instance.destroy()
     NoticeRepository.instance.destroy()
     UserRepository.instance.destroy()
     QnaRepository.instance.destroy()
     DrivingRepository.instance.destroy()
     MapLocationRepository.instance.destroy()
     AnalyticsManager.destroy()*/
  }

}