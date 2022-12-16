package com.google.ar.core.examples.java.helloar;

import static org.junit.Assert.assertTrue;
import static nl.uu.cs.aplib.AplibEDSL.SEQ;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import eu.iv4xr.framework.mainConcepts.TestAgent;
import eu.iv4xr.framework.mainConcepts.WorldEntity;
import helloAr.testAgentSupport.GoalLib;
import helloAr.testAgentSupport.MyAgentEnv;
import helloAr.testAgentSupport.MyAgentState;
import nl.uu.cs.aplib.mainConcepts.GoalStructure;

//ROTATION: Debug
@LargeTest
@RunWith(AndroidJUnit4.class)
public class RotationTest {

    @Rule
    public ActivityTestRule<HelloArActivity> mActivityTestRule = new ActivityTestRule<>(HelloArActivity.class);

    @Test
    public void helloArActivityTest() throws InterruptedException {

        TestAgent agent = new TestAgent("agentSmith","tester") ;
        MyAgentState state = new MyAgentState() ;
        boolean correctRotation = false;

        agent.attachState(state)
             .attachEnvironment(new MyAgentEnv(mActivityTestRule.getActivity())) ;
        GoalLib goalLib = new GoalLib() ;
        GoalStructure G = SEQ(
                goalLib.clickButtonG(agent, "Playback", 2000),
                goalLib.selectVideoG(agent, 1, 35000),      //TODO: Provide proper video
                goalLib.tapScreenG(agent,300,1500,3000)
        ) ;
        agent.setGoal(G) ;

        int k=0 ;
        while(G.getStatus().inProgress() && k < 20) {
            System.out.println(">>> k="+k) ;
            agent.update() ;
            int numberOfAnchorsDisplayed = 0;

            // The pose may change each time Session.update() is called.
            // The pose should only be used for rendering if getTrackingState() returns TrackingState.TRACKING
            String trackingStateObjectSeen = "";
            String trackingStateObjectNotSeen = "";

            for(WorldEntity a : state.worldmodel().elements.values()) {
                if (a.type.equals("3DObj")) {
                    System.out.println("a.properties.get(\"trackingState\"): " + a.properties.get("trackingState"));
                    trackingStateObjectSeen = String.valueOf(a.properties.get("trackingState"));
                    break;
                }
            }

            Thread.sleep(5000);

            for(WorldEntity a : state.worldmodel().elements.values()) {
                if (a.type.equals("3DObj")) {
                    System.out.println("a.properties.get(\"trackingState\"): " + a.properties.get("trackingState"));
                    trackingStateObjectNotSeen = String.valueOf(a.properties.get("trackingState"));
                    break;
                }
            }

            if (trackingStateObjectSeen == trackingStateObjectNotSeen) {
                if (trackingStateObjectSeen == "TRACKING") {
                    correctRotation = true;
                }
            }

            for(WorldEntity a : state.worldmodel().elements.values()) {
                numberOfAnchorsDisplayed ++;
            }

            //There are a maximum of 4 anchors displayed
            boolean maxAnchorsCondition = numberOfAnchorsDisplayed <= 2;
            if (!maxAnchorsCondition) {
                mActivityTestRule.getActivity().testFinishedMessage(false);
                Thread.sleep(60000);
            }
            assertTrue(maxAnchorsCondition);
        }

        boolean rotationCondition = correctRotation;
        if (!rotationCondition) {
            mActivityTestRule.getActivity().testFinishedMessage(false);
            Thread.sleep(60000);
        }
        assertTrue(rotationCondition);

        boolean statusCondition = G.getStatus().success();
        if(!statusCondition) {
            mActivityTestRule.getActivity().testFinishedMessage(false);
            Thread.sleep(60000);
        }
        assertTrue(statusCondition);
    }
}