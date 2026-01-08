package secondEngine.renderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import secondEngine.components.CompositeSpriteRenderer;
import secondEngine.components.SpriteRenderer;
import secondEngine.components.TextRenderer;
import secondEngine.objects.GameObject;

public class Renderer {
    private final int MAX_BATCH_SIZE = 1000;
    private List<BatchRendererSprite> spriteBatches;
    private List<BatchRendererFont> textBatches;

    public Renderer() {
        this.spriteBatches = new ArrayList<>();
        this.textBatches = new ArrayList<>();
    }

    public void add(GameObject go) {
        go.getComponent(CompositeSpriteRenderer.class).ifPresent(compSprite -> add(compSprite));

        go.getComponent(SpriteRenderer.class).ifPresent(spr -> add(spr, (int) go.transform.position.z));

        go.getComponent(TextRenderer.class).ifPresent(text -> add(text));
    }

    public void add(CompositeSpriteRenderer compSprite) {
        for (int i = 0; i < compSprite.size(); i++) {
            SpriteRenderer sprite = compSprite.getSpriteRenderer(i);
            int zIndex = (int) compSprite.getTransform(i).position.z;
            add(sprite, zIndex);
        }
    }

    public void add(SpriteRenderer spriteRenderer) {
        int zIndex = (int) spriteRenderer.getTransform().position.z;
        add(spriteRenderer, zIndex);

    }

    private void add(SpriteRenderer sprite, int zIndex) {
        if (sprite.isAddedToRenderer()) {
            return;
        }
        boolean added = false;
        for (BatchRendererSprite batch : spriteBatches) {
            if (batch.hasRoom() && batch.getzIndex() == zIndex) {
                Texture tex = sprite.getTexture();
                if (tex == null || batch.hasTextureRoom() || batch.hasTexture(tex)) {
                    batch.addSprite(sprite);
                    added = true;
                    break;
                }
            }
        }
        if (!added) {
            BatchRendererSprite newBatch = new BatchRendererSprite(MAX_BATCH_SIZE, zIndex);
            newBatch.start();
            spriteBatches.add(newBatch);
            newBatch.addSprite(sprite);
            Collections.sort(spriteBatches);
        }
    }

    public void add(TextRenderer text) {
        if (text.isAddedToRenderer()) {
            return;
        }
        if (textBatches.isEmpty()) {
            BatchRendererFont newBatch = new BatchRendererFont(128);
            newBatch.start();
            textBatches.add(newBatch);
        }
        BatchRendererFont batch = textBatches.get(0);
        batch.addText(text);
    }

    public void render() {
        for (BatchRendererSprite batch : spriteBatches) {
            batch.render();
        }
        for (BatchRendererFont batch : textBatches) {
            batch.render();
        }
    }
}
