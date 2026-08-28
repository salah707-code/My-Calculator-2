package com.example.model

import androidx.annotation.StringRes
import com.example.R

enum class UnitCategory(
    @StringRes val titleRes: Int,
    val iconName: String
) {
    LENGTH(R.string.unit_cat_length, "ruler"),
    WEIGHT(R.string.unit_cat_weight, "scale"),
    CURRENCY(R.string.unit_cat_currency, "currency"),
    TEMPERATURE(R.string.unit_cat_temp, "temperature"),
    AREA(R.string.unit_cat_area, "area"),
    VOLUME(R.string.unit_cat_volume, "volume")
}

data class ConversionUnit(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val symbol: String,
    val factorToBase: Double // How many base units are in 1 of this unit
)

object UnitDefinitions {

    val lengthUnits = listOf(
        ConversionUnit("km", "كيلومتر", "Kilometer", "km", 1000.0),
        ConversionUnit("m", "متر", "Meter", "m", 1.0), // Base
        ConversionUnit("cm", "سنتيمتر", "Centimeter", "cm", 0.01),
        ConversionUnit("mm", "ميليمتر", "Millimeter", "mm", 0.001),
        ConversionUnit("mi", "ميل", "Mile", "mi", 1609.344),
        ConversionUnit("yd", "ياردة", "Yard", "yd", 0.9144),
        ConversionUnit("ft", "قدم", "Foot", "ft", 0.3048),
        ConversionUnit("in", "بوصة (إنش)", "Inch", "in", 0.0254)
    )

    val weightUnits = listOf(
        ConversionUnit("t", "طن متري", "Metric Ton", "t", 1000.0),
        ConversionUnit("kg", "كيلوغرام", "Kilogram", "kg", 1.0), // Base
        ConversionUnit("g", "غرام", "Gram", "g", 0.001),
        ConversionUnit("mg", "ميليغرام", "Milligram", "mg", 0.000001),
        ConversionUnit("lb", "باوند (رطل)", "Pound", "lb", 0.45359237),
        ConversionUnit("oz", "أونصة (أوقية)", "Ounce", "oz", 0.02834952)
    )

    // Base unit USD ($)
    val currencyUnits = listOf(
        ConversionUnit("USD", "دولار أمريكي", "US Dollar", "$", 1.0), // Base
        ConversionUnit("EUR", "يورو أوروبي", "Euro", "€", 1.08),
        ConversionUnit("GBP", "جنيه إسترليني", "British Pound", "£", 1.28),
        ConversionUnit("SAR", "ريال سعودي", "Saudi Riyal", "ر.س", 0.2666),
        ConversionUnit("AED", "درهم إماراتي", "UAE Dirham", "د.إ", 0.2723),
        ConversionUnit("KWD", "دينار كويتي", "Kuwaiti Dinar", "د.ك", 3.26),
        ConversionUnit("QAR", "ريال قطري", "Qatari Riyal", "ر.ق", 0.2747),
        ConversionUnit("EGP", "جنيه مصري", "Egyptian Pound", "ج.م", 0.0205),
        ConversionUnit("JOD", "دينار أردني", "Jordanian Dinar", "د.أ", 1.41),
        ConversionUnit("TRY", "ليرة تركية", "Turkish Lira", "₺", 0.029),
        ConversionUnit("JPY", "ين ياباني", "Japanese Yen", "¥", 0.0067),
        ConversionUnit("CAD", "دولار كندي", "Canadian Dollar", "C$", 0.73)
    )

    val tempUnits = listOf(
        ConversionUnit("C", "مئوية", "Celsius", "°C", 1.0),
        ConversionUnit("F", "فهرنهايت", "Fahrenheit", "°F", 1.0),
        ConversionUnit("K", "كلفن", "Kelvin", "K", 1.0)
    )

    val areaUnits = listOf(
        ConversionUnit("km2", "كيلومتر مربع", "Square Kilometer", "km²", 1000000.0),
        ConversionUnit("ha", "هكتار", "Hectare", "ha", 10000.0),
        ConversionUnit("m2", "متر مربع", "Square Meter", "m²", 1.0), // Base
        ConversionUnit("cm2", "سنتيمتر مربع", "Square Centimeter", "cm²", 0.0001),
        ConversionUnit("acre", "فدان / أكر", "Acre", "ac", 4046.856),
        ConversionUnit("ft2", "قدم مربع", "Square Foot", "ft²", 0.092903)
    )

    val volumeUnits = listOf(
        ConversionUnit("m3", "متر مكعب", "Cubic Meter", "m³", 1000.0),
        ConversionUnit("L", "لتر", "Liter", "L", 1.0), // Base
        ConversionUnit("mL", "ميليلتر", "Milliliter", "mL", 0.001),
        ConversionUnit("gal", "غالون أمريكي", "US Gallon", "gal", 3.78541),
        ConversionUnit("cup", "كوب قياسي", "US Cup", "cup", 0.236588),
        ConversionUnit("fl_oz", "أونصة سائلة", "Fluid Ounce", "fl oz", 0.0295735)
    )

    fun getUnitsForCategory(category: UnitCategory): List<ConversionUnit> {
        return when (category) {
            UnitCategory.LENGTH -> lengthUnits
            UnitCategory.WEIGHT -> weightUnits
            UnitCategory.CURRENCY -> currencyUnits
            UnitCategory.TEMPERATURE -> tempUnits
            UnitCategory.AREA -> areaUnits
            UnitCategory.VOLUME -> volumeUnits
        }
    }
}
