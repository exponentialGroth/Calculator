package com.exponential_groth.calculator.editor.functionality

import com.exponential_groth.calculator.editor.parsableNonDeletableSeparator
import com.exponential_groth.calculator.editor.texLeftAbs
import com.exponential_groth.calculator.editor.texRightAbs

/**
 * Returns the index of the first element of the expression ending at [exprEnd].
 * Outside of brackets, the expression will include only operators with lower precedence than [prec] and implicit multiplication.
 * Fractions are not handled, so do not use a [prec] greater than [Operator.FRACTION.precedence].
 */
internal fun indexOfExprStart(l: List<String>, exprEnd: Int, parsable: Boolean, prec: Int = 5): Int {
    require(prec <= Operator.FRACTION.precedence)
    var openBrackets = 0
    val lowerPrecSymbols = Operator.entries.filter { it.precedence < prec }
        .map { if (parsable) it.parsableSymbol else it.texSymbol }
    for (i in exprEnd downTo 0) {
        val el = l[i]
        if (openBrackets == 0 && el in lowerPrecSymbols) return i+1
        openBrackets += el.count { it == '}' || it == ')' } - el.count { it == '{' || it == '(' }
        if (openBrackets < 0) return i+1
    }
    return 0
}

/**
 * Returns the successor of the index of the last element of the expression starting at [exprStart] and ending right before an operator with lower precedence than [prec] or the end of [l].
 * Outside of brackets, the expression will include only implicit multiplication and operators with lower precedence than [prec].
 * Fractions are not handled, so do not use a [prec] greater than [Operator.FRACTION.precedence].
 * @throws IllegalArgumentException if [prec] > [Operator.FRACTION.precedence]
 */
internal fun indexOfExprEnd(l: List<String>, exprStart: Int, parsable: Boolean, prec: Int = 5): Int {
    require(prec <= Operator.FRACTION.precedence)  // because fractions are not handled correctly (only operations without a symbol between brackets in a single list element are)
    var openBrackets = 0
    val lowerPrecSymbols = Operator.entries
        .filter { it.precedence < prec }
        .map(if (parsable) {it -> it.parsableSymbol} else {it -> it.texSymbol})
        .let { if (parsable) it.plus(parsableNonDeletableSeparator) else it }
    for (i in exprStart until l.size) {
        val el = l[i]
        if (openBrackets == 0 && el in lowerPrecSymbols) return i
        for (char in el) {
            if (char == '{' || char == '(') {
                openBrackets++
            } else if (char == '}' || char == ')') {
                if (openBrackets == 0) return i
                openBrackets--
            }
        }
    }
    return l.size
}

/**
 * @param i The last index of the num
 * @return The start of the numerical expression or i+1 if there is no number
 */
internal fun indexOfNumberStart(l: List<String>, i: Int): Int {
    var i = i
    while (i > 0 && l[i-1].let { it.length == 1 && (it.first() == '.' || it.first().isDigit()) }) i--
    return i
}

internal fun indexOfDigitsEnd(l: List<String>, i: Int) = (i until l.size).firstOrNull { index ->
    l[index].let { it.length != 1 || !it.first().isDigit() }
}?: l.size

/**
 * Returns the index of the bracket which closes the parenthesized expression containing [startIndex].
 */
internal fun indexOfClosingBracket(l: List<String>, startIndex: Int, brackets: Pair<Char, Char> = '{' to '}'): Int {
    var numOfOpenBrackets = 1
    for (i in startIndex until l.size) {
        for (c in l[i]) {
            when (c) {
                brackets.first -> numOfOpenBrackets++
                brackets.second -> numOfOpenBrackets--
            }
            if (numOfOpenBrackets == 0) return i
        }
    }
    return -1
}

internal fun indexOfClosingVert(l: List<String>, startIndex: Int): Int {
    var numOfVerts = 1
    for (i in startIndex until l.size) {
        when (l[i]) {
            texLeftAbs -> numOfVerts++
            texRightAbs -> numOfVerts--
        }
        if (numOfVerts == 0) return i
    }
    return -1
}