package com.exponential_groth.calculator.editor.functionality

enum class SingleSymbolExpression(val parsable: String, val tex: String) {
    _0("0"), _1("1"), _2("2"), _3("3"), _4("4"), _5("5"), _6("6"), _7("7"), _8("8"), _9("9"),
    POINT("."), UNARY_MINUS("-", "-"),
    PI("<pi>", "\\pi "), E("<e>", "e "),
    LEFT_BRACKET("("), RIGHT_BRACKET(")")
    ;

    constructor(both: String): this(both, both)
    fun isDigit() = parsable.length == 1 && parsable[0].isDigit()
}