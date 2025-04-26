package secondEngine.components;

import secondEngine.Component;
import secondEngine.renderer.Texture;

import org.joml.Vector2f;
import org.joml.Vector4f;

/**
 * Stores information about how a sprite is rendered
 */
public class SpriteRenderer extends Component {
    private Vector4f color;
    private Sprite sprite;

    private Transform lastTransform;

    private boolean isDirty = false;

    public SpriteRenderer(Vector4f color) {
        this.color = color;
        this.sprite = null;
    }

    public SpriteRenderer(Sprite sprite) {
        this.sprite = sprite;
        this.color = new Vector4f(1, 1, 1, 1);
    }

    @Override
    public void start() {
        lastTransform = gameObject.transform.copy();
        isDirty = true;
    }

    @Override
    public void update(float dt) {
        if (!this.lastTransform.equals(gameObject.transform) ) {
            gameObject.transform.copy(lastTransform);
            isDirty = true;
        }
    }

    public Vector4f getColor() {
        return this.color;
    }
    public void setColor(Vector4f color) {
        if (!this.color.equals(color)) {
            this.isDirty = true;
            this.color = color;
        }
    }

    public void setTexture(Texture texture) {
        this.sprite.setTexture(texture);
        this.isDirty = true;
    }

    public Texture getTexture() {
        return this.sprite.getTexture();
    }
    
    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        this.isDirty = true;
    }

    public Vector2f[] getTexCoords() {
        return this.sprite.getTexCoords();
    }

    public boolean isDirty() {
        return this.isDirty;
    }

    public void setDirty() {
        this.isDirty = true;
    }

    public void setClean() {
        this.isDirty = false;
    }
}