package com.exponential_groth.calculator

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.exponential_groth.mathview.MathView
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)


    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.exponential_groth.calculator", appContext.packageName)
    }

    @Test
    fun multiArgumentFunctions() {
        onView(withId(R.id.button24)).perform(click()) // log
        onView(withId(R.id.button11)).perform(click()) // 3
        onView(withId(R.id.buttonBlueRight)).perform(click()) // ->
        onView(withId(R.id.button14)).perform(click()) // 2
        onView(withId(R.id.button1)).perform(click()) // 7
        onView(withId(R.id.button2)).perform(click()) // =
        val expected = "\\( 3.0 \\)"
        onView(withId(R.id.MVResult)).check(matches(withMathViewText(expected)))
    }


    private fun withMathViewText(expected: String): Matcher<View?>? {
        return object : TypeSafeMatcher<View?>() {
            override fun matchesSafely(view: View?): Boolean {
                if (view !is MathView) return false
                return expected == view.text
            }

            override fun describeTo(description: Description?) {}
        }
    }
}