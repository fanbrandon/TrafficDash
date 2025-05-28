package tage.nodeControllers;

import tage.*;
import org.joml.*;

public class DriveController extends NodeController {
    private float speed = 1.0f;
    private Engine engine;
    private Vector3f initialPosition;
    private float totalTime = 0;

    public DriveController() {
        super();
    }

    public DriveController(Engine e, GameObject go, float s) {
        super();
        engine = e;
        speed = s;
        initialPosition = new Vector3f();
        go.getLocalTranslation().getTranslation(initialPosition);
    }

    @Override
    public void apply(GameObject go) {
        float elapsedTime = super.getElapsedTime();
        totalTime += elapsedTime;

        float zOffset = speed * totalTime;
        float newZ = initialPosition.z - zOffset;

        // Reset to initial position if it goes past -25.0 in Z
        if (newZ < -25.0f) {
            totalTime = 0; // Reset time to restart movement
            go.setLocalTranslation(new Matrix4f().translation(initialPosition));
        } else {
            go.setLocalTranslation(new Matrix4f().translation(initialPosition.x, initialPosition.y, newZ));
        }
    }
}