package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AccentColor
import com.example.model.AppLanguage
import com.example.model.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentTheme: AppTheme,
    currentAccentColor: AccentColor,
    currentLanguage: AppLanguage,
    hapticEnabled: Boolean,
    soundEnabled: Boolean,
    thousandsSeparatorEnabled: Boolean,
    onThemeChange: (AppTheme) -> Unit,
    onAccentColorChange: (AccentColor) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onHapticChange: (Boolean) -> Unit,
    onSoundChange: (Boolean) -> Unit,
    onThousandsSeparatorChange: (Boolean) -> Unit,
    onClearAllHistory: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showClearHistoryDialog by remember { mutableStateOf(false) }

    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.clear_history_confirm_title),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(text = stringResource(R.string.clear_history_confirm_message))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearAllHistory()
                        showClearHistoryDialog = false
                    },
                    modifier = Modifier.testTag("confirm_settings_clear_history")
                ) {
                    Text(
                        text = stringResource(R.string.clear),
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Theme Section
            SettingsSectionHeader(
                icon = Icons.Default.Palette,
                title = stringResource(R.string.settings_appearance_section)
            )

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.settings_appearance_section),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Row 1: SYSTEM, LIGHT, DARK
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeOptionChip(
                            label = stringResource(R.string.theme_system),
                            icon = Icons.Default.SettingsBrightness,
                            selected = currentTheme == AppTheme.SYSTEM,
                            onClick = { onThemeChange(AppTheme.SYSTEM) },
                            modifier = Modifier.weight(1f)
                        )

                        ThemeOptionChip(
                            label = stringResource(R.string.theme_light),
                            icon = Icons.Default.LightMode,
                            selected = currentTheme == AppTheme.LIGHT,
                            onClick = { onThemeChange(AppTheme.LIGHT) },
                            modifier = Modifier.weight(1f)
                        )

                        ThemeOptionChip(
                            label = stringResource(R.string.theme_dark),
                            icon = Icons.Default.DarkMode,
                            selected = currentTheme == AppTheme.DARK,
                            onClick = { onThemeChange(AppTheme.DARK) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Row 2: METALLIC, AMOLED, MIDNIGHT_BLUE
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeOptionChip(
                            label = stringResource(R.string.theme_metallic),
                            icon = Icons.Default.Palette,
                            selected = currentTheme == AppTheme.METALLIC,
                            onClick = { onThemeChange(AppTheme.METALLIC) },
                            modifier = Modifier.weight(1f)
                        )

                        ThemeOptionChip(
                            label = stringResource(R.string.theme_amoled),
                            icon = Icons.Default.DarkMode,
                            selected = currentTheme == AppTheme.AMOLED,
                            onClick = { onThemeChange(AppTheme.AMOLED) },
                            modifier = Modifier.weight(1f)
                        )

                        ThemeOptionChip(
                            label = stringResource(R.string.theme_midnight_blue),
                            icon = Icons.Default.Palette,
                            selected = currentTheme == AppTheme.MIDNIGHT_BLUE,
                            onClick = { onThemeChange(AppTheme.MIDNIGHT_BLUE) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Language Section
            SettingsSectionHeader(
                icon = Icons.Default.Language,
                title = stringResource(R.string.settings_language_section)
            )

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LanguageOptionChip(
                            label = stringResource(R.string.lang_system),
                            selected = currentLanguage == AppLanguage.SYSTEM,
                            onClick = { onLanguageChange(AppLanguage.SYSTEM) },
                            modifier = Modifier.weight(1f)
                        )

                        LanguageOptionChip(
                            label = stringResource(R.string.lang_arabic),
                            selected = currentLanguage == AppLanguage.ARABIC,
                            onClick = { onLanguageChange(AppLanguage.ARABIC) },
                            modifier = Modifier.weight(1f)
                        )

                        LanguageOptionChip(
                            label = stringResource(R.string.lang_english),
                            selected = currentLanguage == AppLanguage.ENGLISH,
                            onClick = { onLanguageChange(AppLanguage.ENGLISH) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Accent Color Customization Section
            SettingsSectionHeader(
                icon = Icons.Default.ColorLens,
                title = stringResource(R.string.settings_accent_color)
            )

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.settings_accent_color_desc),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Row 1: BLUE, EMERALD, PURPLE
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AccentColorOptionCard(
                            accent = AccentColor.BLUE,
                            selected = currentAccentColor == AccentColor.BLUE,
                            onClick = { onAccentColorChange(AccentColor.BLUE) },
                            modifier = Modifier.weight(1f)
                        )
                        AccentColorOptionCard(
                            accent = AccentColor.EMERALD,
                            selected = currentAccentColor == AccentColor.EMERALD,
                            onClick = { onAccentColorChange(AccentColor.EMERALD) },
                            modifier = Modifier.weight(1f)
                        )
                        AccentColorOptionCard(
                            accent = AccentColor.PURPLE,
                            selected = currentAccentColor == AccentColor.PURPLE,
                            onClick = { onAccentColorChange(AccentColor.PURPLE) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Row 2: ORANGE, ROSE, TEAL
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AccentColorOptionCard(
                            accent = AccentColor.ORANGE,
                            selected = currentAccentColor == AccentColor.ORANGE,
                            onClick = { onAccentColorChange(AccentColor.ORANGE) },
                            modifier = Modifier.weight(1f)
                        )
                        AccentColorOptionCard(
                            accent = AccentColor.ROSE,
                            selected = currentAccentColor == AccentColor.ROSE,
                            onClick = { onAccentColorChange(AccentColor.ROSE) },
                            modifier = Modifier.weight(1f)
                        )
                        AccentColorOptionCard(
                            accent = AccentColor.TEAL,
                            selected = currentAccentColor == AccentColor.TEAL,
                            onClick = { onAccentColorChange(AccentColor.TEAL) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Feedback & Sound Section
            SettingsSectionHeader(
                icon = Icons.Default.TouchApp,
                title = stringResource(R.string.settings_feedback_section)
            )

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    SettingsSwitchRow(
                        title = stringResource(R.string.settings_vibration),
                        description = stringResource(R.string.settings_vibration_desc),
                        checked = hapticEnabled,
                        onCheckedChange = onHapticChange,
                        icon = Icons.Default.TouchApp,
                        testTag = "switch_vibration"
                    )

                    SettingsSwitchRow(
                        title = stringResource(R.string.settings_sound),
                        description = stringResource(R.string.settings_sound_desc),
                        checked = soundEnabled,
                        onCheckedChange = onSoundChange,
                        icon = Icons.Default.VolumeUp,
                        testTag = "switch_sound"
                    )

                    SettingsSwitchRow(
                        title = stringResource(R.string.settings_thousands_sep),
                        description = stringResource(R.string.settings_thousands_sep_desc),
                        checked = thousandsSeparatorEnabled,
                        onCheckedChange = onThousandsSeparatorChange,
                        icon = Icons.Default.FormatListNumbered,
                        testTag = "switch_separator"
                    )
                }
            }

            // Data Management Section
            SettingsSectionHeader(
                icon = Icons.Default.DeleteSweep,
                title = stringResource(R.string.settings_data_section)
            )

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showClearHistoryDialog = true }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.clear_history),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.settings_clear_history_desc),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // About Section
            SettingsSectionHeader(
                icon = Icons.Default.Info,
                title = stringResource(R.string.settings_about_section)
            )

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.calculator_title),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = "${stringResource(R.string.app_version)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.app_description),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AccentColorOptionCard(
    accent: AccentColor,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (selected) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
    }

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(
            if (selected) 2.dp else 1.dp,
            if (selected) accent.color else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 2.dp else 0.dp),
        modifier = modifier.testTag("accent_${accent.name.lowercase()}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(accent.color),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(accent.titleRes),
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
fun SettingsSectionHeader(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(start = 4.dp, top = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ThemeOptionChip(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
        },
        shape = RoundedCornerShape(12.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = modifier.testTag("theme_${label.lowercase()}")
    )
}

@Composable
fun LanguageOptionChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
        },
        shape = RoundedCornerShape(12.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = modifier.testTag("lang_${label.lowercase()}")
    )
}

@Composable
fun SettingsSwitchRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: ImageVector,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}



