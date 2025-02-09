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

    }
    public SpriteSheet(Texture texture, int spriteWidth, int spriteHeight, int numSprites, int spacing) {
        this.sprites = new ArrayList<>();

        this.texture = texture;

        int i = 0;
        int currentX = 0;
        int currentY = 0;
        while (i < numSprites) {
            while (currentX < texture.getWidth()) {
                while (currentY < texture.getHeight()) {
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
                    

                    sprites.add(new Sprite(this.texture, currentTexCoords));

                    currentX += spriteWidth + spacing;
                    currentY += spriteHeight + spacing;
                }
            }
        }

    }

    public Sprite getSprite(int index) {
        return this.sprites.get(index);
    }

}
