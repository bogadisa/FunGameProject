package secondEngine.objects;

import java.util.Optional;

import secondEngine.Window;
import secondEngine.components.SpriteRenderer;
import secondEngine.components.helpers.Sprite;

public class SpriteObject {
    public static GameObject generate(Optional<Sprite> sprite, float sizeX, float sizeY) {
        assert sprite.isPresent() : "Cannot make a sprite object without a sprite";
        return generate(sprite.get(), sizeX, sizeY);
    }

    public static GameObject generate(Sprite sprite, float sizeX, float sizeY) {
        GameObject spriteObj = Window.getScene().createGameObject("SpriteObjectGen");
        Window.getScene().worldGrid().addObject(spriteObj);
        spriteObj.transform.scale.x = sizeX;
        spriteObj.transform.scale.y = sizeY;

        spriteObj.addComponent(new SpriteRenderer()).setSprite(sprite);

        return spriteObj;
    }
}
