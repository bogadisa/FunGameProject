package secondEngine.objects;

import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;

import secondEngine.Window;
import secondEngine.Config.UIconfig;
import secondEngine.components.CompositeSpriteRenderer;
import secondEngine.components.Overlay;
import secondEngine.components.Transform;
import secondEngine.components.helpers.GridState;
import secondEngine.components.helpers.Sprite;
import secondEngine.util.AssetPool;

public class OverlayObject {
    public static GameObject generate(Sprite[] sprites, int numSpritesX, int numSpritesY) {
        int scale = UIconfig.getScale();
        GameObject overlayObj = Window.getScene().createGameObject("OverlayObjectGen", new Transform().init(new Vector3f(400, 400, 0), new Vector3f(scale, scale, 1)));
        
        Overlay overlay = new Overlay().init(overlayObj, sprites, numSpritesX, numSpritesY);
        overlayObj.addComponent(overlay);
        return overlayObj;
    }

    public static GameObject generateGrid() {
        Sprite gridSprite = new Sprite().setTexture(AssetPool.getTexture("overlay/gridCell.png"));
        Sprite[] gridSprites = {gridSprite, gridSprite, gridSprite, gridSprite};

        Vector2i gridScale = GridState.getGridScale();
        
        GameObject gridObj  = OverlayObject.generate(gridSprites, gridScale.x*3, gridScale.y*2);
        Vector3f gridPosition = new Vector3f();
        gridPosition.x = Window.getScene().camera().position.x + 0.5f*GridState.getGridSize();
        gridPosition.y = Window.getScene().camera().position.y + 0.5f*GridState.getGridSize();
        gridPosition.z = gridObj.transform.position.z;
        gridObj.transform.position.set(gridPosition);

        Overlay overlay = gridObj.getComponent(Overlay.class);

        // a white grid to test
        CompositeSpriteRenderer spriteRenderer = gridObj.getComponent(CompositeSpriteRenderer.class);
        // a white grid to test
        // spriteRenderer.setColor(new Vector4f(1.0f, 1.0f, 1.0f, 0.5f));
        spriteRenderer.setColor(new Vector4f(0.0f, 0.0f, 0.0f, 0.1f));
        gridObj.setName("gridObj");
        gridObj.setSerializeOnSave(false);

        return gridObj;
    }
}
