package com.umpa2020.tracer

import android.annotation.SuppressLint
import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication


/**
 * 잠금화면 Example Application
 */
@SuppressLint("Registered")

/**
 *  자바에서 Hello.java라는 소스코드를 컴파일 하면 Hello.class라는 파일이 생성됨.
 *  안드로이드는 Hello.class 파일을 dx라는 툴을 이용해서 여러개의 .class파일을 하나의 classes.dex 파일로 생성.
 *  dex(Dalvik Executable) 파일은 안드로이드의 달빅 가상 머신(DVM)에서 실행되는 파일 포맷임.
 *  (https://source.android.com/devices/tech/dalvik/dex-format.html)
 *
 *  이 dex 파일의 제약으로 인해 함수 개수는 65K(65,536)개를 초과할 수 없음. 이를 해결하기 위해서 Multidex라는 개념이 등장.
 *  Multidex를 적용하면 APK 하나당 하나의 classes.dex 파일을 생성하는 것이 아니라 classes1.dex, classes2.dex와 같은 여러개의 dex 파일을
 *  생성하여 65K 문제를 해결하는 것.
 *
 *  간단한 앱을 만드는 경우 65K 문제를 고려해서 Multidex를 적용할 필요는 없지만
 *  사용하는 라이브러리가 많고, 앱의 기능이 추가되면서 함수 개수가 많아질 때 Multidex를 적용하면 됨.
 */
class LockScreenApplication : MultiDexApplication() {

  init {
    instance = this@LockScreenApplication
  }

  companion object {
    private var instance: LockScreenApplication? = null
    const val notificationId: Int = 1

    fun applicationContext() : Context? {
      return instance?.applicationContext
    }
  }

  override fun attachBaseContext(base: Context) {
    super.attachBaseContext(base)
    MultiDex.install(this@LockScreenApplication)
  }

  override fun onTerminate() {
    super.onTerminate()
    instance = null
  }
}