package secondEngine.components.helpers;

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

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof Sprite)) return false;

        Sprite s = (Sprite)o;
        return s.texCoords.equals(this.texCoords) && s.texture.getFilepath().equals(this.texture.getFilepath());
    }
}
