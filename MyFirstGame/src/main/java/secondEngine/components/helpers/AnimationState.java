package secondEngine.components.helpers;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector4f;

import secondEngine.util.AssetPool;

public class AnimationState {
    public String title;
    public List<Frame> animationFrames = new ArrayList<>();

    private static Sprite defaultSprite = new Sprite();
    private static Vector4f defaultColor = new Vector4f(1, 1, 1, 1);
    private transient float time = 0.0f;
    private transient int currentFrame = 0;
    private boolean doesLoop = false;

    public void refreshTextures() {
        for (Frame frame : animationFrames) {
            frame.sprite.setTexture(AssetPool.getTexture(frame.sprite.getTexture().getFilepath()));
        }
    }

    private Sprite getLatestSprite() {
        if (animationFrames.size() > 0) {
            Frame prevFrame = animationFrames.get(animationFrames.size() - 1);
            return prevFrame.sprite;
        }
        return defaultSprite;
    }

    public void addFrame(Sprite sprite, float frameTime) {
        animationFrames.add(new Frame(sprite, frameTime));
    }

    public void addFrame(Sprite sprite, Vector4f color, float frameTime) {
        animationFrames.add(new Frame(sprite, color, frameTime));
    }

    public void addFrame(Vector4f color, float frameTime) {
        Sprite prevSprite = getLatestSprite();
        animationFrames.add(new Frame(prevSprite, color, frameTime));
    }

    public void addFrames(List<Sprite> sprites, float frameTime) {
        for (Sprite sprite : sprites) {
            this.animationFrames.add(new Frame(sprite, frameTime));
        }
    }

    public void setLoop(boolean doesLoop) {
        this.doesLoop = doesLoop;
    }

    /**
     * @return True if the animation is done, False otherwise
     */
    public boolean update(float dt) {
        if (currentFrame < animationFrames.size()) {
            time -= dt;
            if (time <= 0) {
                if (!(currentFrame == animationFrames.size() - 1 && !doesLoop)) {
                    currentFrame = (currentFrame + 1) % animationFrames.size();
                } else {
                    // Only happens if it doesnt loop, and the animation is finished
                    return true;
                }
                time = animationFrames.get(currentFrame).frameTime;
            }
        }
        return false;
    }

    public Sprite getCurrentSprite() {
        if (currentFrame < animationFrames.size()) {
            return animationFrames.get(currentFrame).sprite;
        }

        return defaultSprite;
    }

    public Vector4f getCurrentColor() {
        if (currentFrame < animationFrames.size()) {
            return animationFrames.get(currentFrame).color;
        }
        return defaultColor;
    }
}
