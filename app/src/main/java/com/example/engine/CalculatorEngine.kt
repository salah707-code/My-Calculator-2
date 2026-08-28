package com.example.engine

import com.example.model.CalculationHistory
import com.example.model.CalculatorKey
import com.example.model.CalculatorMode
import com.example.model.CalculatorState
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class CalculatorEngine(
    private var useThousandsSeparator: Boolean = true,
    private val divideByZeroText: String = "لا يمكن القسمة على صفر",
    private val invalidText: String = "غير صالح"
) {
    private val mathContext = MathContext.DECIMAL128
    
    private var currentInput: String = "0"
    private var storedOperand: BigDecimal? = null
    private var currentOperator: String? = null
    private var secondaryDisplay: String = ""
    private var isNewInput: Boolean = true
    private var isError: Boolean = false
    private var errorMessage: String? = null
    private var isDegMode: Boolean = true
    private var calculatorMode: CalculatorMode = CalculatorMode.BASIC
    
    // For repeating '=' functionality
    private var lastOperator: String? = null
    private var lastSecondOperand: BigDecimal? = null
    
    // Last successful calculated history item
    private var lastHistoryItem: CalculationHistory? = null

    fun setThousandsSeparator(enabled: Boolean) {
        useThousandsSeparator = enabled
    }

    fun setDegMode(enabled: Boolean) {
        isDegMode = enabled
    }

    fun setCalculatorMode(mode: CalculatorMode) {
        calculatorMode = mode
    }

    fun toggleDegMode(): Boolean {
        isDegMode = !isDegMode
        return isDegMode
    }

    fun toggleCalculatorMode(): CalculatorMode {
        calculatorMode = if (calculatorMode == CalculatorMode.BASIC) CalculatorMode.SCIENTIFIC else CalculatorMode.BASIC
        return calculatorMode
    }

    fun getState(): CalculatorState {
        return CalculatorState(
            primaryDisplay = if (isError) (errorMessage ?: invalidText) else formatForDisplay(currentInput),
            secondaryDisplay = secondaryDisplay,
            activeOperator = currentOperator,
            isError = isError,
            errorMessage = errorMessage,
            lastCalculation = lastHistoryItem,
            isDegMode = isDegMode,
            calculatorMode = calculatorMode
        )
    }

    fun onKeyPress(key: CalculatorKey): CalculatorState {
        lastHistoryItem = null // reset per-event
        when (key) {
            is CalculatorKey.Digit -> handleDigit(key.value)
            is CalculatorKey.DoubleZero -> handleMultipleZeroes("00")
            is CalculatorKey.TripleZero -> handleMultipleZeroes("000")
            is CalculatorKey.DecimalDot -> handleDecimalDot()
            is CalculatorKey.Add -> handleOperator("+")
            is CalculatorKey.Subtract -> handleOperator("−")
            is CalculatorKey.Multiply -> handleOperator("×")
            is CalculatorKey.Divide -> handleOperator("÷")
            is CalculatorKey.Power -> handleOperator("^")
            is CalculatorKey.SquareRoot -> handleSquareRoot()
            is CalculatorKey.Square -> handleSquare()
            is CalculatorKey.Inverse -> handleInverse()
            is CalculatorKey.Pi -> handlePi()
            is CalculatorKey.EulerE -> handleEulerE()
            is CalculatorKey.Sin -> handleSin()
            is CalculatorKey.Cos -> handleCos()
            is CalculatorKey.Tan -> handleTan()
            is CalculatorKey.Log -> handleLog()
            is CalculatorKey.Ln -> handleLn()
            is CalculatorKey.AngleModeToggle -> handleAngleModeToggle()
            is CalculatorKey.Equals -> handleEquals()
            is CalculatorKey.Percent -> handlePercent()
            is CalculatorKey.PlusMinus -> handlePlusMinus()
            is CalculatorKey.AllClear -> handleAllClear()
            is CalculatorKey.Backspace -> handleBackspace()
        }
        return getState()
    }

    fun resetWithInitialValue(value: String) {
        handleAllClear()
        currentInput = sanitizeNumberString(value)
        isNewInput = false
    }

    private fun handleDigit(digit: String) {
        if (isError) {
            handleAllClear()
        }

        if (isNewInput) {
            currentInput = digit
            isNewInput = false
        } else {
            if (currentInput == "0") {
                currentInput = digit
            } else if (currentInput == "-0") {
                currentInput = "-$digit"
            } else if (currentInput.replace("-", "").replace(".", "").length < 30) {
                currentInput += digit
            }
        }
    }

    private fun handleMultipleZeroes(zeros: String) {
        if (isError) {
            handleAllClear()
        }

        if (isNewInput) {
            currentInput = "0"
            isNewInput = false
        } else {
            if (currentInput == "0" || currentInput == "-0") {
                // Keep as 0
                return
            }
            if (currentInput.replace("-", "").replace(".", "").length + zeros.length <= 30) {
                currentInput += zeros
            }
        }
    }

    private fun handleDecimalDot() {
        if (isError) {
            handleAllClear()
        }

        if (isNewInput) {
            currentInput = "0."
            isNewInput = false
        } else {
            if (!currentInput.contains(".")) {
                currentInput = if (currentInput.isEmpty()) "0." else "$currentInput."
            }
        }
    }

    private fun handleSquareRoot() {
        if (isError) return
        val currentVal = parseToBigDecimal(currentInput)
        if (currentVal < BigDecimal.ZERO) {
            isError = true
            errorMessage = invalidText
            return
        }
        val result = try {
            val doubleVal = currentVal.toDouble()
            val sqrtVal = kotlin.math.sqrt(doubleVal)
            BigDecimal.valueOf(sqrtVal).round(MathContext(16, RoundingMode.HALF_UP)).stripTrailingZeros()
        } catch (e: Exception) {
            isError = true
            errorMessage = invalidText
            return
        }

        val exprDisplay = "√(${formatBigDecimalForDisplay(currentVal)})"
        val resDisplay = formatBigDecimalForDisplay(result)
        secondaryDisplay = "$exprDisplay ="
        currentInput = formatBigDecimalToString(result)
        storedOperand = result
        isNewInput = true

        lastHistoryItem = CalculationHistory(
            expression = exprDisplay,
            result = resDisplay,
            timestamp = System.currentTimeMillis()
        )
    }

    private fun handleSquare() {
        if (isError) return
        val currentVal = parseToBigDecimal(currentInput)
        val result = try {
            currentVal.multiply(currentVal, mathContext).stripTrailingZeros()
        } catch (e: Exception) {
            isError = true
            errorMessage = invalidText
            return
        }

        val exprDisplay = "(${formatBigDecimalForDisplay(currentVal)})²"
        val resDisplay = formatBigDecimalForDisplay(result)
        secondaryDisplay = "$exprDisplay ="
        currentInput = formatBigDecimalToString(result)
        storedOperand = result
        isNewInput = true

        lastHistoryItem = CalculationHistory(
            expression = exprDisplay,
            result = resDisplay,
            timestamp = System.currentTimeMillis()
        )
    }

    private fun handleInverse() {
        if (isError) return
        val currentVal = parseToBigDecimal(currentInput)
        if (currentVal.compareTo(BigDecimal.ZERO) == 0) {
            isError = true
            errorMessage = divideByZeroText
            return
        }
        val result = try {
            BigDecimal.ONE.divide(currentVal, 16, RoundingMode.HALF_UP).stripTrailingZeros()
        } catch (e: Exception) {
            isError = true
            errorMessage = invalidText
            return
        }

        val exprDisplay = "1/(${formatBigDecimalForDisplay(currentVal)})"
        val resDisplay = formatBigDecimalForDisplay(result)
        secondaryDisplay = "$exprDisplay ="
        currentInput = formatBigDecimalToString(result)
        storedOperand = result
        isNewInput = true

        lastHistoryItem = CalculationHistory(
            expression = exprDisplay,
            result = resDisplay,
            timestamp = System.currentTimeMillis()
        )
    }

    private fun handlePi() {
        if (isError) {
            handleAllClear()
        }
        currentInput = "3.141592653589793"
        isNewInput = false
    }

    private fun handleEulerE() {
        if (isError) {
            handleAllClear()
        }
        currentInput = "2.718281828459045"
        isNewInput = false
    }

    private fun handleAngleModeToggle() {
        isDegMode = !isDegMode
    }

    private fun handleSin() {
        if (isError) return
        val currentVal = parseToBigDecimal(currentInput)
        val doubleVal = currentVal.toDouble()
        val result = try {
            val rad = if (isDegMode) Math.toRadians(doubleVal) else doubleVal
            var sinVal = Math.sin(rad)
            if (isDegMode) {
                val mod360 = ((doubleVal % 360.0) + 360.0) % 360.0
                if (mod360 == 0.0 || mod360 == 180.0) sinVal = 0.0
                else if (mod360 == 90.0) sinVal = 1.0
                else if (mod360 == 270.0) sinVal = -1.0
                else if (mod360 == 30.0 || mod360 == 150.0) sinVal = 0.5
                else if (mod360 == 210.0 || mod360 == 330.0) sinVal = -0.5
            }
            BigDecimal.valueOf(sinVal).setScale(10, RoundingMode.HALF_UP).stripTrailingZeros()
        } catch (e: Exception) {
            isError = true
            errorMessage = invalidText
            return
        }

        val angleSuffix = if (isDegMode) "°" else " rad"
        val exprDisplay = "sin(${formatBigDecimalForDisplay(currentVal)}$angleSuffix)"
        val resDisplay = formatBigDecimalForDisplay(result)
        secondaryDisplay = "$exprDisplay ="
        currentInput = formatBigDecimalToString(result)
        storedOperand = result
        isNewInput = true

        lastHistoryItem = CalculationHistory(
            expression = exprDisplay,
            result = resDisplay,
            timestamp = System.currentTimeMillis()
        )
    }

    private fun handleCos() {
        if (isError) return
        val currentVal = parseToBigDecimal(currentInput)
        val doubleVal = currentVal.toDouble()
        val result = try {
            val rad = if (isDegMode) Math.toRadians(doubleVal) else doubleVal
            var cosVal = Math.cos(rad)
            if (isDegMode) {
                val mod360 = ((doubleVal % 360.0) + 360.0) % 360.0
                if (mod360 == 90.0 || mod360 == 270.0) cosVal = 0.0
                else if (mod360 == 0.0) cosVal = 1.0
                else if (mod360 == 180.0) cosVal = -1.0
                else if (mod360 == 60.0 || mod360 == 300.0) cosVal = 0.5
                else if (mod360 == 120.0 || mod360 == 240.0) cosVal = -0.5
            }
            BigDecimal.valueOf(cosVal).setScale(10, RoundingMode.HALF_UP).stripTrailingZeros()
        } catch (e: Exception) {
            isError = true
            errorMessage = invalidText
            return
        }

        val angleSuffix = if (isDegMode) "°" else " rad"
        val exprDisplay = "cos(${formatBigDecimalForDisplay(currentVal)}$angleSuffix)"
        val resDisplay = formatBigDecimalForDisplay(result)
        secondaryDisplay = "$exprDisplay ="
        currentInput = formatBigDecimalToString(result)
        storedOperand = result
        isNewInput = true

        lastHistoryItem = CalculationHistory(
            expression = exprDisplay,
            result = resDisplay,
            timestamp = System.currentTimeMillis()
        )
    }

    private fun handleTan() {
        if (isError) return
        val currentVal = parseToBigDecimal(currentInput)
        val doubleVal = currentVal.toDouble()
        val result = try {
            if (isDegMode) {
                val mod180 = ((doubleVal % 180.0) + 180.0) % 180.0
                if (mod180 == 90.0) {
                    isError = true
                    errorMessage = invalidText
                    return
                }
            }
            val rad = if (isDegMode) Math.toRadians(doubleVal) else doubleVal
            var tanVal = Math.tan(rad)
            if (isDegMode) {
                val mod180 = ((doubleVal % 180.0) + 180.0) % 180.0
                if (mod180 == 0.0) tanVal = 0.0
                else if (mod180 == 45.0) tanVal = 1.0
                else if (mod180 == 135.0) tanVal = -1.0
            }
            if (tanVal.isNaN() || tanVal.isInfinite()) {
                isError = true
                errorMessage = invalidText
                return
            }
            BigDecimal.valueOf(tanVal).setScale(10, RoundingMode.HALF_UP).stripTrailingZeros()
        } catch (e: Exception) {
            isError = true
            errorMessage = invalidText
            return
        }

        val angleSuffix = if (isDegMode) "°" else " rad"
        val exprDisplay = "tan(${formatBigDecimalForDisplay(currentVal)}$angleSuffix)"
        val resDisplay = formatBigDecimalForDisplay(result)
        secondaryDisplay = "$exprDisplay ="
        currentInput = formatBigDecimalToString(result)
        storedOperand = result
        isNewInput = true

        lastHistoryItem = CalculationHistory(
            expression = exprDisplay,
            result = resDisplay,
            timestamp = System.currentTimeMillis()
        )
    }

    private fun handleLog() {
        if (isError) return
        val currentVal = parseToBigDecimal(currentInput)
        if (currentVal <= BigDecimal.ZERO) {
            isError = true
            errorMessage = invalidText
            return
        }
        val result = try {
            val logVal = Math.log10(currentVal.toDouble())
            if (logVal.isNaN() || logVal.isInfinite()) {
                isError = true
                errorMessage = invalidText
                return
            }
            BigDecimal(logVal, mathContext).stripTrailingZeros()
        } catch (e: Exception) {
            isError = true
            errorMessage = invalidText
            return
        }

        val exprDisplay = "log(${formatBigDecimalForDisplay(currentVal)})"
        val resDisplay = formatBigDecimalForDisplay(result)
        secondaryDisplay = "$exprDisplay ="
        currentInput = formatBigDecimalToString(result)
        storedOperand = result
        isNewInput = true

        lastHistoryItem = CalculationHistory(
            expression = exprDisplay,
            result = resDisplay,
            timestamp = System.currentTimeMillis()
        )
    }

    private fun handleLn() {
        if (isError) return
        val currentVal = parseToBigDecimal(currentInput)
        if (currentVal <= BigDecimal.ZERO) {
            isError = true
            errorMessage = invalidText
            return
        }
        val result = try {
            val lnVal = Math.log(currentVal.toDouble())
            if (lnVal.isNaN() || lnVal.isInfinite()) {
                isError = true
                errorMessage = invalidText
                return
            }
            BigDecimal(lnVal, mathContext).stripTrailingZeros()
        } catch (e: Exception) {
            isError = true
            errorMessage = invalidText
            return
        }

        val exprDisplay = "ln(${formatBigDecimalForDisplay(currentVal)})"
        val resDisplay = formatBigDecimalForDisplay(result)
        secondaryDisplay = "$exprDisplay ="
        currentInput = formatBigDecimalToString(result)
        storedOperand = result
        isNewInput = true

        lastHistoryItem = CalculationHistory(
            expression = exprDisplay,
            result = resDisplay,
            timestamp = System.currentTimeMillis()
        )
    }

    private fun handleOperator(op: String) {
        if (isError) {
            return
        }

        val currentVal = parseToBigDecimal(currentInput)

        if (storedOperand != null && currentOperator != null && !isNewInput) {
            // Consecutive calculation (e.g. 5 + 5 + 5)
            val result = executeOperation(storedOperand!!, currentVal, currentOperator!!)
            if (result == null) {
                // Error (e.g. divide by zero)
                return
            }
            storedOperand = result
            currentInput = formatBigDecimalToString(result)
            secondaryDisplay = "${formatBigDecimalForDisplay(result)} $op"
        } else {
            storedOperand = currentVal
            secondaryDisplay = "${formatBigDecimalForDisplay(currentVal)} $op"
        }

        currentOperator = op
        isNewInput = true
        lastOperator = null
        lastSecondOperand = null
    }

    private fun handleEquals() {
        if (isError) return

        if (currentOperator != null) {
            val op1 = storedOperand ?: BigDecimal.ZERO
            val op2 = if (!isNewInput) parseToBigDecimal(currentInput) else op1
            val op = currentOperator!!

            val result = executeOperation(op1, op2, op)
            if (result == null) return

            val exprDisplay = "${formatBigDecimalForDisplay(op1)} $op ${formatBigDecimalForDisplay(op2)}"
            val resDisplay = formatBigDecimalForDisplay(result)

            secondaryDisplay = "$exprDisplay ="
            currentInput = formatBigDecimalToString(result)
            storedOperand = result
            lastOperator = op
            lastSecondOperand = op2
            currentOperator = null
            isNewInput = true

            // Record history
            lastHistoryItem = CalculationHistory(
                expression = exprDisplay,
                result = resDisplay,
                timestamp = System.currentTimeMillis()
            )
        } else if (lastOperator != null && lastSecondOperand != null) {
            // Repeated '=' press!
            val op1 = parseToBigDecimal(currentInput)
            val op2 = lastSecondOperand!!
            val op = lastOperator!!

            val result = executeOperation(op1, op2, op)
            if (result == null) return

            val exprDisplay = "${formatBigDecimalForDisplay(op1)} $op ${formatBigDecimalForDisplay(op2)}"
            val resDisplay = formatBigDecimalForDisplay(result)

            secondaryDisplay = "$exprDisplay ="
            currentInput = formatBigDecimalToString(result)
            storedOperand = result
            isNewInput = true

            lastHistoryItem = CalculationHistory(
                expression = exprDisplay,
                result = resDisplay,
                timestamp = System.currentTimeMillis()
            )
        }
    }

    private fun handlePercent() {
        if (isError) return

        val currentVal = parseToBigDecimal(currentInput)
        val percentFraction = currentVal.divide(BigDecimal("100"), 10, RoundingMode.HALF_UP).stripTrailingZeros()

        if (storedOperand != null && (currentOperator == "+" || currentOperator == "−")) {
            // Percent of stored operand, e.g., 200 + 10% = 200 + (200 * 0.10)
            val percentVal = storedOperand!!.multiply(percentFraction, mathContext).stripTrailingZeros()
            currentInput = formatBigDecimalToString(percentVal)
        } else {
            // Direct percentage (e.g. 50% = 0.5)
            currentInput = formatBigDecimalToString(percentFraction)
        }
        isNewInput = false
    }

    private fun handlePlusMinus() {
        if (isError) return

        if (currentInput == "0" || currentInput.isEmpty()) {
            return
        }

        currentInput = if (currentInput.startsWith("-")) {
            currentInput.substring(1)
        } else {
            "-$currentInput"
        }
    }

    private fun handleBackspace() {
        if (isError) {
            handleAllClear()
            return
        }

        if (isNewInput) {
            // If just finished an operation, backspace does nothing or clears
            return
        }

        if (currentInput.length > 1) {
            currentInput = currentInput.dropLast(1)
            if (currentInput == "-" || currentInput.isEmpty()) {
                currentInput = "0"
            }
        } else {
            currentInput = "0"
        }
    }

    private fun handleAllClear() {
        currentInput = "0"
        storedOperand = null
        currentOperator = null
        secondaryDisplay = ""
        isNewInput = true
        isError = false
        errorMessage = null
        lastOperator = null
        lastSecondOperand = null
        lastHistoryItem = null
    }

    private fun executeOperation(op1: BigDecimal, op2: BigDecimal, op: String): BigDecimal? {
        return try {
            val res = when (op) {
                "+" -> op1.add(op2, mathContext)
                "−", "-" -> op1.subtract(op2, mathContext)
                "×", "*" -> op1.multiply(op2, mathContext)
                "÷", "/" -> {
                    if (op2.compareTo(BigDecimal.ZERO) == 0) {
                        isError = true
                        errorMessage = divideByZeroText
                        return null
                    }
                    op1.divide(op2, 16, RoundingMode.HALF_UP)
                }
                "^" -> {
                    val double1 = op1.toDouble()
                    val double2 = op2.toDouble()
                    val powVal = Math.pow(double1, double2)
                    if (powVal.isNaN() || powVal.isInfinite()) {
                        isError = true
                        errorMessage = invalidText
                        return null
                    }
                    BigDecimal(powVal, mathContext)
                }
                else -> op2
            }
            res.stripTrailingZeros()
        } catch (e: ArithmeticException) {
            isError = true
            errorMessage = invalidText
            null
        } catch (e: Exception) {
            isError = true
            errorMessage = invalidText
            null
        }
    }

    private fun parseToBigDecimal(str: String): BigDecimal {
        return try {
            val sanitized = sanitizeNumberString(str)
            if (sanitized.isEmpty() || sanitized == "-" || sanitized == ".") {
                BigDecimal.ZERO
            } else {
                BigDecimal(sanitized)
            }
        } catch (e: Exception) {
            BigDecimal.ZERO
        }
    }

    private fun sanitizeNumberString(str: String): String {
        return str.replace(",", "")
            .replace("،", "")
            .replace(" ", "")
            .replace("−", "-")
    }

    private fun formatBigDecimalToString(bd: BigDecimal): String {
        val plain = bd.stripTrailingZeros().toPlainString()
        return if (plain.length > 20 || (plain.contains(".") && plain.indexOf(".") > 15)) {
            bd.toString()
        } else {
            plain
        }
    }

    fun formatForDisplay(rawString: String): String {
        if (!useThousandsSeparator) return rawString
        if (rawString.isEmpty() || rawString == "-" || rawString == ".") return rawString

        return try {
            val isNegative = rawString.startsWith("-")
            val unsigned = if (isNegative) rawString.substring(1) else rawString

            val parts = unsigned.split(".")
            val integerPart = parts[0]
            val decimalPart = if (parts.size > 1) parts[1] else null
            val hasTrailingDot = unsigned.endsWith(".")

            val formattedInteger = if (integerPart.isNotEmpty()) {
                val symbols = DecimalFormatSymbols(Locale.US).apply {
                    groupingSeparator = ','
                }
                val formatter = DecimalFormat("#,###", symbols)
                formatter.format(BigDecimal(integerPart))
            } else {
                "0"
            }

            val result = buildString {
                if (isNegative) append("-")
                append(formattedInteger)
                if (hasTrailingDot) {
                    append(".")
                } else if (decimalPart != null) {
                    append(".")
                    append(decimalPart)
                }
            }
            result
        } catch (e: Exception) {
            rawString
        }
    }

    fun formatBigDecimalForDisplay(bd: BigDecimal): String {
        val str = formatBigDecimalToString(bd)
        return formatForDisplay(str)
    }
}
