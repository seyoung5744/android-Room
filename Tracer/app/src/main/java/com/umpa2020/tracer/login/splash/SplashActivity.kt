package com.umpa2020.tracer.login.splash

import android.Manifest
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.umpa2020.tracer.R
import com.umpa2020.tracer.extensions.show
import com.umpa2020.tracer.login.LoginActivity
import com.umpa2020.tracer.main.MainActivity
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.activity_splash.*
import kotlin.system.exitProcess

class SplashActivity : AppCompatActivity() {
  val WSY = "WSY"
  private val multiplePermissionsCode = 100          //권한
  private val requiredPermissions = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.READ_EXTERNAL_STORAGE
  )
  private var mAuth: FirebaseAuth? = null

  // firebase DB
  private var mFirestoreDB: FirebaseFirestore? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_splash)

    checkPermissions()          //모든 권한 확인
  }

  override fun onPause() {
    super.onPause()
    Logg.d("onPause()")
  }

  override fun onStop() {
    super.onStop()
    Logg.d("onStop()")
  }

  override fun onDestroy() {
    super.onDestroy()
    Logg.d("onDestroy()")
  }

  override fun onBackPressed() {
    super.onBackPressed()
    Logg.d("뒤로 감~~")
    finishAffinity() //  해당 앱의 루트 액티비티를 종료시킨다. (API  16미만은 ActivityCompat.finishAffinity())
    System.runFinalization() // 간단히 말해 현재 작업중인 쓰레드가 다 종료되면, 종료 시키라는 명령어
    exitProcess(0) // 현재 액티비티를 종료시킨다.
  }

  private fun checkPermissions() {
    val rejectedPermissionList = ArrayList<String>()
    //필요한 퍼미션들을 하나씩 끄집어내서 현재 권한을 받았는지 체크
    for (permission in requiredPermissions) {
      if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
        //만약 권한이 없다면 rejectedPermissionList에 추가
        Logg.d("Add reject Permission$permission")
        rejectedPermissionList.add(permission)
      }
    }
    //거절된 퍼미션이 있다면...
    if (rejectedPermissionList.isNotEmpty()) {
      //권한 요청!
      val array = arrayOfNulls<String>(rejectedPermissionList.size)
      ActivityCompat.requestPermissions(
        this,
        rejectedPermissionList.toArray(array),
        multiplePermissionsCode
      )
    } else {
      launchApp()
    }
  }

  fun launchApp() {
    /**
     *  Firestore 초기화
     */
    mFirestoreDB = FirebaseFirestore.getInstance()
    ObjectAnimator.ofFloat(redMovingView, "translationX", 2000f).apply {
      duration = 1500
      start()
    }
    Logg.i("프리퍼런스에 저장된 자동 로그인 유무 : z${UserInfo.autoLoginKey}b")
    Handler().postDelayed({
      // 앞의 과정이 약간의 시간이 필요하거나 한 경우 바로 어떤 명령을 실행하지 않고 잠시 딜레이를 갖고 실행
      /**
       *  // 로그인 고유 값이 있으면 --> 회원가입 진행 끝났다고 생각하고 일단ㄱㄱ -> 수정해야함
       */

      // 앱 설치시에는 isEmpty() 즉, 값이 없다.
      if (UserInfo.autoLoginKey.isEmpty()) { // 로그인 고유 값이 있으면 --> 회원가입 진행 끝났다고 생각하고 일단ㄱㄱ -> 수정해야함
        val nextIntent = Intent(this@SplashActivity, LoginActivity::class.java)
        startActivity(nextIntent)
        finish()
      } else {
        // shared에 로그인 ID 고유값이 없으면 초기 가입자 or (로그아웃 or 앱 삭제 후 재 로그인)
        // main으로

        val nextIntent = Intent(this@SplashActivity, MainActivity::class.java)
        startActivity(nextIntent)
        finish()

        //  로그인 고유 값이 있는데 로그아웃으로 인한것이면 Db에서 개인 정보가 있는지 검사가 필요??
        // 검사해서 데이터가 있으면...음....ㅠㅠ
      }
    }, 800)
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    when (requestCode) {
      multiplePermissionsCode -> {
        if (grantResults.isNotEmpty()) {
          for ((i, permission) in permissions.withIndex()) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
              //권한 획득 실패
              Logg.d("reject Permission$i")
              Logg.d("reject Permission$permission")
              getString(R.string.sorry).show()
              finish()
            }
          }
        }
      }
    }
    launchApp()
    UserInfo.permission = 1
  }
}
