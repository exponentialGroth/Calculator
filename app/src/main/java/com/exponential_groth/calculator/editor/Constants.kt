package com.exponential_groth.calculator.editor

import com.exponential_groth.calculator.editor.functionality.Operator
import com.exponential_groth.calculator.editor.functionality.SingleSymbolExpression


const val parsableSeparator = ","
const val parsableNonDeletableSeparator = ";"
const val parsableFractionStart = "{{"
const val parsableMixedFractionStart = "{ "
const val parsableExponentEnd = " }"
const val cursor = "\\mid "
const val texSeparator = ", "
const val square = "\\square "
const val arrow = "\\to"
const val texSpace = " \\, "
const val texLeftAbs = "\\vert "
const val texRightAbs = " \\vert"
const val texLeftBracket = "("
const val texRightBracket = ")"

val exponentiationTypes = listOf(Operator.EXPONENTIATION, Operator.EXPONENTIATION__1, Operator.EXPONENTIATION_2, Operator.EXPONENTIATION_3)
val tokensWithoutSquare = listOf(texRightAbs, texRightBracket, "}",
    Operator.FACTORIAL.texSymbol, Operator.PERCENT.texSymbol, Operator.DEGREE.texSymbol, Operator.RADIAN.texSymbol, Operator.GRADIAN.texSymbol,
    SingleSymbolExpression.PI.tex, SingleSymbolExpression.E.tex, SingleSymbolExpression.POINT.tex)
val parsableNonDeletableTokens = listOf(Operator.FRACTION.parsableSymbol, parsableNonDeletableSeparator, "}", "}}", "+{")