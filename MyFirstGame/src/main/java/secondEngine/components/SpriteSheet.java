package secondEngine.components;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;

import secondEngine.Component;
import secondEngine.renderer.Texture;
import secondEngine.util.AssetPool;

public class SpriteSheet {
    private Texture texture;
    private List<Sprite> sprites;

    public SpriteSheet(String filepath) {
        this.sprites = new ArrayList<>();

        this.texture = AssetPool.getTexture(filepath);

        String metadata[] = filepath.substring(0, filepath.length() - 4).split("_");

        int spriteWidth = Integer.parseInt(metadata[1]);
        int spriteHeight = Integer.parseInt(metadata[2]);
        int numSprites = Integer.parseInt(metadata[3]);

        loadSpriteSheet(this.texture, spriteWidth, spriteHeight, numSprites, 0);
    }

    private void loadSpriteSheet(Texture texture, int spriteWidth, int spriteHeight, int numSprites, int spacing) {
        // System.out.println(spriteWidth + "" + spriteHeight + "" + numSprites);
        // System.exit(0);

        int i = 0;
        int currentX = 0;
        int currentY = 0;
        while (i < numSprites) {
            System.out.println(i);
            float topY = currentY + spriteHeight;
            float rightX = currentX + spriteWidth;
            float leftX = currentX;
            float bottomY = currentY;
            // TODO potential source of wrong image orientation
            Vector2f currentTexCoords[] = {
                    new Vector2f(rightX, topY),
                    new Vector2f(leftX, bottomY),
                    new Vector2f(rightX, bottomY),
                    new Vector2f(leftX, topY)
            };

            Sprite sprite = new Sprite(this.texture, currentTexCoords);
            this.sprites.add(sprite);
            i++;

            currentX += spriteWidth + spacing;
            if (currentX >= texture.getWidth()) {
                currentX = 0;
                currentY += spriteHeight + spacing;
            }
        }
    }

    public Sprite getSprite(int index) {
        return sprites.get(index);
    }
}
