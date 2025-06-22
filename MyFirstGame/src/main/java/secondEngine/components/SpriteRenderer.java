package secondEngine.components;

import secondEngine.Component;
import secondEngine.components.helpers.Sprite;
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

    private boolean isHidden = false;
    private transient boolean isDirty = true;

    private transient CompositeSpriteRenderer compositeSpriteRenderer = null;
    private transient int compositeIndex;

    private transient boolean addedToRenderer = false;

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

    public void refreshTexture() {
        if (this.sprite.getTexture() != null) {
            this.sprite.setTexture(AssetPool.getTexture(this.sprite.getTexture().getFilepath()));
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
        // TODO needs to do something special for when the new sprite is in another batch renderer?
        this.sprite = sprite;
        this.isDirty = true;
        return this;
    }

    public Transform getTransform() {
        if (this.compositeSpriteRenderer != null) {
            return this.compositeSpriteRenderer.getTransform(compositeIndex);
        }

        return this.gameObject.transform;
    }

    public Vector2f[] getTexCoords() {
        return this.sprite.getTexCoords();
    }

    public boolean isHidden() {
        return this.isHidden;
    }
    
    public void setHidden(boolean isHidden) {
        this.isDirty = true;
        this.isHidden = isHidden;
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

    public void setCompositeRenderer(CompositeSpriteRenderer compSprite, int index) {
        this.gameObject = compSprite.gameObject;
        compositeSpriteRenderer = compSprite;
        compositeIndex = index;
    }

    public boolean isAddedToRenderer() {
        return addedToRenderer;
    }

    public void setAddedToRenderer(boolean addedToRenderer) {
        this.addedToRenderer = addedToRenderer;
    }
}