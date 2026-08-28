package com.example.data.local

import com.example.model.CalculationHistory
import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val historyDao: HistoryDao) {
    val allHistory: Flow<List<CalculationHistory>> = historyDao.getAllHistory()

    suspend fun insert(history: CalculationHistory): Long {
        return historyDao.insertHistory(history)
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
