package com.exponential_groth.calculator.result

import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class Result(
    val plainResult: Double? = null,
    var outputType: OutputType = OutputType.DECIMAL,
    /** If (plainResult == null) {x, y} || {r, theta}  else if (outputType in {ENG, SCI}) {factor, exponent}*/
    var numbers: List<Double>? = null,
    val df: DecimalFormat
) {

    val fraction: Pair<Long, Long>? = plainResult?.toFraction()
    var stringRepresentation = ""
    private val isWhole get() = plainResult != null && plainResult.rem(1) == 0.0
    private val isPeriodic get() = fraction != null && fraction.second.let { it % 2 != 0L && it % 5 != 0L }

    init {
        format(outputType)
    }

    fun format(type: OutputType): String {
        if (stringRepresentation != "" &&
            (outputType == OutputType.POLAR || outputType == OutputType.CARTESIAN ||  // Multivalued can not be formatted (it is like that with my school calculator, but I plan to improve that)
            isWhole && outputType == OutputType.DECIMAL && type !in listOf(OutputType.ENGINEERING, OutputType.SCIENTIFIC, OutputType.FACTORIZATION)
        )) return stringRepresentation
        var newType = type

        if (plainResult != null && plainResult > Long.MAX_VALUE && type !in listOf(OutputType.ENGINEERING, OutputType.SCIENTIFIC)) {
            return format(OutputType.SCIENTIFIC)
        }

        stringRepresentation = when (newType) {
            OutputType.DECIMAL -> {
                if (isWhole)
                    plainResult!!.toLong().toString()
                else
                    df.format(plainResult!!)
            }
            OutputType.FRACTION -> {
                if (fraction == null && stringRepresentation == "") {  // for the first format call (otherwise stringRepresentation would remain empty)
                    newType = OutputType.DECIMAL
                    format(newType)
                } else if (fraction == null) {
                    newType = outputType
                    stringRepresentation
                } else "\\frac{${fraction.first}}{${fraction.second}}"
            }
            OutputType.MIXED_FRACTION -> {
                if (fraction == null && stringRepresentation == "") {  // for the first format call
                    newType = OutputType.DECIMAL
                    format(newType)
                } else if (fraction == null) {
                    newType = outputType
                    stringRepresentation
                } else {
                    val fractionalPart = reduceFraction(fraction.first % fraction.second, fraction.second)
                    val wholePart = plainResult!!.toLong().takeIf { it != 0L }?.toString()?:""
                    "$wholePart \\frac{${fractionalPart.first}}{${fractionalPart.second}}"
                }
            }
            OutputType.PERIODIC -> {
                if (!isPeriodic) {
                    newType = OutputType.DECIMAL
                    format(newType)
                } else fraction?.let{ fractionToDecimal(it.first, it.second) }?: stringRepresentation.ifEmpty {
                    newType = OutputType.DECIMAL
                    format(newType)
                }
            }
            OutputType.SCIENTIFIC -> {
                val shift = (log10(plainResult!!) - 0.5).roundToInt()
                val factor = plainResult * 10.0.pow(-shift)
                numbers = listOf(factor, shift.toDouble())
                "${df.format(factor)} \\times 10^{$shift}"
            }
            OutputType.ENGINEERING -> {
                val shift = (log10(plainResult!!) - 0.5).roundToInt().let {
                    it - it % 3 - if (it < 0) 3 else 0
                }
                val factor = plainResult * 10.0.pow(-shift)
                numbers = listOf(factor, shift.toDouble())
                "${df.format(factor)} \\times 10^{$shift}"
            }
            OutputType.FACTORIZATION -> {
                val n = plainResult!!.roundToLong()
                val factors = primeFactors(n)
                factors.entries.joinToString(" \\times ") {
                    "${it.key}^{${it.value}}"
                }
            }
            OutputType.POLAR -> {
                "r = ${df.format(numbers!![0])}, \\theta = ${df.format(numbers!![1])}"
            }
            OutputType.CARTESIAN -> {
                "x = ${df.format(numbers!![0])}, y = ${df.format(numbers!![1])}"
            }
        }
        outputType = newType
        return stringRepresentation
    }

    fun switchFractionType() {
        if (plainResult == null || isWhole || fraction == null) return
        if (outputType == OutputType.MIXED_FRACTION)
            format(OutputType.FRACTION)
        else {
            format(OutputType.MIXED_FRACTION)
        }
    }

    fun switchSAndD() {
        if (plainResult == null) return
        if (outputType == OutputType.DECIMAL && fraction != null) {
            format(OutputType.FRACTION)
        } else if ((outputType == OutputType.FRACTION || outputType == OutputType.MIXED_FRACTION) && fraction != null) {
            format(OutputType.PERIODIC)
        } else {
            format(OutputType.DECIMAL)
        }
    }

    /** If [dir] is true, adds 3 to the exponent, if not subtracts 3 */
    fun shiftEngNotation(dir: Boolean) {
        if (outputType != OutputType.ENGINEERING) {
            format(OutputType.ENGINEERING)
            return
        }
        val newExponent = numbers!![1] + if (dir) 3 else -3
        numbers = listOf(plainResult!! * 10.0.pow(-newExponent), newExponent)
        stringRepresentation = "${df.format(numbers!![0])} \\times 10^{${newExponent.toInt()}}"
    }

    fun asUserFriendlyString() = stringRepresentation
        .replace("\\times", "·")
        .replace("}{", "/")
        .replace("\\frac{", "")
        .replace("\\overline{", "|")
        .filter { it != '{' && it != '}' }

    companion object {
        class FormattingException(msg: Int? = null): Exception(msg?.toString())
    }
}