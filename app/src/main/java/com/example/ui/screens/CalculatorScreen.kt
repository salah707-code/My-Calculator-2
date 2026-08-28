package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
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
    onKeyClick: (CalculatorKey, View) -> Unit,
    onOpenSettings: () -> Unit,
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
                .padding(top = 6.dp)
                .widthIn(max = 600.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Action Bar with sleek luxury logo and single Settings icon
            TopActionBar(
                isScientific = isScientific,
                isDegMode = state.isDegMode,
                onOpenSettings = onOpenSettings,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 4.dp)
            )

            // Large Display Screen Area with responsive elegant numbers
            DisplayScreen(
                expression = state.secondaryDisplay,
                displayValue = state.primaryDisplay,
                isError = state.isError,
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
                    .weight(1f)
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            )

            // Keypad Container Area - Compact, ergonomic 35% scaled-down buttons
            Card(
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = if (isDark) 0.35f else 0.2f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isScientific) 420.dp else 360.dp)
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
                                .fillMaxSize()
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        )
                    } else {
                        BasicKeypadGrid(
                            activeOperator = state.activeOperator,
                            onKeyClick = onKeyClick,
                            isDarkTheme = isDark,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 10.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TopActionBar(
    isScientific: Boolean,
    isDegMode: Boolean,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.height(52.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Brand & Mode Indicator Pill with Luxury Graphic Logo
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.calculator_lux_icon_1787915115809),
                    contentDescription = stringResource(R.string.app_name),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column {
                Text(
                    text = stringResource(R.string.app_name),
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (isScientific) {
                    Text(
                        text = if (isDegMode) "DEG (°)" else "RAD (rad)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Single Consolidated Settings Button with polished aesthetic
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f), CircleShape)
                .clickable { onOpenSettings() }
                .testTag("btn_open_settings"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(R.string.settings_title),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun DisplayScreen(
    expression: String,
    displayValue: String,
    isError: Boolean,
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
                .padding(vertical = 4.dp),
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
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Primary Result Display (Doubled font size, smart 2-line splitting / auto-resize)
            AutoResizeText(
                text = displayValue,
                maxFontSize = 56.sp,
                minFontSize = 22.sp,
                maxLines = 2,
                fontWeight = FontWeight.Bold,
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
    onKeyClick: (CalculatorKey, View) -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val spacing = 7.dp

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        // Row 1: [ AC ] | [ ⌫ ] | [ % ] | [ ÷ ] (Operator on the RIGHT)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            CalculatorKeyButton(
                key = CalculatorKey.AllClear,
                keyType = KeyType.UTILITY,
                onClick = { view -> onKeyClick(CalculatorKey.AllClear, view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Backspace,
                keyType = KeyType.UTILITY,
                onClick = { view -> onKeyClick(CalculatorKey.Backspace, view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Percent,
                keyType = KeyType.UTILITY,
                onClick = { view -> onKeyClick(CalculatorKey.Percent, view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Divide,
                keyType = KeyType.OPERATOR,
                isActiveOperator = activeOperator == "÷" || activeOperator == "/",
                onClick = { view -> onKeyClick(CalculatorKey.Divide, view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2: [ 7 ] | [ 8 ] | [ 9 ] | [ × ] (Operator on the RIGHT)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            CalculatorKeyButton(
                key = CalculatorKey.Digit("7"),
                keyType = KeyType.NUMBER,
                onClick = { view -> onKeyClick(CalculatorKey.Digit("7"), view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Digit("8"),
                keyType = KeyType.NUMBER,
                onClick = { view -> onKeyClick(CalculatorKey.Digit("8"), view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Digit("9"),
                keyType = KeyType.NUMBER,
                onClick = { view -> onKeyClick(CalculatorKey.Digit("9"), view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Multiply,
                keyType = KeyType.OPERATOR,
                isActiveOperator = activeOperator == "×" || activeOperator == "*",
                onClick = { view -> onKeyClick(CalculatorKey.Multiply, view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 3: [ 4 ] | [ 5 ] | [ 6 ] | [ − ] (Operator on the RIGHT)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            CalculatorKeyButton(
                key = CalculatorKey.Digit("4"),
                keyType = KeyType.NUMBER,
                onClick = { view -> onKeyClick(CalculatorKey.Digit("4"), view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Digit("5"),
                keyType = KeyType.NUMBER,
                onClick = { view -> onKeyClick(CalculatorKey.Digit("5"), view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Digit("6"),
                keyType = KeyType.NUMBER,
                onClick = { view -> onKeyClick(CalculatorKey.Digit("6"), view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Subtract,
                keyType = KeyType.OPERATOR,
                isActiveOperator = activeOperator == "−" || activeOperator == "-",
                onClick = { view -> onKeyClick(CalculatorKey.Subtract, view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 4: [ 1 ] | [ 2 ] | [ 3 ] | [ + ] (Operator on the RIGHT)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            CalculatorKeyButton(
                key = CalculatorKey.Digit("1"),
                keyType = KeyType.NUMBER,
                onClick = { view -> onKeyClick(CalculatorKey.Digit("1"), view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Digit("2"),
                keyType = KeyType.NUMBER,
                onClick = { view -> onKeyClick(CalculatorKey.Digit("2"), view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Digit("3"),
                keyType = KeyType.NUMBER,
                onClick = { view -> onKeyClick(CalculatorKey.Digit("3"), view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Add,
                keyType = KeyType.OPERATOR,
                isActiveOperator = activeOperator == "+",
                onClick = { view -> onKeyClick(CalculatorKey.Add, view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 5: [ ± ] | [ 000 ] | [ 00 ] | [ 0 ]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            CalculatorKeyButton(
                key = CalculatorKey.PlusMinus,
                keyType = KeyType.UTILITY,
                onClick = { view -> onKeyClick(CalculatorKey.PlusMinus, view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.TripleZero,
                keyType = KeyType.NUMBER,
                onClick = { view -> onKeyClick(CalculatorKey.TripleZero, view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.DoubleZero,
                keyType = KeyType.NUMBER,
                onClick = { view -> onKeyClick(CalculatorKey.DoubleZero, view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Digit("0"),
                keyType = KeyType.NUMBER,
                onClick = { view -> onKeyClick(CalculatorKey.Digit("0"), view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 6: [ . ] (weight 1) | [ = ] (weight 3 - prominent equal result action with uniform shading)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            CalculatorKeyButton(
                key = CalculatorKey.DecimalDot,
                keyType = KeyType.NUMBER,
                onClick = { view -> onKeyClick(CalculatorKey.DecimalDot, view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Equals,
                keyType = KeyType.EQUALS,
                onClick = { view -> onKeyClick(CalculatorKey.Equals, view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(3f)
            )
        }
    }
}

@Composable
fun ScientificKeypadGrid(
    activeOperator: String?,
    isDegMode: Boolean,
    onKeyClick: (CalculatorKey, View) -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val spacing = 5.dp

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        // Scientific Row 1: [ DEG/RAD ] | [ sin ] | [ cos ] | [ tan ] | [ log ]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            CalculatorKeyButton(
                key = CalculatorKey.AngleModeToggle,
                keyType = KeyType.SCIENTIFIC,
                isActiveOperator = isDegMode,
                customLabel = if (isDegMode) "DEG" else "RAD",
                onClick = { view -> onKeyClick(CalculatorKey.AngleModeToggle, view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Sin,
                keyType = KeyType.SCIENTIFIC,
                onClick = { view -> onKeyClick(CalculatorKey.Sin, view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Cos,
                keyType = KeyType.SCIENTIFIC,
                onClick = { view -> onKeyClick(CalculatorKey.Cos, view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Tan,
                keyType = KeyType.SCIENTIFIC,
                onClick = { view -> onKeyClick(CalculatorKey.Tan, view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Log,
                keyType = KeyType.SCIENTIFIC,
                onClick = { view -> onKeyClick(CalculatorKey.Log, view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
        }

        // Scientific Row 2: [ ln ] | [ √ ] | [ x² ] | [ xʸ ] | [ π ]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            CalculatorKeyButton(
                key = CalculatorKey.Ln,
                keyType = KeyType.SCIENTIFIC,
                onClick = { view -> onKeyClick(CalculatorKey.Ln, view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.SquareRoot,
                keyType = KeyType.SCIENTIFIC,
                onClick = { view -> onKeyClick(CalculatorKey.SquareRoot, view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Square,
                keyType = KeyType.SCIENTIFIC,
                onClick = { view -> onKeyClick(CalculatorKey.Square, view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Power,
                keyType = KeyType.SCIENTIFIC,
                isActiveOperator = activeOperator == "^",
                onClick = { view -> onKeyClick(CalculatorKey.Power, view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Pi,
                keyType = KeyType.SCIENTIFIC,
                onClick = { view -> onKeyClick(CalculatorKey.Pi, view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 1: [ AC ] | [ ⌫ ] | [ % ] | [ ÷ ] (Operator on the RIGHT)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.15f),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            CalculatorKeyButton(
                key = CalculatorKey.AllClear,
                keyType = KeyType.UTILITY,
                onClick = { view -> onKeyClick(CalculatorKey.AllClear, view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Backspace,
                keyType = KeyType.UTILITY,
                onClick = { view -> onKeyClick(CalculatorKey.Backspace, view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Percent,
                keyType = KeyType.UTILITY,
                onClick = { view -> onKeyClick(CalculatorKey.Percent, view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Divide,
                keyType = KeyType.OPERATOR,
                isActiveOperator = activeOperator == "÷" || activeOperator == "/",
                onClick = { view -> onKeyClick(CalculatorKey.Divide, view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2: [ 7 ] | [ 8 ] | [ 9 ] | [ × ] (Operator on the RIGHT)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.15f),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            CalculatorKeyButton(
                key = CalculatorKey.Digit("7"),
                keyType = KeyType.NUMBER,
                onClick = { view -> onKeyClick(CalculatorKey.Digit("7"), view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Digit("8"),
                keyType = KeyType.NUMBER,
                onClick = { view -> onKeyClick(CalculatorKey.Digit("8"), view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Digit("9"),
                keyType = KeyType.NUMBER,
                onClick = { view -> onKeyClick(CalculatorKey.Digit("9"), view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Multiply,
                keyType = KeyType.OPERATOR,
                isActiveOperator = activeOperator == "×" || activeOperator == "*",
                onClick = { view -> onKeyClick(CalculatorKey.Multiply, view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 3: [ 4 ] | [ 5 ] | [ 6 ] | [ − ] (Operator on the RIGHT)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.15f),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            CalculatorKeyButton(
                key = CalculatorKey.Digit("4"),
                keyType = KeyType.NUMBER,
                onClick = { view -> onKeyClick(CalculatorKey.Digit("4"), view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Digit("5"),
                keyType = KeyType.NUMBER,
                onClick = { view -> onKeyClick(CalculatorKey.Digit("5"), view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Digit("6"),
                keyType = KeyType.NUMBER,
                onClick = { view -> onKeyClick(CalculatorKey.Digit("6"), view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Subtract,
                keyType = KeyType.OPERATOR,
                isActiveOperator = activeOperator == "−" || activeOperator == "-",
                onClick = { view -> onKeyClick(CalculatorKey.Subtract, view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 4: [ 1 ] | [ 2 ] | [ 3 ] | [ + ] (Operator on the RIGHT)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.15f),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            CalculatorKeyButton(
                key = CalculatorKey.Digit("1"),
                keyType = KeyType.NUMBER,
                onClick = { view -> onKeyClick(CalculatorKey.Digit("1"), view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Digit("2"),
                keyType = KeyType.NUMBER,
                onClick = { view -> onKeyClick(CalculatorKey.Digit("2"), view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Digit("3"),
                keyType = KeyType.NUMBER,
                onClick = { view -> onKeyClick(CalculatorKey.Digit("3"), view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Add,
                keyType = KeyType.OPERATOR,
                isActiveOperator = activeOperator == "+",
                onClick = { view -> onKeyClick(CalculatorKey.Add, view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 5: [ ± ] | [ e ] | [ 0 ] | [ 1/x ]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.15f),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            CalculatorKeyButton(
                key = CalculatorKey.PlusMinus,
                keyType = KeyType.UTILITY,
                onClick = { view -> onKeyClick(CalculatorKey.PlusMinus, view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.EulerE,
                keyType = KeyType.SCIENTIFIC,
                onClick = { view -> onKeyClick(CalculatorKey.EulerE, view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Digit("0"),
                keyType = KeyType.NUMBER,
                onClick = { view -> onKeyClick(CalculatorKey.Digit("0"), view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Inverse,
                keyType = KeyType.SCIENTIFIC,
                onClick = { view -> onKeyClick(CalculatorKey.Inverse, view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 6: [ . ] (weight 1) | [ = ] (weight 3 - prominent equal result action with uniform shading)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.15f),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            CalculatorKeyButton(
                key = CalculatorKey.DecimalDot,
                keyType = KeyType.NUMBER,
                onClick = { view -> onKeyClick(CalculatorKey.DecimalDot, view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            CalculatorKeyButton(
                key = CalculatorKey.Equals,
                keyType = KeyType.EQUALS,
                onClick = { view -> onKeyClick(CalculatorKey.Equals, view) },
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(3f)
            )
        }
    }
}
