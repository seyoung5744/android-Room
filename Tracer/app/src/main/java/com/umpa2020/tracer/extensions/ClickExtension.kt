package com.umpa2020.tracer.extensions

import android.os.SystemClock
import android.view.View
import com.umpa2020.tracer.constant.Constants

fun View.single():Boolean{
  val currentClickTime = SystemClock.uptimeMillis()
  val elapsedTime = currentClickTime - Constants.mLastClickTime
  Constants.mLastClickTime = currentClickTime

  // 중복클릭 아닌 경우
  if (elapsedTime > Constants.MIN_CLICK_INTERVAL) {
    return true
  }
  return false
}