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
    fun move() {

    }

    @Test
    fun delete() {

    }
}