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
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import a3.*;
import a3.client.*;

/**
 * The {@code BackAction} class defines an input action that moves the player's avatar backward
 * when triggered, as long as certain game conditions are met.
 * 
 * <p>This action is executed when the corresponding input event is received, moving the avatar
 * backward at a speed determined by the game's delta time. The action will not execute if
 * the player has run out of fuel, the game has been won or lost, or the game timer has expired.</p>
 * 
 * @author Brandon Fan
 */
public class BackAction extends AbstractInputAction {
    private MyGame game; 
    private GameObject av; 
    private float moveSpeed; 
    private Camera cam; 
    private boolean gameLose, gameWin; 
    private double timer; 
    private ProtocolClient protClient;
    private Sound walk;
    private double walkSoundTimer = 0;
    private double walkSoundDuration = 1.0; // How long the walk sound should be allowed to play (in seconds)
    private AnimatedShape avatarShape;
    private PhysicsObject avatarObject;

    /**
     * Constructs a new {@code BackAction} and associates it with the game instance.
     * 
     * @param g The current game instance.
     */
    public BackAction(MyGame g) {
        game = g;
    }

    /**
     * Performs the backward movement action when triggered.
     * 
     * <p>This method retrieves the avatar and camera from the game, checks the game's current
     * state, and moves the avatar backward if the conditions allow. Movement is only executed
     * if the player has fuel remaining, the game is not in a win/loss state, and the timer has not expired.</p>
     * 
     * @param time The time since the last update (not used directly in this method).
     * @param e The input event that triggered the action.
     */
    @Override
    public void performAction(float time, Event e) {
        av = game.getAvatar(); 
        cam = game.getCamera(); 
        gameLose = game.gameLose(); 
        gameWin = game.gameWin(); 
        timer = game.getGameTimer(); 
        moveSpeed = 2f * (float) game.getDeltaTime();
        protClient = game.getProtClient();
        walk = game.getWalkSound();
        avatarShape = game.getAvatarShape();
        avatarObject = game.getPhysicsAvatar();

        av.BackAction(moveSpeed);
        

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

