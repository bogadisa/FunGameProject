package secondEngine.components;

import org.joml.Vector2f;

import secondEngine.renderer.Texture;

/**
 *  Stores a sprite sheet texture and uv-coordinates of the sprite on the sprite sheet 
 */
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
        /* texCoords = {top right, bottom left, bottom right, top left} */
        this.texture = texture;
        this.texCoords = texCoords;
    }

    public Sprite() {
        this.texture = new Texture();
        Vector2f texCoords[] = {
            new Vector2f(1.0f, 1.0f),
            new Vector2f(0.0f, 0.0f),
            new Vector2f(1.0f, 0.0f),
            new Vector2f(0.0f, 1.0f)
        };
        this.texCoords = texCoords;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Texture getTexture() {
        return this.texture;
    }

    public Vector2f[] getTexCoords() {
        return this.texCoords;
    }
}
