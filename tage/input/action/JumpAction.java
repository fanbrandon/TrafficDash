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

import a3.*;
import a3.client.*;

public class JumpAction extends AbstractInputAction {
    private MyGame game; 
    private GameObject av; 
    private Camera cam; 
    private float jumpSpeed; 
    private float vertSpeed;
    private boolean isInAir;
    private boolean gameLose, gameWin; 
    private double timer; 
    private ProtocolClient protClient;

    public JumpAction(MyGame g) {
        game = g;
    }

    @Override
    public void performAction(float time, Event e){
        cam = game.getCamera();
        gameLose = game.gameLose();
        gameWin = game.gameWin();
        timer = game.getGameTimer();
        protClient = game.getProtClient();
        jumpSpeed = game.getJumpSpeed();
        vertSpeed = game.getVerticalSpeed();
        isInAir = game.getIsJumping();

        if (!isInAir) { 
			isInAir = true;
			vertSpeed = jumpSpeed;  // Set the initial vertical speed
            game.setIsJumping(isInAir);
            game.setVerticalSpeed(vertSpeed);
		}

    }

    
}
