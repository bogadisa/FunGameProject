package secondEngine.components;

import secondEngine.Window;
import secondEngine.components.helpers.Sprite;
import secondEngine.grid.GriddableComponent;
import secondEngine.renderer.BatchRendererSprite;
import secondEngine.renderer.Texture;
import secondEngine.util.AssetPool;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Stores information about how a sprite is rendered
 */
public class SpriteRenderer extends GriddableComponent {
    private Vector4f color = new Vector4f(1, 1, 1, 1);
    private Sprite sprite = new Sprite().setTexture(null);

    private transient Transform lastTransform;

    private boolean isHidden = false;
    private transient boolean isDirty = true;

    private transient CompositeSpriteRenderer compositeSpriteRenderer = null;
    private transient int compositeIndex = -1;

    private transient int batchIndex = -1;
    private transient BatchRendererSprite renderer;
    private transient boolean addedToRenderer = false;

    @Override
    public void start() {
        refreshTexture();
        updateZIndex((int) gameObject.transform.position.z);

        this.lastTransform = gameObject.transform.copy();
    }

    @Override
    public void update(float dt) {
        if (!this.lastTransform.equals(gameObject.transform)) {
            if (this.lastTransform.position.z != gameObject.transform.position.z) {
                updateZIndex((int) gameObject.transform.position.z);
            }
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

    private void updateZIndex(int zIndex) {
        if (this.renderer == null || this.renderer.getzIndex() == zIndex) {
            return;
        }
        this.renderer.removeSprite(this);
        Window.getScene().renderer().add(this);
    }

    private void updateRenderer(Texture texture) {
        if (this.renderer == null || this.renderer.hasTexture(texture)) {
            return;
        }
        this.renderer.removeSprite(this);
        Window.getScene().renderer().add(this);
    }

    public SpriteRenderer setTexture(Texture texture) {
        this.sprite.setTexture(texture);
        updateRenderer(texture);
        this.isDirty = true;
        return this;
    }

    public Texture getTexture() {
        return this.sprite.getTexture();
    }

    public SpriteRenderer setSprite(Sprite sprite) {
        // TODO needs to do something special for when the new sprite is in another
        // batch renderer?
        this.sprite = sprite;
        updateRenderer(sprite.getTexture());
        this.isDirty = true;
        return this;
    }

    public Transform getTransform() {
        if (this.compositeIndex >= 0) {
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

    public int getBatchIndex() {
        return this.batchIndex;
    }

    public void setAddedToRenderer(boolean addedToRenderer, BatchRendererSprite renderer, int index) {
        this.batchIndex = index;
        setAddedToRenderer(addedToRenderer, renderer);
    }

    public void setAddedToRenderer(boolean addedToRenderer, BatchRendererSprite renderer) {
        this.addedToRenderer = addedToRenderer;
        this.renderer = renderer;
        if (!addedToRenderer) {
            this.batchIndex = -1;
        }
    }

    public int getCompositeIndex() {
        return compositeIndex;
    }

    @Override
    public Vector3f getPosition() {
        return getTransform().position;
    }

    @Override
    public Vector3f getScale() {
        return getTransform().scale;
    }

    @Override
    public int getObjectId() {
        return this.hashCode();
    }

}