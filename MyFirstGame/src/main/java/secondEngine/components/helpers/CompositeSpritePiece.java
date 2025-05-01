package secondEngine.components.helpers;

import org.joml.Vector3f;

import secondEngine.components.SpriteRenderer;

public class CompositeSpritePiece {
    public SpriteRenderer spriteRenderer;
    public Vector3f offset = new Vector3f(0);
    public float rotation = 0.0f;
    public boolean flip = false;

    public CompositeSpritePiece(SpriteRenderer spriteRenderer) {
        this.spriteRenderer = spriteRenderer;
    }
}
