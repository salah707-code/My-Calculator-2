package com.example.model

import androidx.annotation.StringRes
import com.example.R

enum class AppLanguage(
    val code: String,
    @StringRes val titleRes: Int,
    val displayName: String
) {
    SYSTEM("", R.string.lang_system, "تلقائي / System"),
    ARABIC("ar", R.string.lang_arabic, "العربية"),
    ENGLISH("en", R.string.lang_english, "English")
}
