package com.example

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.example.model.AccentColor
import com.example.model.AppLanguage
import com.example.model.AppTheme
import com.example.ui.CalculatorViewModel
import com.example.ui.screens.CalculatorScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.UnitConverterScreen
import com.example.ui.theme.CalculatorTheme
import java.util.Locale

enum class CurrentDestination {
    CALCULATOR,
    HISTORY,
    SETTINGS,
    UNIT_CONVERTER
}

class MainActivity : ComponentActivity() {

    private val viewModel: CalculatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val accentColor by viewModel.accentColor.collectAsState()
            val language by viewModel.language.collectAsState()

            val baseContext = LocalContext.current
            val localizedContext = remember(language, baseContext) {
                when (language) {
                    AppLanguage.SYSTEM -> baseContext
                    AppLanguage.ARABIC -> createLocalizedContext(baseContext, "ar")
                    AppLanguage.ENGLISH -> createLocalizedContext(baseContext, "en")
                }
            }

            CompositionLocalProvider(
                LocalContext provides localizedContext,
                LocalConfiguration provides localizedContext.resources.configuration
            ) {
                CalculatorTheme(appTheme = themeMode, accentColor = accentColor) {
                    MainAppContent(viewModel = viewModel)
                }
            }
        }
    }

    private fun createLocalizedContext(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}

@Composable
fun MainAppContent(viewModel: CalculatorViewModel) {
    var currentDestination by remember { mutableStateOf(CurrentDestination.CALCULATOR) }

    val uiState by viewModel.uiState.collectAsState()
    val historyList by viewModel.historyList.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val accentColor by viewModel.accentColor.collectAsState()
    val language by viewModel.language.collectAsState()
    val hapticEnabled by viewModel.hapticEnabled.collectAsState()
    val soundEnabled by viewModel.soundEnabled.collectAsState()
    val thousandsSeparatorEnabled by viewModel.thousandsSeparatorEnabled.collectAsState()

    BackHandler(enabled = currentDestination != CurrentDestination.CALCULATOR) {
        currentDestination = CurrentDestination.CALCULATOR
    }

    AnimatedContent(
        targetState = currentDestination,
        transitionSpec = {
            if (targetState != CurrentDestination.CALCULATOR) {
                (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                    slideOutHorizontally { width -> -width / 3 } + fadeOut()
                )
            } else {
                (slideInHorizontally { width -> -width / 3 } + fadeIn()).togetherWith(
                    slideOutHorizontally { width -> width } + fadeOut()
                )
            }
        },
        label = "screen_transition",
        modifier = Modifier.fillMaxSize()
    ) { destination ->
        when (destination) {
            CurrentDestination.CALCULATOR -> {
                CalculatorScreen(
                    state = uiState,
                    currentTheme = themeMode,
                    onKeyClick = { key, view -> viewModel.onKeyClicked(key, view) },
                    onOpenSettings = { currentDestination = CurrentDestination.SETTINGS }
                )
            }
            CurrentDestination.HISTORY -> {
                HistoryScreen(
                    historyList = historyList,
                    onBack = { currentDestination = CurrentDestination.CALCULATOR },
                    onUseResult = { result ->
                        viewModel.reuseResult(result)
                        currentDestination = CurrentDestination.CALCULATOR
                    },
                    onDeleteSingle = { item -> viewModel.deleteHistoryItem(item) },
                    onClearAll = { viewModel.clearAllHistory() }
                )
            }
            CurrentDestination.SETTINGS -> {
                SettingsScreen(
                    currentTheme = themeMode,
                    currentAccentColor = accentColor,
                    currentLanguage = language,
                    hapticEnabled = hapticEnabled,
                    soundEnabled = soundEnabled,
                    thousandsSeparatorEnabled = thousandsSeparatorEnabled,
                    calculatorMode = uiState.calculatorMode,
                    isDegMode = uiState.isDegMode,
                    onThemeChange = { theme -> viewModel.setTheme(theme) },
                    onAccentColorChange = { accent -> viewModel.setAccentColor(accent) },
                    onLanguageChange = { lang -> viewModel.setLanguage(lang) },
                    onHapticChange = { enabled -> viewModel.setHaptic(enabled) },
                    onSoundChange = { enabled -> viewModel.setSound(enabled) },
                    onThousandsSeparatorChange = { enabled -> viewModel.setThousandsSeparator(enabled) },
                    onModeChange = { mode -> viewModel.setCalculatorMode(mode) },
                    onToggleDegMode = { viewModel.toggleDegMode() },
                    onOpenHistory = { currentDestination = CurrentDestination.HISTORY },
                    onOpenUnitConverter = { currentDestination = CurrentDestination.UNIT_CONVERTER },
                    onClearAllHistory = { viewModel.clearAllHistory() },
                    onBack = { currentDestination = CurrentDestination.CALCULATOR }
                )
            }
            CurrentDestination.UNIT_CONVERTER -> {
                UnitConverterScreen(
                    currentTheme = themeMode,
                    language = language,
                    hapticEnabled = hapticEnabled,
                    soundEnabled = soundEnabled,
                    thousandsSeparatorEnabled = thousandsSeparatorEnabled,
                    onBack = { currentDestination = CurrentDestination.SETTINGS },
                    onUseResultInCalculator = { result ->
                        viewModel.reuseResult(result)
                        currentDestination = CurrentDestination.CALCULATOR
                    }
                )
            }
        }
    }
}
