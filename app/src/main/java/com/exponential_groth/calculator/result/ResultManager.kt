package com.exponential_groth.calculator.result

import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

class ResultManager {
    private val results = mutableListOf<Result>()
    private var currentResultIndex = -1

    var defaultOutputType: OutputType = OutputType.DECIMAL
    var maxDecPlaces: Int = 15
        set(value) {
            df.maximumFractionDigits = value
            field = value
        }
    var roundingMode: RoundingMode = RoundingMode.HALF_UP
        set(value) {
            df.roundingMode = value
            field = value
        }
    private val df = DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH)).apply {
        maximumFractionDigits = maxDecPlaces
        roundingMode = this@ResultManager.roundingMode
    }

    val isHidden get() = currentResultIndex == -1
    val outputType get() = results.getOrNull(currentResultIndex)?.outputType
    val stringRepresentation get() = results.getOrNull(currentResultIndex)?.stringRepresentation?:""
    val stringToShare get() = results.getOrNull(currentResultIndex)?.asUserFriendlyString()?:""

    fun formatResult(type: OutputType) = results.getOrNull(currentResultIndex)?.format(type)?:""
    fun switchFractionType() = results.getOrNull(currentResultIndex)?.switchFractionType()?: Unit
    fun switchSAndD() = results.getOrNull(currentResultIndex)?.switchSAndD()?: Unit
    /** If [dir] is true, adds 3 to the exponent, if not subtracts 3 */
    fun shiftEngNotation(dir: Boolean) = results.getOrNull(currentResultIndex)?.shiftEngNotation(dir)?: Unit


    fun addResult(plainResult: Double? = null, outputType: OutputType = defaultOutputType, numbers: List<Double>? = null) {
        results.add(Result(plainResult, outputType, numbers, df))
        currentResultIndex = results.size-1
    }
    fun moveBack() {
        currentResultIndex = max(currentResultIndex-1, 0)
    }
    fun moveForward() {
        currentResultIndex = min(currentResultIndex+1, results.size-1)
    }
    fun hide() {
        currentResultIndex = -1
    }
}