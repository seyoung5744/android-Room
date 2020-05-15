package com.umpa2020.tracer.lockscreen.util

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.umpa2020.tracer.util.Logg


/**
 * 잠금화면을 스와이프하면 finish 하도록 하는 touch util
 *
 * View의 상대 좌표, 절대 좌표
 * https://www.crocus.co.kr/1618
 *
 */

open class ViewUnLock(val context: Context, val lockScreenView: ConstraintLayout) :
  View.OnTouchListener {

  private var firstTouchY = 0f
  private var lastLayoutY = 0f
  private var isLockOpen = false

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouch(v: View, event: MotionEvent): Boolean {
    when (event.action) {
      MotionEvent.ACTION_DOWN -> {
        firstTouchY = event.y
        Logg.d("firstTouchY : $firstTouchY") // 위로 올리면 숫자가 줄어듬.
        isLockOpen = true
      }

      MotionEvent.ACTION_MOVE -> {
        if (isLockOpen) {
          Logg.d(event.y.toString())
          // touchMoveY
          // 위로 올리면 음수
          // 아래로 내리면 양수
          val touchMoveY = event.y - firstTouchY
          Logg.d("touchMoveY : $touchMoveY")

          if(touchMoveY < 0)
            lockScreenView.y = lockScreenView.y + touchMoveY
          Logg.d("이동 : ${lockScreenView.y}")


          lastLayoutY = -lockScreenView.y
//          }
        } else {
          return false
        }
      }
      MotionEvent.ACTION_UP -> {
        if (isLockOpen) {
          lockScreenView.y = 0f
          optimizeForground(lastLayoutY)
        }

        isLockOpen = false
        firstTouchY = 0f
        lastLayoutY = 0f
      }
      else -> {
      }
    }

    return true
  }


  private fun optimizeForground(forgroundY: Float) {

    val displayMetrics = context.resources.displayMetrics
    val mDeviceHeight = displayMetrics.heightPixels

    Logg.d(forgroundY.toString())

    if(forgroundY > 500) {
      Logg.d("실행?")
      val animation = TranslateAnimation(0f, 0f,  -forgroundY,-mDeviceHeight.toFloat())
      animation.duration = 300
      animation.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation) {}

        override fun onAnimationEnd(animation: Animation) {
          lockScreenView.y = -mDeviceHeight.toFloat()
          lockScreenView.x = 0f
          onFinish()
        }

        override fun onAnimationRepeat(animation: Animation) {}
      })

      lockScreenView.startAnimation(animation)
    }
  }

  open fun onFinish() {

  }
}