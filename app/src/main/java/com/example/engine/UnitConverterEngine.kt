package com.example.engine

import com.example.model.ConversionUnit
import com.example.model.UnitCategory
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object UnitConverterEngine {

    fun convert(
        value: Double,
        fromUnit: ConversionUnit,
        toUnit: ConversionUnit,
        category: UnitCategory
    ): Double {
        if (fromUnit.id == toUnit.id) return value

        if (category == UnitCategory.TEMPERATURE) {
            return convertTemperature(value, fromUnit.id, toUnit.id)
        }

        // Convert fromUnit to Base unit, then Base unit to toUnit
        val baseValue = value * fromUnit.factorToBase
        return baseValue / toUnit.factorToBase
    }

    private fun convertTemperature(value: Double, fromId: String, toId: String): Double {
        // Step 1: Convert from source to Celsius
        val celsius = when (fromId) {
            "C" -> value
            "F" -> (value - 32.0) * 5.0 / 9.0
            "K" -> value - 273.15
            else -> value
        }

        // Step 2: Convert Celsius to target
        return when (toId) {
            "C" -> celsius
            "F" -> (celsius * 9.0 / 5.0) + 32.0
            "K" -> celsius + 273.15
            else -> celsius
        }
    }

    fun formatResult(
        value: Double,
        useThousandsSeparator: Boolean = true,
        maxDecimals: Int = 6
    ): String {
        if (value.isNaN() || value.isInfinite()) return "0"
        
        // Handle exact integer case
        if (value == value.toLong().toDouble() && Math.abs(value) < 1e12) {
            val longVal = value.toLong()
            return if (useThousandsSeparator) {
                val symbols = DecimalFormatSymbols(Locale.US)
                DecimalFormat("#,###", symbols).format(longVal)
            } else {
                longVal.toString()
            }
        }

        val pattern = if (useThousandsSeparator) "#,##0.######" else "0.######"
        val symbols = DecimalFormatSymbols(Locale.US)
        val df = DecimalFormat(pattern, symbols).apply {
            maximumFractionDigits = maxDecimals
            roundingMode = RoundingMode.HALF_UP
        }
        return df.format(value)
    }

    fun getFormulaSummary(
        fromUnit: ConversionUnit,
        toUnit: ConversionUnit,
        category: UnitCategory
    ): String {
        if (fromUnit.id == toUnit.id) {
            return "1 ${fromUnit.symbol} = 1 ${toUnit.symbol}"
        }
        val oneConverted = convert(1.0, fromUnit, toUnit, category)
        val formatted = formatResult(oneConverted, useThousandsSeparator = true, maxDecimals = 4)
        return "1 ${fromUnit.symbol} = $formatted ${toUnit.symbol}"
    }
}
