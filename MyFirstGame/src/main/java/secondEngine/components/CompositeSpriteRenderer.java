package secondEngine.components;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;
import org.joml.Vector4f;

import secondEngine.Component;
import secondEngine.components.helpers.CompositeSpritePiece;

public class CompositeSpriteRenderer extends Component {
    private List<CompositeSpritePiece> spritePieces;

    private int numSprites;

    public CompositeSpriteRenderer init() {
        spritePieces = new ArrayList<>();
        return this;
    }

    @Override
    public void start() {
        int index = 0;
        for (CompositeSpritePiece spritePiece: spritePieces) {
            spritePiece.spriteRenderer.setCompositeRenderer(this, index);

            spritePiece.spriteRenderer.start();

            index++;
        }
    }

    @Override
    public void update(float dt) {
        for (CompositeSpritePiece spritePiece: spritePieces) {
            spritePiece.spriteRenderer.update(dt);
        }
    }

    public CompositeSpriteRenderer addSpriteRenderer(SpriteRenderer spr) {
        return addSpriteRenderer(spr, new Vector3f(0, 0, 0));
    }

    public CompositeSpriteRenderer addSpriteRenderer(SpriteRenderer spr, Vector3f offset) {
        return addSpriteRenderer(spr, offset, 0, false);
    }
    public CompositeSpriteRenderer addSpriteRenderer(SpriteRenderer spr, float rotation) {
        return addSpriteRenderer(spr, new Vector3f(0, 0, 0), rotation, false);
    }

    public CompositeSpriteRenderer addSpriteRenderer(SpriteRenderer spr, Vector3f offset, boolean flip) {
        return addSpriteRenderer(spr, offset, 0, flip);
    }

    public CompositeSpriteRenderer addSpriteRenderer(SpriteRenderer spr, Vector3f offset, float rotation) {
        return addSpriteRenderer(spr, offset, rotation, false);
    }

    public CompositeSpriteRenderer addSpriteRenderer(SpriteRenderer spr, Vector3f offset, float rotation, boolean flip) {
        CompositeSpritePiece piece = new CompositeSpritePiece(spr);
        piece.offset = offset;
        piece.rotation = rotation;
        piece.flip = flip;
        spritePieces.add(piece);
        spr.setCompositeRenderer(this, numSprites);
        numSprites++;
        return this;
    }

    

    public SpriteRenderer getSpriteRenderer(int index) {
        return spritePieces.get(index).spriteRenderer;
    }

    public Transform getTransform(int index) {
        Transform transform = gameObject.transform.copy();
        CompositeSpritePiece piece = spritePieces.get(index);
        transform.position.add(piece.offset);
        transform.rotation = piece.rotation;
        if (piece.flip) {
            transform.scale.x = -transform.scale.x;
        }
        return transform;
    }

    public CompositeSpriteRenderer refreshTextures() {
        for (CompositeSpritePiece spritePiece: spritePieces) {
            spritePiece.spriteRenderer.refreshTexture();
        }
        return this;
    }

    public CompositeSpriteRenderer setColor(Vector4f color) {
        for (CompositeSpritePiece spritePiece: spritePieces) {
            spritePiece.spriteRenderer.setColor(color);
        }
        return this;
    }

    public CompositeSpriteRenderer setDirty() {
        for (CompositeSpritePiece spritePiece: spritePieces) {
            spritePiece.spriteRenderer.setDirty();
        }
        return this;
    }

    public int size() {
        return this.numSprites;
    }

}
