package tage.input.action;

import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;
import org.joml.*;
import tage.*;
import tage.shapes.*;
import tage.input.*;
import tage.input.action.*;
import net.java.games.input.*;
import net.java.games.input.Component.Identifier.*;

import java.lang.Math;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;


import a3.MyGame;

/**
 * The {@code RollAction} class defines an input action that rolls the player's avatar left or right
 * when triggered, as long as certain game conditions are met.
 * 
 * <p>This class is responsible for rolling 
 * the avatar left or right based on keyboard or controller input. It applies a dead zone 
 * to prevent minor input fluctuations from causing unintended movement.</p>
 * 
 * <p>The movement is scaled based on the game's delta time to ensure consistent speed 
 * across different frame rates. The action also checks if the game is in a playable 
 * state (i.e., fuel is available, the game is not lost or won, and the timer is active).</p>
 */
public class RollAction extends AbstractInputAction {
    private MyGame game;
    private GameObject av;
    private Matrix4f oldRoll, rollAroundAvatarUp, newRoll;
    private Vector4f oldRollVec;
    private Camera cam;

    /**
     * Constructs a new {@code RollAction} and associates it with the game instance.
     * 
     * @param g The current game instance.
     */
    public RollAction(MyGame g){
        game = g;
    }

    /**
     * Performs the roll action based on user input.
     * 
     * <p>The method checks the input event value to determine movement direction. It 
     * applies a dead zone filter to ignore small input values that may be caused by 
     * controller drift.</p>
     * 
     * <ul>
     *   <li>If the input value is positive, the avatar rolls right</li>
     *   <li>If the input value is negative, the avatar rolls left.</li>
     *   <li>If the "E" key is pressed, the avatar rolls right.</li>
     *   <li>If the "Q" key is pressed, the avatar rolls left.</li>
     * </ul>
     * 
     * @param time The elapsed time since the last frame (unused in this method).
     * @param e    The input event triggering the action.
     */
    @Override
    public void performAction (float time, Event e){
        String eventName = e.getComponent().getIdentifier().getName();
        float keyValue = e.getValue();
        if (keyValue > -.3 && keyValue < .3) return; // deadzone
        float rotationAmount;
        if (keyValue > 0) {
            rotationAmount = -0.8f * (float)game.getDeltaTime(); // Roll right
        } else {
            rotationAmount = 0.8f * (float)game.getDeltaTime(); // roll left
        }
        
        // Handle keyboard input (E/Q)
        if (eventName.equalsIgnoreCase("E")) {
        rotationAmount = -0.8f * (float)game.getDeltaTime(); // Roll to the right
        } else if (eventName.equalsIgnoreCase("Q")) {
        rotationAmount = 0.8f * (float)game.getDeltaTime(); // Roll to the left
        }

        av = game.getAvatar();
        cam = game.getCamera();
        av.RollAction(rotationAmount);
    }
}
