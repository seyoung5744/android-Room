package com.umpa2020.tracer.roomDatabase.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.umpa2020.tracer.roomDatabase.MyRoomDatabase
import com.umpa2020.tracer.roomDatabase.entity.MapRecordData
import com.umpa2020.tracer.roomDatabase.repository.RecordRepository
import kotlinx.coroutines.launch

class RecordViewModel(application: Application) : AndroidViewModel(application) {
  // The ViewModel maintains a reference to the repository to get data.
  private val repository =  RecordRepository()

  // LiveData gives us updated words when they change.
  val allRecords: LiveData<MapRecordData>
//  val allTimes: List<MapRecordData>

  init {
    // Gets reference to RecordDeo from RecordRoomDatabase to construct
    // the correct RecordRepository.
    //val recordDao = MyRoomDatabase.getDatabase(application, viewModelScope).recordDao()

    allRecords = repository.allRecord
//    allTimes = repository.allTime
  }

  /**
   * The implementation of insert() in the database is completely hidden from the UI.
   * Room ensures that you're not doing any long running operations on
   * the main thread, blocking the UI, so we don't need to handle changing Dispatchers.
   * ViewModels have a coroutine scope based on their lifecycle called
   * viewModelScope which we can use here.
   */

  fun insert(mapRecords: MapRecordData) = viewModelScope.launch {
    repository.insert(mapRecords)
  }

  //  fun getTime() : MapRecordData {
//    val temp =  repository.getTime()
//    return temp
//  }
  fun deleteAll() = viewModelScope.launch {
    repository.deleteAll()
  }

  /**
   * update 방법은 테이터베이스에서 매개 변수로 지정된 엔티티 집합을 수정한다. 각 엔티티의 기본 키와 일치하는 조회를 사용한다.
   */
  fun updateSpeedDistance(speed: String, distance: String) = viewModelScope.launch {
    repository.updateSpeedDistance(speed, distance)
  }

  fun updateStartTime(startTime: Long) = viewModelScope.launch {
    repository.updateStartTime(startTime)
  }

  fun updateTimeControl(whenStopped: Long, flag: Boolean) = viewModelScope.launch {
    repository.updateTimeControl(whenStopped, flag)
  }

  fun updateTimeText(timeText : String) = viewModelScope.launch {
    repository.updateTimeText(timeText)
  }

}