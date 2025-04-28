package secondEngine.components.helpers;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;

import secondEngine.components.Sprite;
import secondEngine.renderer.Texture;
import secondEngine.util.AssetPool;

    /** 
     * Stores the sprite sheet texture, and a list of individual sprites 
    */
public class SpriteSheet {
    private Texture texture;
    private List<Sprite> sprites;

    public SpriteSheet(String filepath) {
        this.sprites = new ArrayList<>();

        this.texture = AssetPool.getTexture(filepath);

        String metadata[] = filepath.substring(0, filepath.length() - 4).split("_");

        int spriteWidth = this.texture.getWidth() / Integer.parseInt(metadata[1]);
        int spriteHeight = this.texture.getHeight() / Integer.parseInt(metadata[2]);
        int numSprites = Integer.parseInt(metadata[3]);

        loadSpriteSheet(this.texture, spriteWidth, spriteHeight, numSprites, 0);
    }

    private void loadSpriteSheet(Texture texture, int spriteWidth, int spriteHeight, int numSprites, int spacing) {
        // System.out.println(spriteWidth + "" + spriteHeight + "" + numSprites);
        // System.exit(0);

        int currentX = 0;
        int currentY = texture.getHeight() - spriteHeight;
        for (int i = 0; i < numSprites; i++) {
            float topY = (currentY + spriteHeight) / (float) texture.getHeight();
            float rightX = (currentX + spriteWidth) / (float) texture.getWidth();
            float leftX = currentX / (float) texture.getWidth();
            float bottomY = currentY / (float) texture.getHeight();
            // TODO potential source of wrong image orientation
            Vector2f currentTexCoords[] = {
                    new Vector2f(rightX, topY),
                    new Vector2f(rightX, bottomY),
                    new Vector2f(leftX, bottomY),
                    new Vector2f(leftX, topY)
            };

            Sprite sprite = new Sprite(this.texture, currentTexCoords);
            this.sprites.add(sprite);

            currentX += spriteWidth + spacing;
            if (currentX >= texture.getWidth()) {
                currentX = 0;
                currentY -= spriteHeight + spacing;
            }
        }
    }

    public Sprite getSprite(int index) {
        return sprites.get(index);
    }
}
