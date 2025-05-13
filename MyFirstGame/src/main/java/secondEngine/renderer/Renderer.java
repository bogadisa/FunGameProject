package secondEngine.renderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import secondEngine.components.CompositeSpriteRenderer;
import secondEngine.components.SpriteRenderer;
import secondEngine.objects.GameObject;

public class Renderer {
    private final int MAX_BATCH_SIZE = 1000;
    private List<BatchRenderer> batches;

    public Renderer() {
        this.batches = new ArrayList<>();
    }

    public void add(GameObject go) {
        if (go.getComponent(CompositeSpriteRenderer.class) != null) {
            add(go.getComponent(CompositeSpriteRenderer.class));
            return;
        }
            
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        int zIndex = (int)go.transform.position.z;
        if (spr != null) {
            add(spr, zIndex);
        }
    }

    public void add(CompositeSpriteRenderer compSprite) {
        for (int i = 0; i < compSprite.size(); i++) {
            SpriteRenderer sprite = compSprite.getSpriteRenderer(i);
            int zIndex = (int)compSprite.getTransform(i).position.z;
            add(sprite, zIndex);
        }
    }

    public void add(SpriteRenderer spriteRenderer) {
        int zIndex = (int)spriteRenderer.gameObject.transform.position.z;
        add(spriteRenderer, zIndex);

    }
    
    private void add(SpriteRenderer sprite, int zIndex) {
        if (sprite.isAddedToRenderer()) {
            return;
        }
        boolean added = false;
        for (BatchRenderer batch: batches) {
            if (batch.hasRoom() && batch.getzIndex() == zIndex) {
                Texture tex = sprite.getTexture();
                if (tex == null || batch.hasTextureRoom() || batch.hasTexture(tex)){
                    batch.addSprite(sprite);
                    added = true;
                    break;
                }
            }
        }
        if (!added) {
            BatchRenderer newBatch = new BatchRenderer(MAX_BATCH_SIZE, zIndex);
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSprite(sprite);
            Collections.sort(batches);
        }
    }

    public void render() {
        for (BatchRenderer batch: batches) {
            batch.render();
        }
    }
}
