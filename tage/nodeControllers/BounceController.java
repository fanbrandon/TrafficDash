package tage.nodeControllers;
import tage.*;
import org.joml.*;
import java.lang.Math;

/**
 * A BounceController is a node controller that makes an object move up and down smoothly in a bouncing motion.
 * 
 * @author Brandon Fan
 */
public class BounceController extends NodeController {
    private float amplitude = 2.0f;  // Maximum bounce height
    private float speed = 1.0f;      // Speed of bouncing
    private Engine engine;
    private Vector3f initialPosition;
    private float totalTime = 0;     // Accumulated time for smooth motion

    /** Creates a default bounce controller with amplitude 2.0 and speed 1.0 */
    public BounceController() { super(); }

    /** Creates a bounce controller with a specified amplitude and speed. */
    public BounceController(Engine e, GameObject go, float amp, float s) {
        super();
        amplitude = amp;
        speed = s;
        engine = e;
        initialPosition = new Vector3f();
        go.getLocalTranslation().getTranslation(initialPosition); // Store initial position
    }

    /** Sets the bounce amplitude (max height). */
    public void setAmplitude(float amp) { amplitude = amp; }

    /** Sets the bounce speed. */
    public void setSpeed(float s) { speed = s; }

    /** Moves the object in a bouncing motion. */
    public void apply(GameObject go) {
        totalTime += super.getElapsedTime() * speed;  // Accumulate time for smooth motion
        float bounceOffset = (float) Math.abs(Math.sin(totalTime)) * amplitude; // Smooth bounce

        // Apply bouncing only on the Y-axis, keeping X and Z unchanged
        go.setLocalTranslation(new Matrix4f().translation(
            initialPosition.x, initialPosition.y + bounceOffset, initialPosition.z));
    }
}