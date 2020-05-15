package com.umpa2020.tracer.roomDatabase.Dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.umpa2020.tracer.roomDatabase.entity.MapRecordData

/**
 *  DAO (data access object)는 SQL 쿼리를 명시하고 메소드 호출과 연결해준다.
 */
@Dao
interface RecordDao {
  // 최신 데이터 가져오기
  @Query("SELECT * FROM map_record ORDER BY uid DESC LIMIT 1")
  fun getAll(): LiveData<MapRecordData>

//  @Query("SELECT * FROM map_record LIMIT 1")
//  fun getTime(): List<MapRecordData>

  //이미 저장된 항목이 있을 경우 데이터를 덮어씁니다.
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(mapRecords: MapRecordData)

  // 속도, 거리 업데이트
  @Query("UPDATE map_record SET speed = :speed, distance = :distance WHERE uid=0")
  suspend fun updateSpeedDistance(speed : String, distance : String)

  @Query("UPDATE map_record SET time = :startTime WHERE uid=0")
  suspend fun updateStartTime(startTime : Long)

  @Query("UPDATE map_record SET timeWhenStop = :whenStopped, timeControl = :flag WHERE uid=0")
  suspend fun updateTimeControl(whenStopped : Long, flag : Boolean)

  @Query("UPDATE map_record SET timeText = :timeText WHERE uid=0")
  suspend fun updateTimeText(timeText : String)

  @Query("DELETE FROM map_record")
  suspend fun deleteAll()

}