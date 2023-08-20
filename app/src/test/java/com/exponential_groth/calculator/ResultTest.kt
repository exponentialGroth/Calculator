package com.exponential_groth.calculator

import com.exponential_groth.calculator.result.OutputType
import com.exponential_groth.calculator.result.ResultManager
import com.exponential_groth.calculator.result.fractionToDecimal
import com.exponential_groth.calculator.result.primeFactors
import com.exponential_groth.calculator.result.toFraction
import org.junit.Assert.assertEquals
import org.junit.Test


class ResultTest {

    @Test
    fun formatResult() {
        val manager = ResultManager()
        val testCases = listOf(
            2672.25 to listOf(
                "\\frac{10689}{4}",
                "2.67225 \\times 10^{3}",
                "2.67225 \\times 10^{3}"
            ),
            0.24 to listOf(
                "\\frac{6}{25}",
                "2.4 \\times 10^{-1}",
                "240 \\times 10^{-3}"
            )
        )

        for ((input, expected) in testCases) {
            manager.addResult(input)
            listOf(OutputType.FRACTION, OutputType.SCIENTIFIC, OutputType.ENGINEERING).forEachIndexed { i, outputType ->
                assertEquals(expected[i], manager.formatResult(outputType))
            }
        }
    }


    @Test
    fun primeFactors() {
        val cases = listOf(
            245L to mapOf(5L to 1, 7L to 2),
            1680L to mapOf(2L to 4, 3L to 1, 5L to 1, 7L to 1)
        )
        for ((input, expected) in cases) {
            assertEquals(expected, primeFactors(input))
        }
    }

    @Test
    fun toFraction() {
        val testCases = listOf(
            0.01171875 to Pair(3L, 256L),
            5.0 / 6 to (5L to 6L),
            0.1 to (1L to 10L),
            306.1 to (3061L to 10L),
            124.24 to (3106L to 25L),
        )
        for ((input, expected) in testCases) {
            assertEquals(expected, input.toFraction())
        }
    }

    @Test
    fun fractionToDecimal() {
        val testCases = listOf(
            (1L to 2L) to "0.5",
            (25L to 3L) to "8.\\overline{3}",
            (5531448190974573L to 2251799813685248L) to "2.\\overline{456}",
        )
        for ((input, expected) in testCases) {
            assertEquals(expected,
                fractionToDecimal(input.first, input.second)
            )
        }
    }
}