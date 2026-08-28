package com.example.model

sealed class CalculatorKey(val symbol: String) {
    // Digits
    data class Digit(val value: String) : CalculatorKey(value)
    
    // Special zeroes
    data object DoubleZero : CalculatorKey("00")
    data object TripleZero : CalculatorKey("000")
    
    // Dot
    data object DecimalDot : CalculatorKey(".")
    
    // Basic Operations
    data object Add : CalculatorKey("+")
    data object Subtract : CalculatorKey("−")
    data object Multiply : CalculatorKey("×")
    data object Divide : CalculatorKey("÷")
    data object Equals : CalculatorKey("=")
    data object Percent : CalculatorKey("%")
    data object PlusMinus : CalculatorKey("±")
    
    // Extended Operations (Powers, Roots, Reciprocal, Constants)
    data object SquareRoot : CalculatorKey("√")
    data object Power : CalculatorKey("^")
    data object Square : CalculatorKey("x²")
    data object Inverse : CalculatorKey("1/x")
    data object Pi : CalculatorKey("π")
    data object EulerE : CalculatorKey("e")

    // Trigonometry & Logarithms (Scientific Mode)
    data object Sin : CalculatorKey("sin")
    data object Cos : CalculatorKey("cos")
    data object Tan : CalculatorKey("tan")
    data object Log : CalculatorKey("log")
    data object Ln : CalculatorKey("ln")

    // Angle Unit Toggle Key
    data object AngleModeToggle : CalculatorKey("DEG")
    
    // Utilities
    data object AllClear : CalculatorKey("AC")
    data object Backspace : CalculatorKey("⌫")
}

enum class KeyType {
    NUMBER,
    OPERATOR,
    FUNCTION,
    SCIENTIFIC,
    UTILITY,
    EQUALS
}
