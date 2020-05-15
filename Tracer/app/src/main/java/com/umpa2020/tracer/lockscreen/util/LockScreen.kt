package com.umpa2020.tracer.lockscreen.util

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat.startForegroundService
import com.umpa2020.tracer.App
import com.umpa2020.tracer.LockScreenApplication
import com.umpa2020.tracer.lockscreen.service.LockScreenService
import com.umpa2020.tracer.util.Logg


/**
 * 잠금화면 Service를 관리하는 Class
 */

object LockScreen {

  /**
   *  run
   *     LockScreenApplication.applicationContext()을 수신객체로 변환하여
   *     블록 안에서 이를 사용
   */
  fun active() {
    Logg.d("LockScreen active()")
    App.applicationContext()?.run {
      Logg.d("서비스 시작 부분")
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // 오레오 이상부터 foregroundService로 실행.
        startForegroundService(Intent(this, LockScreenService::class.java))
        Logg.d("서비스 시작1")
      } else {
        startService(Intent(this, LockScreenService::class.java))
        Logg.d("서비스 시작2")
      }
    }
  }

  fun deActivate() {
    App.applicationContext()?.run {
      stopService(Intent(this, LockScreenService::class.java))
    }
  }

  fun getLockScreenStatus(): Boolean {
    val lockScreenPreferences = App.applicationContext()?.run {
      // 저장된 값을 가져오기 위해 같은 파일 명을 찾음.
      // mode : Context.MODE_PRIVATE : 해당 앱에서만 접근 가능.
      getSharedPreferences("LockScreenStatus", Context.MODE_PRIVATE)
    }
    Logg.d(lockScreenPreferences.toString())

    // getBoolean : preferences에서 부울 값을 검색하십시오(회수하십시오).
    // Retrieve a boolean value from the preferences.
    return lockScreenPreferences?.getBoolean("LockScreenStatus", false)!!
  }

//    val isActive: Boolean
//        get() = LockScreenApplication.applicationContext()?.let {
//            isMyServiceRunning(LockScreenService::class.java)
//        } ?: kotlin.run {
//            false
//        }
//
//    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
//        val manager = LockScreenApplication.applicationContext()?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (serviceClass.name == service.service.className) {
//                return true
//            }
//        }
//        return false
//    }

}