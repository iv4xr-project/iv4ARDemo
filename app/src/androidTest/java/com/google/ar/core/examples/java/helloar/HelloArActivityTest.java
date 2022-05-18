package com.google.ar.core.examples.java.helloar;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.CoordinatesProvider;
import androidx.test.espresso.action.GeneralClickAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Tap;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

//

@LargeTest
@RunWith(AndroidJUnit4.class)
public class HelloArActivityTest {

    @Rule
    //public ActivityTestRule<HelloArActivity> mActivityTestRule = new ActivityTestRule<>(HelloArActivity.class);
    //public IntentsTestRule<HelloArActivity> mActivityTestRule = new IntentsTestRule<>(HelloArActivity.class);
    public ActivityScenarioRule<HelloArActivity> mActivityTestRule = new ActivityScenarioRule <>(HelloArActivity.class);

    /*
    private HelloArActivity launchedActivity;

    @Before
    public void setUp() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        //this is the key part
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //this is the key part
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        launchedActivity = mActivityTestRule.launchActivity(intent);
    }
     */

    @Test
    public void helloArActivityTest() throws InterruptedException {
        //wait(2000);
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.playback_button), withText("Playback"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatButton.perform(click());

        // Click on the recorded video
        //onView(withText("AR.mp4")).perform(click());
        //onView(withId(R.id.surfaceview)).perform(touchDownAndUp(20, 50));
        //clickXY(50,50);
        /*ViewInteraction video = onView(
                allOf(withId(android.R.id.title), withText("AR.mp4")));*/
        /*ViewInteraction video = onView(
                allOf(withParent(IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class))));
        video.perform(click());*/
        // Long tap (select) on a video in the gallery
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).click(100, 1500);
        Thread.sleep(1000);
        // Tap on SELECT
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).click(900, 300);
        Thread.sleep(40000);

        //Tap the screen to place an item
        //onView(withId(R.id.surfaceview)).perform(touchDownAndUp(50, 50));
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).click(100, 1500);
        Thread.sleep(3000);
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).click(600, 1500);
        Thread.sleep(3000);
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).click(400, 1000);
        Thread.sleep(3000);
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).click(300, 1000);
        Thread.sleep(10000);
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    public static ViewAction clickXY(final int x, final int y){
        return new GeneralClickAction(
                Tap.SINGLE,
                new CoordinatesProvider() {
                    @Override
                    public float[] calculateCoordinates(View view) {

                        final int[] screenPos = new int[2];
                        view.getLocationOnScreen(screenPos);

                        final float screenX = screenPos[0] + x;
                        final float screenY = screenPos[1] + y;
                        float[] coordinates = {screenX, screenY};

                        return coordinates;
                    }
                },
                Press.FINGER);
    }
}
