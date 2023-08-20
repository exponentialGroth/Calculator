package com.exponential_groth.calculator.result

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sign
import kotlin.math.sqrt

private val Double.mantissa get() = toBits() and 0x000fffffffffffffL/* or 0x0010000000000000L*/
private val Double.exponent get() = (toBits() and 0x7ff0000000000000 ushr  52) - 1023


fun reduceFraction(numerator: Long, denominator: Long): Pair<Long, Long> {
    val sign = if ((numerator > 0) xor (denominator > 0)) -1 else 1
    val gcd = gcd(abs(numerator), abs(denominator))
    return Pair(
        sign * abs(numerator) / gcd,
        abs(denominator) / gcd
    )
}

private fun gcd(a: Long, b: Long): Long { // binary gcd algorithm is faster than the euclidean one
    require(a > 0 && b > 0)
    if (a == 0L) return b
    if (b == 0L) return a

    val shift = (a or b).countTrailingZeroBits()
    var u = a
    var v = b
    u = u shr u.countTrailingZeroBits()

    do {
        v = v shr  v.countTrailingZeroBits()
        v -= u
        val m = v shr  63
        u += v and m
        v = (v + m) xor m
    } while (v != 0L)

    return u shl shift
}


fun fractionToDecimal(numerator: Long, denominator: Long): String {
    val nume = abs(numerator)
    val deno = abs(denominator)
    val sign = if ((numerator < 0) xor (denominator < 0)) "-" else ""
    if (deno == 0L) {
        return ""
    } else if (nume == 0L) {
        return "0"
    } else if (nume % deno == 0L) {
        return "$sign${nume / deno}"
    }
    val map = HashMap<Long, Int>()
    val rst = StringBuffer("$sign${nume/deno}.")
    var end = nume % deno * 10
    var i = 0
    while (end != 0L) {
        if (map.containsKey(end)) {
            rst.insert(rst.indexOf(".") + map[end]!! + 1, "\\overline{")
            rst.append("}")
            return rst.toString()
        }
        rst.append(end / deno)
        map[end] = i++
        end = end % deno * 10
    }
    return rst.toString()
}

fun Double.toFraction(): Pair<Long, Long>? {
    val sign = this.sign.toInt()
    val exponent = this.exponent
    var mantissa = this.mantissa shl 12

/*    val maxExponent = 31  // it would work as long as exponent < 52 but that can take long
    if (exponent > maxExponent && this <= Long.MAX_VALUE)
        return toLong() to 1L
    else if (exponent > maxExponent)
        return null
    else if (exponent == -1023L)  // Subnormal numbers are not supported
        return null*/

    if (abs(exponent) >= 32)
        return null

    asRepeatingDecimalToFraction()?.let { return it }

    if (mantissa.countTrailingZeroBits() < 12 + 5) return null  // should have at least 5 zeros to make sure that it is not repeating with a bigger repeating length
    var numerator = 1L
    var denominator = 1L
    var leadingZeros = mantissa.countLeadingZeroBits()
    while (leadingZeros != 64) {
        numerator = (numerator shl (leadingZeros+1)) + 1
        denominator = denominator shl (leadingZeros+1)
        mantissa = mantissa shl (leadingZeros + 1)
        leadingZeros = mantissa.countLeadingZeroBits()
    }
    if (exponent >= 0)
        numerator = numerator shl exponent.toInt()
    else
        denominator = denominator shl -exponent.toInt()

    return reduceFraction(sign * numerator, denominator)
}


private fun Double.asRepeatingDecimalToFraction(): Pair<Long, Long>? {
    val exp = exponent.toInt()
    val numOfDigitsToCheck = 52 - max(exp, 0) - 2 // don't check last two digits and the ones that account for the integer part
    val digitsToCheck = (mantissa and (0L.inv() shl 2)) shl (62 - numOfDigitsToCheck) // remove the bits mentioned above and trim start
    if ((digitsToCheck ushr  (64 - numOfDigitsToCheck)).countTrailingZeroBits() > numOfDigitsToCheck / 2) return null
    var currentlyCheckedRepetendLength = numOfDigitsToCheck / 2
    var minRepeatingPartLength = numOfDigitsToCheck
    var smallestRepetendLength: Int? = null
    val alreadyChecked = mutableListOf<Int>()
    while (currentlyCheckedRepetendLength > 0) {
        var isRecurringWithCurrentLength = true
        for (i in (64 - numOfDigitsToCheck + currentlyCheckedRepetendLength) until (64 - (numOfDigitsToCheck - minRepeatingPartLength))) {
            if ((digitsToCheck and (1L shl (i - currentlyCheckedRepetendLength)) == 0L) != (digitsToCheck and (1L shl i) == 0L)) {
                isRecurringWithCurrentLength = false
                break
            }
        }
        if (!isRecurringWithCurrentLength && smallestRepetendLength != null) {
            if (currentlyCheckedRepetendLength == 1) break
            alreadyChecked.add(currentlyCheckedRepetendLength)
            do {
                currentlyCheckedRepetendLength = smallestRepetendLength / smallestDivisor(smallestRepetendLength, smallestRepetendLength / currentlyCheckedRepetendLength)
            } while (currentlyCheckedRepetendLength > 1 && alreadyChecked.any { it % currentlyCheckedRepetendLength == 0 })
            if (currentlyCheckedRepetendLength == 1) break
        } else if (!isRecurringWithCurrentLength) {
            currentlyCheckedRepetendLength--
            if (currentlyCheckedRepetendLength != 0) minRepeatingPartLength--
        } else {
            smallestRepetendLength = currentlyCheckedRepetendLength
            if (currentlyCheckedRepetendLength == 1) break
            currentlyCheckedRepetendLength /= smallestPrimeFactor(currentlyCheckedRepetendLength)
            alreadyChecked.clear()
        }
    }

    if (smallestRepetendLength == null) return null
    val repetend = ((((1L shl smallestRepetendLength) - 1) shl (minRepeatingPartLength - smallestRepetendLength)) and (digitsToCheck ushr (64-numOfDigitsToCheck))) ushr (minRepeatingPartLength - smallestRepetendLength)
    var shiftRepetitionStart = 0
    val allDigits = mantissa
    for (i in (2 + minRepeatingPartLength) until (52 - max(exp, 0))) {
        if ((allDigits and (1L shl (i - smallestRepetendLength)) == 0L) != (allDigits and (1L shl i) == 0L)) break
        shiftRepetitionStart++
    }

//    I.APPP... = (IAP - IA) / ((2^n - 1) * 2^k)   with n = repetendLength, k = digits between . and first P
    val p = repetend.rotateRight(shiftRepetitionStart, smallestRepetendLength)
    val ia = (mantissa or 0x0010000000000000L) ushr  (2 + minRepeatingPartLength + shiftRepetitionStart)
    val numerator = ((ia shl smallestRepetendLength) or p) - ia  // Do I need to check for overflow here and the line below?
    val denominator = ((1L shl smallestRepetendLength) - 1) shl (50 - minRepeatingPartLength - shiftRepetitionStart - exp)
    return reduceFraction(sign.toInt() * numerator, denominator)
}



/**returns the smallest divisor of [n] that is bigger than [k]*/
private fun smallestDivisor(n: Int, k: Int): Int {
    for (i in k+1 until n) {
        if (n % i == 0) return i
    }
    return n
}

private fun smallestPrimeFactor(n: Int): Int { // fast enough, because n is never bigger than 50
    if (n % 2 == 0) return 2
    var i = 3
    while (i <= sqrt(n.toDouble())) {
        if (n % i == 0) return i
        i += 2
    }
    return n
}

fun primeFactors(num: Long): Map<Long, Int> {
    var n = num
    val factors = mutableMapOf<Long, Int>()
    var i = 2L
    while (i * i <= n) {
        while (n % i == 0L) {
            val occurrences = factors[i]?:0
            factors[i] = occurrences + 1
            n /= i
        }
        i++
    }
    if (n > 1) {
        val occurrences = factors[i]?:0
        factors[n] = occurrences + 1
    }
    return factors.toMap()
}

private fun Long.rotateRight(n: Int, size: Int) = (((1L shl n) - 1) shl (size - n)) and
        (this shl (size - n)) or
        (this ushr n)