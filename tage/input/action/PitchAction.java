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
 * The {@code PitchAction} class defines an input action that pitches the player's avatar up or down
 * when triggered, as long as certain game conditions are met.
 * 
 * <p>This class is responsible for pitches
 * the avatar up or down based on keyboard or controller input. It applies a dead zone 
 * to prevent minor input fluctuations from causing unintended movement.</p>
 * 
 * <p>The movement is scaled based on the game's delta time to ensure consistent speed 
 * across different frame rates. The action also checks if the game is in a playable 
 * state (i.e., fuel is available, the game is not lost or won, and the timer is active).</p>
 */
public class PitchAction extends AbstractInputAction {
    private MyGame game;
    private GameObject av;
    private Matrix4f oldPitch, pitchAroundAvatarUp, newPitch;
    private Vector4f oldPitchVec;
    private Camera cam;

    /**
     * Constructs a new {@code PitchAction} and associates it with the game instance.
     * 
     * @param g The current game instance.
     */
    public PitchAction(MyGame g){
        game =g;
    }

    /**
     * Performs the pitch action based on user input.
     * 
     * <p>The method checks the input event value to determine movement direction. It 
     * applies a dead zone filter to ignore small input values that may be caused by 
     * controller drift.</p>
     * 
     * <ul>
     *   <li>If the input value is positive, the avatar pitches up</li>
     *   <li>If the input value is negative, the avatar pitches down.</li>
     *   <li>If the "UP" key is pressed, the avatar pitches up.</li>
     *   <li>If the "DOWN" key is pressed, the avatar pitches down.</li>
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
        float pitchAmount;
        if (keyValue > 0) {
            pitchAmount = 0.8f * (float)game.getDeltaTime(); // Tilt Up 
        } else {
            pitchAmount = -0.8f * (float)game.getDeltaTime(); // Tilt Down 
        }
        
        // Handle keyboard input (Up Arrow/Down Arrow)
        if (eventName.equalsIgnoreCase("UP")) {
        pitchAmount = -0.8f * (float)game.getDeltaTime(); // Tilt Up
        } else if (eventName.equalsIgnoreCase("DOWN")) {
        pitchAmount = 0.8f * (float)game.getDeltaTime(); // Tilt Down
        }

        av = game.getAvatar();
        cam = game.getCamera();
        av.PitchAction(pitchAmount);
    }
}
