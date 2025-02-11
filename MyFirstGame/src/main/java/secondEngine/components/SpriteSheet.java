package secondEngine.components;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;

import secondEngine.renderer.Texture;

public class SpriteSheet {
    private Texture texture;
    private List<Sprite> sprites;

    public SpriteSheet(Texture texture) {
        this.sprites = new ArrayList<>();

        this.texture = texture;

        this.sprites.add(new Sprite(texture));
    }

    public SpriteSheet(Texture texture, int spriteWidth, int spriteHeight, int numSprites, int spacing) {
        this.sprites = new ArrayList<>();

        this.texture = texture;

        loadSpriteSheet(texture, spriteWidth, spriteHeight, numSprites, spacing);
    }

    public SpriteSheet(String filepath) {
        this.sprites = new ArrayList<>();

        this.texture = new Texture(filepath);

        String metadata[] = filepath.split("_");
        metadata[3] = metadata[3].substring(0, 2); // removing .png

        int spriteWidth = Integer.parseInt(metadata[1]);
        int spriteHeight = Integer.parseInt(metadata[2]);
        int numSprites = Integer.parseInt(metadata[3]);

        loadSpriteSheet(this.texture, numSprites, spriteWidth, spriteHeight, 0);
    }

    private void loadSpriteSheet(Texture texture, int spriteWidth, int spriteHeight, int numSprites, int spacing) {

        int i = 0;
        int currentX = 0;
        int currentY = 0;
        while (i < numSprites) {
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

            this.sprites.add(new Sprite(this.texture, currentTexCoords));

            currentX += spriteWidth + spacing;
            if (currentX >= texture.getWidth()) {
                currentX = 0;
                currentY += spriteHeight + spacing;
            }
        }
    }

    public Sprite getSprite(int index) {
        return this.sprites.get(index);
    }

}
