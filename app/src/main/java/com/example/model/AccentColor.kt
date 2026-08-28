package com.example.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.example.R

enum class AccentColor(
    @StringRes val titleRes: Int,
    val color: Color,
    val darkColor: Color,
    val lightSoft: Color,
    val darkSoft: Color
) {
    BLUE(
        titleRes = R.string.accent_blue,
        color = Color(0xFF2563EB),
        darkColor = Color(0xFF3B82F6),
        lightSoft = Color(0xFFEFF6FF),
        darkSoft = Color(0xFF1E293B)
    ),
    EMERALD(
        titleRes = R.string.accent_emerald,
        color = Color(0xFF059669),
        darkColor = Color(0xFF10B981),
        lightSoft = Color(0xFFECFDF5),
        darkSoft = Color(0xFF064E3B)
    ),
    PURPLE(
        titleRes = R.string.accent_purple,
        color = Color(0xFF7C3AED),
        darkColor = Color(0xFF8B5CF6),
        lightSoft = Color(0xFFF5F3FF),
        darkSoft = Color(0xFF2E1065)
    ),
    ORANGE(
        titleRes = R.string.accent_orange,
        color = Color(0xFFEA580C),
        darkColor = Color(0xFFF97316),
        lightSoft = Color(0xFFFFF7ED),
        darkSoft = Color(0xFF431407)
    ),
    ROSE(
        titleRes = R.string.accent_rose,
        color = Color(0xFFE11D48),
        darkColor = Color(0xFFF43F5E),
        lightSoft = Color(0xFFFFF1F2),
        darkSoft = Color(0xFF4C0519)
    ),
    TEAL(
        titleRes = R.string.accent_teal,
        color = Color(0xFF0D9488),
        darkColor = Color(0xFF14B8A6),
        lightSoft = Color(0xFFF0FDFA),
        darkSoft = Color(0xFF134E4A)
    )
}
