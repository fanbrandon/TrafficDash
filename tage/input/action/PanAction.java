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
 * The {@code PanAction} class defines an action zooms the camera left, right, up, and down on the
 * camera's x and z axes.
 * 
 * <p>This action is executed when the corresponding input event is received,
 * moving up, down, left or right with the camera. This is used with a second viewport.
 * 
 * @author Brandon Fan
 */
public class PanAction extends AbstractInputAction {
    private MyGame game;
    private Camera cam;
    private Vector3f oldLoc, newLoc;
    
    /**
     * Constructs a new {@code PanAction} and associates it with the game instance.
     * 
     * @param g The current game instance.
     */
    public PanAction(MyGame g){
        game = g;
    }

    /**
     * Perform pan action on the second viewport when triggered.
     * 
     * <p>This method moves the camera's location up,down, left or right on the
     * x or z axis based on the input received.
     * 
     * @param time The time since last update.
     * @param e The input event that triggered action.
     */
    public void performAction(float time, Event e){
        oldLoc = cam.getLocation();
        String eventName = e.getComponent().getIdentifier().getName();
        

        if (eventName.equalsIgnoreCase("I")) {
            cam.setLocation(cam.getLocation().add(0, 0, -0.1f)); // Pan up
        } else if (eventName.equalsIgnoreCase("K")) {
            cam.setLocation(cam.getLocation().add(0, 0, 0.1f)); // Pan down
        } else if (eventName.equalsIgnoreCase("J")) {
            cam.setLocation(cam.getLocation().add(-0.1f, 0, 0)); // Pan left
        } else if (eventName.equalsIgnoreCase("L")) {
            cam.setLocation(cam.getLocation().add(0.1f, 0, 0)); // Pan right
        }
    }
}
