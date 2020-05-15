package com.umpa2020.tracer.util

import android.os.SystemClock
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.umpa2020.tracer.constant.Constants


interface OnSingleClickListener : View.OnClickListener {
  fun onSingleClick(v: View?)

  override fun onClick(v: View?){
    val currentClickTime = SystemClock.uptimeMillis()
    val elapsedTime = currentClickTime - Constants.mLastClickTime
    Constants.mLastClickTime = currentClickTime

    // 중복클릭 아닌 경우
    if (elapsedTime > Constants.MIN_CLICK_INTERVAL) {
      onSingleClick(v)
    }
  }
}