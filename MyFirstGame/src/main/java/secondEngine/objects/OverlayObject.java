package secondEngine.objects;

import org.joml.Vector3f;

import secondEngine.Config;
import secondEngine.Window;
import secondEngine.Config.UIconfig;
import secondEngine.components.CompositeSpriteRenderer;
import secondEngine.components.Sprite;
import secondEngine.components.SpriteRenderer;
import secondEngine.components.Transform;

public class OverlayObject {
    public static GameObject generate(Sprite[] sprites, int numSpritesX, int numSpritesY) {
        Config.getUIconfig();
        int scale = UIconfig.getScale();
        GameObject overlayObj = Window.getScene().createGameObject("OverlayObjectGen", new Transform().init(new Vector3f(400, 400, 0), new Vector3f(scale, scale, 1)));
        CompositeSpriteRenderer compSprite = new CompositeSpriteRenderer().init();
        Sprite corner = sprites[0];
        Sprite edge = sprites[1];
        Sprite fill = sprites[2];

        for (int i = 1; i <= numSpritesX; i++) {
            for (int j = 1; j <= numSpritesY; j++) {
                Sprite piece = fill;
                int rotation = 0;
                boolean flip = false;
                if (j == 1) {
                    piece = edge;
                    rotation = 180;
                    if (i == 1 || i == numSpritesX) {
                        piece = corner;
                        if (i == 1) {
                            flip = true;
                        }
                    }
                } else if (j == numSpritesY) {
                    piece = edge;
                    if (i == 1 || i == numSpritesX) {
                        piece = corner;
                        if (i == numSpritesX) {
                            flip = true;
                        }
                    }
                } else {
                    if (i == 1 || i == numSpritesX) {
                        piece = edge;
                        rotation = 90;
                        if (i == numSpritesX) {
                            flip = true;
                        }
                    }
                }
    
                compSprite.addSpriteRenderer(new SpriteRenderer().setSprite(piece), new Vector3f((i - 1) * scale, (j - 1) * scale, 2), rotation, flip);
            }
        }
        
        overlayObj.addComponent(compSprite);
        return overlayObj;
    }
}
