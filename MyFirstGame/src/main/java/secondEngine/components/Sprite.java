package secondEngine.components;

import org.joml.Vector2f;

import secondEngine.renderer.Texture;

/**
 *  Stores a sprite sheet texture and uv-coordinates of the sprite on the sprite sheet 
 */
public class Sprite {
    private Texture texture = new Texture();
    // private Vector2f[] texCoords = {
    //     new Vector2f(1.0f, 1.0f),
    //     new Vector2f(0.0f, 0.0f),
    //     new Vector2f(1.0f, 0.0f),
    //     new Vector2f(0.0f, 1.0f)
    // };
    private Vector2f[] texCoords = {
        new Vector2f(1.0f, 0.0f),
        new Vector2f(1.0f, 1.0f),
        new Vector2f(0.0f, 1.0f),
        new Vector2f(0.0f, 0.0f)
    };

    public Sprite setTexture(Texture texture) {
        this.texture = texture;
        return this;
    }

    public Texture getTexture() {
        return this.texture;
    }

    public Sprite setTexCoords(Vector2f[] texCoords) {
        this.texCoords = texCoords;
        return this;
    }

    public Vector2f[] getTexCoords() {
        return this.texCoords;
    }
}
