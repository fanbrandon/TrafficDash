package tage.nodeControllers;
import tage.*;

import org.joml.*;
import java.lang.Math;

/**
 * A ScalingController is a node controller that, when enabled,
 * makes an object scale by smoothly increasing and decreasing its size.
 * 
 * @author Brandon Fan
 */
public class ScaleController extends NodeController {
    private float scaleFactor = 0.5f; // Maximum scale increase
    private float speed = 1.0f;
    private float totalTime = 0;

    /** Creates a scale controller with a specified scale and speed */
    public ScaleController(float scale, float s) {
        super();
        scaleFactor = scale;
        speed = s;
    }

    /** This is called automatically by the RenderSystem (via SceneGraph) once per frame
	*   during display().  It is for engine use and should not be called by the application.
	*/
    public void apply(GameObject go) {
        totalTime += super.getElapsedTime() * speed;
        float scale = 1.0f + (float) Math.sin(totalTime) * scaleFactor; // Scale variation

        go.setLocalScale(new Matrix4f().scaling(scale, scale, scale));
    }
}