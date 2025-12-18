package secondEngine.objects;

import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;

import secondEngine.Window;
import secondEngine.Config.UIconfig;
import secondEngine.components.CompositeSpriteRenderer;
import secondEngine.components.InteractiveStateMachine;
import secondEngine.components.Overlay;
import secondEngine.components.AnimationStateMachine;
import secondEngine.components.Transform;
import secondEngine.components.helpers.Sprite;
import secondEngine.components.helpers.SpriteSheet;
import secondEngine.components.helpers.AnimationState;
import secondEngine.components.helpers.InteractableState;
import secondEngine.util.AssetPool;
import secondEngine.util.InteractableFactory.InteractableIds;
import secondEngine.util.PrefabFactory.PrefabIds;
import secondEngine.util.PrefabFactory.PrefabIds.OverlayPrefabs;
import secondEngine.util.PrefabFactory.PrefabIds.OverlayPrefabs.InventoryLayout;

public class OverlayObject {

    public static <OverlayType extends PrefabIds> GameObject generate(SpriteSheet spriteSheet,
            OverlayType overlayType) {
        GameObject overlayObj = Window.getScene().createGameObject("OverlayObjectGen");

        if (overlayType.getClass().isAssignableFrom(OverlayPrefabs.InventoryLayout.class)) {
            InventoryLayout layout = InventoryLayout.class.cast(overlayType);
            overlayObj = generateInventory(overlayObj, spriteSheet, layout);
        }
        return overlayObj;
    }

    public static GameObject generate(Sprite[] sprites, int numSpritesX, int numSpritesY) {
        int scale = UIconfig.getScale();
        GameObject overlayObj = Window.getScene().createGameObject("OverlayObjectGen",
                new Transform().init(new Vector3f(400, 400, 0), new Vector3f(scale, scale, 1)));

        Overlay overlay = new Overlay().init(overlayObj, sprites, numSpritesX, numSpritesY);
        overlayObj.addComponent(overlay);
        return overlayObj;
    }

    private static GameObject generateInventory(GameObject inventoryObject, SpriteSheet spriteSheet,
            InventoryLayout layout) {
        // int scale = UIconfig.getScale();
        // inventoryObject.transform.init(new Vector3f(16, 16, 0), new Vector3f(scale,
        // scale, 1));
        Sprite corner = spriteSheet.getSprite(0);
        Sprite edge = spriteSheet.getSprite(1);
        Sprite inventory = spriteSheet.getSprite(2);
        Sprite fill = spriteSheet.getSprite(3);
        Sprite[] sprites = { fill, corner, edge };
        Sprite[] layoutSprites = { inventory };
        Overlay overlay = new Overlay().init(inventoryObject, sprites, layoutSprites, AssetPool.getLayout(layout));
        inventoryObject.setName("inventoryObj");
        inventoryObject.addComponent(overlay);
        return inventoryObject;
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

        AnimationState defaultState = new AnimationState().init("default");
        defaultState.addFrame(gridSprite, new Vector4f(0, 0, 0, 0.1f), defaultFrameTime);

        AnimationState colored = new AnimationState().init("colored");
        colored.addFrame(new Vector4f(1, 0, 0, 0.1f), defaultFrameTime);

        AnimationStateMachine stateMachine = new AnimationStateMachine();

        stateMachine.addState(defaultState);
        stateMachine.addState(colored);

        stateMachine.setDefaultState(defaultState, defaultState);

        stateMachine.addTrigger(defaultState, colored, "addColor");
        stateMachine.addTrigger(colored, colored, "addColor");
        stateMachine.addTrigger(colored, defaultState, "removeColor");
        stateMachine.addTrigger(defaultState, defaultState, "removeColor");

        gridObj.addComponent(stateMachine);

        gridObj.setSerializeOnSave(false);

        return gridObj;
    }
}
