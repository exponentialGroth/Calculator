package com.exponential_groth.calculator.parser

import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.acosh
import kotlin.math.asin
import kotlin.math.asinh
import kotlin.math.atan
import kotlin.math.atanh
import kotlin.math.cos
import kotlin.math.cosh
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sinh
import kotlin.math.sqrt
import kotlin.math.tan
import kotlin.math.tanh

class Parser(var angleUnit: AngleUnit) {
    private lateinit var input: String
    private lateinit var tokenizer: Tokenizer
    private var lookAhead: Token? = null

    fun parseMultiValuedExpr(input: String, variables: Map<String, Double>): List<Double> {
        this.input = input
        tokenizer = Tokenizer(input, variables)
        lookAhead = tokenizer.nextToken()?: throw IllegalExpressionException("Empty input")
        val function = Function.fromNumber(lookAhead?.value!!)
        eat(TokenType.IDENTIFIER)
        eat(TokenType.PARENTHESIS_LEFT)
        val expr1 = expression()
        eat(TokenType.SEPARATOR)
        val expr2 = expression()
        return evalMultiValuedFunction(function, expr1, expr2)
    }

    fun parse(input: String, variables: Map<String, Double>): Double {
        this.input = input
        tokenizer = Tokenizer(input, variables)
        lookAhead = tokenizer.nextToken()?: throw IllegalExpressionException("Empty input")
        return expression()
    }

    private fun eat(tokenType: TokenType): Token {
        val token = lookAhead?: throw IllegalExpressionException()
        if (token.type != tokenType) throw UnexpectedTokenException("$token")
        lookAhead = tokenizer.nextToken()
        return token
    }

    private fun getPrecedence(type: TokenType?): Int = when (type) {
        null -> 0
        else -> type.prec?: 0
    }

    private fun evalMultiValuedFunction(f: Function, vararg params: Double): List<Double> {
        return when (f) {
            Function.POL -> {
                val r = sqrt(params[0].pow(2) + params[1].pow(2))
                val theta = asin(params[1] / r)
                listOf(r, theta)
            }
            Function.REC -> {
                val x = params[0] * cos(params[1].convert(angleUnit, AngleUnit.RADIAN))
                val y = params[0] * sin(params[1].convert(angleUnit, AngleUnit.RADIAN))
                listOf(x, y)
            }
            else -> throw IllegalExpressionException()
        }
    }

    private fun evalFunction(f: Function, vararg x: Double): Double {
        val result = when (f) {
            Function.SIN -> sin(x[0].convert(angleUnit, AngleUnit.RADIAN))
            Function.ASIN -> asin(x[0]).convert(AngleUnit.RADIAN, angleUnit)
            Function.SINH -> sinh(x[0])
            Function.ASINH -> asinh(x[0])
            Function.COS -> cos(x[0].convert(angleUnit, AngleUnit.RADIAN))
            Function.ACOS -> acos(x[0]).convert(AngleUnit.RADIAN, angleUnit)
            Function.COSH -> cosh(x[0])
            Function.ACOSH -> acosh(x[0])
            Function.TAN -> tan(x[0].convert(angleUnit, AngleUnit.RADIAN))
            Function.ATAN -> atan(x[0]).convert(AngleUnit.RADIAN, angleUnit)
            Function.TANH -> tanh(x[0])
            Function.ATANH -> atanh(x[0])
            Function.LN -> ln(x[0])
            Function.LOG -> if (x.size == 1) log10(x[0]) else log2(x[1]) / log2(x[0]) // always two arguments, because of MainActivity and FUNCTION.LOG.arguments == 2
            Function.SQRT -> if (x.size == 1) sqrt(x[0]) else x[1].pow(1.0 / x[0])
            Function.ABS -> abs(x[0])
            Function.POL -> sqrt(x[0].pow(2) + x[1].pow(2))
            Function.REC -> x[0] * cos(x[1])
        }
        if (result.isNaN()) throw MathException(when (f) {
            Function.ASIN -> MathExceptionType.DOMAIN_ASIN
            Function.ACOS -> MathExceptionType.DOMAIN_ACOS
            Function.ACOSH -> MathExceptionType.DOMAIN_ACOSH
            Function.ATANH -> MathExceptionType.DOMAIN_ATANH
            Function.LN, Function.LOG -> MathExceptionType.DOMAIN_LOGARITHM
            Function.SQRT -> MathExceptionType.DOMAIN_SQRT
            Function.POL -> MathExceptionType.DOMAIN_EXPONENTIATION
            else -> null
        })
        if (result.isInfinite()) throw MathException(MathExceptionType.INFINITY)
        return result
    }


    private fun expression(prec: Int = 0): Double {
        var left = prefix()
        while (prec < getPrecedence(lookAhead?.type)) {
            left = infix(left, lookAhead!!.type)
        }
        return left
    }

    private fun prefix(): Double {
        return when (lookAhead?.type) {
            TokenType.PARENTHESIS_LEFT -> parenthesizedExpression()
            TokenType.UNARY -> unaryExpression()
            TokenType.IDENTIFIER -> functionExpression()
            else -> eat(TokenType.NUMBER).value!!
        }
    }

    private fun infix(left: Double, operatorType: TokenType): Double {
        val token = eat(operatorType)
        val newPrec = operatorType.prec?: throw IllegalExpressionException("wrong token for infix")
        val result = when (token.type) {
            TokenType.ADDITION -> left + expression(newPrec)
            TokenType.SUBTRACTION -> left - expression(newPrec)
            TokenType.MULTIPLICATION, TokenType.IMPLICIT_MULTIPLICATION -> left * expression(newPrec)
            TokenType.DIVISION, TokenType.FRACTION -> left / expression(newPrec)
            TokenType.PERMUTATION -> {
                val right = expression(newPrec)
                val delta = 0.0000001
                if (abs(left - left.roundToInt()) >= delta || abs(right - right.roundToInt()) >= delta)
                    Double.NaN
                else
                    perm(left.roundToInt(), right.roundToInt()).toDouble()
            }
            TokenType.COMBINATION -> {
                val right = expression(newPrec)
                val delta = 0.0000001
                if (abs(left - left.roundToInt()) >= delta || abs(right - right.roundToInt()) >= delta)
                    Double.NaN
                else
                    comb(left.roundToInt(), right.roundToInt()).toDouble()
            }
            TokenType.FACTORIAL -> {
                val delta = 0.0000001
                if (abs(left - left.roundToInt()) >= delta)
                    Double.NaN
                else
                    factorial(left.roundToInt())
            }
            TokenType.PERCENT -> left * 0.01
            TokenType.DEGREE -> left.convert(AngleUnit.DEGREE, angleUnit)
            TokenType.RADIAN -> left.convert(AngleUnit.RADIAN, angleUnit)
            TokenType.GRADIAN -> left.convert(AngleUnit.GRADIAN, angleUnit)
            TokenType.EXPONENTIATION -> left.pow(expression(newPrec-1)) // -1 because of right associativity
            else -> throw IllegalExpressionException("wrong token for infix")
        }
        if (result.isNaN()) throw MathException(when (token.type) {
            TokenType.PERMUTATION -> MathExceptionType.DOMAIN_PERM
            TokenType.COMBINATION -> MathExceptionType.DOMAIN_COMB
            TokenType.FACTORIAL -> {
                MathExceptionType.DOMAIN_FACTORIAL
            }
            TokenType.EXPONENTIATION -> MathExceptionType.DOMAIN_EXPONENTIATION
            else -> null
        })
        if (result.isInfinite()) throw MathException(MathExceptionType.INFINITY)
        return result
    }


    private fun parenthesizedExpression(): Double {
        eat(TokenType.PARENTHESIS_LEFT)
        val expr = expression()
        eat(TokenType.PARENTHESIS_RIGHT)
        return expr
    }

    private fun unaryExpression(): Double {
        eat(TokenType.UNARY)
        return -expression(getPrecedence(TokenType.UNARY))
    }

    /** @throws IllegalExpressionException when there is no parenthesis after the identifier*/
    private fun functionExpression(): Double {
        val id = eat(TokenType.IDENTIFIER).value?.let { Function.fromNumber(it) }?: throw IllegalExpressionException()
        if (lookAhead?.type != TokenType.PARENTHESIS_LEFT) throw IllegalExpressionException(lookAhead?.toString())

        if (id.arguments == ONE_OR_TWO_ARGUMENTS || id.arguments == 2) {
            eat(TokenType.PARENTHESIS_LEFT)
            val expr1 = if (lookAhead?.type == TokenType.PARENTHESIS_LEFT) {
                parenthesizedExpression()
            } else expression()

            try {
                eat(TokenType.SEPARATOR)
            } catch (e: UnexpectedTokenException) {  // has only one argument
                eat(TokenType.PARENTHESIS_RIGHT)
                return evalFunction(id, expr1)
            }

            val expr2 = if (lookAhead?.type == TokenType.PARENTHESIS_LEFT) {
                parenthesizedExpression()
            } else expression()
            eat(TokenType.PARENTHESIS_RIGHT)
            return evalFunction(id, expr1, expr2)
        }
        val expr = parenthesizedExpression()
        return evalFunction(id, expr)
    }


    private fun perm(n: Int, k: Int): Long {
        if (n <= 0 || k < 0 || n < k) throw MathException(MathExceptionType.DOMAIN_PERM)
        return (n-k + 1..n).fold(1L) {acc, i -> acc * i}
    }

    private fun comb(n: Int, k: Int): Long {
        if (n <= 0 || k < 0 || n < k) throw MathException(MathExceptionType.DOMAIN_COMB)
        return perm(n, k) / (2..k).fold(1L) { acc, i -> acc * i }
    }

    private fun factorial(n: Int): Double {
        if (n !in 0..107) throw MathException(MathExceptionType.DOMAIN_FACTORIAL)
        if (n == 0 || n == 1) return 1.0
        var result = 1.0
        for (i in 2..n) {
            result *= i
        }
        return result
    }
}