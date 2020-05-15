package com.umpa2020.tracer.roomDatabase.repository

import androidx.lifecycle.LiveData
import com.umpa2020.tracer.roomDatabase.Dao.GpsDao
import com.umpa2020.tracer.roomDatabase.MyRoomDatabase
import com.umpa2020.tracer.roomDatabase.entity.GPSData

class GpsRepository {
  val gpsDao = MyRoomDatabase.instance.gpsDao()
  val allGps: LiveData<GPSData> = gpsDao.getAll()
  val isUid: LiveData<GPSData> = gpsDao.isUid()

  suspend fun insert(gpsData: GPSData) {
    gpsDao.insert(gpsData)
  }

  suspend fun deleteAll() {
    gpsDao.deleteAll()
  }

  suspend fun updateLastPosition(lat: Double, lng: Double) {
    gpsDao.updateLastPosition(lat, lng)
  }
}