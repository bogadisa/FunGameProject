package secondEngine.renderer;

import java.util.ArrayList;
import java.util.List;

import secondEngine.components.SpriteRenderer;
import secondEngine.objects.GameObject;

public class Renderer {
    private final int MAX_BATCH_SIZE = 1000;
    private List<BatchRenderer> batches;

    public Renderer() {
        this.batches = new ArrayList<>();
    }

    public void add(GameObject go) {
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        if (spr != null) {
            add(spr);
        }
    }
    
    private void add(SpriteRenderer sprite) {
        boolean added = false;
        for (BatchRenderer batch: batches) {
            if (batch.hasRoom()) {
                Texture tex = sprite.getTexture();
                if (tex == null || batch.hasTextureRoom() || batch.hasTexture(tex)){
                    batch.addSprite(sprite);
                    added = true;
                    break;
                }
            }
        }
        if (!added) {
            BatchRenderer newBatch = new BatchRenderer(MAX_BATCH_SIZE);
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSprite(sprite);
        }
    }

    public void render() {
        for (BatchRenderer batch: batches) {
            batch.render();
        }
    }
}
