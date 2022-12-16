package com.google.ar.core.examples.java.helloar;

import static org.junit.Assert.assertTrue;
import static nl.uu.cs.aplib.AplibEDSL.SEQ;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.view.View;
import android.widget.ImageView;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.ByteBuffer;

import eu.iv4xr.framework.mainConcepts.TestAgent;
import eu.iv4xr.framework.mainConcepts.WorldEntity;
import helloAr.testAgentSupport.GoalLib;
import helloAr.testAgentSupport.MyAgentEnv;
import helloAr.testAgentSupport.MyAgentState;
import nl.uu.cs.aplib.mainConcepts.GoalStructure;

//DEPTH: Debug
@LargeTest
@RunWith(AndroidJUnit4.class)
public class DepthTest {

    @Rule
    public ActivityTestRule<HelloArActivity> mActivityTestRule = new ActivityTestRule<>(HelloArActivity.class);


    @Test
    public void helloArActivityTest() throws InterruptedException {

        TestAgent agent = new TestAgent("agentSmith", "tester");
        MyAgentState state = new MyAgentState();

        agent.attachState(state).attachEnvironment(new MyAgentEnv(mActivityTestRule.getActivity()));
        GoalLib goalLib = new GoalLib();
        GoalStructure G = SEQ(goalLib.clickButtonG(agent, "Playback", 2000),
                goalLib.selectVideoG(agent, 1, 10000));     //TODO: Provide proper video & tap Settings>Depth_mode
        agent.setGoal(G);

        int k=0 ;
        while(G.getStatus().inProgress() && k < 20) {
            System.out.println(">>> k=" + k);

            agent.update();
            int numberOfAnchorsDisplayed = 0;

            //Get the depth image
            Image depthImage = mActivityTestRule.getActivity().getDepthImage();
            try {
                ByteBuffer buffer = depthImage.getPlanes()[0].getBuffer();
                byte[] bytes = new byte[buffer.capacity()];
                buffer.get(bytes);
                Bitmap bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);

                //TODO: Show image to check it is correct
                // Use depthImage
                View v = mActivityTestRule.getActivity().getWindow().getDecorView();     //Debug
                ImageView mImageView;
                mImageView = (ImageView) v.findViewById(R.id.my_image_view);
                //new v             https://stackoverflow.com/questions/10796660/convert-image-to-bitmap
                mImageView.buildDrawingCache();
                Bitmap bmap = mImageView.getDrawingCache();
                //new ^
                mImageView.setImageBitmap(bmap);

                //TODO: Know the coordinates of two places that are at different depth at the same time
                // There should be big Color changes
                int nearX = 500, nearY = 100;   //Known in advance
                int farX = 70, farY = 100;      //Known in advance

                    /*
                    Distance by Color:
                    R < G < B
                    [ R+ | G- | B- ]
                    <
                    [ R+ | G+ | B- ]
                    <
                    [ R- | G+ | B- ]
                    <
                    [ R- | G+ | B+ ]
                    <
                    [ R- | G- | B+ ]
                     */

                boolean correctDepth = false;
                String nearPredominantColor = "";
                String farPredominantColor = "";

                //Get the depth color of the pixel in the depth image
                //Getting pixel color by position x and y
                int nearColor = bitmapImage.getPixel(nearX, nearY);
                int nearRed = Color.red(nearColor);
                int nearGreen = Color.green(nearColor);
                int nearBlue = Color.blue(nearColor);

                    /*
                    int nearClr = depthImage.getRGB(nearX, nearY);
                    int nearRed =   (nearClr & 0x00ff0000) >> 16;
                    int nearGreen = (nearClr & 0x0000ff00) >> 8;
                    int nearBlue =   nearClr & 0x000000ff;
                    */
                    /*
                    Color mycolor = new Color(img.getRGB(x, y));
                    int red = mycolor.getRed();
                    int green = mycolor.getGreen();
                    int blue = mycolor.getBlue();
                    int alpha = mycolor.getAlpha();
                    */

                System.out.println("Near Red Color value = " + nearRed);
                System.out.println("Near Green Color value = " + nearGreen);
                System.out.println("Near Blue Color value = " + nearBlue);

                if (nearRed > nearGreen) {
                    if (nearRed > nearBlue) {
                        nearPredominantColor = "red";
                    } else {
                        nearPredominantColor = "blue";
                    }
                } else {
                    if (nearGreen > nearBlue) {
                        nearPredominantColor = "green";
                    } else {
                        nearPredominantColor = "blue";
                    }
                }

                int farColor = bitmapImage.getPixel(farX, farY);
                int farRed = Color.red(farColor);
                int farGreen = Color.green(farColor);
                int farBlue = Color.blue(farColor);

                System.out.println("Far Red Color value = " + farRed);
                System.out.println("Far Green Color value = " + farGreen);
                System.out.println("Far Blue Color value = " + farBlue);

                if (farRed > farGreen) {
                    if (farRed > farBlue) {
                        farPredominantColor = "red";
                    } else {
                        farPredominantColor = "blue";
                    }
                } else {
                    if (farGreen > farBlue) {
                        farPredominantColor = "green";
                    } else {
                        farPredominantColor = "blue";
                    }
                }

                //Check if the depth relation is correct
                if (nearPredominantColor == "red" && farPredominantColor == "red") {
                    if (nearRed != farRed) {
                        if (nearRed > farRed) correctDepth = true;
                    }
                } else if (nearPredominantColor == "red" && farPredominantColor == "green") {
                    correctDepth = true;
                } else if (nearPredominantColor == "red" && farPredominantColor == "blue") {
                    correctDepth = true;
                } else if (nearPredominantColor == "green" && farPredominantColor == "green") {
                    if (nearGreen != farGreen) {
                        if (nearGreen > farGreen) correctDepth = true;
                    }
                } else if (nearPredominantColor == "green" && farPredominantColor == "blue") {
                    correctDepth = true;
                } else if (nearPredominantColor == "blue" && farPredominantColor == "blue") {
                    if (nearBlue != farBlue) {
                        if (nearBlue > farBlue) correctDepth = true;
                    }
                }

                boolean depthCondition = correctDepth;
                if (!depthCondition) {
                    mActivityTestRule.getActivity().testFinishedMessage(false);
                    Thread.sleep(60000);
                }
                assertTrue(depthCondition);

                for (WorldEntity a : state.worldmodel().elements.values()) {
                    if (a.type.equals("3DObj")) {
                        numberOfAnchorsDisplayed++;
                    }
                }

                //There are a maximum of 4 anchors displayed
                boolean maxAnchorsCondition = numberOfAnchorsDisplayed <= 2;
                if (!maxAnchorsCondition) {
                    mActivityTestRule.getActivity().testFinishedMessage(false);
                    Thread.sleep(60000);
                }
                assertTrue(maxAnchorsCondition);

                boolean statusCondition = G.getStatus().success();
                if(!statusCondition) {
                    mActivityTestRule.getActivity().testFinishedMessage(false);
                    Thread.sleep(60000);
                }
                assertTrue(statusCondition);
            } catch (Exception e) {
                mActivityTestRule.getActivity().testFinishedMessage(false);
                Thread.sleep(60000);
                assertTrue(false);
            }
        }
    }
}
