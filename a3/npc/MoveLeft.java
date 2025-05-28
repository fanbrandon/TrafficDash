package a3.npc;

import tage.ai.behaviortrees.*;
import a3.MyGame;
import tage.*;
import org.joml.*;

public class MoveLeft extends BTAction {
    private GameObject go;

    public MoveLeft(GameObject go) {
        this.go = go;
    }

    public BTStatus update(float elapsedTimeMS) {
        Vector3f pos = go.getWorldLocation();
        go.setLocalTranslation(go.getLocalTranslation().translate(-3.0f, 0f, 0f));
        return BTStatus.BH_SUCCESS;
    }
}
