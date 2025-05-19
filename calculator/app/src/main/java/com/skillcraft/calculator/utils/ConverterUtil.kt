package com.skillcraft.calculator.utils

import kotlin.math.roundToInt

class ConverterUtil {
    
    // Currency conversion rates (simplified, would use API in production)
    private val currencyRates = mapOf(
        "USD" to 1.0,
        "EUR" to 0.85,
        "GBP" to 0.73,
        "JPY" to 110.0,
        "CAD" to 1.25,
        "AUD" to 1.35,
        "CNY" to 6.45,
        "INR" to 74.5
    )
    
    // Length conversion factors (to meters)
    private val lengthFactors = mapOf(
        "Meter" to 1.0,
        "Kilometer" to 1000.0,
        "Centimeter" to 0.01,
        "Millimeter" to 0.001,
        "Mile" to 1609.34,
        "Yard" to 0.9144,
        "Foot" to 0.3048,
        "Inch" to 0.0254
    )
    
    // Weight conversion factors (to kilograms)
    private val weightFactors = mapOf(
        "Kilogram" to 1.0,
        "Gram" to 0.001,
        "Milligram" to 0.000001,
        "Metric Ton" to 1000.0,
        "Pound" to 0.453592,
        "Ounce" to 0.0283495,
        "Stone" to 6.35029
    )
    
    // Temperature units
    private val temperatureUnits = listOf("Celsius", "Fahrenheit", "Kelvin")
    
    fun getCurrencyUnits(): List<String> = currencyRates.keys.toList()
    
    fun getLengthUnits(): List<String> = lengthFactors.keys.toList()
    
    fun getWeightUnits(): List<String> = weightFactors.keys.toList()
    
    fun getTemperatureUnits(): List<String> = temperatureUnits
    
    fun convertCurrency(value: Double, fromUnit: String, toUnit: String): Double {
        if (fromUnit == toUnit) return value
        
        val fromRate = currencyRates[fromUnit] ?: return value
        val toRate = currencyRates[toUnit] ?: return value
        
        return round((value / fromRate) * toRate, 2)
    }
    
    fun convertLength(value: Double, fromUnit: String, toUnit: String): Double {
        if (fromUnit == toUnit) return value
        
        val fromFactor = lengthFactors[fromUnit] ?: return value
        val toFactor = lengthFactors[toUnit] ?: return value
        
        return round((value * fromFactor) / toFactor, 4)
    }
    
    fun convertWeight(value: Double, fromUnit: String, toUnit: String): Double {
        if (fromUnit == toUnit) return value
        
        val fromFactor = weightFactors[fromUnit] ?: return value
        val toFactor = weightFactors[toUnit] ?: return value
        
        return round((value * fromFactor) / toFactor, 4)
    }
    
    fun convertTemperature(value: Double, fromUnit: String, toUnit: String): Double {
        if (fromUnit == toUnit) return value
        
        // Convert to Kelvin first
        val kelvin = when (fromUnit) {
            "Celsius" -> value + 273.15
            "Fahrenheit" -> (value - 32) * 5 / 9 + 273.15
            "Kelvin" -> value
            else -> value
        }
        
        // Convert from Kelvin to target unit
        return when (toUnit) {
            "Celsius" -> round(kelvin - 273.15, 2)
            "Fahrenheit" -> round((kelvin - 273.15) * 9 / 5 + 32, 2)
            "Kelvin" -> round(kelvin, 2)
            else -> value
        }
    }
    
    private fun round(value: Double, decimals: Int): Double {
        val factor = 10.0.pow(decimals.toDouble())
        return (value * factor).roundToInt() / factor
    }
    
    private fun Double.pow(exponent: Double): Double = Math.pow(this, exponent)
}
