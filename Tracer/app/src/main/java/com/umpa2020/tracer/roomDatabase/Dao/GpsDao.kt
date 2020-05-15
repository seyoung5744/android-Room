package com.umpa2020.tracer.roomDatabase.Dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.umpa2020.tracer.roomDatabase.entity.GPSData

@Dao
interface GpsDao {
  // 최신 데이터 가져오기
  @Query("SELECT * FROM gps ORDER BY uid LIMIT 1")
  fun getAll(): LiveData<GPSData>

  //이미 저장된 항목이 있을 경우 데이터를 덮어씁니다.
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(gpsData: GPSData)

  @Query("UPDATE gps SET lat = :lat, lng = :lng WHERE uid=0")
  suspend fun updateLastPosition(lat : Double, lng : Double)

  @Query("DELETE FROM gps")
  suspend fun deleteAll()

  @Query("SELECT * FROM gps ORDER BY uid LIMIT 1")
  fun isUid() : LiveData<GPSData>
}