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
 * The {@code YawAction} class defines an action input the turns the player's avatar
 * left or right relative to the avatar's local axis.
 * 
 * <p>This action is executed when the corresponding input event is received, turning
 * the avatar either left or right.
 * 
 * @author Brandon Fan
 */
public class YawAction extends AbstractInputAction {
    private MyGame game;
    private GameObject av;
    private Matrix4f oldRotation, rotAroundAvatarUp, newRotation;
    private Vector4f oldUp;
    private Camera cam;

    /**
     * Constructs a new {@code YawAction} and associates it with the game instance.
     * 
     * @param g The current game instance.
     */
    public YawAction(MyGame g){
        game =g;
    }

    /**
     * Perform local yaw acton of player avatar when triggered.
     * 
     * <p>This method retrieves the avatar and camera from the game and turns
     * the avatar based on input received.
     * 
     * @param time The time since last update.
     * @param e The input event that triggered action.
     */
    @Override
    public void performAction (float time, Event e){
        String eventName = e.getComponent().getIdentifier().getName();
        float keyValue = e.getValue();
        if (keyValue > -.3 && keyValue < .3) return; // deadzone
        float rotationAmount;
        if (keyValue > 0) {
            rotationAmount = -0.8f * (float)game.getDeltaTime(); // Rotate left (negative direction)
        } else {
            rotationAmount = 0.8f * (float)game.getDeltaTime(); // Rotate right (positive direction)
        }
        
        // Handle keyboard input (A/D) or (Left/Right)
        if (eventName.equalsIgnoreCase("LEFT")) {
        rotationAmount = -0.8f * (float)game.getDeltaTime(); // Rotate left
        } else if (eventName.equalsIgnoreCase("RIGHT")) {
        rotationAmount = 0.8f * (float)game.getDeltaTime(); // Rotate right
        }

        av = game.getAvatar();
        cam = game.getCamera();
        av.YawAction(rotationAmount);
        
    }
}
