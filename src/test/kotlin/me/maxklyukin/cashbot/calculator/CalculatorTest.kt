package me.maxklyukin.cashbot.calculator

import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class CalculatorTest {

    private val calc = Calculator

    @Test
    fun itFailsForEmptyExpression() {
        assertFails("Empty expression", "")
    }

    @Test
    fun itReturnsNumbers() {
        assertCalculates("5", "5")
    }

    @Test
    fun itFailsForNumbersAfterNumber() {
        assertFails("Unexpected Number after Number", "5 7")
    }

    @Test
    fun itReturnsFloatingNumbers() {
        assertCalculates("1.5", "1.5")
    }

    @Test
    fun itReturnsNegativeNumbers() {
        assertCalculates("-9", "-9")
        assertCalculates("-3.2", "-3.2")
    }

    @Test
    fun itIgnoresSpaces() {
        assertCalculates("-9", " -  9 ")
        assertCalculates("3.2", "  3.2")
    }

    @Test
    fun itFailsForOperationAfterOperation() {
        assertFails("Unexpected Addition after Subtraction", "- +7")
    }

    @Test
    fun itAddsTwoNumbers() {
        assertCalculates("7", "5+2")
        assertCalculates("12.2", "2.5+9.7")
    }

    @Test
    fun itIgnoresSpacesInArithmeticOperation() {
        assertCalculates("11", " 4  +   7     ")
        assertCalculates("2.2", " -   1.1 + 3.3 ")
    }

    @Test
    fun itSubtractsNumbers() {
        assertCalculates("1", "8 - 7")
        assertCalculates("-111.42", "-69.420 - 42")
    }

    @Test
    fun itFailsForInvalidMultiplication() {
        assertFails("Unexpected Multiplication", "*7")
    }

    @Test
    fun itMultipliesNumbers() {
        assertCalculates("56", "8 * 7")
        assertCalculates("-2915.64", "-69.420 * 42")
    }

    @Test
    fun itDividesNumbers() {
        assertCalculates("2", "8 / 4")
        assertCalculates("-1.6528571429", "-69.420 / 42")
    }

    @Test
    fun itAdds3Numbers() {
        assertCalculates("7", "2+2+3")
    }

    @Test
    fun itDoesMultipleOperations() {
        assertCalculates("11", "2*4+3")
    }

    @Test
    fun itRespectsOperationOrder() {
        assertCalculates("8", "4+8/2")
    }

    @Test
    fun itFailsForOperationAfterOperation2() {
        assertFails("Unexpected Multiplication after Division", "5 / * 7")
        assertFails("Unexpected Exponentiation after Exponentiation", "5 ^ ^ 7")
    }

    @Test
    fun itCalculatesComplexArithmetics() {
        assertCalculates("10", "4+8/2+6/3")
    }

    @Test
    fun itDoesExponentiation() {
        assertCalculates("8", "2^3")
    }

    @Test
    fun itDoesTwoExponentiationOperations() {
        assertCalculates("4096", "2^3^4")
    }

    @Test
    fun itCanExponentiateFloatBase() {
        assertCalculates("1.1156683467", "1.01^11")
    }

    @Test
    fun itCanExponentiateFloatPowers() {
        assertCalculates("185.6207484", "108^(1.1156683467)")
    }

    @Test
    fun itCanExponentiateFloatPowers2() {
        assertCalculates("185.6207484", "108^(1.01^11)")
    }

    @Test
    fun itRespectsOperationOrderWithExponentiation() {
        assertCalculates("684.135743256", "420*1.05^10")
    }

    @Test
    fun itUnderstandsBrackets() {
        assertCalculates("0.5", "(3-2)/2")
    }

    @Test
    fun itUnderstandsRecursiveBrackets() {
        assertCalculates("1", "(3 - (2 - 1))/2")
    }

    @Test
    fun itCalculatesComplexExpression() {
        assertCalculates("32.8413269898", "(3^6 - (2/7 - 1.02))/22.22")
    }

    private fun assertCalculates(expected: String, expressionString: String) {
        val result = calc.calculate(expressionString)

        assertTrue(BigDecimal(expected).compareTo(result) == 0, "expected:<$expected> but was:<${result.stripTrailingZeros().toPlainString()}>")
    }

    private fun assertFails(expectedMessage: String, expressionString: String) {
        val exception = assertFailsWith<CalculationException> { calc.calculate(expressionString) }

        assertEquals(expectedMessage, exception.message, "Wrong expected exception message")
    }
}