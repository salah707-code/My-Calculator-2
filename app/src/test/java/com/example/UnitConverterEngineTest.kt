package com.example

import com.example.engine.UnitConverterEngine
import com.example.model.ConversionUnit
import com.example.model.UnitCategory
import com.example.model.UnitDefinitions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UnitConverterEngineTest {

    @Test
    fun testLengthConversion_KmToMeters() {
        val km = ConversionUnit("km", "كيلومتر", "Kilometer", "km", 1000.0)
        val m = ConversionUnit("m", "متر", "Meter", "m", 1.0)
        val result = UnitConverterEngine.convert(5.0, km, m, UnitCategory.LENGTH)
        assertEquals(5000.0, result, 0.001)
    }

    @Test
    fun testLengthConversion_MetersToCm() {
        val m = ConversionUnit("m", "متر", "Meter", "m", 1.0)
        val cm = ConversionUnit("cm", "سنتيمتر", "Centimeter", "cm", 0.01)
        val result = UnitConverterEngine.convert(2.5, m, cm, UnitCategory.LENGTH)
        assertEquals(250.0, result, 0.001)
    }

    @Test
    fun testWeightConversion_KgToGrams() {
        val kg = ConversionUnit("kg", "كيلوغرام", "Kilogram", "kg", 1.0)
        val g = ConversionUnit("g", "غرام", "Gram", "g", 0.001)
        val result = UnitConverterEngine.convert(3.0, kg, g, UnitCategory.WEIGHT)
        assertEquals(3000.0, result, 0.001)
    }

    @Test
    fun testTemperatureConversion_CelsiusToFahrenheit() {
        val c = ConversionUnit("C", "مئوية", "Celsius", "°C", 1.0)
        val f = ConversionUnit("F", "فهرنهايت", "Fahrenheit", "°F", 1.0)
        val result = UnitConverterEngine.convert(100.0, c, f, UnitCategory.TEMPERATURE)
        assertEquals(212.0, result, 0.001)

        val resultZero = UnitConverterEngine.convert(0.0, c, f, UnitCategory.TEMPERATURE)
        assertEquals(32.0, resultZero, 0.001)
    }

    @Test
    fun testTemperatureConversion_FahrenheitToCelsius() {
        val c = ConversionUnit("C", "مئوية", "Celsius", "°C", 1.0)
        val f = ConversionUnit("F", "فهرنهايت", "Fahrenheit", "°F", 1.0)
        val result = UnitConverterEngine.convert(32.0, f, c, UnitCategory.TEMPERATURE)
        assertEquals(0.0, result, 0.001)
    }

    @Test
    fun testCurrencyConversion_UsdToSar() {
        val usd = ConversionUnit("USD", "دولار", "USD", "$", 1.0)
        val sar = ConversionUnit("SAR", "ريال سعودي", "SAR", "ر.س", 0.2666)
        val result = UnitConverterEngine.convert(100.0, usd, sar, UnitCategory.CURRENCY)
        assertTrue(result > 370.0 && result < 380.0)
    }

    @Test
    fun testFormatResult_Thousands() {
        val formatted = UnitConverterEngine.formatResult(1250000.0, useThousandsSeparator = true)
        assertEquals("1,250,000", formatted)
    }
}
