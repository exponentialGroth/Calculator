package com.exponential_groth.calculator.editor.functionality

import com.exponential_groth.calculator.editor.texSpace
import com.exponential_groth.calculator.editor.parsableExponentEnd
import com.exponential_groth.calculator.editor.parsableFractionStart
import com.exponential_groth.calculator.editor.parsableMixedFractionStart
import com.exponential_groth.calculator.editor.parsableSeparator
import com.exponential_groth.calculator.editor.parsableTokensWithoutSquare
import com.exponential_groth.calculator.editor.square
import com.exponential_groth.calculator.editor.texRecurringPartEnd
import com.exponential_groth.calculator.editor.texSeparator
import com.exponential_groth.calculator.editor.tokensWithoutSquare

enum class Operator(
    val parsableSymbol: String,
    val texSymbol: String,
    internal val precedence: Int,
): MathExpression {
    COMMA(parsableSeparator, texSeparator, 0),
    PLUS("+", "+", 1),
    MINUS("-", "-", 1),
    MULTIPLICATION("*", " \\times ", 2),
    DIVISION("÷", " \\div ", 2),
    COMB("C", " C ", 3),
    PERM("P", " P ", 3),
    FACTORIAL("!", "!", 8),
    PERCENT("%", "\\% ", 8),
    DEGREE("°", "°", 8),
    RADIAN("®", "^r", 8),
    GRADIAN("ĝ", "^g", 8),
    EXPONENTIATION("^{", "^{", 8) {
        override fun addToParsable(l: ArrayList<String>, i: Int): Int = addExponentiationToParsable(l, i, emptyList())
        override fun addToTex(l: ArrayList<String>, i: Int): Int = addExponentiationToTex(l, i, emptyList())
        override fun removeFromParsable(l: ArrayList<String>, i: Int) = removeExponentiation(l, i)  // only overwritten in this exponentiation because
        override fun removeFromTex(l: ArrayList<String>, i: Int) = removeExponentiation(l, i)       // the editor will always call this one for removal
    },
    EXPONENTIATION__1("^{", "^{", 8) {
        override fun addToParsable(l: ArrayList<String>, i: Int): Int = addExponentiationToParsable(l, i, listOf("-", "1"))
        override fun addToTex(l: ArrayList<String>, i: Int): Int = addExponentiationToTex(l, i, listOf("-", "1"))
    },
    EXPONENTIATION_2("^{", "^{", 8) {
        override fun addToParsable(l: ArrayList<String>, i: Int): Int = addExponentiationToParsable(l, i, listOf("2"))
        override fun addToTex(l: ArrayList<String>, i: Int): Int = addExponentiationToTex(l, i, listOf("2"))
    },
    EXPONENTIATION_3("^{", "^{", 8) {
        override fun addToParsable(l: ArrayList<String>, i: Int): Int = addExponentiationToParsable(l, i, listOf("3"))
        override fun addToTex(l: ArrayList<String>, i: Int): Int = addExponentiationToTex(l, i, listOf("3"))
    },
    EXPONENTIATION_10("^{", "^{", 8) {  // Casio fx would add just "10^{" (the base cannot be changed afterwards)
        override fun addToParsable(l: ArrayList<String>, i: Int): Int {
            l.addAll(i, listOf("1", "0"))
            return addExponentiationToParsable(l, i+2, emptyList())
        }
        override fun addToTex(l: ArrayList<String>, i: Int): Int {
            l.addAll(i, listOf("1", "0"))
            return addExponentiationToTex(l, i+2, emptyList())
        }
    },
    EXPONENTIATION_E("^{", "^{", 8) {  // Casio fx would add just "e^{" (the base cannot be changed afterwards)
        override fun addToParsable(l: ArrayList<String>, i: Int): Int { // and would not pull the following expression into the exponent
            l.add(i, SingleSymbolExpression.E.parsable)
            return addExponentiationToParsable(l, i+1, emptyList())
        }
        override fun addToTex(l: ArrayList<String>, i: Int): Int {
            l.add(i, SingleSymbolExpression.E.tex)
            return addExponentiationToTex(l, i+1, emptyList())
        }
    },
    FRACTION("}/{", "\\frac{", 7) {
        override fun addToParsable(l: ArrayList<String>, i: Int): Int {
            l.add(indexOfExprEnd(l, i, parsable = true), "}}")
            l.add(i, parsableSymbol)
            val start = indexOfExprStart(l, i-1, parsable = true)
            l.add(start, parsableFractionStart)
            return if (start == i) i+1 else i+2
        }
        override fun addToTex(l: ArrayList<String>, i: Int): Int {
            l.add(indexOfExprEnd(l, i, parsable = false), "}")
            l.add(i, "}{")
            val start = indexOfExprStart(l, i-1, parsable = false)
            l.add(start, texSymbol)
            return if (start == i) i+1 else i+2
        }

        override fun removeFromParsable(l: ArrayList<String>, i: Int) {
            val mid = indexOfClosingBracket(l, i)
            l.removeAt(indexOfClosingBracket(l, mid+1))
            l.removeAt(mid)
            l.removeAt(i-1)
        }
        override fun removeFromTex(l: ArrayList<String>, i: Int) {
            val mid = indexOfClosingBracket(l, i)
            l.removeAt(indexOfClosingBracket(l, mid+1))
            if (l.getOrNull(mid+1) == square) l.removeAt(mid+1)
            l.removeAt(mid)
            if (l.getOrNull(i) == square) l.removeAt(i)
            l.removeAt(i-1)
        }
    },
    MIXED_FRACTION("}/{", "\\frac{", 7) {  // Note that the whole part only contains digits, max 1 '.', max 1 '-' in front
        override fun addToParsable(l: ArrayList<String>, i: Int): Int {
            val start = indexOfNumberStart(l, i)
            val end = indexOfExprEnd(l, i, parsable = true)
            l.add(end, "}}")
            l.add(end.takeIf { start != i }?: i, parsableSymbol)
            l.add(i, "+{")
            l.add(start, parsableMixedFractionStart)
            return i+1 + if (start != i) 1 else 0
        }
        override fun addToTex(l: ArrayList<String>, i: Int): Int {
            val start = indexOfNumberStart(l, i)
            val hasNumInfront = i > 0 && start != i
            val end = indexOfExprEnd(l, i, parsable = false)
            l.add(end, "}")
            if (hasNumInfront) {
                l.add(end, square)
                l.add(end, "}{")
                if (end == i) l.add(end, square)
            } else {
                if (end == i) l.add(end, square)
                l.add(i, "}{")
                l.add(i, square)
            }
            l.add(i, texSymbol)
            if (!hasNumInfront) {
                l.add(i, square)
                l.add(i, texSpace)
                return i+1
            }
            l.add(start, texSpace)
            return i+2
        }
        override fun removeFromParsable(l: ArrayList<String>, i: Int) {
            val fractionStart = (i until l.size).find {
                l[it] == "+{"
            }!!
            val mid = indexOfClosingBracket(l, fractionStart+1)
            l.removeAt(indexOfClosingBracket(l, mid+1))
            l.removeAt(mid)
            l.removeAt(fractionStart)
            l.removeAt(i-1)
        }
        override fun removeFromTex(l: ArrayList<String>, i: Int) {
            val fractionStart = (i until l.size).find {
                l[it] == texSymbol
            }!!
            val mid = indexOfClosingBracket(l, fractionStart+1)
            l.removeAt(indexOfClosingBracket(l, mid+1))
            if (l.getOrNull(mid+1) == square) l.removeAt(mid+1)
            l.removeAt(mid)
            if (l.getOrNull(mid-1) == square) l.removeAt(mid-1)
            l.removeAt(fractionStart)
            if (l.getOrNull(i) == square) l.removeAt(i)
            l.removeAt(i-1)
        }
    },
    PERIOD("|", "\\overline{", 9) {
        override fun addToTex(l: ArrayList<String>, i: Int): Int {
            val end = indexOfDigitsEnd(l, i)
            l.add(end, texRecurringPartEnd)
            if (end == i) l.add(i, square)
            l.add(i, texSymbol)
            return i+1
        }
        override fun removeFromTex(l: ArrayList<String>, i: Int) {
            l.removeAt(indexOfClosingBracket(l, i))
            if (l[i] == square) l.removeAt(i)
            l.removeAt(i-1)
        }
    }
    ;

    override fun addToParsable(l: ArrayList<String>, i: Int): Int {
        l.add(i, parsableSymbol)
        return i+1
    }
    override fun addToTex(l: ArrayList<String>, i: Int): Int {
        l.add(i, texSymbol)
        return i+1
    }

    override fun removeFromParsable(l: ArrayList<String>, i: Int) {
        l.removeAt(i-1)
    }

    override fun removeFromTex(l: ArrayList<String>, i: Int) {
        l.removeAt(i-1)
    }


    internal fun addExponentiationToParsable(l: ArrayList<String>, i: Int, exponent: List<String>): Int {
        val end = if (exponent.isEmpty()) indexOfExprEnd(l, i, true) else i
        l.add(end, parsableExponentEnd)
        l.addAll(i, exponent)
        l.add(i, parsableSymbol)
        return if (i == 0 || l[i-1] !in parsableTokensWithoutSquare && !l[i-1].let { it.length == 1 && it.first().isDigit()})
            i
        else
            i + 1 + exponent.size.let { if (it != 0) it+1 else 0 }   // if (x⁻¹,x², x³) after exponentiation else start of exponent
    }
    internal fun addExponentiationToTex(l: ArrayList<String>, i: Int, exponent: List<String>): Int {
        val end = if (exponent.isEmpty()) indexOfExprEnd(l, i, false) else i
        l.add(end, "}")
        l.addAll(i, exponent)
        l.add(i, texSymbol)
        if (exponent.isEmpty()) addSquareIfNecessary(l, end+1)
        val cursorInBase = addSquareIfNecessary(l, i)
        return if (cursorInBase)
            i
        else
            i + 1 + exponent.size.let { if (it != 0) it+1 else 0 }  // if (x⁻¹,x², x³) after exponentiation else start of exponent
    }
    internal fun removeExponentiation(l: ArrayList<String>, i: Int) {
        if (l[i] == square) {
            repeat(2) { l.removeAt(i-1) }
        } else l.removeAt(indexOfClosingBracket(l, i))
        l.removeAt(i-1)
    }

    private fun addSquareIfNecessary(l: ArrayList<String>, i: Int): Boolean {
        if (i == 0 ||
            l[i-1] !in tokensWithoutSquare
            && !l[i-1].let { it.length == 1 && it.first().isDigit()}
        ) {
            l.add(i, square)
            return true
        }
        return false
    }
}