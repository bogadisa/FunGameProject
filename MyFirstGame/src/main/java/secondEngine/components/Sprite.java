package secondEngine.components;

import org.joml.Vector2f;

import secondEngine.renderer.Texture;

public class Sprite {
    private Texture texture;
    private Vector2f[] texCoords;
    public Sprite(Texture texture) {
        this.texture = texture;
        Vector2f texCoords[] = {
            new Vector2f(1.0f, 1.0f),
            new Vector2f(0.0f, 0.0f),
            new Vector2f(1.0f, 0.0f),
            new Vector2f(0.0f, 1.0f)
        };
        this.texCoords = texCoords;
    }

    public Sprite(Texture texture, Vector2f[] texCoords) {
        this.texture = texture;
        this.texCoords = texCoords;
    }
}
