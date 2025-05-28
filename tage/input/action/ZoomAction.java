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
 * The {@code ZoomAction} class defines an action zooms the camera up and down on the
 * camera's y axis.
 * 
 * <p>This action is executed when the corresponding input event is received, zooming
 * either in or out with the camera. This is used with a second viewport.
 * 
 * @author Brandon Fan
 */
public class ZoomAction extends AbstractInputAction {
    private MyGame game;
    private Camera cam;
    private Vector3f oldLoc, newLoc;
    private float zoomAmount;

    /**
     * Constructs a new {@code ZoomAction} and associates it with the game instance.
     * 
     * @param g The current game instance.
     */
    public ZoomAction(MyGame g){
        game = g;
    }

    /**
     * Perform zoom action on the second viewport when triggered.
     * 
     * <p>This method moves the camera's location up or down on the
     * y-axis based on the input received.
     * 
     * @param time The time since last update.
     * @param e The input event that triggered action.
     */
    @Override
    public void performAction(float time, Event e){
        oldLoc = cam.getLocation();
        String eventName = e.getComponent().getIdentifier().getName();

        // Handle keyboard input (C/V)
        if (eventName.equalsIgnoreCase("C")) {
        zoomAmount = 0.1f;
        } else if (eventName.equalsIgnoreCase("V")) {
        zoomAmount = -0.1f;
        }
        Vector3f newLoc = new Vector3f(oldLoc.x(), oldLoc.y() + zoomAmount, oldLoc.z());
        cam.setLocation(newLoc);
    }
}
