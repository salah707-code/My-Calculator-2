package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.CalculationHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM calculation_history WHERE isDeleted = 0 ORDER BY timestamp DESC")
    fun getActiveHistory(): Flow<List<CalculationHistory>>

    @Query("SELECT * FROM calculation_history WHERE isDeleted = 1 ORDER BY deletedAt DESC, timestamp DESC")
    fun getTrashHistory(): Flow<List<CalculationHistory>>

    @Query("SELECT * FROM calculation_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<CalculationHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: CalculationHistory): Long

    @Update
    suspend fun updateHistory(history: CalculationHistory)

    @Query("UPDATE calculation_history SET note = :note WHERE id = :id")
    suspend fun updateNote(id: Long, note: String)

    @Query("UPDATE calculation_history SET isDeleted = 1, deletedAt = :timestamp WHERE id = :id")
    suspend fun moveToTrash(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE calculation_history SET isDeleted = 0, deletedAt = NULL WHERE id = :id")
    suspend fun restoreFromTrash(id: Long)

    @Query("UPDATE calculation_history SET isDeleted = 1, deletedAt = :timestamp WHERE isDeleted = 0")
    suspend fun moveAllToTrash(timestamp: Long = System.currentTimeMillis())

    @Delete
    suspend fun deleteHistory(history: CalculationHistory)

    @Query("DELETE FROM calculation_history WHERE id = :id")
    suspend fun deletePermanently(id: Long)

    @Query("DELETE FROM calculation_history WHERE isDeleted = 1")
    suspend fun clearTrash()

    @Query("DELETE FROM calculation_history")
    suspend fun clearAllHistory()
}
