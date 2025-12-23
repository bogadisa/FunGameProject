package secondEngine.components;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import secondEngine.Component;
import secondEngine.Window;
import secondEngine.components.helpers.CompositeSpritePiece;

public class CompositeSpriteRenderer extends Component {
    private List<CompositeSpritePiece> spritePieces;

    private int numSprites;

    private boolean started = false;

    public CompositeSpriteRenderer init() {
        spritePieces = new ArrayList<>();
        return this;
    }

    private void startSpritePiece(int index) {
        CompositeSpritePiece spritePiece = spritePieces.get(index);
        spritePiece.spriteRenderer.setCompositeRenderer(this, index);

        spritePiece.spriteRenderer.start();
    }

    @Override
    public void start() {
        for (int i = 0; i < spritePieces.size(); i++) {
            startSpritePiece(i);
        }
        this.started = true;
    }

    @Override
    public void update(float dt) {
        for (CompositeSpritePiece spritePiece : spritePieces) {
            spritePiece.spriteRenderer.update(dt);
        }
    }

    public CompositeSpriteRenderer addSpriteRenderer(SpriteRenderer spr, Vector3f scale) {
        return addSpriteRenderer(spr, scale, new Vector3f(0, 0, 0));
    }

    public CompositeSpriteRenderer addSpriteRenderer(SpriteRenderer spr, Vector3f scale, Vector3f offset) {
        return addSpriteRenderer(spr, scale, offset, 0, false);
    }

    public CompositeSpriteRenderer addSpriteRenderer(SpriteRenderer spr, Vector3f scale, float rotation) {
        return addSpriteRenderer(spr, scale, new Vector3f(0, 0, 0), rotation, false);
    }

    public CompositeSpriteRenderer addSpriteRenderer(SpriteRenderer spr, Vector3f scale, Vector3f offset,
            boolean flip) {
        return addSpriteRenderer(spr, scale, offset, 0, flip);
    }

    public CompositeSpriteRenderer addSpriteRenderer(SpriteRenderer spr, Vector3f scale, Vector3f offset,
            float rotation) {
        return addSpriteRenderer(spr, scale, offset, rotation, false);
    }

    public CompositeSpriteRenderer addSpriteRenderer(SpriteRenderer spr, Vector3f scale, Vector3f offset,
            float rotation, boolean flip) {
        CompositeSpritePiece piece = new CompositeSpritePiece(spr, scale);
        piece.offset = offset;
        piece.rotation = rotation;
        piece.flip = flip;
        spritePieces.add(piece);
        spr.setCompositeRenderer(this, numSprites);
        if (started) {
            startSpritePiece(numSprites);
        }
        numSprites++;

        return this;
    }

    public SpriteRenderer getSpriteRenderer(int index) {
        return spritePieces.get(index).spriteRenderer;
    }

    public void updateOffset(int index, Vector3f offset) {
        CompositeSpritePiece piece = spritePieces.get(index);
        piece.offset = offset;
    }

    public Transform getTransform(int index) {
        Transform transform = gameObject.transform.copy();
        CompositeSpritePiece piece = spritePieces.get(index);
        transform.position.add(piece.offset);
        transform.rotation = piece.rotation;
        transform.scale = piece.scale;
        if (piece.flip) {
            transform.scale.x = -transform.scale.x;
        }
        return transform;
    }

    public CompositeSpriteRenderer refreshTextures() {
        for (CompositeSpritePiece spritePiece : spritePieces) {
            spritePiece.spriteRenderer.refreshTexture();
        }
        return this;
    }

    public CompositeSpriteRenderer setColor(Vector4f color) {
        for (CompositeSpritePiece spritePiece : spritePieces) {
            spritePiece.spriteRenderer.setColor(color);
        }
        return this;
    }

    public CompositeSpriteRenderer setDirty() {
        for (CompositeSpritePiece spritePiece : spritePieces) {
            spritePiece.spriteRenderer.setDirty();
        }
        return this;
    }

    public int size() {
        return this.numSprites;
    }

}
