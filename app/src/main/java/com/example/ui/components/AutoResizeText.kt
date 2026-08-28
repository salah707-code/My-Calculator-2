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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun AutoResizeText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    maxFontSize: TextUnit = 80.sp,
    minFontSize: TextUnit = 24.sp,
    maxLines: Int = 2,
    fontWeight: FontWeight = FontWeight.Bold,
    textAlign: TextAlign = TextAlign.End,
    style: TextStyle = LocalTextStyle.current
) {
    var fontScale by remember(text) { mutableFloatStateOf(1f) }
    var readyToDraw by remember(text) { mutableStateOf(false) }

    val currentFontSize = (maxFontSize.value * fontScale).coerceAtLeast(minFontSize.value).sp
    val calculatedLineHeight = (currentFontSize.value * 1.08f).sp

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Text(
            text = text,
            modifier = Modifier
                .fillMaxWidth()
                .drawWithContent {
                    if (readyToDraw) {
                        drawContent()
                    }
                },
            color = color,
            fontSize = currentFontSize,
            lineHeight = calculatedLineHeight,
            fontWeight = fontWeight,
            textAlign = textAlign,
            maxLines = maxLines,
            softWrap = true,
            overflow = TextOverflow.Ellipsis,
            style = style,
            onTextLayout = { textLayoutResult ->
                if (textLayoutResult.didOverflowHeight || textLayoutResult.didOverflowWidth) {
                    if (currentFontSize.value > minFontSize.value) {
                        fontScale *= 0.85f
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
