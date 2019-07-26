package com.hedvig.app


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.annotation.IntDef
import android.graphics.Point
import android.graphics.Rect
import android.view.MotionEvent
import android.widget.TextView
import androidx.test.espresso.UiController
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.ViewAction


@LargeTest
@RunWith(AndroidJUnit4::class)
class OnboardingTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(SplashActivity::class.java)

    @Test
    fun onboardingTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(7000)

        val button = onView(
            allOf(withId(R.id.getHedvig), isDisplayed()))
        button.check(matches(isDisplayed()))

        val button2 = onView(
            allOf(withId(R.id.login), isDisplayed()))
        button2.check(matches(isDisplayed()))

        val appCompatButton = onView(
            allOf(withId(R.id.getHedvig), withText("Skaffa Hedvig"), isDisplayed()))
        appCompatButton.perform(click())

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(7000)

        val textView = onView(
            allOf(withId(R.id.hedvigMessage), withText("Hej! Jag heter Hedvig \uD83D\uDC4B"),
                isDisplayed()))
        textView.check(matches(withText("Hej! Jag heter Hedvig \uD83D\uDC4B")))

        val textView2 = onView(
            allOf(withId(R.id.hedvigMessage), withText("Vad heter du?"), isDisplayed()))
        textView2.check(matches(withText("Vad heter du?")))

        val editText = onView(
            allOf(withId(R.id.inputText), withHint("Förnamn"),
                isDisplayed()))
        editText.check(matches(withHint("Förnamn")))

        //TODO: should not be displayed
//        val imageView = onView(
//                allOf(withId(R.id.uploadFile), withContentDescription("Ladda upp fil"), isDisplayed()))
//        imageView.check(matches(isDisplayed()))


        val chatTextInput = onView(
            allOf(withId(R.id.inputText), isDisplayed()))
        chatTextInput.perform(replaceText("clientbot"), closeSoftKeyboard())

        val chatTextInput7 = onView(
            allOf(withId(R.id.inputText), withText("clientbot"), isDisplayed()))
        chatTextInput7.perform(closeSoftKeyboard())

        onView(withId(R.id.inputText)).perform(ClickDrawableAction(ClickDrawableAction.RIGHT))

        Thread.sleep(7000)

        val textView3 = onView(
            allOf(withId(R.id.hedvigMessage), withText("Trevligt att träffas Clientbot!"), isDisplayed()))
        textView3.check(matches(withText("Trevligt att träffas Clientbot!")))

        val textView4 = onView(
            allOf(withId(R.id.hedvigMessage), withText("För att kunna ge dig ett prisförslag behöver jag ställa några snabba frågor"),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                        1),
                    0),
                isDisplayed()))
        textView4.check(matches(withText("För att kunna ge dig ett prisförslag behöver jag ställa några snabba frågor")))

        val textView5 = onView(
            allOf(withText("Okej!"),
                childAtPosition(
                    allOf(withId(R.id.singleSelectContainer),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java),
                            0)),
                    0),
                isDisplayed()))
        textView5.check(matches(withText("Okej!")))

        val textView6 = onView(
            allOf(withText("Jag är redan medlem"),
                childAtPosition(
                    allOf(withId(R.id.singleSelectContainer),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java),
                            0)),
                    1),
                isDisplayed()))
        textView6.check(matches(withText("Jag är redan medlem")))

        val appCompatTextView = onView(
            allOf(withText("Okej!"),
                childAtPosition(
                    allOf(withId(R.id.singleSelectContainer),
                        childAtPosition(
                            withClassName(`is`("android.widget.FrameLayout")),
                            1)),
                    0),
                isDisplayed()))
        appCompatTextView.perform(click())

        val textView7 = onView(
            allOf(withId(R.id.hedvigMessage), withText("Först, vad är din mailadress?"),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                        1),
                    0),
                isDisplayed()))
        textView7.check(matches(withText("Först, vad är din mailadress?")))

        val editText2 = onView(
            allOf(withId(R.id.inputText),
                childAtPosition(
                    allOf(withId(R.id.textInputContainer),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java),
                            0)),
                    2),
                isDisplayed()))
        editText2.check(matches(withText("")))

        val editText3 = onView(
            allOf(withId(R.id.inputText),
                childAtPosition(
                    allOf(withId(R.id.textInputContainer),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java),
                            0)),
                    2),
                isDisplayed()))
        editText3.check(matches(withText("")))

        val chatTextInput8 = onView(
            allOf(withId(R.id.inputText),
                childAtPosition(
                    allOf(withId(R.id.textInputContainer),
                        childAtPosition(
                            withClassName(`is`("android.widget.FrameLayout")),
                            0)),
                    2),
                isDisplayed()))
        chatTextInput8.perform(replaceText("clientbot@hedvig.com"), closeSoftKeyboard())

        val textView8 = onView(
            allOf(withId(R.id.hedvigMessage), withText("Tack! Bor du i lägenhet eller eget hus?"),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                        1),
                    0),
                isDisplayed()))
        textView8.check(matches(withText("Tack! Bor du i lägenhet eller eget hus?")))

        val textView9 = onView(
            allOf(withText("Lägenhet"),
                childAtPosition(
                    allOf(withId(R.id.singleSelectContainer),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java),
                            0)),
                    0),
                isDisplayed()))
        textView9.check(matches(withText("Lägenhet")))

        val textView10 = onView(
            allOf(withText("Hus"),
                childAtPosition(
                    allOf(withId(R.id.singleSelectContainer),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java),
                            0)),
                    1),
                isDisplayed()))
        textView10.check(matches(withText("Hus")))

        val appCompatTextView2 = onView(
            allOf(withText("Lägenhet"),
                childAtPosition(
                    allOf(withId(R.id.singleSelectContainer),
                        childAtPosition(
                            withClassName(`is`("android.widget.FrameLayout")),
                            1)),
                    0),
                isDisplayed()))
        appCompatTextView2.perform(click())

        val textView11 = onView(
            allOf(withId(R.id.hedvigMessage), withText("\uD83D\uDC4D"),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                        1),
                    0),
                isDisplayed()))
        textView11.check(matches(withText("\uD83D\uDC4D")))

        val textView12 = onView(
            allOf(withId(R.id.hedvigMessage), withText("Vad är ditt personnummer? Jag behöver det så att jag kan hämta din adress"),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                        1),
                    0),
                isDisplayed()))
        textView12.check(matches(withText("Vad är ditt personnummer? Jag behöver det så att jag kan hämta din adress")))

        val editText4 = onView(
            allOf(withId(R.id.inputText), withText("ååååmmddxxxx"),
                childAtPosition(
                    allOf(withId(R.id.textInputContainer),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java),
                            0)),
                    0),
                isDisplayed()))
        editText4.check(matches(withText("ååååmmddxxxx")))

        val chatTextInput9 = onView(
            allOf(withId(R.id.inputText),
                childAtPosition(
                    allOf(withId(R.id.textInputContainer),
                        childAtPosition(
                            withClassName(`is`("android.widget.FrameLayout")),
                            0)),
                    2),
                isDisplayed()))
        chatTextInput9.perform(replaceText("195810033356"), closeSoftKeyboard())

        val textView13 = onView(
            allOf(withId(R.id.hedvigMessage), withText("Konstigt, just nu kan jag inte hitta din adress. Så jag behöver ställa några extra frågor \uD83D\uDE0A"),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                        1),
                    0),
                isDisplayed()))
        textView13.check(matches(withText("Konstigt, just nu kan jag inte hitta din adress. Så jag behöver ställa några extra frågor \uD83D\uDE0A")))

        val textView14 = onView(
            allOf(withId(R.id.hedvigMessage), withText("Vad heter du i efternamn?"),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                        1),
                    0),
                isDisplayed()))
        textView14.check(matches(withText("Vad heter du i efternamn?")))

        val editText5 = onView(
            allOf(withId(R.id.inputText),
                childAtPosition(
                    allOf(withId(R.id.textInputContainer),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java),
                            0)),
                    2),
                isDisplayed()))
        editText5.check(matches(withText("")))

        val chatTextInput10 = onView(
            allOf(withId(R.id.inputText),
                childAtPosition(
                    allOf(withId(R.id.textInputContainer),
                        childAtPosition(
                            withClassName(`is`("android.widget.FrameLayout")),
                            0)),
                    2),
                isDisplayed()))
        chatTextInput10.perform(replaceText("Botsson"), closeSoftKeyboard())

        val textView15 = onView(
            allOf(withId(R.id.hedvigMessage), withText("Vilken gatuadress bor du på?"),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                        1),
                    0),
                isDisplayed()))
        textView15.check(matches(withText("Vilken gatuadress bor du på?")))

        val editText6 = onView(
            allOf(withId(R.id.inputText), withText("Kungsgatan 1"),
                childAtPosition(
                    allOf(withId(R.id.textInputContainer),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java),
                            0)),
                    2),
                isDisplayed()))
        editText6.check(matches(withText("Kungsgatan 1")))

        val chatTextInput11 = onView(
            allOf(withId(R.id.inputText),
                childAtPosition(
                    allOf(withId(R.id.textInputContainer),
                        childAtPosition(
                            withClassName(`is`("android.widget.FrameLayout")),
                            0)),
                    2),
                isDisplayed()))
        chatTextInput11.perform(replaceText("testgatan 418"), closeSoftKeyboard())

        val chatTextInput12 = onView(
            allOf(withId(R.id.inputText),
                childAtPosition(
                    allOf(withId(R.id.textInputContainer),
                        childAtPosition(
                            withClassName(`is`("android.widget.FrameLayout")),
                            0)),
                    2),
                isDisplayed()))
        chatTextInput12.perform(replaceText("93591"), closeSoftKeyboard())

        val textView16 = onView(
            allOf(withId(R.id.hedvigMessage), withText("Vad är ditt postnummer?"),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                        1),
                    0),
                isDisplayed()))
        textView16.check(matches(withText("Vad är ditt postnummer?")))

        val chatTextInput13 = onView(
            allOf(withId(R.id.inputText), withText("93591"),
                childAtPosition(
                    allOf(withId(R.id.textInputContainer),
                        childAtPosition(
                            withClassName(`is`("android.widget.FrameLayout")),
                            0)),
                    2),
                isDisplayed()))
        chatTextInput13.perform(click())

        val chatTextInput14 = onView(
            allOf(withId(R.id.inputText), withText("93591"),
                childAtPosition(
                    allOf(withId(R.id.textInputContainer),
                        childAtPosition(
                            withClassName(`is`("android.widget.FrameLayout")),
                            0)),
                    2),
                isDisplayed()))
        chatTextInput14.perform(replaceText(""))

        val chatTextInput15 = onView(
            allOf(withId(R.id.inputText),
                childAtPosition(
                    allOf(withId(R.id.textInputContainer),
                        childAtPosition(
                            withClassName(`is`("android.widget.FrameLayout")),
                            0)),
                    2),
                isDisplayed()))
        chatTextInput15.perform(closeSoftKeyboard())

        val editText7 = onView(
            allOf(withId(R.id.inputText), withText("123 45"),
                childAtPosition(
                    allOf(withId(R.id.textInputContainer),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java),
                            0)),
                    0),
                isDisplayed()))
        editText7.check(matches(withText("123 45")))

        val chatTextInput16 = onView(
            allOf(withId(R.id.inputText),
                childAtPosition(
                    allOf(withId(R.id.textInputContainer),
                        childAtPosition(
                            withClassName(`is`("android.widget.FrameLayout")),
                            0)),
                    2),
                isDisplayed()))
        chatTextInput16.perform(click())

        val chatTextInput17 = onView(
            allOf(withId(R.id.inputText),
                childAtPosition(
                    allOf(withId(R.id.textInputContainer),
                        childAtPosition(
                            withClassName(`is`("android.widget.FrameLayout")),
                            0)),
                    2),
                isDisplayed()))
        chatTextInput17.perform(replaceText("93591"), closeSoftKeyboard())

        val textView17 = onView(
            allOf(withId(R.id.hedvigMessage), withText("Hur många kvadratmeter är lägenheten?"),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                        1),
                    0),
                isDisplayed()))
        textView17.check(matches(withText("Hur många kvadratmeter är lägenheten?")))

        val editText8 = onView(
            allOf(withId(R.id.inputText),
                childAtPosition(
                    allOf(withId(R.id.textInputContainer),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java),
                            0)),
                    0),
                isDisplayed()))
        editText8.check(matches(withText("")))

        val chatTextInput18 = onView(
            allOf(withId(R.id.inputText),
                childAtPosition(
                    allOf(withId(R.id.textInputContainer),
                        childAtPosition(
                            withClassName(`is`("android.widget.FrameLayout")),
                            0)),
                    2),
                isDisplayed()))
        chatTextInput18.perform(click())

        val chatTextInput19 = onView(
            allOf(withId(R.id.inputText),
                childAtPosition(
                    allOf(withId(R.id.textInputContainer),
                        childAtPosition(
                            withClassName(`is`("android.widget.FrameLayout")),
                            0)),
                    2),
                isDisplayed()))
        chatTextInput19.perform(replaceText("42"), closeSoftKeyboard())

        val chatTextInput20 = onView(
            allOf(withId(R.id.inputText), withText("42"),
                childAtPosition(
                    allOf(withId(R.id.textInputContainer),
                        childAtPosition(
                            withClassName(`is`("android.widget.FrameLayout")),
                            0)),
                    2),
                isDisplayed()))
        chatTextInput20.perform(click())

        val textView18 = onView(
            allOf(withId(R.id.hedvigMessage), withText("Perfekt! Hyr du eller äger du den?"),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                        1),
                    0),
                isDisplayed()))
        textView18.check(matches(withText("Perfekt! Hyr du eller äger du den?")))

        val textView19 = onView(
            allOf(withText("Jag hyr den"),
                childAtPosition(
                    allOf(withId(R.id.singleSelectContainer),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java),
                            0)),
                    0),
                isDisplayed()))
        textView19.check(matches(withText("Jag hyr den")))

        val textView20 = onView(
            allOf(withText("Jag äger den"),
                childAtPosition(
                    allOf(withId(R.id.singleSelectContainer),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java),
                            0)),
                    1),
                isDisplayed()))
        textView20.check(matches(withText("Jag äger den")))

        val appCompatTextView3 = onView(
            allOf(withText("Jag äger den"),
                childAtPosition(
                    allOf(withId(R.id.singleSelectContainer),
                        childAtPosition(
                            withClassName(`is`("android.widget.FrameLayout")),
                            1)),
                    1),
                isDisplayed()))
        appCompatTextView3.perform(click())

        val textView21 = onView(
            allOf(withId(R.id.hedvigMessage), withText("Okej! Hur många bor där?"),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                        1),
                    0),
                isDisplayed()))
        textView21.check(matches(withText("Okej! Hur många bor där?")))

        val editText9 = onView(
            allOf(withId(R.id.inputText),
                childAtPosition(
                    allOf(withId(R.id.textInputContainer),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java),
                            0)),
                    0),
                isDisplayed()))
        editText9.check(matches(withText("")))

        val chatTextInput21 = onView(
            allOf(withId(R.id.inputText),
                childAtPosition(
                    allOf(withId(R.id.textInputContainer),
                        childAtPosition(
                            withClassName(`is`("android.widget.FrameLayout")),
                            0)),
                    2),
                isDisplayed()))
        chatTextInput21.perform(replaceText("1"), closeSoftKeyboard())

        val textView22 = onView(
            allOf(withId(R.id.hedvigMessage), withText("Toppen!"),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                        1),
                    0),
                isDisplayed()))
        textView22.check(matches(withText("Toppen!")))

        val textView23 = onView(
            allOf(withId(R.id.hedvigMessage), withText("Äger du något som du tar med dig utanför hemmet som är värt över 50 000 kr som du vill försäkra? \uD83D\uDC8D"),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                        1),
                    0),
                isDisplayed()))
        textView23.check(matches(withText("Äger du något som du tar med dig utanför hemmet som är värt över 50 000 kr som du vill försäkra? \uD83D\uDC8D")))

        val textView24 = onView(
            allOf(withText("Ja, berätta om objektsförsäkring"),
                childAtPosition(
                    allOf(withId(R.id.singleSelectContainer),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java),
                            0)),
                    0),
                isDisplayed()))
        textView24.check(matches(withText("Ja, berätta om objektsförsäkring")))

        val textView25 = onView(
            allOf(withText("Nej, gå vidare utan"),
                childAtPosition(
                    allOf(withId(R.id.singleSelectContainer),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java),
                            0)),
                    1),
                isDisplayed()))
        textView25.check(matches(withText("Nej, gå vidare utan")))

        val appCompatTextView4 = onView(
            allOf(withText("Nej, gå vidare utan"),
                childAtPosition(
                    allOf(withId(R.id.singleSelectContainer),
                        childAtPosition(
                            withClassName(`is`("android.widget.FrameLayout")),
                            1)),
                    1),
                isDisplayed()))
        appCompatTextView4.perform(click())

        val textView26 = onView(
            allOf(withId(R.id.hedvigMessage), withText("Vad bra! Då täcks dina prylar av drulleförsäkringen när du är ute på äventyr"),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                        1),
                    0),
                isDisplayed()))
        textView26.check(matches(withText("Vad bra! Då täcks dina prylar av drulleförsäkringen när du är ute på äventyr")))

        val textView27 = onView(
            allOf(withId(R.id.hedvigMessage), withText("Köper du någon dyr pryl i framtiden så fixar jag så klart det också!"),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                        1),
                    0),
                isDisplayed()))
        textView27.check(matches(withText("Köper du någon dyr pryl i framtiden så fixar jag så klart det också!")))

        val textView28 = onView(
            allOf(withId(R.id.hedvigMessage), withText("Har du någon hemförsäkring idag?"),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                        1),
                    0),
                isDisplayed()))
        textView28.check(matches(withText("Har du någon hemförsäkring idag?")))

        val textView29 = onView(
            allOf(withText("Ja"),
                childAtPosition(
                    allOf(withId(R.id.singleSelectContainer),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java),
                            0)),
                    0),
                isDisplayed()))
        textView29.check(matches(withText("Ja")))

        val textView30 = onView(
            allOf(withText("Nej"),
                childAtPosition(
                    allOf(withId(R.id.singleSelectContainer),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java),
                            0)),
                    1),
                isDisplayed()))
        textView30.check(matches(withText("Nej")))

        val appCompatTextView5 = onView(
            allOf(withText("Nej"),
                childAtPosition(
                    allOf(withId(R.id.singleSelectContainer),
                        childAtPosition(
                            withClassName(`is`("android.widget.FrameLayout")),
                            1)),
                    1),
                isDisplayed()))
        appCompatTextView5.perform(click())

        val textView31 = onView(
            allOf(withId(R.id.hedvigMessage), withText("Sådärja, tack Clientbot! Det var alla frågor jag hade!"),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                        1),
                    0),
                isDisplayed()))
        textView31.check(matches(withText("Sådärja, tack Clientbot! Det var alla frågor jag hade!")))

        val textView32 = onView(
            allOf(withText("Gå vidare för att se ditt förslag \uD83D\uDC4F"),
                childAtPosition(
                    allOf(withId(R.id.singleSelectContainer),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java),
                            0)),
                    0),
                isDisplayed()))
        textView32.check(matches(withText("Gå vidare för att se ditt förslag \uD83D\uDC4F")))

        val appCompatTextView6 = onView(
            allOf(withText("Gå vidare för att se ditt förslag \uD83D\uDC4F"),
                childAtPosition(
                    allOf(withId(R.id.singleSelectContainer),
                        childAtPosition(
                            withClassName(`is`("android.widget.FrameLayout")),
                            1)),
                    0),
                isDisplayed()))
        appCompatTextView6.perform(click())

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(7000)

        val textView33 = onView(
            allOf(withText("Försäkringsförslag"),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                        0),
                    0),
                isDisplayed()))
        textView33.check(matches(withText("Försäkringsförslag")))

        val textView34 = onView(
            allOf(withId(R.id.offerToolbarAddress), withText("testgatan 418"),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                        0),
                    1),
                isDisplayed()))
        textView34.check(matches(withText("testgatan 418")))

        val button3 = onView(
            allOf(withId(R.id.offerToolbarSign),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.offerToolbar),
                        0),
                    1),
                isDisplayed()))
        button3.check(matches(isDisplayed()))

        val imageView2 = onView(
            allOf(withId(R.id.offerChatButton),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.offerToolbar),
                        0),
                    2),
                isDisplayed()))
        imageView2.check(matches(isDisplayed()))

        val appCompatImageView = onView(
            allOf(withId(R.id.offerChatButton),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.offerToolbar),
                        0),
                    3),
                isDisplayed()))
        appCompatImageView.perform(click())

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(7000)

        val textView35 = onView(
            allOf(withId(R.id.hedvigMessage), withText("Hade du någon fundering?"),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                        1),
                    0),
                isDisplayed()))
        textView35.check(matches(withText("Hade du någon fundering?")))

        val imageView3 = onView(
            allOf(withId(R.id.closeChatButton), withContentDescription("Stäng"),
                childAtPosition(
                    allOf(withId(R.id.frameLayout),
                        childAtPosition(
                            withId(R.id.chatRoot),
                            0)),
                    0),
                isDisplayed()))
        imageView3.check(matches(isDisplayed()))

        val imageView4 = onView(
            allOf(withId(R.id.resetChatButton), withContentDescription("Starta om"),
                childAtPosition(
                    allOf(withId(R.id.frameLayout),
                        childAtPosition(
                            withId(R.id.chatRoot),
                            0)),
                    2),
                isDisplayed()))
        imageView4.check(matches(isDisplayed()))

        val appCompatImageView2 = onView(
            allOf(withId(R.id.resetChatButton), withContentDescription("Starta om"),
                childAtPosition(
                    allOf(withId(R.id.frameLayout),
                        childAtPosition(
                            withId(R.id.chatRoot),
                            0)),
                    2),
                isDisplayed()))
        appCompatImageView2.perform(click())

        val textView36 = onView(
            allOf(withText("Vill du börja om?"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0),
                    0),
                isDisplayed()))
        textView36.check(matches(withText("Vill du börja om?")))

        val textView37 = onView(
            allOf(withText("Om du trycker ja så börjar konversationen om från början"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0),
                    1),
                isDisplayed()))
        textView37.check(matches(withText("Om du trycker ja så börjar konversationen om från början")))

        val textView38 = onView(
            allOf(withId(R.id.chatResetDialogNegativeButton), withText("Nej"),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                        2),
                    0),
                isDisplayed()))
        textView38.check(matches(withText("Nej")))

        val textView39 = onView(
            allOf(withId(R.id.chatResetDialogPositiveButton), withText("Ja"),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                        2),
                    1),
                isDisplayed()))
        textView39.check(matches(withText("Ja")))

        val appCompatTextView7 = onView(
            allOf(withId(R.id.chatResetDialogPositiveButton), withText("Ja"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        2),
                    1),
                isDisplayed()))
        appCompatTextView7.perform(click())
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

class ClickDrawableAction(@Location val drawableLocation: Int) : ViewAction {


    override fun getConstraints(): Matcher<View> {
        return allOf(isAssignableFrom(TextView::class.java), object : BoundedMatcher<View, TextView>(TextView::class.java) {
            override fun matchesSafely(tv: TextView): Boolean {
                //get focus so drawables are visible and if the textview has a drawable in the position then return a match
                return tv.requestFocusFromTouch() && tv.compoundDrawables[drawableLocation] != null

            }

            override fun describeTo(description: Description) {
                description.appendText("has drawable")
            }
        })
    }

    override fun getDescription(): String {
        return "click drawable "
    }

    override fun perform(uiController: UiController, view: View) {
        val tv = view as TextView//we matched
        if (tv.requestFocusFromTouch())
        //get focus so drawables are visible
        {
            //get the bounds of the drawable image
            val drawableBounds = tv.compoundDrawables[drawableLocation].bounds
            val drawablePadding = Rect()
            tv.compoundDrawables[drawableLocation].getPadding(drawablePadding)

            //calculate the drawable click location for left, top, right, bottom
            val clickPoint = arrayOfNulls<Point>(4)
            clickPoint[LEFT] = Point(tv.left + drawableBounds.width() / 2, (tv.pivotY + drawableBounds.height() / 2).toInt())
            clickPoint[TOP] = Point((tv.pivotX + drawableBounds.width() / 2).toInt(), tv.top + drawableBounds.height() / 2)
            clickPoint[RIGHT] = Point((tv.right + drawableBounds.width() / 2f).toInt() - drawablePadding.right, (tv.pivotY + drawableBounds.height() / 2).toInt())
            clickPoint[BOTTOM] = Point((tv.pivotX + drawableBounds.width() / 2).toInt(), tv.bottom + drawableBounds.height() / 2)

            if (tv.dispatchTouchEvent(MotionEvent.obtain(android.os.SystemClock.uptimeMillis(), android.os.SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, clickPoint[drawableLocation]!!.x.toFloat(), clickPoint[drawableLocation]!!.y.toFloat(), 0)))
                tv.dispatchTouchEvent(MotionEvent.obtain(android.os.SystemClock.uptimeMillis(), android.os.SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, clickPoint[drawableLocation]!!.x.toFloat(), clickPoint[drawableLocation]!!.y.toFloat(), 0))
        }
    }

    @IntDef(LEFT, TOP, RIGHT, BOTTOM)
    annotation class Location

    companion object {
        const val LEFT = 0
        const val TOP = 1
        const val RIGHT = 2
        const val BOTTOM = 3
    }
}
