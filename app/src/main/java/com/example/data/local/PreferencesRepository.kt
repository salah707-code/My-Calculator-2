package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AccentColor
import com.example.model.AppLanguage
import com.example.model.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("calculator_settings", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(loadTheme())
    val themeMode: StateFlow<AppTheme> = _themeMode.asStateFlow()

    private val _accentColor = MutableStateFlow(loadAccentColor())
    val accentColor: StateFlow<AccentColor> = _accentColor.asStateFlow()

    private val _appLanguage = MutableStateFlow(loadLanguage())
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    private val _hapticEnabled = MutableStateFlow(prefs.getBoolean(KEY_HAPTIC, true))
    val hapticEnabled: StateFlow<Boolean> = _hapticEnabled.asStateFlow()

    private val _soundEnabled = MutableStateFlow(prefs.getBoolean(KEY_SOUND, true))
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _thousandsSeparatorEnabled = MutableStateFlow(prefs.getBoolean(KEY_THOUSANDS_SEP, true))
    val thousandsSeparatorEnabled: StateFlow<Boolean> = _thousandsSeparatorEnabled.asStateFlow()

    private val _scientificModeEnabled = MutableStateFlow(prefs.getBoolean(KEY_SCIENTIFIC_MODE, false))
    val scientificModeEnabled: StateFlow<Boolean> = _scientificModeEnabled.asStateFlow()

    private val _degModeEnabled = MutableStateFlow(prefs.getBoolean(KEY_DEG_MODE, true))
    val degModeEnabled: StateFlow<Boolean> = _degModeEnabled.asStateFlow()

    fun setTheme(theme: AppTheme) {
        prefs.edit().putString(KEY_THEME, theme.name).apply()
        _themeMode.value = theme
    }

    fun setAccentColor(accent: AccentColor) {
        prefs.edit().putString(KEY_ACCENT, accent.name).apply()
        _accentColor.value = accent
    }

    fun setLanguage(language: AppLanguage) {
        prefs.edit().putString(KEY_LANGUAGE, language.name).apply()
        _appLanguage.value = language
    }

    fun setHapticEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HAPTIC, enabled).apply()
        _hapticEnabled.value = enabled
    }

    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SOUND, enabled).apply()
        _soundEnabled.value = enabled
    }

    fun setThousandsSeparatorEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_THOUSANDS_SEP, enabled).apply()
        _thousandsSeparatorEnabled.value = enabled
    }

    fun setScientificModeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SCIENTIFIC_MODE, enabled).apply()
        _scientificModeEnabled.value = enabled
    }

    fun setDegModeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DEG_MODE, enabled).apply()
        _degModeEnabled.value = enabled
    }

    private fun loadTheme(): AppTheme {
        val name = prefs.getString(KEY_THEME, AppTheme.SYSTEM.name) ?: AppTheme.SYSTEM.name
        return try {
            AppTheme.valueOf(name)
        } catch (e: Exception) {
            AppTheme.SYSTEM
        }
    }

    private fun loadLanguage(): AppLanguage {
        val name = prefs.getString(KEY_LANGUAGE, AppLanguage.SYSTEM.name) ?: AppLanguage.SYSTEM.name
        return try {
            AppLanguage.valueOf(name)
        } catch (e: Exception) {
            AppLanguage.SYSTEM
        }
    }

    private fun loadAccentColor(): AccentColor {
        val name = prefs.getString(KEY_ACCENT, AccentColor.BLUE.name) ?: AccentColor.BLUE.name
        return try {
            AccentColor.valueOf(name)
        } catch (e: Exception) {
            AccentColor.BLUE
        }
    }

    companion object {
        private const val KEY_THEME = "pref_theme"
        private const val KEY_ACCENT = "pref_accent_color"
        private const val KEY_LANGUAGE = "pref_language"
        private const val KEY_HAPTIC = "pref_haptic"
        private const val KEY_SOUND = "pref_sound"
        private const val KEY_THOUSANDS_SEP = "pref_thousands_sep"
        private const val KEY_SCIENTIFIC_MODE = "pref_scientific_mode"
        private const val KEY_DEG_MODE = "pref_deg_mode"
    }
}

