package com.exponential_groth.calculator.editor

import com.exponential_groth.calculator.editor.functionality.Function
import com.exponential_groth.calculator.editor.functionality.MathExpression
import com.exponential_groth.calculator.editor.functionality.Operator
import com.exponential_groth.calculator.editor.functionality.SingleSymbolExpression
import com.exponential_groth.calculator.editor.functionality.indexOfClosingBracket


class Editor {
    private val texInput = arrayListOf<String>()
    private val parsableInput = arrayListOf<String>()
    private var texInputCursorPos = 0
    private var parsableInputCursorPos = 0
    private val texHistory = arrayListOf<List<String>>()
    private val parsableHistory = arrayListOf<List<String>>()
    private var pointInHistory = 0

    private val mixedFractionHandler = MixedFractionHandler()
    private val recurringDecimalHandler = RecurringDecimalHandler()
    private var saveTo: String? = null
    private var editM: Boolean? = null

    var hasEvaluated = false
        set(value) {
            if (parsableInput.isNotEmpty() && value) {
                parsableHistory.add(parsableInput.map { it })
                texHistory.add(texInput.map { it })
            }
            field = value
        }
    var hyperbolic = false
    val isEmpty get() = parsableInput.isEmpty()
    val hasMultivaluedExpression get() =
        (parsableInput.first().removeSuffix("(") in listOf(Function.POL.parsableSymbol, Function.REC.parsableSymbol)) &&
            indexOfClosingBracket(parsableInput, 1, '(' to ')') in listOf(-1, parsableInput.size-1)
    var onClear: () -> Unit = {}

    fun getTexInput(withCursor: Boolean = true) = if (!withCursor) texInput.joinToString("") else
        texInput.subList(0, texInputCursorPos).joinToString("").plus(cursor)
            .let { it.takeIf { texInputCursorPos == texInput.size }?:
            it.plus(texInput.subList(texInputCursorPos, texInput.size).joinToString("")) }

    fun getParsableInput() = parsableInput.joinToString("")


    private fun beforeAdd(expression: MathExpression? = null) {
        removeUnnecessarySquare()
        if (hasEvaluated) {
            ac()
            if (expression is Operator) {
                add("Ans")
            }
        }
    }

    /** Inserts the respective string representation of [expr] in both inputs.*/
    fun add(expr: SingleSymbolExpression): Boolean {
        if (mixedFractionHandler.isExpressionNotAllowed(expr) ||
            recurringDecimalHandler.isSingleSymbolExpressionNotAllowed(expr, texInput.getOrNull(texInputCursorPos-1))
            ) return false
        beforeAdd()
        parsableInput.add(parsableInputCursorPos, (expr.parsable))
        parsableInputCursorPos++
        texInput.add(texInputCursorPos, expr.tex)
        texInputCursorPos++
        mixedFractionHandler.update(expr)
        return true
    }

    fun add(variable: String): Boolean {
        if (mixedFractionHandler.inWholePart || recurringDecimalHandler.inRecurringPart) return false
        beforeAdd()
        texInput.add(texInputCursorPos, variable)
        parsableInput.add(parsableInputCursorPos, "<$variable>")
        texInputCursorPos++
        parsableInputCursorPos++
        return true
    }

    fun add(constant: String, texConstant: String = constant): Boolean {
        if (mixedFractionHandler.inWholePart || recurringDecimalHandler.inRecurringPart) return false
        beforeAdd()
        texInput.add(texInputCursorPos, texConstant)
        parsableInput.add(parsableInputCursorPos, "<$constant>")
        texInputCursorPos++
        parsableInputCursorPos++
        return true
    }

    fun add(e: MathExpression): Boolean {
        if (mixedFractionHandler.isExpressionNotAllowed(e) || recurringDecimalHandler.inRecurringPart) return false
        beforeAdd(e)
        if (e in exponentiationTypes && parsableInput.getOrNull(parsableInputCursorPos-1) == parsableExponentEnd) return false  // adding another exponent is only allowed with parentheses around the first power
        with (if (e is Function && hyperbolic) e.toHyperbolic() else e) {
            parsableInputCursorPos = addToParsable(parsableInput, parsableInputCursorPos)
            texInputCursorPos = addToTex(texInput, texInputCursorPos)
        }
        if (e == Operator.MIXED_FRACTION && parsableInput[parsableInputCursorPos-1] == parsableMixedFractionStart) {
            mixedFractionHandler.inWholePart = true
        } else if (e == Operator.PERIOD) {
            recurringDecimalHandler.inRecurringPart = true
        }
        return true
    }

    fun store(variable: String): Boolean {
        if (isEmpty || saveTo != null || editM != null) return false
        saveTo = variable
        texInput.add(texInput.size, "$arrow $variable")
        parsableInput.add(parsableInput.size, ">>$variable")
        hasEvaluated = true
        return true
    }

    fun editM(plus: Boolean): Boolean {
        if (isEmpty || saveTo != null || editM != null) return false
        editM = plus
        texInput.add(texInput.size, " M" + if (plus) '+' else '-')
        parsableInput.add(parsableInput.size, " M" + if (plus) '+' else '-')
        hasEvaluated = true
        return true
    }

    fun moveLeft(): Boolean {
        if (hasEvaluated) {
            texInputCursorPos = texInput.size
            parsableInputCursorPos = parsableInput.size
            pointInHistory = 0
            hasEvaluated = false
            return true
        }
        if (parsableInput.isEmpty()) return false
        if (parsableInputCursorPos == 0 && texInputCursorPos == 0) {
            parsableInputCursorPos = parsableInput.size
            texInputCursorPos = texInput.size
            return true
        }

        if (!onlyMoveTexCursorLeft) {
            do {
                parsableInputCursorPos--
                if (parsableInput[parsableInputCursorPos] == Operator.PERIOD.parsableSymbol)
                    recurringDecimalHandler.inRecurringPart = false
            } while (illegalParsableCursorPos)
        } else {
            recurringDecimalHandler.inRecurringPart = true  // because currently, that is the only reason for onlyMoveTexCursorRight to be false
        }
        do { texInputCursorPos-- } while (illegalTexCursorPos)

        if (mixedFractionHandler.inWholePart && parsableInput.getOrNull(parsableInputCursorPos) == parsableMixedFractionStart)
            mixedFractionHandler.onLeaveMixedFraction()
        else if (!mixedFractionHandler.inWholePart && parsableInput.getOrNull(parsableInputCursorPos) == "+{")
            mixedFractionHandler.inWholePart = true
        return true
    }
    fun moveRight(): Boolean {
        if (hasEvaluated) {
            texInputCursorPos = 0
            parsableInputCursorPos = 0
            pointInHistory = 0
            hasEvaluated = false
            return true
        }
        if (parsableInput.isEmpty()) return false
        if (texInputCursorPos == texInput.size && parsableInputCursorPos == parsableInput.size) {
            parsableInputCursorPos = 0
            texInputCursorPos = 0
            return true
        }

        if (!onlyMoveTexCursorRight) {
            do {
                if (parsableInput[parsableInputCursorPos] == Operator.PERIOD.parsableSymbol)
                    recurringDecimalHandler.inRecurringPart = true
                parsableInputCursorPos++
            } while (illegalParsableCursorPos)
        } else {
            recurringDecimalHandler.inRecurringPart = false  // because currently, that is the only reason for onlyMoveTexCursorRight to be false
        }
        do { texInputCursorPos++ } while (illegalTexCursorPos)

        if (!mixedFractionHandler.inWholePart && parsableInput.getOrNull(parsableInputCursorPos-1) == parsableMixedFractionStart)
            mixedFractionHandler.inWholePart = true
        else if (mixedFractionHandler.inWholePart && parsableInput.getOrNull(parsableInputCursorPos-1) == "+{")
            mixedFractionHandler.onLeaveMixedFraction()
        return true
    }

    fun moveUp(): Boolean {
        if (pointInHistory >= parsableHistory.size - 1) return false
        pointInHistory++
        texInput.clear()
        texInput.addAll(texHistory[(texHistory.size-1) - pointInHistory])
        parsableInput.clear()
        parsableInput.addAll(parsableHistory[(parsableHistory.size-1) - pointInHistory])
        return true
    }
    fun moveDown(): Boolean {
        if (pointInHistory <= 0) return false
        pointInHistory--
        texInput.clear()
        texInput.addAll(texHistory[(texHistory.size-1) - pointInHistory])
        parsableInput.clear()
        parsableInput.addAll(parsableHistory[(parsableHistory.size-1) - pointInHistory])
        return true
    }

    fun del(): Boolean {
        if (hasEvaluated || parsableInput.isEmpty()) return false
        if ((saveTo != null || editM != null) && texInputCursorPos == texInput.size) {
            saveTo = null
            editM = null
            texInput.removeLast()
            texInputCursorPos--
            parsableInput.removeLast()
            parsableInputCursorPos--
            return true
        }
        val preventDigitAfterRecurringDecimal = texInput.getOrNull(texInputCursorPos-2) == texRecurringPartEnd &&
                texInput.getOrNull(texInputCursorPos)?.let { it.length == 1 && it[0].isDigit() } == true
        if (preventDigitAfterRecurringDecimal) return false

        if (parsableInputCursorPos == 0) moveRight()
        val elToDel = parsableInput[parsableInputCursorPos-1]
        if (elToDel in parsableNonDeletableTokens || texInput[texInputCursorPos-1] == texRecurringPartEnd) {
            moveLeft()
            return true
        } else getExpressionTypeToDelete()?.also {
            it.removeFromParsable(parsableInput, parsableInputCursorPos)
            it.removeFromTex(texInput, texInputCursorPos)
        }?: run {
            parsableInput.removeAt(parsableInputCursorPos-1)
            texInput.removeAt(texInputCursorPos-1)
        }
        if (texInput.getOrNull(texInputCursorPos-1) == square) {  // exponentiation is the only expression that would need a subtraction of two from texInputCursorPos
            texInput.removeAt(texInputCursorPos-1)
            texInputCursorPos--
        }
        parsableInputCursorPos--
        texInputCursorPos--
        addNecessarySquare()
        if (elToDel == parsableMixedFractionStart) {
            mixedFractionHandler.onLeaveMixedFraction()
        } else if (mixedFractionHandler.inWholePart && elToDel == SingleSymbolExpression.POINT.parsable) {
            mixedFractionHandler.containsDecimalPoint = false
        } else if (mixedFractionHandler.inWholePart && texInput[texInputCursorPos] == square) {
            mixedFractionHandler.isWholePartEmpty = true
        } else if (elToDel == Operator.PERIOD.parsableSymbol) {
            recurringDecimalHandler.inRecurringPart = false
        }
        return true
    }

    fun ac(): Boolean {
        if (parsableInput.isEmpty()) return false
        parsableInput.clear()
        texInput.clear()
        parsableInputCursorPos = 0
        texInputCursorPos = 0
        pointInHistory = 0
        saveTo = null
        editM = null
        hasEvaluated = false
        mixedFractionHandler.onLeaveMixedFraction()
        onClear()
        return true
    }


    private val illegalParsableCursorPos get(): Boolean =  // I think there arent any illegal positions because I put the respective elements into a single string
        parsableInputCursorPos < 0 || parsableInputCursorPos > parsableInput.size

    private val illegalTexCursorPos get(): Boolean =
        texInputCursorPos != 0 && texInputCursorPos != texInput.size && texInput[texInputCursorPos-1] == square

    private val onlyMoveTexCursorRight get(): Boolean =
        texInput.getOrNull(texInputCursorPos) == texRecurringPartEnd

    private val onlyMoveTexCursorLeft get(): Boolean =
        texInput.getOrNull(texInputCursorPos-1) == texRecurringPartEnd


    private fun removeUnnecessarySquare() {  // only before adding something to the input
        if (texInput.getOrNull(texInputCursorPos) == square) texInput.removeAt(texInputCursorPos)
    }
    private fun addNecessarySquare() {
        if (parsableInputCursorPos !in 1 until parsableInput.size) return
        val before = parsableInput[parsableInputCursorPos-1].trim()
        val after = parsableInput[parsableInputCursorPos].trim()
        if (before.last() in listOf('{', parsableNonDeletableSeparator) && after.first() in listOf('}', parsableNonDeletableSeparator)
            || after.first() == '^'
            || before == "{" && after == "+{"
        ) {
            texInput.add(texInputCursorPos, square)
        }
    }

    private fun getExpressionTypeToDelete(): MathExpression? {
        return when (val input = parsableInput[parsableInputCursorPos-1].removeSuffix("(")) {
            parsableFractionStart -> Operator.FRACTION
            parsableMixedFractionStart -> Operator.MIXED_FRACTION
            else -> Operator.entries.find { it.parsableSymbol == input}
                ?: Function.entries.find { it.parsableSymbol == input }.takeUnless { it == Function.SQRT }  // because there are two versions of sqrt
                ?: when (texInput[texInputCursorPos-1]) {
                    Function.SQRT.texSymbol -> Function.SQRT
                    Function.RT.texSymbol -> Function.RT
                    else -> null
                }
        }
    }
}