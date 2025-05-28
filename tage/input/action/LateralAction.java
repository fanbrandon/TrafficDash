package tage.input.action;

import tage.input.action.AbstractInputAction;
import tage.physics.PhysicsObject;
import net.java.games.input.Event;
import org.joml.*;
import tage.*;
import tage.audio.Sound;
import tage.shapes.*;
import tage.input.*;
import tage.input.action.*;
import net.java.games.input.*;
import net.java.games.input.Component.Identifier.*;

import java.lang.Math;
import java.net.ProtocolFamily;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import a3.*;
import a3.client.*;

/**
 * The {@code LateralAction} class defines an input action that moves the player's avatar left or right
 * when triggered, as long as certain game conditions are met.
 * 
 * <p>This class is responsible for moving 
 * the avatar left or right based on keyboard or controller input. It applies a dead zone 
 * to prevent minor input fluctuations from causing unintended movement.</p>
 * 
 * <p>The movement is scaled based on the game's delta time to ensure consistent speed 
 * across different frame rates. The action also checks if the game is in a playable 
 * state (i.e., fuel is available, the game is not lost or won, and the timer is active).</p>
 */
public class LateralAction extends AbstractInputAction {
    private MyGame game; 
    private GameObject av; 
     private PhysicsObject avatarObject;
    private Camera cam; 
    private float moveSpeed; 
    private boolean gameLose, gameWin; 
    private double timer; 
    private ProtocolClient protClient;
    private Sound walk;
    private double walkSoundTimer = 0;
    private double walkSoundDuration = 1.0; // How long the walk sound should be allowed to play (in seconds)
    private AnimatedShape avatarShape;

    /**
     * Constructs a new lateral movement action.
     * 
     * @param g The game instance to associate with this action.
     */
    public LateralAction(MyGame g) {
        game = g;
    }

    /**
     * Performs the Lateral movement action based on user input.
     * 
     * <p>The method checks the input event value to determine movement direction. It 
     * applies a dead zone filter to ignore small input values that may be caused by 
     * controller drift.</p>
     * 
     * <ul>
     *   <li>If the input value is positive, the avatar moves right.</li>
     *   <li>If the input value is negative, the avatar moves left.</li>
     *   <li>If the "D" key is pressed, the avatar moves right.</li>
     *   <li>If the "A" key is pressed, the avatar moves left.</li>
     * </ul>
     * 
     * <p>The movement is only executed if the game conditions allow it.
     * 
     * 
     * @param time The elapsed time since the last frame (unused in this method).
     * @param e    The input event triggering the action.
     */
    @Override
    public void performAction(float time, Event e) {
        av = game.getAvatar();
        avatarShape = game.getAvatarShape();
        if (avatarShape == null) return;  
        float keyValue = e.getValue();
        String eventName = e.getComponent().getIdentifier().getName();

        // Apply a dead zone to avoid unintentional small movements
        if (keyValue > -0.1 && keyValue < 0.1) return;

        // Determine movement direction based on input value
        if (keyValue > 0) {
            moveSpeed = -3f; // Move Left
        } else {
            moveSpeed = 3f; // Move Right
        }

        
        if (eventName.equalsIgnoreCase("D")) {
            moveSpeed = -3f; // Move Right
        }
        if (eventName.equalsIgnoreCase("A")) {
            moveSpeed = 3f; // Move Left
        }

        moveSpeed *= (float) game.getDeltaTime();
        cam = game.getCamera();
        gameLose = game.gameLose();
        gameWin = game.gameWin();
        timer = game.getGameTimer();
        protClient = game.getProtClient();
        avatarObject = game.getPhysicsAvatar();
        walk = game.getWalkSound();
        avatarShape = game.getAvatarShape();
        
        
        av.LateralAction(moveSpeed);
       

        if (!game.getIsMoving() && !game.getIsJumping()) {
            walk.play();
            avatarShape.stopAnimation(); // <- make sure clean
            avatarShape.playAnimation("Walk", 0.5f, AnimatedShape.EndType.LOOP, 0); // smoother blend
            game.setIsMoving(true);
            game.resetWalkSoundTimer(); // <-- reset timer
        }

        protClient.sendMoveMessage(av.getWorldLocation());
        
    }
}