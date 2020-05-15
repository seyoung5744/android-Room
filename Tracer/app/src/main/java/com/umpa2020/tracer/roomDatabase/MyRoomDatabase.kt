package com.umpa2020.tracer.roomDatabase

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.umpa2020.tracer.App
import com.umpa2020.tracer.roomDatabase.Dao.GpsDao
import com.umpa2020.tracer.roomDatabase.Dao.RecordDao
import com.umpa2020.tracer.roomDatabase.entity.GPSData
import com.umpa2020.tracer.roomDatabase.entity.MapRecordData

// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(entities = arrayOf(MapRecordData::class, GPSData::class), version = 1)
abstract class MyRoomDatabase : RoomDatabase() {
  abstract fun gpsDao(): GpsDao
  abstract fun recordDao(): RecordDao

  // Singleton prevents multiple instances of database opening at the
  // same time.
  companion object {
    var instance = Room.databaseBuilder(
      App.instance,
      MyRoomDatabase::class.java,
      "record_database"
    ).build()
  }
}