package secondEngine.components;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import secondEngine.Component;

public class CompositeSpriteRenderer extends Component {
    private List<SpriteRenderer> sprites;
    private List<Vector3f> offsets;

    private int numSprites;

    public CompositeSpriteRenderer init() {
        sprites = new ArrayList<>();
        offsets = new ArrayList<>();
        return this;
    }

    @Override
    public void start() {
        int index = 0;
        for (SpriteRenderer sprite: sprites) {
            sprite.setCompositeRenderer(this, index);

            sprite.start();

            index++;
        }
    }

    @Override
    public void update(float dt) {
        for (SpriteRenderer sprite: sprites) {
            sprite.update(dt);
        }
    }

    public CompositeSpriteRenderer addSpriteRenderer(SpriteRenderer spr) {
        return addSpriteRenderer(spr, new Vector3f(0, 0, 0));
    }

    public CompositeSpriteRenderer addSpriteRenderer(SpriteRenderer spr, Vector3f offset) {
        sprites.add(spr);
        offsets.add(offset);
        spr.setCompositeRenderer(this, numSprites);
        numSprites++;
        return this;
    }

    public SpriteRenderer getSpriteRenderer(int index) {
        return sprites.get(index);
    }

    public Transform getTransform(int index) {
        Vector3f offset = offsets.get(index);
        Transform actualPosition = gameObject.transform.copy();
        actualPosition.position.x += offset.x;
        actualPosition.position.y += offset.y;
        actualPosition.position.z += offset.z;
        return actualPosition;
    }

    public void refreshTextures() {
        for (SpriteRenderer sprite: sprites) {
            sprite.refreshTexture();
        }
    }

    public int size() {
        return this.numSprites;
    }

}
