package jade;

import jade.Frame;
import jade.Sprite;
import util.AssetPool;

import java.util.ArrayList;
import java.util.List;

public class AnimationState {
    public String title;
    public List<Frame> animationFrames = new ArrayList<>();

    private static Sprite defaultSprite = new Sprite();
    private transient float timeTracker = 0.0f;
    private transient int currentSprite = 0;
    public boolean doesLoop = false;

    public void addFrame(Sprite sprite, float frameTime) {
        animationFrames.add(new Frame(sprite, frameTime));
    }

    public void setLoop(boolean b) {
        doesLoop = b;
    }

    public void update(float dt) {
        if (currentSprite < animationFrames.size()) {
            timeTracker -= dt;
            if (timeTracker <= 0) {
                if (doesLoop || currentSprite != animationFrames.size() - 1) {
                    currentSprite = (currentSprite + 1) % animationFrames.size();
                }
                timeTracker = animationFrames.get(currentSprite).frameTime;
            }
        }
    }

    public Sprite getCurrentSprite() {
        if (currentSprite < animationFrames.size()) {
            return animationFrames.get(currentSprite).sprite;
        }

        return defaultSprite;
    }

    public void refreshTextures() {
        for(Frame frame : animationFrames) {
            frame.sprite.setTexture((AssetPool.getTexture(frame.sprite.getTexture().getFilepath())));
        }
    }
}
