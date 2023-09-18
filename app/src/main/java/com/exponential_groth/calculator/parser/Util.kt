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


enum class ConstantsType {
    IMPORTANT, PARTICLE, QUANTUM_MECHANICS, OTHER
}
enum class Constant(val token: String, val value: Double, val type: ConstantsType, val texRepr: String = token) {  // values from https://physics.nist.gov/cuu/Constants/Table/allascii.txt
    SPEED_OF_LIGHT("c", 299792458.0, ConstantsType.IMPORTANT), PERMEABILITY("mu", 1.25663706212e-6, ConstantsType.IMPORTANT, "\\mu_0"),
    PERMITTIVITY("eps", 8.8541878128e-12, ConstantsType.IMPORTANT, "\\epsilon_0"), NEWTONIAN_CONSTANT_OF_GRAVITATION("G", 6.67430e-11, ConstantsType.IMPORTANT),
    ELECTRON_MASS("me", 9.1093837015e-31, ConstantsType.PARTICLE, "m_e"), NEUTRON_MASS("mn", 1.67492749804e-27, ConstantsType.PARTICLE, "m_n"), PROTON_MASS("mpr", 1.67262192369e-27, ConstantsType.PARTICLE, "m_{pr}"),
    ELEMENTARY_CHARGE("ee", 	1.602176634e-19, ConstantsType.PARTICLE, "e"),
    ELECTRON_SPECIFIC_CHARGE("qe", -1.75882001076e11, ConstantsType.PARTICLE, "q_e"), PROTON_SPECIFIC_CHARGE("qp", 9.5788331560e7, ConstantsType.PARTICLE, "q_p"),
    MUON_MASS("mm", 1.883531627e-28, ConstantsType.PARTICLE, "m_m"), DEUTERON_MASS("md", 3.3435837724e-27, ConstantsType.PARTICLE, "m_d"),
    PLANCK_CONSTANT("h", 6.62607015e-34, ConstantsType.QUANTUM_MECHANICS), PLANCK_MASS("mpl", 2.176434e-08, ConstantsType.QUANTUM_MECHANICS, "m_p"), PLANCK_LENGTH("lp", 1.616255e-35, ConstantsType.QUANTUM_MECHANICS, "l_p"), PLANCK_TIME("tp", 5.391247e-44, ConstantsType.QUANTUM_MECHANICS, "t_p"),
    RYDBERG_CONSTANT("ry", 10973731.56816, ConstantsType.QUANTUM_MECHANICS), RYDBERG_FREQUENCY("ryf", 3.2898419602508e+15, ConstantsType.QUANTUM_MECHANICS, "f_{Ry}"), RYDBERG_ENERGY_JOULE("ryj", 2.1798723611035e-18, ConstantsType.QUANTUM_MECHANICS, "E_{Ry}"), RYDBERG_ENERGY_EV("ryev", 13.605693122994, ConstantsType.QUANTUM_MECHANICS, "E_{Ry}"),
    ATOMIC_MASS_UNIT("u", 1.66053906660e-27, ConstantsType.OTHER), AVOGADRO_CONSTANT("NA", 6.02214076e+23, ConstantsType.OTHER, "N_A"),
    BOLTZMANN_CONSTANT("k", 1.380649e-23, ConstantsType.OTHER), FARADAY_CONSTANT("f", 96485.33212, ConstantsType.OTHER, "F"), MOLAR_GAS_CONSTANT("R", 8.314462618, ConstantsType.OTHER);
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

