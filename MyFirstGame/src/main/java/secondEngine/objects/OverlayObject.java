package secondEngine.objects;

import java.util.Map;

import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;

import secondEngine.Window;
import secondEngine.Config.UIconfig;
import secondEngine.components.CompositeSpriteRenderer;
import secondEngine.components.Inventory;
import secondEngine.components.Overlay;
import secondEngine.components.AnimationStateMachine;
import secondEngine.components.Transform;
import secondEngine.components.helpers.Sprite;
import secondEngine.components.helpers.SpriteSheet;
import secondEngine.objects.overlay.Layout;
import secondEngine.components.helpers.AnimationState;
import secondEngine.components.helpers.InventorySlot;
import secondEngine.util.AssetPool;
import secondEngine.util.PrefabFactory.PrefabIds;
import secondEngine.util.PrefabFactory.PrefabIds.OverlayPrefabs;
import secondEngine.util.PrefabFactory.PrefabIds.OverlayPrefabs.InventoryLayout;

public class OverlayObject {

    public static <OverlayType extends PrefabIds> GameObject generate(SpriteSheet spriteSheet,
            OverlayType overlayType) {
        GameObject overlayObj = Window.getScene().createGameObject("OverlayObjectGen",
                new Transform().init(new Vector3f(400, 400, 0)));

        if (overlayType.getClass().isAssignableFrom(OverlayPrefabs.InventoryLayout.class)) {
            InventoryLayout layout = InventoryLayout.class.cast(overlayType);
            overlayObj = generateInventory(overlayObj, spriteSheet, layout);
        }
        overlayObj.transform.scale = overlayObj.getComponent(Overlay.class).getScale();
        Window.getScene().worldGrid().addObject(overlayObj);
        return overlayObj;
    }

    public static GameObject generate(Sprite[] sprites, int numSpritesX, int numSpritesY) {
        int scale = UIconfig.getScale();
        GameObject overlayObj = Window.getScene().createGameObject("OverlayObjectGen",
                new Transform().init(new Vector3f(400, 400, 0), new Vector3f(scale, scale, 1)));

        Overlay overlay = new Overlay().init(overlayObj, sprites, numSpritesX, numSpritesY);
        overlayObj.addComponent(overlay);
        overlayObj.transform.scale = overlayObj.getComponent(Overlay.class).getScale();
        return overlayObj;
    }

    private static GameObject generateInventory(GameObject inventoryObject, SpriteSheet spriteSheet,
            InventoryLayout layout) {
        inventoryObject.setName("inventoryObj");
        Inventory inventory = new Inventory().init(layout.getNumSlots(), 64);
        Inventory inventoryObj = new Inventory().init(2, 64);
        InventorySlot slot1 = new InventorySlot(inventoryObj, PrefabIds.GroundPrefabs.Spring.DIRT_1, 1, 64);
        inventory.transferFrom(slot1, 1);
        inventoryObject.addComponent(inventory);
        Sprite cornerSprite = spriteSheet.getSprite(0);
        Sprite edgeSprite = spriteSheet.getSprite(1);
        Sprite inventorySprite = spriteSheet.getSprite(2);
        Sprite fillSprite = spriteSheet.getSprite(3);
        Sprite[] sprites = { fillSprite, cornerSprite, edgeSprite };
        Map<Layout.SlotType, Sprite> layoutSprites = Map.of(Layout.SlotType.Interactable.INVENTORY, inventorySprite);
        Overlay overlay = new Overlay().init(inventoryObject, sprites, layoutSprites, AssetPool.getLayout(layout));
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
        if (spriteRenderer == null) {
            spriteRenderer = new CompositeSpriteRenderer().init();
            gridObj.addComponent(spriteRenderer);
        }
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
