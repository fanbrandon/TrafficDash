package a3.client;

import java.util.UUID;

import tage.*;
import tage.shapes.*;

import org.joml.*;
import a3.*;

// A ghost MUST be connected as a child of the root,
// so that it will be rendered, and for future removal.
// The ObjShape and TextureImage associated with the ghost
// must have already been created during loadShapes() and
// loadTextures(), before the game loop is started.

public class GhostAvatar extends GameObject {
    UUID uuid;
    private AnimatedShape animatedShape;

    public GhostAvatar(UUID id, AnimatedShape s, TextureImage t, Vector3f p) {
        super(GameObject.root(), s, t);
        uuid = id;
        setPosition(p);
        this.animatedShape = s;
        animatedShape.playAnimation("Idle", 0.5f, AnimatedShape.EndType.LOOP, 0);
    }

    public UUID getID() { return uuid; }
    public void setPosition(Vector3f m) { setLocalLocation(m); }
    public Vector3f getPosition() { return getWorldLocation(); }

    public void playAnimation(String animationName, float speed, AnimatedShape.EndType endType, int startFrame) {
        animatedShape.playAnimation(animationName, speed, endType, startFrame);
    }

    public void stopAnimation() {
        animatedShape.stopAnimation();
    }

    public void updateAnimation() {
        animatedShape.updateAnimation();
    }
}
