package secondEngine.components.helpers;

import org.joml.Vector4f;

public class Frame {
    public Sprite sprite;
    public float frameTime;
    public Vector4f color = new Vector4f(1, 1, 1, 1);

    public Frame(Sprite sprite, float frameTime) {
        this.sprite = sprite;
        this.frameTime = frameTime;
    }

    public Frame(Sprite sprite, Vector4f color, float frameTime) {
        this(sprite, frameTime);
        this.color = color;
    }
}
