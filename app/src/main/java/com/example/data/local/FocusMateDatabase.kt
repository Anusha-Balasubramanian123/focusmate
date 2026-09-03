package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.local.dao.FocusSessionDao
import com.example.data.local.dao.TaskDao
import com.example.data.local.entity.FocusSessionEntity
import com.example.data.local.entity.TaskEntity

@Database(
  entities = [
    TaskEntity::class,
    FocusSessionEntity::class
  ],
  version = 1,
  exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FocusMateDatabase : RoomDatabase() {
  abstract fun taskDao(): TaskDao
  abstract fun focusSessionDao(): FocusSessionDao

  companion object {
    @Volatile
    private var INSTANCE: FocusMateDatabase? = null

    fun getDatabase(context: Context): FocusMateDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          FocusMateDatabase::class.java,
          "focusmate_database"
        )
        .fallbackToDestructiveMigration()
        .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
