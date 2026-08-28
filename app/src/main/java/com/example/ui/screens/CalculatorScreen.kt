package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppTheme
import com.example.model.CalculatorKey
import com.example.model.CalculatorMode
import com.example.model.CalculatorState
import com.example.model.KeyType
import com.example.ui.components.AutoResizeText
import com.example.ui.components.CalculatorKeyButton

@Composable
fun CalculatorScreen(
    state: CalculatorState,
    currentTheme: AppTheme,
    onKeyClick: (CalculatorKey) -> Unit,
    onToggleMode: () -> Unit,
    onToggleDegMode: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark = when (currentTheme) {
        AppTheme.SYSTEM -> isSystemInDarkTheme()
        AppTheme.LIGHT -> false
        AppTheme.DARK, AppTheme.METALLIC, AppTheme.AMOLED, AppTheme.MIDNIGHT_BLUE -> true
    }
    val isScientific = state.calculatorMode == CalculatorMode.SCIENTIFIC

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(top = 2.dp)
                .widthIn(max = 600.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Navigation & Action Bar
            TopActionBar(
                currentTheme = currentTheme,
                isScientific = isScientific,
                onToggleMode = onToggleMode,
                onOpenHistory = onOpenHistory,
                onOpenSettings = onOpenSettings,
                onToggleTheme = onToggleTheme,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 2.dp)
            )

            // Compact Display Screen Area
            DisplayScreen(
                expression = state.secondaryDisplay,
                displayValue = state.primaryDisplay,
                isError = state.isError,
                isScientific = isScientific,
                isDegMode = state.isDegMode,
                onToggleDegMode = onToggleDegMode,
                onCopy = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Calculator Value", state.primaryDisplay)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(
                        context,
                        context.getString(R.string.copied_to_clipboard),
                        Toast.LENGTH_SHORT
                    ).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 2.dp)
            )

            // Mode Selector Pill (Basic vs Scientific)
            ModeSelectorBar(
                isScientific = isScientific,
                isDegMode = state.isDegMode,
                onToggleMode = onToggleMode,
                onToggleDegMode = onToggleDegMode,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 4.dp)
            )

            // Keypad Container Area
            Card(
                shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
            ) {
                // Force LTR layout inside keypad so operations (+, −, ×, ÷, =) are strictly on the RIGHT column
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    if (isScientific) {
                        ScientificKeypadGrid(
                            activeOperator = state.activeOperator,
                            isDegMode = state.isDegMode,
                            onKeyClick = onKeyClick,
                            isDarkTheme = isDark,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        )
                    } else {
                        BasicKeypadGrid(
                            activeOperator = state.activeOperator,
                            onKeyClick = onKeyClick,
                            isDarkTheme = isDark,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TopActionBar(
    currentTheme: AppTheme,
    isScientific: Boolean,
    onToggleMode: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.height(44.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Brand & Logo Pill
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isScientific) "f(x)" else "±",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = if (isScientific) 11.sp else 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = stringResource(R.string.app_name),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Action Pill Buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mode toggle quick button
            IconButton(
                onClick = onToggleMode,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(
                        if (isScientific) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .testTag("btn_toggle_mode_top")
            ) {
                Icon(
                    imageVector = if (isScientific) Icons.Default.Functions else Icons.Default.Calculate,
                    contentDescription = stringResource(R.string.mode_toggle_desc),
                    tint = if (isScientific) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Theme toggle
            IconButton(
                onClick = onToggleTheme,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .testTag("btn_toggle_theme")
            ) {
                val icon = when (currentTheme) {
                    AppTheme.SYSTEM -> Icons.Default.SettingsBrightness
                    AppTheme.LIGHT -> Icons.Default.LightMode
                    AppTheme.DARK, AppTheme.AMOLED -> Icons.Default.DarkMode
                    AppTheme.METALLIC, AppTheme.MIDNIGHT_BLUE -> Icons.Default.Palette
                }
                Icon(
                    imageVector = icon,
                    contentDescription = stringResource(R.string.settings_appearance_section),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(17.dp)
                )
            }

            // History Button
            IconButton(
                onClick = onOpenHistory,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .testTag("btn_open_history")
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = stringResource(R.string.history_title),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(17.dp)
                )
            }

            // Settings Button
            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .testTag("btn_open_settings")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.settings_title),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(17.dp)
                )
            }
        }
    }
}

@Composable
fun ModeSelectorBar(
    isScientific: Boolean,
    isDegMode: Boolean,
    onToggleMode: () -> Unit,
    onToggleDegMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Mode Switcher Pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(3.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Basic Mode Segment
                val basicBg by animateColorAsState(
                    targetValue = if (!isScientific) MaterialTheme.colorScheme.primary else Color.Transparent,
                    label = "basic_tab_bg"
                )
                val basicText by animateColorAsState(
                    targetValue = if (!isScientific) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    label = "basic_tab_txt"
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9.dp))
                        .background(basicBg)
                        .clickable { if (isScientific) onToggleMode() }
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                        .testTag("btn_mode_basic"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.mode_basic),
                        fontSize = 13.sp,
                        fontWeight = if (!isScientific) FontWeight.Bold else FontWeight.Medium,
                        color = basicText
                    )
                }

                // Scientific Mode Segment
                val sciBg by animateColorAsState(
                    targetValue = if (isScientific) MaterialTheme.colorScheme.primary else Color.Transparent,
                    label = "sci_tab_bg"
                )
                val sciText by animateColorAsState(
                    targetValue = if (isScientific) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    label = "sci_tab_txt"
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9.dp))
                        .background(sciBg)
                        .clickable { if (!isScientific) onToggleMode() }
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                        .testTag("btn_mode_scientific"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.mode_scientific),
                        fontSize = 13.sp,
                        fontWeight = if (isScientific) FontWeight.Bold else FontWeight.Medium,
                        color = sciText
                    )
                }
            }
        }

        // Angle Mode Indicator & Toggle Chip (when in Scientific mode)
        if (isScientific) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f))
                    .clickable { onToggleDegMode() }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .testTag("btn_angle_unit_top"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isDegMode) stringResource(R.string.angle_deg) else stringResource(R.string.angle_rad),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun DisplayScreen(
    expression: String,
    displayValue: String,
    isError: Boolean,
    isScientific: Boolean,
    isDegMode: Boolean,
    onToggleDegMode: () -> Unit,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onCopy
            )
            .testTag("calculator_display"),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            // Secondary Expression Display
            AnimatedContent(
                targetState = expression,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "expression_anim"
            ) { targetExpr ->
                Text(
                    text = targetExpr.ifEmpty { " " },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Primary Result Display (High capacity, auto-resizing, compact height)
            AutoResizeText(
                text = displayValue,
                maxFontSize = if (isScientific) 42.sp else 46.sp,
                minFontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun BasicKeypadGrid(
    activeOperator: String?,
    onKeyClick: (CalculatorKey) -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val keyHeight = 54.dp
    val spacing = 8.dp

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        // Row 1: [ AC ] | [ ⌫ ] | [ % ] | [ ÷ ] (Operator on the RIGHT)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            CalculatorKeyButton(
                key = CalculatorKey.AllClear,
                keyType = KeyType.UTILITY,
                onClick = { onKeyClick(CalculatorKey.AllClear) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(keyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Backspace,
                keyType = KeyType.UTILITY,
                onClick = { onKeyClick(CalculatorKey.Backspace) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(keyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Percent,
                keyType = KeyType.UTILITY,
                onClick = { onKeyClick(CalculatorKey.Percent) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(keyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Divide,
                keyType = KeyType.OPERATOR,
                isActiveOperator = activeOperator == "÷" || activeOperator == "/",
                onClick = { onKeyClick(CalculatorKey.Divide) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(keyHeight)
            )
        }

        // Row 2: [ 7 ] | [ 8 ] | [ 9 ] | [ × ] (Operator on the RIGHT)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            CalculatorKeyButton(
                key = CalculatorKey.Digit("7"),
                keyType = KeyType.NUMBER,
                onClick = { onKeyClick(CalculatorKey.Digit("7")) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(keyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Digit("8"),
                keyType = KeyType.NUMBER,
                onClick = { onKeyClick(CalculatorKey.Digit("8")) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(keyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Digit("9"),
                keyType = KeyType.NUMBER,
                onClick = { onKeyClick(CalculatorKey.Digit("9")) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(keyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Multiply,
                keyType = KeyType.OPERATOR,
                isActiveOperator = activeOperator == "×" || activeOperator == "*",
                onClick = { onKeyClick(CalculatorKey.Multiply) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(keyHeight)
            )
        }

        // Row 3: [ 4 ] | [ 5 ] | [ 6 ] | [ − ] (Operator on the RIGHT)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            CalculatorKeyButton(
                key = CalculatorKey.Digit("4"),
                keyType = KeyType.NUMBER,
                onClick = { onKeyClick(CalculatorKey.Digit("4")) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(keyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Digit("5"),
                keyType = KeyType.NUMBER,
                onClick = { onKeyClick(CalculatorKey.Digit("5")) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(keyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Digit("6"),
                keyType = KeyType.NUMBER,
                onClick = { onKeyClick(CalculatorKey.Digit("6")) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(keyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Subtract,
                keyType = KeyType.OPERATOR,
                isActiveOperator = activeOperator == "−" || activeOperator == "-",
                onClick = { onKeyClick(CalculatorKey.Subtract) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(keyHeight)
            )
        }

        // Row 4: [ 1 ] | [ 2 ] | [ 3 ] | [ + ] (Operator on the RIGHT)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            CalculatorKeyButton(
                key = CalculatorKey.Digit("1"),
                keyType = KeyType.NUMBER,
                onClick = { onKeyClick(CalculatorKey.Digit("1")) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(keyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Digit("2"),
                keyType = KeyType.NUMBER,
                onClick = { onKeyClick(CalculatorKey.Digit("2")) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(keyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Digit("3"),
                keyType = KeyType.NUMBER,
                onClick = { onKeyClick(CalculatorKey.Digit("3")) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(keyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Add,
                keyType = KeyType.OPERATOR,
                isActiveOperator = activeOperator == "+",
                onClick = { onKeyClick(CalculatorKey.Add) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(keyHeight)
            )
        }

        // Row 5: [ ± ] | [ 000 ] | [ 00 ] | [ 0 ]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            CalculatorKeyButton(
                key = CalculatorKey.PlusMinus,
                keyType = KeyType.UTILITY,
                onClick = { onKeyClick(CalculatorKey.PlusMinus) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(keyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.TripleZero,
                keyType = KeyType.NUMBER,
                onClick = { onKeyClick(CalculatorKey.TripleZero) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(keyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.DoubleZero,
                keyType = KeyType.NUMBER,
                onClick = { onKeyClick(CalculatorKey.DoubleZero) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(keyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Digit("0"),
                keyType = KeyType.NUMBER,
                onClick = { onKeyClick(CalculatorKey.Digit("0")) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(keyHeight)
            )
        }

        // Row 6: [ . ] (weight 1) | [ = ] (weight 3 - rightmost equal result action)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            CalculatorKeyButton(
                key = CalculatorKey.DecimalDot,
                keyType = KeyType.NUMBER,
                onClick = { onKeyClick(CalculatorKey.DecimalDot) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(keyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Equals,
                keyType = KeyType.EQUALS,
                onClick = { onKeyClick(CalculatorKey.Equals) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(3f).height(keyHeight)
            )
        }
    }
}

@Composable
fun ScientificKeypadGrid(
    activeOperator: String?,
    isDegMode: Boolean,
    onKeyClick: (CalculatorKey) -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val sciKeyHeight = 39.dp
    val numKeyHeight = 46.dp
    val spacing = 6.dp

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        // Scientific Row 1: [ DEG/RAD ] | [ sin ] | [ cos ] | [ tan ] | [ log ]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            CalculatorKeyButton(
                key = CalculatorKey.AngleModeToggle,
                keyType = KeyType.SCIENTIFIC,
                customLabel = if (isDegMode) "DEG" else "RAD",
                onClick = { onKeyClick(CalculatorKey.AngleModeToggle) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(sciKeyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Sin,
                keyType = KeyType.SCIENTIFIC,
                onClick = { onKeyClick(CalculatorKey.Sin) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(sciKeyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Cos,
                keyType = KeyType.SCIENTIFIC,
                onClick = { onKeyClick(CalculatorKey.Cos) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(sciKeyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Tan,
                keyType = KeyType.SCIENTIFIC,
                onClick = { onKeyClick(CalculatorKey.Tan) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(sciKeyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Log,
                keyType = KeyType.SCIENTIFIC,
                onClick = { onKeyClick(CalculatorKey.Log) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(sciKeyHeight)
            )
        }

        // Scientific Row 2: [ ln ] | [ √ ] | [ x² ] | [ xʸ ] | [ π ]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            CalculatorKeyButton(
                key = CalculatorKey.Ln,
                keyType = KeyType.SCIENTIFIC,
                onClick = { onKeyClick(CalculatorKey.Ln) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(sciKeyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.SquareRoot,
                keyType = KeyType.SCIENTIFIC,
                onClick = { onKeyClick(CalculatorKey.SquareRoot) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(sciKeyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Square,
                keyType = KeyType.SCIENTIFIC,
                onClick = { onKeyClick(CalculatorKey.Square) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(sciKeyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Power,
                keyType = KeyType.SCIENTIFIC,
                isActiveOperator = activeOperator == "^",
                onClick = { onKeyClick(CalculatorKey.Power) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(sciKeyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Pi,
                keyType = KeyType.SCIENTIFIC,
                onClick = { onKeyClick(CalculatorKey.Pi) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(sciKeyHeight)
            )
        }

        // Row 1: [ AC ] | [ ⌫ ] | [ % ] | [ ÷ ] (Operator on the RIGHT)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            CalculatorKeyButton(
                key = CalculatorKey.AllClear,
                keyType = KeyType.UTILITY,
                onClick = { onKeyClick(CalculatorKey.AllClear) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(numKeyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Backspace,
                keyType = KeyType.UTILITY,
                onClick = { onKeyClick(CalculatorKey.Backspace) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(numKeyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Percent,
                keyType = KeyType.UTILITY,
                onClick = { onKeyClick(CalculatorKey.Percent) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(numKeyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Divide,
                keyType = KeyType.OPERATOR,
                isActiveOperator = activeOperator == "÷" || activeOperator == "/",
                onClick = { onKeyClick(CalculatorKey.Divide) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(numKeyHeight)
            )
        }

        // Row 2: [ 7 ] | [ 8 ] | [ 9 ] | [ × ] (Operator on the RIGHT)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            CalculatorKeyButton(
                key = CalculatorKey.Digit("7"),
                keyType = KeyType.NUMBER,
                onClick = { onKeyClick(CalculatorKey.Digit("7")) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(numKeyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Digit("8"),
                keyType = KeyType.NUMBER,
                onClick = { onKeyClick(CalculatorKey.Digit("8")) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(numKeyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Digit("9"),
                keyType = KeyType.NUMBER,
                onClick = { onKeyClick(CalculatorKey.Digit("9")) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(numKeyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Multiply,
                keyType = KeyType.OPERATOR,
                isActiveOperator = activeOperator == "×" || activeOperator == "*",
                onClick = { onKeyClick(CalculatorKey.Multiply) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(numKeyHeight)
            )
        }

        // Row 3: [ 4 ] | [ 5 ] | [ 6 ] | [ − ] (Operator on the RIGHT)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            CalculatorKeyButton(
                key = CalculatorKey.Digit("4"),
                keyType = KeyType.NUMBER,
                onClick = { onKeyClick(CalculatorKey.Digit("4")) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(numKeyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Digit("5"),
                keyType = KeyType.NUMBER,
                onClick = { onKeyClick(CalculatorKey.Digit("5")) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(numKeyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Digit("6"),
                keyType = KeyType.NUMBER,
                onClick = { onKeyClick(CalculatorKey.Digit("6")) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(numKeyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Subtract,
                keyType = KeyType.OPERATOR,
                isActiveOperator = activeOperator == "−" || activeOperator == "-",
                onClick = { onKeyClick(CalculatorKey.Subtract) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(numKeyHeight)
            )
        }

        // Row 4: [ 1 ] | [ 2 ] | [ 3 ] | [ + ] (Operator on the RIGHT)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            CalculatorKeyButton(
                key = CalculatorKey.Digit("1"),
                keyType = KeyType.NUMBER,
                onClick = { onKeyClick(CalculatorKey.Digit("1")) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(numKeyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Digit("2"),
                keyType = KeyType.NUMBER,
                onClick = { onKeyClick(CalculatorKey.Digit("2")) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(numKeyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Digit("3"),
                keyType = KeyType.NUMBER,
                onClick = { onKeyClick(CalculatorKey.Digit("3")) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(numKeyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Add,
                keyType = KeyType.OPERATOR,
                isActiveOperator = activeOperator == "+",
                onClick = { onKeyClick(CalculatorKey.Add) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(numKeyHeight)
            )
        }

        // Row 5: [ ± ] | [ e ] | [ 0 ] | [ 1/x ]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            CalculatorKeyButton(
                key = CalculatorKey.PlusMinus,
                keyType = KeyType.UTILITY,
                onClick = { onKeyClick(CalculatorKey.PlusMinus) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(numKeyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.EulerE,
                keyType = KeyType.SCIENTIFIC,
                onClick = { onKeyClick(CalculatorKey.EulerE) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(numKeyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Digit("0"),
                keyType = KeyType.NUMBER,
                onClick = { onKeyClick(CalculatorKey.Digit("0")) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(numKeyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Inverse,
                keyType = KeyType.SCIENTIFIC,
                onClick = { onKeyClick(CalculatorKey.Inverse) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(numKeyHeight)
            )
        }

        // Row 6: [ . ] (weight 1) | [ = ] (weight 3 - rightmost equal result action)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            CalculatorKeyButton(
                key = CalculatorKey.DecimalDot,
                keyType = KeyType.NUMBER,
                onClick = { onKeyClick(CalculatorKey.DecimalDot) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f).height(numKeyHeight)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Equals,
                keyType = KeyType.EQUALS,
                onClick = { onKeyClick(CalculatorKey.Equals) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(3f).height(numKeyHeight)
            )
        }
    }
}
