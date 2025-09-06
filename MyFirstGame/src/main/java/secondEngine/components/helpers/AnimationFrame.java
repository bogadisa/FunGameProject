package secondEngine.components.helpers;

import org.joml.Vector4f;

public class AnimationFrame {
    public Sprite sprite;
    public float frameTime;
    public Vector4f color = new Vector4f(1, 1, 1, 1);

    public AnimationFrame(Sprite sprite, float frameTime) {
        this.sprite = sprite;
        this.frameTime = frameTime;
    }

    public AnimationFrame(Sprite sprite, Vector4f color, float frameTime) {
        this(sprite, frameTime);
        this.color = color;
    }
}
