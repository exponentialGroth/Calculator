package com.exponential_groth.calculator.editor.functionality

import com.exponential_groth.calculator.editor.parsableNonDeletableSeparator
import com.exponential_groth.calculator.editor.square
import com.exponential_groth.calculator.editor.texLeftAbs
import com.exponential_groth.calculator.editor.texLeftBracket
import com.exponential_groth.calculator.editor.texRightAbs

enum class Function(
    val parsableSymbol: String,
    val texSymbol: String
): MathExpression {
    SIN("sin", "\\sin"), SINH("sinh", "\\sinh"),
    ASIN("asin", "\\sin^{-1}"), ASINH("sin^{-1}", "\\sinh"),
    COS("cos", "\\cos"), COSH("cosh", "\\cosh"),
    ACOS("acos", "\\cos^{-1}"), ACOSH("cosh^{-1}", "\\cosh"),
    TAN("tan", "\\tan"), TANH("tanh", "\\tanh"),
    ATAN("atan", "\\tan^{-1}"), ATANH("tanh^{-1}", "\\tanh"),
    LOG("log", "log"), LN("ln", "ln"),
    LOGXY("log{", "log_{") {
        override fun addToParsable(l: ArrayList<String>, i: Int): Int {
            l.addAll(i, listOf(parsableSymbol, parsableNonDeletableSeparator, "}"))
            return i+1
        }
        override fun addToTex(l: ArrayList<String>, i: Int): Int {
            l.addAll(i, listOf(texSymbol, square, "}$texLeftBracket"))
            return i+1
        }
        override fun removeFromParsable(l: ArrayList<String>, i: Int) {
            repeat(indexOfExprEnd(l, i, true) - i + 1) {
                l.removeAt(i)
            }
            l.removeAt(i-1)
        }
        override fun removeFromTex(l: ArrayList<String>, i: Int) {
            repeat(indexOfClosingBracket(l, i) - i + 1) {
                l.removeAt(i)
            }
            l.removeAt(i-1)
        }
    },
    SQRT("sqrt{", "\\sqrt{") {
        override fun addToParsable(l: ArrayList<String>, i: Int): Int {
            l.add(i, "}")
            l.add(i, parsableSymbol)
            return i+1
        }
        override fun addToTex(l: ArrayList<String>, i: Int): Int {
            l.addAll(i, listOf(texSymbol, square, "}"))
            return i+1
        }

        override fun removeFromParsable(l: ArrayList<String>, i: Int) {
            l.removeAt(indexOfClosingBracket(l, i))
            l.removeAt(i-1)
        }
        override fun removeFromTex(l: ArrayList<String>, i: Int) {
            if (l[i] == square) l.removeAt(i)
            l.removeAt(indexOfClosingBracket(l, i))
            l.removeAt(i-1)
        }
    },
    CBRT("sqrt{", "\\sqrt[") {
        override fun addToParsable(l: ArrayList<String>, i: Int): Int {
            l.addAll(i, listOf(parsableSymbol, "3", parsableNonDeletableSeparator, "}"))
            return i+3
        }
        override fun addToTex(l: ArrayList<String>, i: Int): Int {
            l.addAll(i, listOf(texSymbol, "3", "]{", square, "}"))
            return i+3
        }
    },
    RT("sqrt{", "\\sqrt[") {
        override fun addToParsable(l: ArrayList<String>, i: Int): Int {
            l.add(indexOfExprEnd(l, i, parsable = true), "}")
            l.add(i, parsableNonDeletableSeparator)
            val startIndex = indexOfExprStart(l, i-1, parsable = true)
            l.add(startIndex, parsableSymbol)
            return if (startIndex == i) i+1 else i+2
        }
        override fun addToTex(l: ArrayList<String>, i: Int): Int {
            l.add(indexOfExprEnd(l, i, parsable = true), "}")
            l.add(i, "]{")
            val startIndex = indexOfExprStart(l, i-1, parsable = true)
            l.add(startIndex, texSymbol)
            return if (startIndex == i) i+1 else i+2
        }

        override fun removeFromParsable(l: ArrayList<String>, i: Int) {
            val separatorIndex = indexOfExprEnd(l, i, true, Operator.COMMA.precedence+1)
            l.removeAt(indexOfClosingBracket(l, separatorIndex+1))
            l.removeAt(separatorIndex)
            l.removeAt(i-1)
        }
        override fun removeFromTex(l: ArrayList<String>, i: Int) {
            val mid = indexOfClosingBracket(l, i, '[' to ']')
            l.removeAt(indexOfClosingBracket(l, mid+1))
            if (l[mid+1] == square) l.removeAt(mid+1)
            l.removeAt(mid)
            if (l[i] == square) l.removeAt(i-1)
            l.removeAt(i-1)
        }
    },
    POL("Pol", "Pol"), REC("Rec", "Rec"),
    ABS("abs{", texLeftAbs) {
        override fun addToParsable(l: ArrayList<String>, i: Int): Int {
            l.add(i, "}")
            l.add(i, parsableSymbol)
            return i+1
        }
        override fun addToTex(l: ArrayList<String>, i: Int): Int {
            l.addAll(i, listOf(texLeftAbs, square, texRightAbs))
            return i+1
        }

        override fun removeFromParsable(l: ArrayList<String>, i: Int) {
            l.removeAt(indexOfClosingBracket(l, i))
            l.removeAt(i-1)
        }
        override fun removeFromTex(l: ArrayList<String>, i: Int) {
            if (l.getOrNull(i) == square) {
                repeat(3) { l.removeAt(i-1) }
                return
            }
            l.removeAt(indexOfClosingVert(l, i))
            l.removeAt(i-1)
        }
    }
    ;

    override fun addToParsable(l: ArrayList<String>, i: Int): Int {
        l.add(i, "$parsableSymbol(")
        return i+1
    }

    override fun addToTex(l: ArrayList<String>, i: Int): Int {
        l.add(i, "$texSymbol$texLeftBracket")
        return i+1
    }

    override fun removeFromParsable(l: ArrayList<String>, i: Int) {
        l.removeAt(i-1)
    }

    override fun removeFromTex(l: ArrayList<String>, i: Int) {
        l.removeAt(i-1)
    }


    fun toHyperbolic() = when (this) {
        SIN -> SINH
        ASIN -> ASINH
        COS -> COSH
        ACOS -> ACOSH
        TAN -> TANH
        ATAN -> ATANH
        else -> this
    }
}