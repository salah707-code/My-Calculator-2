package com.example.ui.components

import android.view.View
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CalculatorKey
import com.example.model.KeyType
import com.example.ui.theme.CleanRed
import com.example.ui.theme.CleanRedDark
import com.example.ui.theme.CleanRedSoft
import com.example.ui.theme.CleanRedSoftDark
import com.example.ui.theme.DarkNumKeyBg
import com.example.ui.theme.DarkNumKeyBorder
import com.example.ui.theme.DarkNumKeyText
import com.example.ui.theme.DarkUtilityKeyBg
import com.example.ui.theme.DarkUtilityKeyText
import com.example.ui.theme.LightNumKeyBg
import com.example.ui.theme.LightNumKeyBorder
import com.example.ui.theme.LightNumKeyText
import com.example.ui.theme.LightUtilityKeyBg
import com.example.ui.theme.LightUtilityKeyText

@Composable
fun CalculatorKeyButton(
    key: CalculatorKey,
    keyType: KeyType,
    onClick: (View) -> Unit,
    modifier: Modifier = Modifier,
    customLabel: String? = null,
    isActiveOperator: Boolean = false,
    isDarkTheme: Boolean = isSystemInDarkTheme()
) {
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1.0f,
        label = "key_scale"
    )

    val isAllClear = key is CalculatorKey.AllClear

    // Determine target container color based on active state, pressed state, and key type
    val targetContainerColor: Color = when {
        isActiveOperator -> MaterialTheme.colorScheme.primary
        isPressed -> MaterialTheme.colorScheme.primary.copy(alpha = if (isDarkTheme) 0.25f else 0.15f)
        isAllClear -> if (isDarkTheme) CleanRedSoftDark else CleanRedSoft
        keyType == KeyType.OPERATOR -> if (isDarkTheme) DarkUtilityKeyBg else LightUtilityKeyBg
        keyType == KeyType.EQUALS -> MaterialTheme.colorScheme.primary
        keyType == KeyType.SCIENTIFIC || keyType == KeyType.FUNCTION -> {
            if (isDarkTheme) DarkUtilityKeyBg else LightUtilityKeyBg
        }
        keyType == KeyType.UTILITY -> if (isDarkTheme) DarkUtilityKeyBg else LightUtilityKeyBg
        else -> if (isDarkTheme) DarkNumKeyBg else LightNumKeyBg
    }

    val containerColor by animateColorAsState(
        targetValue = targetContainerColor,
        label = "btn_bg_color"
    )

    // Determine target text/icon color
    val targetContentColor: Color = when {
        isActiveOperator -> MaterialTheme.colorScheme.onPrimary
        isPressed -> MaterialTheme.colorScheme.primary
        isAllClear -> if (isDarkTheme) CleanRedDark else CleanRed
        keyType == KeyType.OPERATOR -> MaterialTheme.colorScheme.primary
        keyType == KeyType.EQUALS -> MaterialTheme.colorScheme.onPrimary
        keyType == KeyType.SCIENTIFIC || keyType == KeyType.FUNCTION -> MaterialTheme.colorScheme.primary
        keyType == KeyType.UTILITY -> if (isDarkTheme) DarkUtilityKeyText else LightUtilityKeyText
        else -> if (isDarkTheme) DarkNumKeyText else LightNumKeyText
    }

    val contentColor by animateColorAsState(
        targetValue = targetContentColor,
        label = "btn_text_color"
    )

    // Uniform border styling with rich colors and prominent active border
    val border: BorderStroke = when {
        isActiveOperator -> BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        isPressed -> BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
        isAllClear -> BorderStroke(
            1.dp,
            if (isDarkTheme) CleanRedDark.copy(alpha = 0.5f) else CleanRed.copy(alpha = 0.4f)
        )
        keyType == KeyType.EQUALS -> BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary
        )
        keyType == KeyType.OPERATOR -> BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = if (isDarkTheme) 0.4f else 0.35f)
        )
        keyType == KeyType.SCIENTIFIC || keyType == KeyType.FUNCTION -> BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
        )
        keyType == KeyType.UTILITY -> BorderStroke(
            1.dp,
            if (isDarkTheme) DarkNumKeyBorder else LightNumKeyBorder
        )
        else -> BorderStroke(
            1.dp,
            if (isDarkTheme) DarkNumKeyBorder else LightNumKeyBorder
        )
    }

    // Uniform shading/elevation across ALL buttons (including =)
    val elevation = if (isDarkTheme) 0.dp else 1.dp

    val testTag = when (key) {
        is CalculatorKey.Digit -> "btn_${key.value}"
        is CalculatorKey.DoubleZero -> "btn_00"
        is CalculatorKey.TripleZero -> "btn_000"
        is CalculatorKey.DecimalDot -> "btn_dot"
        is CalculatorKey.Add -> "btn_add"
        is CalculatorKey.Subtract -> "btn_sub"
        is CalculatorKey.Multiply -> "btn_mul"
        is CalculatorKey.Divide -> "btn_div"
        is CalculatorKey.Power -> "btn_pow"
        is CalculatorKey.SquareRoot -> "btn_sqrt"
        is CalculatorKey.Square -> "btn_sqr"
        is CalculatorKey.Inverse -> "btn_inv"
        is CalculatorKey.Pi -> "btn_pi"
        is CalculatorKey.EulerE -> "btn_e"
        is CalculatorKey.Sin -> "btn_sin"
        is CalculatorKey.Cos -> "btn_cos"
        is CalculatorKey.Tan -> "btn_tan"
        is CalculatorKey.Log -> "btn_log"
        is CalculatorKey.Ln -> "btn_ln"
        is CalculatorKey.AngleModeToggle -> "btn_deg_rad"
        is CalculatorKey.Equals -> "btn_equals"
        is CalculatorKey.Percent -> "btn_percent"
        is CalculatorKey.PlusMinus -> "btn_pm"
        is CalculatorKey.AllClear -> "btn_ac"
        is CalculatorKey.Backspace -> "btn_backspace"
    }

    val displayText = customLabel ?: key.symbol

    // Font size scaled down by 35% for clean, elegant proportions
    val fontSize = when {
        displayText.length >= 4 -> 11.sp
        displayText.length == 3 -> 13.sp
        displayText.length == 2 -> 15.sp
        keyType == KeyType.FUNCTION || keyType == KeyType.SCIENTIFIC -> 14.sp
        keyType == KeyType.OPERATOR || keyType == KeyType.EQUALS -> 22.sp
        keyType == KeyType.UTILITY -> 17.sp
        else -> 21.sp
    }

    Card(
        onClick = { onClick(view) },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = border,
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation,
            pressedElevation = 0.dp
        ),
        interactionSource = interactionSource,
        modifier = modifier
            .scale(scale)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = displayText,
                fontSize = fontSize,
                fontWeight = if (keyType == KeyType.NUMBER) FontWeight.SemiBold else FontWeight.Bold,
                color = contentColor
            )
        }
    }
}
