package a3.npc;

import tage.ai.behaviortrees.*;
import a3.MyGame;
import tage.*;
import org.joml.*;


public class CarReset extends BTAction {
    private GameObject go;
    private Vector3f startPos;

    public CarReset(GameObject go, Vector3f startPos) {
        this.go = go;
        this.startPos = startPos;
    }

    public BTStatus update(float time) {
        Vector3f curr = go.getWorldLocation();
        float x = startPos.x();
        float y = startPos.y();
        float z = startPos.z(); 

        go.setLocalTranslation(new Matrix4f().translation(x, y, z));
        return BTStatus.BH_SUCCESS;
    }
}