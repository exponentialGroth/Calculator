package com.exponential_groth.calculator.parser

import kotlin.math.pow

internal enum class TokenType(val s: String, val prec: Int?) {
    PARENTHESIS_LEFT("(", null),
    PARENTHESIS_RIGHT(")", null),
    NUMBER("NUMBER", null),
    SEPARATOR(",", null),
    ADDITION("+", 1),
    SUBTRACTION("-", 1),
    MULTIPLICATION("*", 2),
    DIVISION("÷", 2),
    PERMUTATION("P", 3),
    COMBINATION("C", 3),
    IMPLICIT_MULTIPLICATION("$", 4),
    UNARY("-", 6),
    FRACTION("/", 7),
    FACTORIAL("!", 8),
    PERCENT("%", 8),
    DEGREE("°", 8),
    RADIAN("®", 8),
    GRADIAN("ĝ", 8),
    EXPONENTIATION("^", 8),
    IDENTIFIER("", 9),
}

internal val TokenSpec = mapOf(
    Regex("\\s+") to null,
    Regex("[,;]") to TokenType.SEPARATOR,
    Regex("(\\d+\\.\\d*\\|\\d+|\\d+(\\.\\d*)?|\\.\\d*\\|?\\d+)|<([A-Z]|Ans|[a-z]+)>") to TokenType.NUMBER,
    Regex("P(?!ol)") to TokenType.PERMUTATION, // (?![[:alpha:]])
    Regex("C") to TokenType.COMBINATION,
    Regex("!") to TokenType.FACTORIAL,
    Regex("%") to TokenType.PERCENT,
    Regex("°") to TokenType.DEGREE,
    Regex("®") to TokenType.RADIAN,
    Regex("ĝ") to TokenType.GRADIAN,
    Regex("\\w+") to TokenType.IDENTIFIER,
    Regex("\\+") to TokenType.ADDITION,
    Regex("^-|(?<=[,;({PC+\\-*÷/^])-") to TokenType.UNARY,
    Regex("-") to TokenType.SUBTRACTION,
    Regex("\\*") to TokenType.MULTIPLICATION,
    Regex("÷") to TokenType.DIVISION,
    Regex("/") to TokenType.FRACTION,
    Regex("\\^") to TokenType.EXPONENTIATION,
    Regex("[({]") to TokenType.PARENTHESIS_LEFT,
    Regex("[)}]") to TokenType.PARENTHESIS_RIGHT,
)

internal data class Token(
    val type: TokenType,
    val value: Double? = null
)


internal const val ONE_OR_TWO_ARGUMENTS = -2

internal enum class Function(val arguments: Int = 1) {
    SIN, ASIN, SINH, ASINH,
    COS, ACOS, COSH, ACOSH,
    TAN, ATAN, TANH, ATANH,
    LN, LOG(ONE_OR_TWO_ARGUMENTS), SQRT(ONE_OR_TWO_ARGUMENTS),
    ABS, REC(2), POL(2);

    companion object {
        private val values = values().toList()
        fun fromNumber(d: Number) = values[d.toInt()]
    }
}


enum class AngleUnit {
    DEGREE, RADIAN, GRADIAN;
}

fun degreeToRadian(angle: Double) = angle * Math.PI / 180
fun radianToGradian(angle: Double) = angle * 200 / Math.PI
fun gradianToDegree(angle: Double) = angle * 0.9

fun Double.convert(from: AngleUnit, to: AngleUnit): Double {
    if (from == to) return this
    return when (to) {
        AngleUnit.DEGREE -> gradianToDegree(convert(from, AngleUnit.GRADIAN))
        AngleUnit.RADIAN -> degreeToRadian(convert(from, AngleUnit.DEGREE))
        AngleUnit.GRADIAN -> radianToGradian(convert(from, AngleUnit.RADIAN))
    }
}



fun repeatingDecimalToDouble(repeatingDecimal: String): Double {  // always positive
    val periodSignPos = repeatingDecimal.indexOf('|')
    val notRepeatingPartStr = repeatingDecimal.substring(0, periodSignPos)
    val notRepeatingPart = notRepeatingPartStr.toDouble() // suffix "." is not a problem
    val repeatingPartStr = repeatingDecimal.substring(periodSignPos+1, repeatingDecimal.length)

    var digitsAfterDecimalPoint = periodSignPos - notRepeatingPartStr.indexOf('.') - 1
    var repeatingPart = repeatingPartStr.toDouble() * 0.1.pow(repeatingPartStr.length + digitsAfterDecimalPoint)
    var finalValue = notRepeatingPart
    do {
        finalValue += repeatingPart
        repeatingPart *= 0.1.pow(repeatingPartStr.length)
        digitsAfterDecimalPoint += repeatingPartStr.length
    } while (digitsAfterDecimalPoint <= 18)
    return finalValue
}



/** Thrown when the input to parse is illegal
 * @param msg the tokens that caused the error*/
open class IllegalExpressionException(msg: String? = null): Exception(msg)
class UnexpectedTokenException(token: String): IllegalExpressionException(token)

class MathException(type: MathExceptionType?): Exception(type?.toString()?:"")
enum class MathExceptionType {
    INFINITY,
    DOMAIN_ASIN, DOMAIN_ACOS, DOMAIN_ACOSH, DOMAIN_ATANH, DOMAIN_LOGARITHM, DOMAIN_SQRT, DOMAIN_EXPONENTIATION, DOMAIN_PERM, DOMAIN_COMB, DOMAIN_FACTORIAL
}

