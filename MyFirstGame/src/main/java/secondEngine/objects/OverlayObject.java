package secondEngine.objects;

import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;

import secondEngine.Window;
import secondEngine.Config.UIconfig;
import secondEngine.components.CompositeSpriteRenderer;
import secondEngine.components.Overlay;
import secondEngine.components.StateMachine;
import secondEngine.components.Transform;
import secondEngine.components.helpers.Sprite;
import secondEngine.components.helpers.AnimationState;
import secondEngine.util.AssetPool;

public class OverlayObject {
    public enum Layouts {
        STANDARD, LINE,
    }

    public static GameObject generate(Sprite[] sprites, int numSpritesX, int numSpritesY) {
        int scale = UIconfig.getScale();
        GameObject overlayObj = Window.getScene().createGameObject("OverlayObjectGen",
                new Transform().init(new Vector3f(400, 400, 0), new Vector3f(scale, scale, 1)));

        Overlay overlay = new Overlay().init(overlayObj, sprites, numSpritesX, numSpritesY);
        overlayObj.addComponent(overlay);
        return overlayObj;
    }

    public static GameObject generateInventory(Sprite[] sprites, Sprite[] layoutSprites, int[][] layout) {
        int scale = UIconfig.getScale();
        GameObject inventoryObj = Window.getScene().createGameObject("OverlayObjectGen",
                new Transform().init(new Vector3f(16, 16, 0), new Vector3f(scale, scale, 1)));

        Overlay overlay = new Overlay().init(inventoryObj, sprites, layoutSprites, layout);
        inventoryObj.addComponent(overlay);

        inventoryObj.setSerializeOnSave(false);

        return inventoryObj;
    }

    public static GameObject generateGrid() {
        Sprite gridSprite = new Sprite().setTexture(AssetPool.getTexture("overlay/gridCell.png"));
        Sprite[] gridSprites = { gridSprite };

        Vector2i gridScale = Window.getScene().worldGrid().getGridScale();
        int gridSize = Window.getScene().worldGrid().getGridSize();

        GameObject gridObj = OverlayObject.generate(gridSprites, gridScale.x * 3, gridScale.y * 2);
        gridObj.setName("gridObj");

        Vector3f gridPosition = new Vector3f();
        gridPosition.x = Window.getScene().camera().position.x + 0.5f * gridSize;
        gridPosition.y = Window.getScene().camera().position.y + 0.5f * gridSize;
        gridPosition.z = gridObj.transform.position.z;
        gridObj.transform.position.set(gridPosition);

        // Overlay overlay = gridObj.getComponent(Overlay.class);

        // a white grid to test
        CompositeSpriteRenderer spriteRenderer = gridObj.getComponent(CompositeSpriteRenderer.class);
        // a white grid to test
        // spriteRenderer.setColor(new Vector4f(1.0f, 1.0f, 1.0f, 0.5f));
        spriteRenderer.setColor(new Vector4f(0.0f, 0.0f, 0.0f, 0.1f));

        float defaultFrameTime = 5.0f;

        AnimationState defaultState = new AnimationState();
        defaultState.title = "default";
        defaultState.addFrame(gridSprite, new Vector4f(0, 0, 0, 0.1f), defaultFrameTime);

        AnimationState colored = new AnimationState();
        colored.title = "colored";
        colored.addFrame(new Vector4f(1, 0, 0, 0.1f), defaultFrameTime);

        StateMachine stateMachine = new StateMachine();

        stateMachine.addState(defaultState);
        stateMachine.addState(colored);

        stateMachine.setDefaultState(defaultState.title);

        stateMachine.addTrigger(defaultState.title, colored.title, "addColor");
        stateMachine.addTrigger(colored.title, colored.title, "addColor");
        stateMachine.addTrigger(colored.title, defaultState.title, "removeColor");
        stateMachine.addTrigger(defaultState.title, defaultState.title, "removeColor");

        gridObj.addComponent(stateMachine);

        gridObj.setSerializeOnSave(false);

        return gridObj;
    }
}
