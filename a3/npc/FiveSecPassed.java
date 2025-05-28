package a3.npc;

import tage.ai.behaviortrees.*;
import a3.MyGame;
import tage.*;
import org.joml.*;

public class FiveSecPassed extends BTCondition {
    private MyGame game;
    private long lastShrinkTime;

    public FiveSecPassed(MyGame game) {
        super(false);
        this.game = game;
        lastShrinkTime = System.nanoTime();
    }

    protected boolean check() {
        long now = System.nanoTime();
        float elapsedSec = (now - lastShrinkTime) / 1_000_000_000.0f;
        if (elapsedSec > 3.0f) {
            lastShrinkTime = now;
            return true;
        }
        return false;
    }
}