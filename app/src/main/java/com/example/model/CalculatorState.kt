package com.example.model

enum class CalculatorMode {
    BASIC,
    SCIENTIFIC
}

data class CalculatorState(
    val primaryDisplay: String = "0",
    val secondaryDisplay: String = "",
    val activeOperator: String? = null,
    val livePreview: String? = null,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val lastCalculation: CalculationHistory? = null,
    val isDegMode: Boolean = true,
    val calculatorMode: CalculatorMode = CalculatorMode.BASIC
)

