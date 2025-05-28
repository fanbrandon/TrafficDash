package a3.npc;

import tage.ai.behaviortrees.*;
import a3.MyGame;
import tage.*;
import org.joml.*;

public class CarOutOfBound extends BTCondition {
    private GameObject go;

    public CarOutOfBound(GameObject go) {
        super(false);
        this.go = go;
    }

    protected boolean check() {
        return go.getWorldLocation().x() < -17 || go.getWorldLocation().x > 17; // Reset if too far forward
    }
}
