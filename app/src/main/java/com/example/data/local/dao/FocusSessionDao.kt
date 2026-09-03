package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.FocusSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSessionDao {
  @Query("SELECT * FROM focus_sessions ORDER BY startTime DESC")
  fun getAllSessionsFlow(): Flow<List<FocusSessionEntity>>

  @Query("SELECT * FROM focus_sessions ORDER BY startTime DESC")
  suspend fun getAllSessions(): List<FocusSessionEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSession(session: FocusSessionEntity)

  @Query("SELECT COALESCE(SUM(durationSeconds), 0) FROM focus_sessions WHERE completed = 1")
  fun getTotalFocusDurationFlow(): Flow<Int>

  @Query("SELECT COALESCE(SUM(durationSeconds), 0) FROM focus_sessions WHERE completed = 1")
  suspend fun getTotalFocusDuration(): Int

  @Query("SELECT COUNT(*) FROM focus_sessions WHERE completed = 1")
  fun getCompletedSessionCountFlow(): Flow<Int>

  @Query("SELECT COUNT(*) FROM focus_sessions WHERE completed = 1")
  suspend fun getCompletedSessionCount(): Int

  @Query("SELECT * FROM focus_sessions WHERE startTime >= :startMs AND startTime <= :endMs ORDER BY startTime ASC")
  fun getSessionsInRangeFlow(startMs: Long, endMs: Long): Flow<List<FocusSessionEntity>>

  @Query("SELECT * FROM focus_sessions WHERE startTime >= :startMs AND startTime <= :endMs ORDER BY startTime ASC")
  suspend fun getSessionsInRange(startMs: Long, endMs: Long): List<FocusSessionEntity>

  @Query("SELECT COALESCE(SUM(durationSeconds), 0) FROM focus_sessions WHERE completed = 1 AND startTime >= :startMs AND startTime <= :endMs")
  fun getFocusDurationInRangeFlow(startMs: Long, endMs: Long): Flow<Int>

  @Query("SELECT COUNT(*) FROM focus_sessions WHERE completed = 1 AND startTime >= :startMs AND startTime <= :endMs")
  fun getFocusSessionCountInRangeFlow(startMs: Long, endMs: Long): Flow<Int>
}
