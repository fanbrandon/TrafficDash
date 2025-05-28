package tage;

import tage.input.action.AbstractInputAction;
import tage.*;
import tage.shapes.*;
import tage.input.*;
import tage.input.action.*;
import net.java.games.input.*;
import net.java.games.input.Component.Identifier.*;

import java.lang.Math;
import java.io.*;
import javax.swing.*;
import org.joml.*;


/**
 * Controls an orbiting third-person camera around the player's avatar.
 * 
 * <p>This class manages camera movement based on user input, allowing changes in
 * azimuth (horizontal rotation), elevation (vertical rotation), and radius (zoom).
 * Input can come from both the keyboard and a gamepad.</p>
 */
public class CameraOrbit3D {
    private Engine engine;
    private Camera camera;
    private GameObject avatar;
    private float cameraAzimuth; // Horizontal rotation angle around the avatar
    private float cameraElevation; // Vertical rotation angle relative to the avatar
    private float cameraRadius; // Distance between the camera and the avatar
    private boolean isAiming = false; // Tracks if aiming mode is active

    /**
     * Constructs a camera orbit controller.
     * 
     * @param cam The camera to be controlled.
     * @param av The avatar (game object) the camera orbits around.
     * @param e The game engine instance.
     */
    public CameraOrbit3D(Camera cam, GameObject av, Engine e) {
        engine = e;
        camera = cam;
        avatar = av;
        cameraAzimuth = 0.0f;
        cameraElevation = 23.0f;
        cameraRadius = 8.0f;
        setupInputs();
        updateCameraPosition();
    }

    /**
     * Sets up input actions for controlling the camera using both
     * keyboard and gamepad inputs.
     */
    private void setupInputs() {
        OrbitAzimuthAction azmAction = new OrbitAzimuthAction();
        OrbitElevationAction elevAction = new OrbitElevationAction();
        OrbitRadiusAction radAction = new OrbitRadiusAction();

        InputManager im = engine.getInputManager();

        // Gamepad controls
        //im.associateActionWithAllGamepads(Component.Identifier.Axis.Z, azmAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        //im.associateActionWithAllGamepads(Component.Identifier.Axis.RX, azmAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        //im.associateActionWithAllGamepads(Component.Identifier.Axis.RY, elevAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        //im.associateActionWithAllGamepads(Component.Identifier.Axis.RZ, elevAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        //im.associateActionWithAllGamepads(Component.Identifier.Button._4, radAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        //im.associateActionWithAllGamepads(Component.Identifier.Button._5, radAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

        // Keyboard controls
        //im.associateActionWithAllKeyboards(Component.Identifier.Key.RIGHT, azmAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        //im.associateActionWithAllKeyboards(Component.Identifier.Key.LEFT, azmAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateActionWithAllKeyboards(Component.Identifier.Key.UP, elevAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateActionWithAllKeyboards(Component.Identifier.Key.DOWN, elevAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateActionWithAllKeyboards(Component.Identifier.Key.T, radAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateActionWithAllKeyboards(Component.Identifier.Key.R, radAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

        //im.associateActionWithAllMice(net.java.games.input.Component.Identifier.Axis.X,azmAction,InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
        
        
    }

    /**
     * Updates the camera's position and orientation based on current azimuth,
     * elevation, and radius values.
     */
     
     public void updateCameraPosition() {
        Vector3f avatarRot = avatar.getWorldForwardVector();
        double avatarAngle = Math.toDegrees(avatarRot.angleSigned(new Vector3f(0, 0, -1), new Vector3f(0, 1, 0)));
        float totalAz = cameraAzimuth - (float) avatarAngle;
        double theta = Math.toRadians(totalAz);
        double phi = Math.toRadians(cameraElevation);
        
        // Compute new camera position
        float x = cameraRadius * (float) (Math.cos(phi) * Math.sin(theta));
        float y = cameraRadius * (float) (Math.sin(phi));
        float z = cameraRadius * (float) (Math.cos(phi) * Math.cos(theta));

        camera.setLocation(new Vector3f(x, y, z).add(avatar.getWorldLocation()));
        camera.lookAt(avatar);
    }
    
     
    

    /**
     * Handles horizontal camera rotation (azimuth) around the avatar.
     */
    private class OrbitAzimuthAction extends AbstractInputAction {
        @Override
        public void performAction(float time, Event event) {
            float rotAmount = 0.0f;

            if (event.getValue() < -0.2) {
                rotAmount = -0.5f; // Rotate left
            } else if (event.getValue() > 0.2) {
                rotAmount = 0.5f; // Rotate right
            }

            // Handle keyboard input (Left/Right Arrow Keys)
            if (event.getComponent().getIdentifier() == Component.Identifier.Key.RIGHT) {
                rotAmount = 0.5f;
            } else if (event.getComponent().getIdentifier() == Component.Identifier.Key.LEFT) {
                rotAmount = -0.5f;
            }

            cameraAzimuth = (cameraAzimuth + rotAmount) % 360;
            updateCameraPosition();
        }
    }

    /**
     * Handles zooming in and out by adjusting the camera radius.
     */
    private class OrbitRadiusAction extends AbstractInputAction {
        @Override
        public void performAction(float time, Event event) {
            float zoomAmount = 0.0f;

            if (event.getValue() < -0.2) {
                zoomAmount = -0.2f; // Zoom in
            } else if (event.getValue() > 0.2) {
                zoomAmount = 0.2f; // Zoom out
            }

            // Handle keyboard input (T/R keys)
            if (event.getComponent().getIdentifier() == Component.Identifier.Key.T) {
                zoomAmount = 0.2f; // Zoom out
            } else if (event.getComponent().getIdentifier() == Component.Identifier.Key.R) {
                zoomAmount = -0.2f; // Zoom in
            }

            // Handle gamepad button input (_4/_5)
            if (event.getComponent().getIdentifier() == Component.Identifier.Button._4) {
                zoomAmount = 0.2f; // Zoom out
            } else if (event.getComponent().getIdentifier() == Component.Identifier.Button._5) {
                zoomAmount = -0.2f; // Zoom in
            }

            cameraRadius = Math.max(2.0f, Math.min(cameraRadius + zoomAmount, 20.0f)); // Clamp zoom range
            updateCameraPosition();
        }
    }

    /**
     * Handles vertical camera movement (elevation).
     */
    private class OrbitElevationAction extends AbstractInputAction {
        @Override
        public void performAction(float time, Event event) {
            float elevationChange = 0.0f;

            if (event.getValue() < -0.2) {
                elevationChange = -0.5f; // Look down
            } else if (event.getValue() > 0.2) {
                elevationChange = 0.5f; // Look up
            }

            // Handle keyboard input (Up/Down Arrow Keys)
            if (event.getComponent().getIdentifier() == Component.Identifier.Key.DOWN) {
                elevationChange = -0.5f;
            } else if (event.getComponent().getIdentifier() == Component.Identifier.Key.UP) {
                elevationChange = 0.5f;
            }

            cameraElevation = Math.max(-5.0f, Math.min(cameraElevation + elevationChange, 80.0f)); // Clamp elevation
            updateCameraPosition();
        }
    }

    
}