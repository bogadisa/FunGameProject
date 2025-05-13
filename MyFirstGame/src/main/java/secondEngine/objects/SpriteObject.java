package secondEngine.objects;

import secondEngine.Window;
import secondEngine.components.SpriteRenderer;
import secondEngine.components.helpers.Sprite;

public class SpriteObject {
    public static GameObject generate(Sprite sprite, float sizeX, float sizeY) {
        GameObject spriteObj = Window.getScene().createGameObject("SpriteObjectGen");
        spriteObj.transform.scale.x = sizeX;
        spriteObj.transform.scale.y = sizeY;

        SpriteRenderer renderer = new SpriteRenderer()
                                        .setSprite(sprite);
        spriteObj.addComponent(renderer);

        return spriteObj;
    }
}
