package secondEngine.components;

import secondEngine.Component;
import secondEngine.renderer.Texture;
import secondEngine.util.AssetPool;

import org.joml.Vector2f;
import org.joml.Vector4f;

/**
 * Stores information about how a sprite is rendered
 */
public class SpriteRenderer extends Component {
    private Vector4f color = new Vector4f(1, 1, 1, 1);
    private Sprite sprite = null;

    private transient Transform lastTransform;

    private transient boolean isDirty = true;

    // public SpriteRenderer(Vector4f color) {
    //     this.color = color;
    //     this.sprite = null;
    // }

    // public SpriteRenderer(Sprite sprite) {
    //     this.sprite = sprite;
    //     this.color = new Vector4f(1, 1, 1, 1);
    // }

    @Override
    public void start() {
        if (this.sprite.getTexture() != null) {
            this.sprite.setTexture(AssetPool.getTexture(this.sprite.getTexture().getFilepath()));
        }
        this.lastTransform = gameObject.transform.copy();
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
    public SpriteRenderer setColor(Vector4f color) {
        if (!this.color.equals(color)) {
            this.isDirty = true;
            this.color = color;
        }
        
        return this;
    }

    public SpriteRenderer setTexture(Texture texture) {
        this.sprite.setTexture(texture);
        this.isDirty = true;
        return this;
    }

    public Texture getTexture() {
        return this.sprite.getTexture();
    }
    
    public SpriteRenderer setSprite(Sprite sprite) {
        this.sprite = sprite;
        this.isDirty = true;
        return this;
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