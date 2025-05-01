package secondEngine.objects;

import org.joml.Vector3f;

import secondEngine.Window;
import secondEngine.components.CompositeSpriteRenderer;
import secondEngine.components.Sprite;
import secondEngine.components.SpriteRenderer;
import secondEngine.components.Transform;

public class OverlayObject {
    public static GameObject generate(Sprite[] sprites, float sizeX, float sizeY) {
        GameObject overlayObj = Window.getScene().createGameObject("OverlayObjectGen");
        int tileWidth = sprites[0].getTexture().getWidth();
        int tileHeight = sprites[0].getTexture().getWidth();
        CompositeSpriteRenderer compSprite = new CompositeSpriteRenderer().init();
        Sprite corner = sprites[0];
        Sprite edge = sprites[1];
        Sprite fill = sprites[2];
        float scale = 32;
        compSprite.addSpriteRenderer(new SpriteRenderer().setSprite(corner), 0);
        compSprite.addSpriteRenderer(new SpriteRenderer().setSprite(edge), new Vector3f(scale, 0, 2), 0);
        compSprite.addSpriteRenderer(new SpriteRenderer().setSprite(corner), new Vector3f(2*scale, 0, 2), true);
        compSprite.addSpriteRenderer(new SpriteRenderer().setSprite(corner), new Vector3f(2*scale, -scale, 2), 180);
        compSprite.addSpriteRenderer(new SpriteRenderer().setSprite(edge), new Vector3f(scale, -scale, 2), 180);
        compSprite.addSpriteRenderer(new SpriteRenderer().setSprite(corner), new Vector3f(0, -scale, 2), 180, true);

        overlayObj.addComponent(compSprite);
        overlayObj.addComponent(new Transform().init(new Vector3f(400, 400, 0), new Vector3f(scale, scale, 1)));
        return overlayObj;
    }
}
