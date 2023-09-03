package com.exponential_groth.calculator.editor

import com.exponential_groth.calculator.editor.functionality.SingleSymbolExpression

internal class RecurringDecimalHandler {
    var inRecurringPart = false

    fun isSingleSymbolExpressionNotAllowed(e: SingleSymbolExpression, texElLeft: String?): Boolean =
        inRecurringPart && !e.isDigit() || !inRecurringPart && e.isDigit() && texElLeft == texRecurringPartEnd
}