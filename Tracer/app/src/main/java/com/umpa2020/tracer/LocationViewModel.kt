package com.umpa2020.tracer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.umpa2020.tracer.util.Logg
import kotlinx.coroutines.launch

class LocationViewModel(application: Application) : AndroidViewModel(application) {
  val distanceSpeed = MutableLiveData<DistanceTimeData>()
  val times = MutableLiveData<TimeData>()

  fun init(recordData: DistanceTimeData, timeData: TimeData) {
    distanceSpeed.value = recordData
    times.value = timeData
  }

  fun setDistanceSpeed(distanceTimeData: DistanceTimeData) = viewModelScope.launch {
    Logg.d("셋팅 돼?")
    distanceSpeed.value = distanceTimeData
  }

  fun setTimes(timeData: TimeData) = viewModelScope.launch {
    times.value = timeData
  }

//  fun setDistance(distanceStr: String) = viewModelScope.launch {
//    distance.value= distanceStr
//  }
//
//  fun updateStartTime(startTime: Long) = viewModelScope.launch {
//    time.value = startTime
//  }
//
//  fun updateWhenStopped(whenStopped: Long) = viewModelScope.launch {
//    timeWhenStop.value = whenStopped
//  }
//  fun updateTimeFlag( flag: Boolean) = viewModelScope.launch {
//    timeControl.value = flag
//  }
//
//  fun updateTimeText(timeTextStr: String) = viewModelScope.launch {
//    timeText.value = timeTextStr
//  }
}