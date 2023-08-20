package com.exponential_groth.calculator

import kotlin.math.E
import kotlin.math.ln
import kotlin.math.pow


const val MULTIVALUED_POL = 1.0
const val MULTIVALUED_REC = 2.0

enum class StorageAction(val variable: String) {
    A("A"), B("B"), C("C"), D("D"), E("E"), F("F"), X("X"), Y("Y"), Z("Z"), M("M"),
    M_PLUS("M") {
        override fun act(prevValue: Double, result: Double): Double = prevValue + result
    },
    M_MINUS("M") {
        override fun act(prevValue: Double, result: Double): Double = prevValue - result
    };

    open fun act(prevValue: Double, result: Double): Double = result
}

fun String.toStorageAction(): StorageAction? {
    return if (startsWith(">>"))
        try {
            StorageAction.valueOf(last().toString())
        } catch (e: IllegalArgumentException) {
            null
        }
    else if (equals(" M+"))
        StorageAction.M_PLUS
    else if (equals(" M-"))
        StorageAction.M_MINUS
    else null
}



fun logisticEquation(expectedReturn: Int, capacity: Boolean, parameters: List<String>): String {
    val a = parameters[0].toDoubleOrNull()?: return "Wrong input!"
    val b = parameters[1].toDoubleOrNull()?: return "Wrong input!"
    val c = parameters[2].toDoubleOrNull()?: return "Wrong input!"

    return if (capacity) {
        val d = parameters[3].toDoubleOrNull()?: return "Wrong input!"
        when (expectedReturn) {
            0 -> (b / (E.pow(a * c) * (b / d - 1) + 1)).toString()
            1 -> (ln(d * (b-a) / (a * (b-d))) / c).toString()
            2 -> (d * a * (E.pow(b*c) - 1) / (a * E.pow(b*c) - d)).toString()
            3 -> (ln(d * (c-a) / (a * (c-d))) / b).toString()
            4 -> ((a * c * E.pow(b * d)) / (c - a + a * E.pow(b * d))).toString()
            else -> ""
        }
    } else {
        when (expectedReturn) {
            0 -> (c / (E.pow(a * b))).toString()
            1 -> (ln(c/a) / b).toString()
            2 -> (ln(c/a) / b).toString()
            3 -> (a * E.pow(b * c)).toString()
            else -> ""
        }
    }
}