package com.exponential_groth.calculator

import com.exponential_groth.calculator.parser.AngleUnit
import com.exponential_groth.calculator.parser.Function
import com.exponential_groth.calculator.parser.Parser
import com.exponential_groth.calculator.parser.Token
import com.exponential_groth.calculator.parser.TokenType
import com.exponential_groth.calculator.parser.Tokenizer
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.sin
import kotlin.math.sqrt

class ParserTest {

    @Test
    fun tokenize() {
        val tests = mapOf(
            "2 + 3*7" to listOf(
                Token(TokenType.NUMBER, 2.0),
                Token(TokenType.ADDITION),
                Token(TokenType.NUMBER, 3.0),
                Token(TokenType.MULTIPLICATION),
                Token(TokenType.NUMBER, 7.0)
            ),
            ".1 ÷ 2.0^-4" to listOf(
                Token(TokenType.NUMBER, .1),
                Token(TokenType.DIVISION),
                Token(TokenType.NUMBER, 2.0),
                Token(TokenType.EXPONENTIATION),
                Token(TokenType.UNARY),
                Token(TokenType.NUMBER, 4.0)
            ),
            "sin(4. P 2)" to listOf(
                Token(TokenType.IDENTIFIER, 0.0),
                Token(TokenType.PARENTHESIS_LEFT),
                Token(TokenType.NUMBER, 4.0),
                Token(TokenType.PERMUTATION),
                Token(TokenType.NUMBER, 2.0),
                Token(TokenType.PARENTHESIS_RIGHT)
            ),
            "2(-1)" to listOf(
                Token(TokenType.NUMBER, 2.0),
                Token(TokenType.IMPLICIT_MULTIPLICATION),
                Token(TokenType.PARENTHESIS_LEFT),
                Token(TokenType.UNARY),
                Token(TokenType.NUMBER, 1.0),
                Token(TokenType.PARENTHESIS_RIGHT)
            ),
            "log(10, 100)" to listOf(
                Token(TokenType.IDENTIFIER, Function.LOG.ordinal.toDouble()),
                Token(TokenType.PARENTHESIS_LEFT),
                Token(TokenType.NUMBER, 10.0),
                Token(TokenType.SEPARATOR),
                Token(TokenType.NUMBER, 100.0),
                Token(TokenType.PARENTHESIS_RIGHT)
            ),
            "sin(<pi>" to listOf(
                Token(TokenType.IDENTIFIER, Function.SIN.ordinal.toDouble()),
                Token(TokenType.PARENTHESIS_LEFT),
                Token(TokenType.NUMBER, Math.PI),
                Token(TokenType.PARENTHESIS_RIGHT)
            ),
            "5!-2" to listOf(
                Token(TokenType.NUMBER, 5.0),
                Token(TokenType.FACTORIAL),
                Token(TokenType.SUBTRACTION),
                Token(TokenType.NUMBER, 2.0)
            ),
            "-2" to listOf(
                Token(TokenType.UNARY),
                Token(TokenType.NUMBER, 2.0)
            ),
            "Rec(2, 3" to listOf(
                Token(TokenType.IDENTIFIER, Function.REC.ordinal.toDouble()),
                Token(TokenType.PARENTHESIS_LEFT),
                Token(TokenType.NUMBER, 2.0),
                Token(TokenType.SEPARATOR),
                Token(TokenType.NUMBER, 3.0),
                Token(TokenType.PARENTHESIS_RIGHT)
            )
        )

        for ((input, expected) in tests) {
            val tokenizer = Tokenizer(input, emptyMap())
            val tokens = mutableListOf<Token>()
            while (true) {
                val nextToken = tokenizer.nextToken()?: break
                tokens.add(nextToken)
            }
            assertEquals(expected, tokens)
        }
    }

    @Test
    fun parse() {
        val tests = mapOf(
            "2. + 3*7" to 23.0,
            ".1 / 2.0^-4" to 1.6,
            "sin(4 P 2)" to sin(12.0),
            "2(4+3)/28(-1)" to -.5,
            "10÷6/3" to 5.0,
            "cos(0)" to 1.0,
            "log(10, 100^2)" to 4.0,
            "log(10^7)" to 7.0,
            "2Pol(2, 1)" to sqrt(5.0) * 2,
            "1° + 2ĝ" to Math.PI * 7.0 / 450,
            ".5sqrt((1+3), 625) - 4" to 0.5 * 5 - 4,
            "sqrt(81) + 1" to 10.0,
            "5!-2" to 118.0,
            "2.|3" to 2.333333333,
        )

        val parser = Parser(AngleUnit.RADIAN)
        for ((input, expected) in tests) {
            assertEquals(
                expected,
                parser.parse(input, emptyMap()),
                0.000001
            )
        }
    }

    @Test
    fun parseMultiValued() {
        val tests = mapOf(
            "Rec(1+1, 1-1" to listOf(2.0, 0.0)
        )

        val parser = Parser(AngleUnit.RADIAN)
        for ((input, expected) in tests) {
            assertEquals(
                expected,
                parser.parseMultiValuedExpr(input, emptyMap())
            )
        }
    }

}