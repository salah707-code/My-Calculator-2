package com.example.data.local

import com.example.model.CalculationHistory
import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val historyDao: HistoryDao) {
    val activeHistory: Flow<List<CalculationHistory>> = historyDao.getActiveHistory()
    val trashHistory: Flow<List<CalculationHistory>> = historyDao.getTrashHistory()
    val allHistory: Flow<List<CalculationHistory>> = historyDao.getAllHistory()

    suspend fun insert(history: CalculationHistory): Long {
        return historyDao.insertHistory(history)
    }

    suspend fun updateNote(id: Long, note: String) {
        historyDao.updateNote(id, note)
    }

    suspend fun moveToTrash(id: Long) {
        historyDao.moveToTrash(id)
    }

    suspend fun restoreFromTrash(id: Long) {
        historyDao.restoreFromTrash(id)
    }

    suspend fun moveAllToTrash() {
        historyDao.moveAllToTrash()
    }

    suspend fun deletePermanently(id: Long) {
        historyDao.deletePermanently(id)
    }

    suspend fun clearTrash() {
        historyDao.clearTrash()
    }

    suspend fun delete(history: CalculationHistory) {
        historyDao.deleteHistory(history)
    }

    suspend fun deleteById(id: Long) {
        historyDao.deleteById(id)
    }

    suspend fun clearAll() {
        historyDao.clearAllHistory()
    }
}
