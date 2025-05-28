package tage.nodeControllers;
import tage.*;
import org.joml.*;
import java.lang.Math;

/**
* A MoveInCircleController is a node controller that moves an object in a circular path.
* The object moves based on elapsed time, radius, and speed.
*
*@author Brandon Fan
*/
public class MoveInCircleController extends NodeController {
    private float radius = 2.0f;
    private float speed = 1.0f;
    private Engine engine;
    private Vector3f initialPosition;
    private float totalTime = 0; // Accumulated time for smooth motion
    
    /** Default constructor with radius 2.0 and speed 1.0 */
    public MoveInCircleController() { super(); }
    
    /** Creates a MoveInCircleController with a specified radius and speed. */
    public MoveInCircleController(Engine e,GameObject go, float r, float s) {
        super();
        radius = r;
        speed = s;
        engine = e;
        initialPosition = new Vector3f();
        go.getLocalTranslation().getTranslation(initialPosition);
    }
    
    /** Sets the movement speed. */
    public void setSpeed(float s) { speed = s; }
    
    /** Sets the movement radius. */
    public void setRadius(float r) { radius = r; }
    
    /** Moves the object in a circular path. */
    public void apply(GameObject go) {
        totalTime += super.getElapsedTime() * speed; // Accumulate time for smooth motion
        float x = (float) Math.cos(totalTime) * radius;
        float z = (float) Math.sin(totalTime) * radius;
        
        go.setLocalTranslation(new Matrix4f().translation(initialPosition.x + x, initialPosition.y, initialPosition.z + z));
    }
}
