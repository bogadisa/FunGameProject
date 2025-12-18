package secondEngine.components.helpers;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector4f;

import secondEngine.util.AssetPool;

public class AnimationState {
    private String title;

    public List<AnimationFrame> animationFrames = new ArrayList<>();

    private static Sprite defaultSprite = new Sprite();
    private static Vector4f defaultColor = new Vector4f(1, 1, 1, 1);
    private transient float time = 0.0f;
    private transient int currentFrame = 0;
    private boolean doesLoop = false;
    private boolean isColorAnimation = false;
    private boolean isSpriteAnimation = false;

    public AnimationState init(String title) {
        this.title = title;
        return this;
    }

    public void refreshTextures() {
        for (AnimationFrame frame : animationFrames) {
            if (frame.sprite != null) {
                frame.sprite.setTexture(AssetPool.getTexture(frame.sprite.getTexture().getFilepath()));
            }
        }
    }

    public void addFrame(Sprite sprite, float frameTime) {
        animationFrames.add(new AnimationFrame(sprite, frameTime));
        isSpriteAnimation = true;
    }

    public void addFrame(Sprite sprite, Vector4f color, float frameTime) {
        animationFrames.add(new AnimationFrame(sprite, color, frameTime));
        isSpriteAnimation = true;
        isColorAnimation = true;
    }

    public void addFrame(Vector4f color, float frameTime) {
        // Sprite prevSprite = getLatestSprite();
        // animationFrames.add(new AnimationFrame(prevSprite, color, frameTime));
        animationFrames.add(new AnimationFrame(null, color, frameTime));
        isColorAnimation = true;
    }

    public void addFrames(List<Sprite> sprites, float frameTime) {
        for (Sprite sprite : sprites) {
            this.addFrame(sprite, frameTime);
        }
    }

    public String getTitle() {
        return title;
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
        if (currentFrame < animationFrames.size() && isSpriteAnimation) {
            return animationFrames.get(currentFrame).sprite;
        }

        return defaultSprite;
    }

    public Vector4f getCurrentColor() {
        if (currentFrame < animationFrames.size() && isColorAnimation) {
            return animationFrames.get(currentFrame).color;
        }
        return defaultColor;
    }

    public boolean isColorAnimation() {
        return isColorAnimation;
    }

    public boolean isSpriteAnimation() {
        return isSpriteAnimation;
    }
}
