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
import tage.audio.*;

import java.lang.Math;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import tage.physics.PhysicsEngine;
import tage.physics.PhysicsObject;
import tage.physics.JBullet.*;


import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.collision.dispatch.CollisionObject;

import a3.*;
import a3.client.*;

/**
 * The {@code FwdAction} class defines an input action that moves the player's avatar forward
 * when triggered, as long as certain game conditions are met.
 * 
 * <p>This class is responsible for moving 
 * the avatar forward based on keyboard or controller input. It applies a dead zone 
 * to prevent minor input fluctuations from causing unintended movement.</p>
 * 
 * <p>The movement is scaled based on the game's delta time to ensure consistent speed 
 * across different frame rates. The action also checks if the game is in a playable 
 * state (i.e., fuel is available, the game is not lost or won, and the timer is active).</p>
 */
public class FwdAction extends AbstractInputAction {
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
     * Constructs a new forward movement action.
     * 
     * @param g The game instance to associate with this action.
     */
    public FwdAction(MyGame g) {
        game = g;
    }

    /**
     * Performs the forward movement action based on user input.
     * 
     * <p>The method checks the input event value to determine movement direction. It 
     * applies a dead zone filter to ignore small input values that may be caused by 
     * controller drift.</p>
     * 
     * <ul>
     *   <li>If the input value is positive, the avatar moves forward.</li>
     *   <li>If the input value is negative, the avatar moves backward.</li>
     *   <li>If the "W" key is pressed, the avatar moves forward.</li>
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

        // Check for dead zone (no movement)
        if (keyValue > -0.3 && keyValue < 0.3) {
            if (game.getIsMoving() && !game.getIsJumping()) {
                avatarShape.stopAnimation();
                avatarShape.playAnimation("Idle", 0.5f, AnimatedShape.EndType.LOOP, 0);
                game.setIsMoving(false);
            }
            return;
        }

        // Movement direction
        if (keyValue > 0) {
            moveSpeed = -5f; // Forward
        } else {
            moveSpeed = 5f; // Backward
        }

        if (eventName.equalsIgnoreCase("W")) {
            moveSpeed = 5f;
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

        av.ForwardAction(moveSpeed);
        
        

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