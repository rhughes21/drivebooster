package com.personal.drivebooster


import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import androidx.test.runner.AndroidJUnit4
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class RegisterActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(LoginActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION")

    @Test
    fun registerActivityTest() {

        val appCompatButton = onView(
                allOf(withId(R.id.no_account_button),
                        isDisplayed()))
        appCompatButton.perform(click())

        val appCompatEditText = onView(
                allOf(withId(R.id.edit_text_username),
                        childAtPosition(
                                allOf(withId(R.id.input_group),
                                        childAtPosition(
                                                withClassName(`is`("android.widget.RelativeLayout")),
                                                0)),
                                0),
                        isDisplayed()))
        appCompatEditText.perform(replaceText("test"), closeSoftKeyboard())

        val appCompatEditText2 = onView(
                allOf(withId(R.id.editTextEmail),
                        childAtPosition(
                                allOf(withId(R.id.input_group),
                                        childAtPosition(
                                                withClassName(`is`("android.widget.RelativeLayout")),
                                                0)),
                                1),
                        isDisplayed()))
        appCompatEditText2.perform(replaceText("test@outlook.com"), closeSoftKeyboard())

        val appCompatEditText3 = onView(
                allOf(withId(R.id.edit_text_password),
                        childAtPosition(
                                allOf(withId(R.id.input_group),
                                        childAtPosition(
                                                withClassName(`is`("android.widget.RelativeLayout")),
                                                0)),
                                2),
                        isDisplayed()))
        appCompatEditText3.perform(replaceText("hampton4"), closeSoftKeyboard())

        val appCompatEditText4 = onView(
                allOf(withId(R.id.edit_text_passwordCnf),
                        childAtPosition(
                                allOf(withId(R.id.input_group),
                                        childAtPosition(
                                                withClassName(`is`("android.widget.RelativeLayout")),
                                                0)),
                                3),
                        isDisplayed()))
        appCompatEditText4.perform(replaceText("hampton4"), closeSoftKeyboard())

        val enterPhoneNoEditText = onView(
                allOf(withId(R.id.edit_text_phone_number),
                        isDisplayed()))
        enterPhoneNoEditText.perform(replaceText("07732290646"), closeSoftKeyboard())

        pressBack()


    }

    private fun childAtPosition(
            parentMatcher: Matcher<View>, position: Int): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
