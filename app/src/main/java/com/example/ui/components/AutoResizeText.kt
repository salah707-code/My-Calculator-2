package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun AutoResizeText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    maxFontSize: TextUnit = 48.sp,
    minFontSize: TextUnit = 18.sp,
    fontWeight: FontWeight = FontWeight.SemiBold,
    textAlign: TextAlign = TextAlign.End,
    style: TextStyle = LocalTextStyle.current
) {
    var fontScale by remember(text) { mutableFloatStateOf(1f) }
    var readyToDraw by remember(text) { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // Auto-scroll to end on new digits
    LaunchedEffect(text) {
        scrollState.scrollTo(scrollState.maxValue)
    }

    val currentFontSize = (maxFontSize.value * fontScale).coerceAtLeast(minFontSize.value).sp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        contentAlignment = Alignment.CenterEnd
    ) {
        Text(
            text = text,
            modifier = Modifier.drawWithContent {
                if (readyToDraw) {
                    drawContent()
                }
            },
            color = color,
            fontSize = currentFontSize,
            fontWeight = fontWeight,
            textAlign = textAlign,
            maxLines = 1,
            softWrap = false,
            style = style,
            onTextLayout = { textLayoutResult ->
                if (textLayoutResult.didOverflowWidth) {
                    if (currentFontSize.value > minFontSize.value) {
                        fontScale *= 0.88f
                    } else {
                        readyToDraw = true
                    }
                } else {
                    readyToDraw = true
                }
            }
        )
    }
}

