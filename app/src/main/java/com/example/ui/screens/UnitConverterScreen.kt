package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.engine.UnitConverterEngine
import com.example.model.AppLanguage
import com.example.model.AppTheme
import com.example.model.ConversionUnit
import com.example.model.UnitCategory
import com.example.model.UnitDefinitions
import com.example.ui.components.AutoResizeText
import com.example.ui.theme.DarkNumKeyBg
import com.example.ui.theme.DarkNumKeyBorder
import com.example.ui.theme.DarkNumKeyText
import com.example.ui.theme.LightNumKeyBg
import com.example.ui.theme.LightNumKeyBorder
import com.example.ui.theme.LightNumKeyText
import com.example.ui.util.FeedbackHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConverterScreen(
    currentTheme: AppTheme,
    language: AppLanguage,
    hapticEnabled: Boolean,
    soundEnabled: Boolean,
    thousandsSeparatorEnabled: Boolean,
    onBack: () -> Unit,
    onUseResultInCalculator: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val feedbackHelper = remember { FeedbackHelper(context) }
    val isDark = when (currentTheme) {
        AppTheme.SYSTEM -> isSystemInDarkTheme()
        AppTheme.LIGHT -> false
        AppTheme.DARK, AppTheme.METALLIC, AppTheme.AMOLED, AppTheme.MIDNIGHT_BLUE -> true
    }

    var selectedCategory by remember { mutableStateOf(UnitCategory.LENGTH) }
    var availableUnits by remember(selectedCategory) {
        mutableStateOf(UnitDefinitions.getUnitsForCategory(selectedCategory))
    }

    var fromUnit by remember(selectedCategory) {
        mutableStateOf(availableUnits.getOrElse(1) { availableUnits.first() })
    }
    var toUnit by remember(selectedCategory) {
        mutableStateOf(availableUnits.first())
    }

    var inputAmount by remember { mutableStateOf("1") }

    // Synchronize units when category changes
    LaunchedEffect(selectedCategory) {
        val units = UnitDefinitions.getUnitsForCategory(selectedCategory)
        availableUnits = units
        if (units.size >= 2) {
            fromUnit = units[0]
            toUnit = units[1]
        } else {
            fromUnit = units[0]
            toUnit = units[0]
        }
    }

    val convertedValue = remember(inputAmount, fromUnit, toUnit, selectedCategory) {
        val numericInput = inputAmount.toDoubleOrNull() ?: 0.0
        val result = UnitConverterEngine.convert(numericInput, fromUnit, toUnit, selectedCategory)
        UnitConverterEngine.formatResult(result, thousandsSeparatorEnabled, maxDecimals = 6)
    }

    val formulaText = remember(fromUnit, toUnit, selectedCategory) {
        UnitConverterEngine.getFormulaSummary(fromUnit, toUnit, selectedCategory)
    }

    fun handleKeypadClick(key: String, view: View) {
        feedbackHelper.triggerKeyClick(hapticEnabled, soundEnabled, view)
        when (key) {
            "AC" -> inputAmount = "0"
            "⌫" -> {
                inputAmount = if (inputAmount.length <= 1 || (inputAmount.length == 2 && inputAmount.startsWith("-"))) {
                    "0"
                } else {
                    inputAmount.dropLast(1)
                }
            }
            "." -> {
                if (!inputAmount.contains(".")) {
                    inputAmount = if (inputAmount.isEmpty() || inputAmount == "0") "0." else "$inputAmount."
                }
            }
            "±" -> {
                if (inputAmount != "0") {
                    inputAmount = if (inputAmount.startsWith("-")) inputAmount.removePrefix("-") else "-$inputAmount"
                }
            }
            "00" -> {
                if (inputAmount != "0" && inputAmount.isNotEmpty() && inputAmount.length < 12) {
                    inputAmount += "00"
                }
            }
            else -> {
                // Digit 0-9
                if (inputAmount == "0") {
                    inputAmount = key
                } else if (inputAmount == "-0") {
                    inputAmount = "-$key"
                } else if (inputAmount.length < 12) {
                    inputAmount += key
                }
            }
        }
    }

    fun swapUnits(view: View) {
        feedbackHelper.triggerKeyClick(hapticEnabled, soundEnabled, view)
        val temp = fromUnit
        fromUnit = toUnit
        toUnit = temp
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.unit_converter_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("converter_back_button")
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
                .navigationBarsPadding()
                .padding(horizontal = 14.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Horizontal Category Selector
            CategorySelectorRow(
                selectedCategory = selectedCategory,
                onCategorySelect = { category ->
                    selectedCategory = category
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Conversion Cards (From & To) with Centered Floating Swap Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // "FROM" Card
                    UnitCard(
                        label = stringResource(R.string.unit_from),
                        currentUnit = fromUnit,
                        availableUnits = availableUnits,
                        isSource = true,
                        displayValue = inputAmount,
                        language = language,
                        onUnitSelected = { unit -> fromUnit = unit },
                        onClear = { inputAmount = "0" },
                        isDark = isDark,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )

                    // "TO" Card
                    UnitCard(
                        label = stringResource(R.string.unit_to),
                        currentUnit = toUnit,
                        availableUnits = availableUnits,
                        isSource = false,
                        displayValue = convertedValue,
                        language = language,
                        onUnitSelected = { unit -> toUnit = unit },
                        onCopy = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Converted Value", convertedValue)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(
                                context,
                                context.getString(R.string.copied_to_clipboard),
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onUseInCalculator = {
                            onUseResultInCalculator(convertedValue.replace(",", ""))
                        },
                        isDark = isDark,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }

                // Floating Swap Button right in the middle
                val view = LocalView.current
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { swapUnits(view) }
                        .testTag("btn_swap_units"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = stringResource(R.string.unit_swap),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            // 3. Formula summary badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.unit_conversion_rate),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                    Text(
                        text = formulaText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // 4. Built-in Ergonomic Compact Numeric Keypad
            ConverterKeypad(
                onKeyClick = { key, keyView -> handleKeypadClick(key, keyView) },
                isDark = isDark,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
            )
        }
    }
}

@Composable
fun CategorySelectorRow(
    selectedCategory: UnitCategory,
    onCategorySelect: (UnitCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(UnitCategory.values()) { category ->
            val isSelected = selectedCategory == category
            val icon = getCategoryIcon(category)

            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelect(category) },
                label = {
                    Text(
                        text = stringResource(category.titleRes),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                shape = RoundedCornerShape(14.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    selectedBorderColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.testTag("chip_cat_${category.name.lowercase()}")
            )
        }
    }
}

@Composable
fun UnitCard(
    label: String,
    currentUnit: ConversionUnit,
    availableUnits: List<ConversionUnit>,
    isSource: Boolean,
    displayValue: String,
    language: AppLanguage,
    onUnitSelected: (ConversionUnit) -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier,
    onClear: (() -> Unit)? = null,
    onCopy: (() -> Unit)? = null,
    onUseInCalculator: (() -> Unit)? = null
) {
    var dropdownExpanded by remember { mutableStateOf(false) }
    val unitName = if (language == AppLanguage.ARABIC) currentUnit.nameAr else currentUnit.nameEn

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(
            1.2.dp,
            if (isSource) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Row: [Label] + [Unit Selector Dropdown Button]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Unit Selector Dropdown Pill
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            .clickable { dropdownExpanded = true }
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "$unitName (${currentUnit.symbol})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        availableUnits.forEach { unit ->
                            val itemUnitName = if (language == AppLanguage.ARABIC) unit.nameAr else unit.nameEn
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = itemUnitName,
                                            fontWeight = if (unit.id == currentUnit.id) FontWeight.Bold else FontWeight.Normal,
                                            color = if (unit.id == currentUnit.id) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = unit.symbol,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                },
                                onClick = {
                                    onUnitSelected(unit)
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Display Number Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                AutoResizeText(
                    text = displayValue,
                    maxFontSize = 36.sp,
                    minFontSize = 20.sp,
                    maxLines = 1,
                    fontWeight = FontWeight.Bold,
                    color = if (isSource) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Bottom Actions Row (Clear / Copy / Use In Calculator)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isSource && onClear != null) {
                    Text(
                        text = stringResource(R.string.clear),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onClear() }
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (onCopy != null) {
                            OutlinedButton(
                                onClick = onCopy,
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = stringResource(R.string.copy), fontSize = 11.sp)
                            }
                        }

                        if (onUseInCalculator != null) {
                            Button(
                                onClick = onUseInCalculator,
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Calculate,
                                    contentDescription = null,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = stringResource(R.string.unit_use_in_calculator), fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConverterKeypad(
    onKeyClick: (String, View) -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val spacing = 6.dp

    // Force LTR layout so numeric layout (1, 2, 3, etc.) is clean and standard
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(spacing)
        ) {
            // Row 1: [ 7 ] [ 8 ] [ 9 ] [ ⌫ ]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {
                ConverterKeyBtn(text = "7", onClick = onKeyClick, isDark = isDark, modifier = Modifier.weight(1f))
                ConverterKeyBtn(text = "8", onClick = onKeyClick, isDark = isDark, modifier = Modifier.weight(1f))
                ConverterKeyBtn(text = "9", onClick = onKeyClick, isDark = isDark, modifier = Modifier.weight(1f))
                ConverterKeyBtn(text = "⌫", onClick = onKeyClick, isDark = isDark, isUtility = true, modifier = Modifier.weight(1f))
            }

            // Row 2: [ 4 ] [ 5 ] [ 6 ] [ AC ]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {
                ConverterKeyBtn(text = "4", onClick = onKeyClick, isDark = isDark, modifier = Modifier.weight(1f))
                ConverterKeyBtn(text = "5", onClick = onKeyClick, isDark = isDark, modifier = Modifier.weight(1f))
                ConverterKeyBtn(text = "6", onClick = onKeyClick, isDark = isDark, modifier = Modifier.weight(1f))
                ConverterKeyBtn(text = "AC", onClick = onKeyClick, isDark = isDark, isClear = true, modifier = Modifier.weight(1f))
            }

            // Row 3: [ 1 ] [ 2 ] [ 3 ] [ ± ]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {
                ConverterKeyBtn(text = "1", onClick = onKeyClick, isDark = isDark, modifier = Modifier.weight(1f))
                ConverterKeyBtn(text = "2", onClick = onKeyClick, isDark = isDark, modifier = Modifier.weight(1f))
                ConverterKeyBtn(text = "3", onClick = onKeyClick, isDark = isDark, modifier = Modifier.weight(1f))
                ConverterKeyBtn(text = "±", onClick = onKeyClick, isDark = isDark, isUtility = true, modifier = Modifier.weight(1f))
            }

            // Row 4: [ 00 ] [ 0 ] [ . ]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {
                ConverterKeyBtn(text = "00", onClick = onKeyClick, isDark = isDark, modifier = Modifier.weight(1f))
                ConverterKeyBtn(text = "0", onClick = onKeyClick, isDark = isDark, modifier = Modifier.weight(2f))
                ConverterKeyBtn(text = ".", onClick = onKeyClick, isDark = isDark, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun ConverterKeyBtn(
    text: String,
    onClick: (String, View) -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier,
    isUtility: Boolean = false,
    isClear: Boolean = false
) {
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1.0f,
        label = "conv_key_scale"
    )

    val containerColor = when {
        isClear -> if (isDark) Color(0xFF451A1A) else Color(0xFFFFEBEE)
        isUtility -> if (isDark) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
        else -> if (isDark) DarkNumKeyBg else LightNumKeyBg
    }

    val contentColor = when {
        isClear -> if (isDark) Color(0xFFFF8A80) else Color(0xFFD32F2F)
        isUtility -> MaterialTheme.colorScheme.primary
        else -> if (isDark) DarkNumKeyText else LightNumKeyText
    }

    Card(
        onClick = { onClick(text, view) },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = BorderStroke(1.dp, if (isDark) DarkNumKeyBorder else LightNumKeyBorder),
        interactionSource = interactionSource,
        modifier = modifier
            .scale(scale)
            .testTag("conv_btn_$text")
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = if (text.length > 1) 18.sp else 22.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }
    }
}

fun getCategoryIcon(category: UnitCategory): ImageVector {
    return when (category) {
        UnitCategory.LENGTH -> Icons.Default.Straighten
        UnitCategory.WEIGHT -> Icons.Default.FitnessCenter
        UnitCategory.CURRENCY -> Icons.Default.CurrencyExchange
        UnitCategory.TEMPERATURE -> Icons.Default.DeviceThermostat
        UnitCategory.AREA -> Icons.Default.CropSquare
        UnitCategory.VOLUME -> Icons.Default.Opacity
    }
}
