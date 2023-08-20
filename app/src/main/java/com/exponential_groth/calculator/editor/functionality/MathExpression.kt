package com.exponential_groth.calculator.editor.functionality

interface MathExpression {
    fun addToParsable(l: ArrayList<String>, i: Int): Int
    fun addToTex(l: ArrayList<String>, i: Int): Int

    /**
     * Removes the expression from [l] that starts at index [i].
     */
    fun removeFromParsable(l: ArrayList<String>, i: Int)

    /**
     * Removes the expression from [l] that starts at index [i].
     */
    fun removeFromTex(l: ArrayList<String>, i: Int)
}