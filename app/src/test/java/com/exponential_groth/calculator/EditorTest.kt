package com.exponential_groth.calculator

import com.exponential_groth.calculator.editor.Editor
import com.exponential_groth.calculator.editor.functionality.Function
import com.exponential_groth.calculator.editor.functionality.Operator
import com.exponential_groth.calculator.editor.functionality.SingleSymbolExpression
import org.junit.Assert.assertEquals
import org.junit.Test

class EditorTest {

    @Test
    fun add() {
        val editor = Editor()
        with (editor) {
            add(SingleSymbolExpression._2)
            add(Operator.FRACTION)
            add(Function.SQRT)
            add(SingleSymbolExpression._3)
        }
        assertEquals("{{2}/{sqrt{3}}}", editor.getParsableInput().filter { it != ' ' })
    }

    @Test
    fun nestedFractions() {
        val editor = Editor()
        with (editor) {
            add(Operator.FRACTION)
            moveRight()
            add(Operator.FRACTION)
        }
        assertEquals("\\frac{\\square}{\\frac{\\square}{\\square}}", editor.getTexInput(withCursor = false).filter { it != ' ' })
        assertEquals("{{}/{{{}/{}}}}", editor.getParsableInput().filter { it != ' ' })

        with(editor) {
            ac()
            add(SingleSymbolExpression._7)
            add(SingleSymbolExpression._1)
            moveLeft()
            add(Operator.FRACTION)
            add(Operator.FRACTION)
            add(SingleSymbolExpression._9)
        }
        assertEquals("\\frac{7}{\\frac{9}{1}}", editor.getTexInput(withCursor = false).filter { it != ' ' })
        assertEquals("{{7}/{{{9}/{1}}}}", editor.getParsableInput().filter { it != ' ' })

    }

    @Test
    fun ansSquaredWithoutSquare() {
        val editor = Editor()
        with(editor) {
            add(SingleSymbolExpression._5)
            hasEvaluated = true
            add(Operator.EXPONENTIATION_2)
        }

        assertEquals("Ans^{2}", editor.getTexInput(withCursor = false).filter { it != ' ' })
    }

    @Test
    fun move() {
    }

    @Test
    fun delete() {

    }
}