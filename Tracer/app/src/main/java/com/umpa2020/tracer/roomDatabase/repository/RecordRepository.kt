package com.umpa2020.tracer.roomDatabase.repository

import androidx.lifecycle.LiveData
import com.umpa2020.tracer.roomDatabase.Dao.RecordDao
import com.umpa2020.tracer.roomDatabase.MyRoomDatabase
import com.umpa2020.tracer.roomDatabase.entity.MapRecordData

/**
 *  Declares the DAO as a private property in the constructor. Pass in the DAO
 *  instead of the whole database, because you only need access to the DAO
 *
 *  [레포지토리(Repository) 란]
 *  레포지토리 클래스는 여러 data sources에 접근을 추상화한다. 레포지토리는 Architecture Components
 *  libraries에 속하지는 않지만 코드 분리 및 아키텍처에 대해 제안된 best practice이다.
 *  레포지토리 클래스는 rest of the application에 대한 데이터 엑세스를 위한 클린한 API를 제공한다.
 */
class RecordRepository() {
  val recordDao=MyRoomDatabase.instance.recordDao()
  // Room executes all queries on a separate thread.
  // Observed LiveData will notify the observer when the data has changed.
  val allRecord : LiveData<MapRecordData> = recordDao.getAll()
//  val allTime : List<MapRecordData> = recordDao.getTime()

  suspend fun insert(mapRecords: MapRecordData) {
    recordDao.insert(mapRecords)
  }

  suspend fun deleteAll(){
    recordDao.deleteAll()
  }

  suspend fun updateSpeedDistance(speed : String, distance : String) {
    recordDao.updateSpeedDistance(speed, distance)
  }

  suspend fun updateStartTime(startTime : Long) {
    recordDao.updateStartTime(startTime)
  }

  suspend fun updateTimeControl(whenStopped : Long, flag : Boolean){
    recordDao.updateTimeControl(whenStopped, flag)
  }

  suspend fun updateTimeText(timeText : String){
    recordDao.updateTimeText(timeText)
  }
}