package a3.npc;

import tage.ai.behaviortrees.*;
import a3.MyGame;
import tage.*;
import org.joml.*;

public class CarTooFar extends BTCondition {
    private GameObject go;

    public CarTooFar(GameObject go) {
        super(false);
        this.go = go;
    }

    protected boolean check() {
        return go.getWorldLocation().z() < -95; // Reset if too far forward
    }
}
