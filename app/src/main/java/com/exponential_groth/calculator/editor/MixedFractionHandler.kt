package com.exponential_groth.calculator.editor

import com.exponential_groth.calculator.editor.functionality.MathExpression
import com.exponential_groth.calculator.editor.functionality.SingleSymbolExpression

internal class MixedFractionHandler {
    var inWholePart = false
    var containsDecimalPoint = false
    var isWholePartEmpty = true

    fun isExpressionNotAllowed(expr: SingleSymbolExpression): Boolean =
        inWholePart && (containsDecimalPoint && expr == SingleSymbolExpression.POINT
                || !expr.isDigit() && expr != SingleSymbolExpression.POINT)

    fun isExpressionNotAllowed(expr: MathExpression): Boolean =
        inWholePart

    fun onLeaveMixedFraction() {
        inWholePart = false
        containsDecimalPoint = false
        isWholePartEmpty = true
    }

    fun update(expr: SingleSymbolExpression) {
        containsDecimalPoint = inWholePart && expr == SingleSymbolExpression.POINT || !inWholePart  // I just saw a video about branch less programming :)
        isWholePartEmpty = !inWholePart
    }
}