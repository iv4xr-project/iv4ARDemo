package com.google.ar.core.examples.java.helloar;

import static org.junit.Assert.assertTrue;
import static nl.uu.cs.aplib.AplibEDSL.SEQ;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import eu.iv4xr.framework.mainConcepts.TestAgent;
import eu.iv4xr.framework.mainConcepts.WorldEntity;
import helloAr.testAgentSupport.GoalLib;
import helloAr.testAgentSupport.MyAgentEnv;
import helloAr.testAgentSupport.MyAgentState;
import nl.uu.cs.aplib.mainConcepts.GoalStructure;

//COLLISION TEST
//Two 3D objects cannot be in the same place (they do not touch)

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CollisionTest {

    @Rule
    public ActivityTestRule<HelloArActivity> mActivityTestRule = new ActivityTestRule<>(HelloArActivity.class);

    @Test
    public void helloArActivityTest() throws InterruptedException {
        //FOR A CERTAIN 3D OBJECT
        double xLength = 0, yLength = 0, zLength = 0;
        boolean thereIsCollision = false;
        List<double[]> hitboxList = new ArrayList<double[]>();

        // Hitbox of 3D object
        // Max and min values of X, Y, Z coordinates
        try {
            boolean vertexReached = false;
            double[] maxCoordinates = {0, 0, 0};
            double[] minCoordinates = {0, 0, 0};
            boolean firstIteration = true;

            InputStreamReader inputStream = new InputStreamReader(mActivityTestRule.getActivity().getAssets().open("pawn.txt"));
            BufferedReader in = new BufferedReader(inputStream);

            String line = in.readLine();

            while (line != null) {
                if(!vertexReached) {
                    line = in.readLine();
                    if (line.startsWith("v ")) vertexReached = true;
                }
                else {
                    if (!line.startsWith("v ")) break;

                    // Use lines that correspond to vertexes

                    List<double[]> maxAndMinList = new ArrayList<double[]>();
                    maxAndMinList.add(maxCoordinates);
                    maxAndMinList.add(minCoordinates);

                    // If it is not the first iteration,
                    // check if the coordinates are the largest or the smallest
                    maxAndMinList = updateMaxAndMin(maxAndMinList, line, firstIteration);
                    maxCoordinates = maxAndMinList.get(0);
                    minCoordinates = maxAndMinList.get(1);

                    if (firstIteration) firstIteration = false;

                    line = in.readLine();
                }
            }

            System.out.println("^^^^ maxCoordinates: [" + maxCoordinates[0] + ", " + maxCoordinates[1] + ", " + maxCoordinates[2] + "]");
            System.out.println("^^^^ minCoordinates: [" + minCoordinates[0] + ", " + minCoordinates[1] + ", " + minCoordinates[2] + "]");

            xLength = maxCoordinates[0] - minCoordinates[0];
            yLength = maxCoordinates[0] - minCoordinates[0];
            zLength = maxCoordinates[0] - minCoordinates[0];

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        TestAgent agent = new TestAgent("agentSmith","tester") ;
        MyAgentState state = new MyAgentState() ;

        agent.attachState(state)
             .attachEnvironment(new MyAgentEnv(mActivityTestRule.getActivity())) ;
        GoalLib goalLib = new GoalLib() ;
        GoalStructure G = SEQ(
                goalLib.clickButtonG(agent, "Playback", 2000),
                goalLib.selectVideoG(agent, 1, 35000),
                goalLib.tapScreenG(agent,300,1500,3000),
                goalLib.tapScreenG(agent,600,1500,3000),
                goalLib.tapScreenG(agent,400,1000,3000),
                goalLib.tapScreenG(agent,500,1000,5000)
        ) ;
        agent.setGoal(G) ;

        int k=0 ;
        while(G.getStatus().inProgress() && k < 20) {
            System.out.println(">>> k="+k) ;
            agent.update() ;
            int numberOfAnchorsDisplayed = 1;

            for(WorldEntity a : state.worldmodel().elements.values()) {
                if (a.type.equals("3DObj")) {
                    if (thereIsCollision) break;

                    //Item coordinates
                    float tx = (float) a.properties.get("tx");
                    float ty = (float) a.properties.get("ty");
                    float tz = (float) a.properties.get("tz");

                    System.out.println("^^^^ Translation X: " + tx);
                    System.out.println("^^^^ Translation Y: " + ty);
                    System.out.println("^^^^ Translation Z: " + tz);

                    //Calculate max and min positions in real space for each coordinate (hitbox in real space)
                    double hitboxMinX = tx - (xLength/2);
                    double hitboxMaxX = tx + (xLength/2);
                    double hitboxMinY = ty - (yLength/2);
                    double hitboxMaxY = ty + (yLength/2);
                    double hitboxMinZ = tz - (zLength/2);
                    double hitboxMaxZ = tz + (zLength/2);

                    //Upload the hitbox list
                    double[] hitbox = new double[6];
                    hitbox[0] = hitboxMinX;
                    hitbox[1] = hitboxMaxX;
                    hitbox[2] = hitboxMinY;
                    hitbox[3] = hitboxMaxY;
                    hitbox[4] = hitboxMinZ;
                    hitbox[5] = hitboxMaxZ;
                    hitboxList.add(hitbox);

                    System.out.println("^^^^ Hitbox of the 3D object:");
                    System.out.println("^^^^ hitboxMinX: " + hitbox[0]);
                    System.out.println("^^^^ hitboxMaxX: " + hitbox[1]);
                    System.out.println("^^^^ hitboxMinY: " + hitbox[2]);
                    System.out.println("^^^^ hitboxMaxY: " + hitbox[3]);
                    System.out.println("^^^^ hitboxMinZ: " + hitbox[4]);
                    System.out.println("^^^^ hitboxMaxZ: " + hitbox[5]);

                    //Check if there are intersections of hitboxes between the last added and the existing ones
                    if (hitboxList.size() > 1) {
                        double[] lastHitboxAdded = hitboxList.get(hitboxList.size() - 1);

                        for (int i = 0; i < hitboxList.size() - 1; i ++) {
                            double[] hitboxInList = hitboxList.get(i);

                            //Collision cases:
                            //If MinX(B) is between MinX(A) and MaxX(A)
                            //and MinY(B) is between MinY(A) and MaxY(A)
                            //
                            //If MinX(B) is between MinX(A) and MaxX(A)
                            //and MaxY(B) is between MinY(A) and MaxY(A)
                            //
                            //If MaxX(B) is between MinX(A) and MaxX(A)
                            //and MinY(B) is between MinY(A) and MaxY(A)
                            //
                            //If MaxX(B) is between MinX(A) and MaxX(A)
                            //and MaxY(B) is between MinY(A) and MaxY(A)
                            //
                            //If MinX(B) <= MinX(A) and MaxX(B) >= MaxX(A)
                            //and MinY(B) is between MinY(A) and MaxY(A)
                            //
                            //If MinX(B) <= MinX(A) and MaxX(B) >= MaxX(A)
                            //and MaxY(B) is between MinY(A) and MaxY(A)
                            //
                            //If MinX(B) >= MinX(A) and MinX(B) <= MaxX(A)
                            //and MinY(B) <= MinY(A) and MaxY(B) >= MaxY(A)
                            //
                            //If MaxX(B) >= MinX(A) and MaxX(B) <= MaxX(A)
                            //and MinY(B) <= MinY(A) and MaxY(B) >= MaxY(A)
                            //
                            //If MinX(B) <= MinX(A) and MaxX(B) >= MaxX(A)
                            //and MinY(B) <= MinY(A) and MaxY(B) >= MaxY(A)
                            if (
                                    (
                                            (lastHitboxAdded[0] >= hitboxInList[0] && lastHitboxAdded[0] <= hitboxInList[1])
                                                    &&
                                                    (lastHitboxAdded[2] >= hitboxInList[2] && lastHitboxAdded[2] <= hitboxInList[3])
                                    )
                                            ||
                                            (
                                                    (lastHitboxAdded[0] >= hitboxInList[0] && lastHitboxAdded[0] <= hitboxInList[1])
                                                            &&
                                                            (lastHitboxAdded[3] >= hitboxInList[2] && lastHitboxAdded[3] <= hitboxInList[3])
                                            )
                                            ||
                                            (
                                                    (lastHitboxAdded[1] >= hitboxInList[0] && lastHitboxAdded[1] <= hitboxInList[1])
                                                            &&
                                                            (lastHitboxAdded[2] >= hitboxInList[2] && lastHitboxAdded[2] <= hitboxInList[3])
                                            )
                                            ||
                                            (
                                                    (lastHitboxAdded[0] <= hitboxInList[0] && lastHitboxAdded[1] >= hitboxInList[1])
                                                            &&
                                                            (lastHitboxAdded[2] >= hitboxInList[2] && lastHitboxAdded[2] <= hitboxInList[3])
                                            )
                                            ||
                                            (
                                                    (lastHitboxAdded[0] <= hitboxInList[0] && lastHitboxAdded[1] >= hitboxInList[1])
                                                            &&
                                                            (lastHitboxAdded[3] >= hitboxInList[2] && lastHitboxAdded[3] <= hitboxInList[3])
                                            )
                                            ||
                                            (
                                                    (lastHitboxAdded[0] >= hitboxInList[0] && lastHitboxAdded[0] <= hitboxInList[1])
                                                            &&
                                                            (lastHitboxAdded[2] <= hitboxInList[2] && lastHitboxAdded[3] >= hitboxInList[3])
                                            )
                                            ||
                                            (
                                                    (lastHitboxAdded[1] >= hitboxInList[0] && lastHitboxAdded[1] <= hitboxInList[1])
                                                            &&
                                                            (lastHitboxAdded[2] <= hitboxInList[2] && lastHitboxAdded[3] >= hitboxInList[3])
                                            )
                                            ||
                                            (
                                                    (lastHitboxAdded[0] <= hitboxInList[0] && lastHitboxAdded[1] >= hitboxInList[1])
                                                            &&
                                                            (lastHitboxAdded[2] <= hitboxInList[2] && lastHitboxAdded[3] >= hitboxInList[3])
                                            )
                            ) {
                                System.out.println("^^^^ THERE IS COLLISION!");
                                thereIsCollision = true;
                                break;
                            }
                        }
                    }

                    boolean collisionCondition = thereIsCollision;
                    if(!collisionCondition) {
                        mActivityTestRule.getActivity().testFinishedMessage(false);
                        Thread.sleep(60000);
                    }
                    assertTrue(collisionCondition) ;

                }

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

        boolean statusCondition = G.getStatus().success();
        if(!statusCondition) {
            mActivityTestRule.getActivity().testFinishedMessage(false);
            Thread.sleep(60000);
        }
        assertTrue(statusCondition);

        //All went fine
        mActivityTestRule.getActivity().testFinishedMessage(true);
        Thread.sleep(60000);

    }

    public ArrayList<double[]> updateMaxAndMin(List<double[]> maxAndMinList, String line, boolean firstIteration) {
        ArrayList<double[]> resultList = new ArrayList<double[]>();
        String[] coordinates = line.split(" ");

        // Assign the coordinates from a line to variables
        String xStr = coordinates[1];
        String yStr = coordinates[2];
        String zStr = coordinates[3];
        double xDou = Double.parseDouble(xStr);
        double yDou = Double.parseDouble(yStr);
        double zDou = Double.parseDouble(zStr);

        double[] maxCoordinates = maxAndMinList.get(0);
        double[] minCoordinates = maxAndMinList.get(1);

        if (firstIteration) {
            maxCoordinates[0] = xDou;
            maxCoordinates[1] = yDou;
            maxCoordinates[2] = zDou;
            minCoordinates[0] = xDou;
            minCoordinates[1] = yDou;
            minCoordinates[2] = zDou;
        } else {
            if (xDou > maxCoordinates[0]) maxCoordinates[0] = xDou;
            if (xDou < minCoordinates[0]) minCoordinates[0] = xDou;
            if (yDou > maxCoordinates[1]) maxCoordinates[1] = yDou;
            if (yDou < minCoordinates[1]) minCoordinates[1] = yDou;
            if (zDou > maxCoordinates[2]) maxCoordinates[2] = zDou;
            if (zDou < minCoordinates[2]) minCoordinates[2] = zDou;
        }

        resultList.add(maxCoordinates);
        resultList.add(minCoordinates);

        return resultList;
    }
}
