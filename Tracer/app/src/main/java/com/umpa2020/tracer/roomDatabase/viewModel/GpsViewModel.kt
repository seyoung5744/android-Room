package com.umpa2020.tracer.roomDatabase.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.umpa2020.tracer.roomDatabase.entity.GPSData
import com.umpa2020.tracer.roomDatabase.repository.GpsRepository
import com.umpa2020.tracer.util.Logg
import kotlinx.coroutines.launch

class GpsViewModel(application: Application) : AndroidViewModel(application) {
  private val repository = GpsRepository()
  val gpsDao = repository.gpsDao
  val allGps: LiveData<GPSData> = repository.allGps
  val isUid: LiveData<GPSData> = repository.isUid

  init {
    if (isUid.value == null) {
      Logg.d("is uid is null")
    }
  }

  fun insert(gpsData: GPSData) = viewModelScope.launch {
    repository.insert(gpsData)
  }

  fun deleteAll() = viewModelScope.launch {
    repository.deleteAll()
  }

  fun updateLastPosition(lat: Double, lng: Double) = viewModelScope.launch {
    repository.updateLastPosition(lat, lng)
  }

}