package com.google.ar.core.examples.java.helloar;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertTrue;

import static nl.uu.cs.aplib.AplibEDSL.* ;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;

import com.google.ar.core.Anchor;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import eu.iv4xr.framework.mainConcepts.TestAgent;
import helloAr.testAgentSupport.GoalLib;
import helloAr.testAgentSupport.MyAgentEnv;
import helloAr.testAgentSupport.MyAgentState;
import nl.uu.cs.aplib.mainConcepts.GoalStructure;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class HelloArActivityTest_WithAgent {

    @Rule
    public ActivityTestRule<HelloArActivity> mActivityTestRule = new ActivityTestRule<>(HelloArActivity.class);
    //public IntentsTestRule<HelloArActivity> mActivityTestRule = new IntentsTestRule<>(HelloArActivity.class);
    //public ActivityScenarioRule<HelloArActivity> mActivityTestRule = new ActivityScenarioRule <>(HelloArActivity.class);



    //@Test
    public void helloArActivityTest()  {

        TestAgent agent = new TestAgent("agentSmith","tester") ;
        MyAgentState state = new MyAgentState() ;

        agent.attachState(state)
             .attachEnvironment(new MyAgentEnv(mActivityTestRule.getActivity())) ;
        GoalLib goalLib = new GoalLib() ;
        GoalStructure G = SEQ(
                goalLib.tapScreenG(agent,50,50,5000),
                goalLib.tapScreenG(agent,50,50,5000),
                goalLib.tapScreenG(agent,50,50,5000)
        ) ;
        agent.setGoal(G) ;

        int k=0 ;
        while(G.getStatus().inProgress() && k < 20) {
            System.out.println(">>> k="+k) ;
            agent.update() ;
        }

        assertTrue(G.getStatus().success());

    }

}
