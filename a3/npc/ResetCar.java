package a3.npc;

import tage.ai.behaviortrees.*;
import a3.MyGame;
import tage.*;
import org.joml.*;


public class ResetCar extends BTAction {
    private GameObject car;
    private Vector3f startPos;

    public ResetCar(GameObject car, Vector3f startPos) {
        this.car = car;
        this.startPos = startPos;
    }

    public BTStatus update(float time) {
        Vector3f curr = car.getWorldLocation();
        float x = curr.x();
        float y = curr.y();
        float z = startPos.z(); // reset Z only

        car.setLocalTranslation(new Matrix4f().translation(x, y, z));
        return BTStatus.BH_SUCCESS;
    }
}
