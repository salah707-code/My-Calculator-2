package com.example.model

import androidx.annotation.StringRes
import com.example.R

enum class AppTheme(@StringRes val titleRes: Int) {
    SYSTEM(R.string.theme_system),
    LIGHT(R.string.theme_light),
    DARK(R.string.theme_dark),
    METALLIC(R.string.theme_metallic),
    AMOLED(R.string.theme_amoled),
    MIDNIGHT_BLUE(R.string.theme_midnight_blue)
}
