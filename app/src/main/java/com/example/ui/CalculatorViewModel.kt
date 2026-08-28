package com.example.ui

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.HistoryRepository
import com.example.data.local.PreferencesRepository
import com.example.engine.CalculatorEngine
import com.example.model.AccentColor
import com.example.model.AppTheme
import com.example.model.CalculationHistory
import com.example.model.CalculatorKey
import com.example.model.CalculatorState
import com.example.ui.util.FeedbackHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val historyRepository = HistoryRepository(db.historyDao())
    val preferencesRepository = PreferencesRepository(application)
    private val feedbackHelper = FeedbackHelper(application)

    private val engine = CalculatorEngine(
        useThousandsSeparator = preferencesRepository.thousandsSeparatorEnabled.value
    )

    private val _uiState = MutableStateFlow(engine.getState())
    val uiState: StateFlow<CalculatorState> = _uiState.asStateFlow()

    val historyList: StateFlow<List<CalculationHistory>> = historyRepository.allHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val themeMode: StateFlow<AppTheme> = preferencesRepository.themeMode
    val accentColor: StateFlow<AccentColor> = preferencesRepository.accentColor
    val appLanguage: StateFlow<com.example.model.AppLanguage> = preferencesRepository.appLanguage
    val language: StateFlow<com.example.model.AppLanguage> = appLanguage
    val hapticEnabled: StateFlow<Boolean> = preferencesRepository.hapticEnabled
    val soundEnabled: StateFlow<Boolean> = preferencesRepository.soundEnabled
    val thousandsSeparatorEnabled: StateFlow<Boolean> = preferencesRepository.thousandsSeparatorEnabled
    val scientificModeEnabled: StateFlow<Boolean> = preferencesRepository.scientificModeEnabled
    val degModeEnabled: StateFlow<Boolean> = preferencesRepository.degModeEnabled

    init {
        engine.setCalculatorMode(if (scientificModeEnabled.value) com.example.model.CalculatorMode.SCIENTIFIC else com.example.model.CalculatorMode.BASIC)
        engine.setDegMode(degModeEnabled.value)
        _uiState.value = engine.getState()

        viewModelScope.launch {
            thousandsSeparatorEnabled.collect { enabled ->
                engine.setThousandsSeparator(enabled)
                _uiState.value = engine.getState()
            }
        }
        viewModelScope.launch {
            scientificModeEnabled.collect { enabled ->
                engine.setCalculatorMode(if (enabled) com.example.model.CalculatorMode.SCIENTIFIC else com.example.model.CalculatorMode.BASIC)
                _uiState.value = engine.getState()
            }
        }
        viewModelScope.launch {
            degModeEnabled.collect { enabled ->
                engine.setDegMode(enabled)
                _uiState.value = engine.getState()
            }
        }
    }

    fun onKeyClicked(key: CalculatorKey, view: View? = null) {
        feedbackHelper.triggerKeyClick(
            hapticEnabled = hapticEnabled.value,
            soundEnabled = soundEnabled.value,
            view = view
        )

        val newState = engine.onKeyPress(key)
        _uiState.value = newState

        // If user tapped AngleModeToggle key on scientific keypad, sync preference
        if (key is CalculatorKey.AngleModeToggle) {
            preferencesRepository.setDegModeEnabled(newState.isDegMode)
        }

        newState.lastCalculation?.let { calculation ->
            viewModelScope.launch {
                historyRepository.insert(calculation)
            }
        }
    }

    fun toggleScientificMode() {
        val newMode = !scientificModeEnabled.value
        preferencesRepository.setScientificModeEnabled(newMode)
    }

    fun toggleDegMode() {
        val newDeg = !degModeEnabled.value
        preferencesRepository.setDegModeEnabled(newDeg)
    }

    fun reuseResult(resultString: String) {
        engine.resetWithInitialValue(resultString)
        _uiState.value = engine.getState()
    }

    fun deleteHistoryItem(history: CalculationHistory) {
        viewModelScope.launch {
            historyRepository.delete(history)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            historyRepository.clearAll()
        }
    }

    fun setTheme(theme: AppTheme) {
        preferencesRepository.setTheme(theme)
    }

    fun setAccentColor(accent: AccentColor) {
        preferencesRepository.setAccentColor(accent)
    }

    fun setLanguage(language: com.example.model.AppLanguage) {
        preferencesRepository.setLanguage(language)
    }

    fun setHaptic(enabled: Boolean) {
        preferencesRepository.setHapticEnabled(enabled)
    }

    fun setSound(enabled: Boolean) {
        preferencesRepository.setSoundEnabled(enabled)
    }

    fun setThousandsSeparator(enabled: Boolean) {
        preferencesRepository.setThousandsSeparatorEnabled(enabled)
    }
}
