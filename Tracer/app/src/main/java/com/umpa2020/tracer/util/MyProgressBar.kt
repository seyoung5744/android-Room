package com.umpa2020.tracer.util

import android.app.Activity
import com.umpa2020.tracer.App

class MyProgressBar {
  var switch = 0
  var mprogressBar = ProgressBar(App.instance.currentActivity() as Activity)

  fun show() {
    if(switch==0) mprogressBar.show()
    switch++
  }

  fun dismiss() {
    switch--
    if (switch == 0) mprogressBar.dismiss()
  }
}