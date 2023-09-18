package com.exponential_groth.calculator.parser


internal class Tokenizer(
    private val input: String,
    private val variables: Map<String, Double>,
    private val constants: Map<String, Double> = Constant.values().associate { it.token to it.value }.plus(listOf("pi" to Math.PI, "e" to Math.E))
) {
    private var cursor = 0
    private var prevType: TokenType? = null

    private val hasMoreTokens get() = cursor < input.length
    private var openBrackets = 0

    private fun match(regex: Regex): String? {
        val matched = regex.matchAt(input, cursor)?: return null
        return matched.value
    }

    private fun getTokenValue(match: String, type: TokenType): Double? {
        if (type == TokenType.NUMBER) return getNumberFromMatch(match)
        if (type != TokenType.IDENTIFIER) return match.toDoubleOrNull()
        return try {
            Function.valueOf(match.uppercase()).ordinal.toDouble()
        } catch (e: IllegalArgumentException) {
            -1.0
        }
    }

    /** Returns null if there is not a next token.*/
    fun nextToken(): Token? {
        if (!hasMoreTokens && openBrackets > 0) {
            openBrackets--
            return Token(TokenType.PARENTHESIS_RIGHT)
        }
        if (!hasMoreTokens) return null
        for ((regex, type) in TokenSpec.entries) {
            val match = match(regex)?: continue
            val actualType = type/*?.let { handleUnary(it) }*/?: run { // space characters
                cursor += match.length
                return nextToken()
            }
            if (usesImplicitMultiplication(actualType)) {
                prevType = TokenType.IMPLICIT_MULTIPLICATION
                return Token(TokenType.IMPLICIT_MULTIPLICATION, null)
            }
            when (actualType) {
                TokenType.PARENTHESIS_LEFT -> openBrackets++
                TokenType.PARENTHESIS_RIGHT -> openBrackets--
                else -> {}
            }
            cursor += match.length
            val tokenValue = getTokenValue(match, actualType)
            prevType = actualType
            return Token(actualType, tokenValue)
        }
        throw IllegalExpressionException("Unexpected token: ${input[cursor]}")
    }

/*    private fun handleUnary(newType: TokenType): TokenType {
        if (newType != TokenType.SUBTRACTION) return newType
        return if (prevType in listOf(TokenType.PARENTHESIS_RIGHT, TokenType.NUMBER))
            TokenType.SUBTRACTION
        else
            TokenType.UNARY
    }*/

    private fun usesImplicitMultiplication(type: TokenType): Boolean =
        (prevType == TokenType.PARENTHESIS_RIGHT || prevType == TokenType.NUMBER) &&
                (type == TokenType.PARENTHESIS_LEFT || type == TokenType.NUMBER || type == TokenType.IDENTIFIER)

    private fun getNumberFromMatch(match: String): Double? {
        if (match.startsWith("<")) return match.dropLast(1).drop(1).let { constants[it]?: variables[it] }  // variable or constant
        val periodSymbolIndex = match.indexOf('|')
        if (periodSymbolIndex != -1) {  // periodic number
            return repeatingDecimalToDouble(match)
        }
        return match.toDouble()
    }
}