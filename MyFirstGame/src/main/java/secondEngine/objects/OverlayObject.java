package secondEngine.objects;

import java.util.Map;
import java.util.Optional;

import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;

import secondEngine.Window;
import secondEngine.Config.UIconfig;
import secondEngine.components.CompositeSpriteRenderer;
import secondEngine.components.InteractiveStateMachine;
import secondEngine.components.Inventory;
import secondEngine.components.Overlay;
import secondEngine.components.TextRenderer;
import secondEngine.components.AnimationStateMachine;
import secondEngine.components.Transform;
import secondEngine.components.helpers.Sprite;
import secondEngine.components.helpers.SpriteSheet;
import secondEngine.components.helpers.TextBox;
import secondEngine.objects.Tiles.GroundManager.GroundPrefabs;
import secondEngine.objects.overlay.Layout;
import secondEngine.objects.overlay.OverlayManager.OverlayPrefabs;
import secondEngine.objects.overlay.OverlayManager.OverlayPrefabs.InventoryLayout;
import secondEngine.objects.overlay.OverlayManager.OverlayPrefabs.Special;
import secondEngine.components.helpers.AnimationState;
import secondEngine.components.helpers.InteractableState;
import secondEngine.components.helpers.InventorySlot;
import secondEngine.util.AssetPool;
import secondEngine.util.InteractableFactory.InteractableIds;
import secondEngine.util.PrefabFactory.PrefabIds;

public class OverlayObject {

    public static <OverlayType extends PrefabIds> GameObject generate(Optional<SpriteSheet> spriteSheet,
            OverlayType overlayType) {
        GameObject overlayObj = Window.getScene().createGameObject("OverlayObjectGen",
                new Transform().init(new Vector3f(400, 400, 0)));

        if (overlayType.isOfType(OverlayPrefabs.InventoryLayout.class)) {
            assert spriteSheet.isPresent() : "Cannot make inventory objects without a spritesheet present";
            InventoryLayout layout = InventoryLayout.class.cast(overlayType);
            overlayObj = generateInventory(overlayObj, spriteSheet.get(), layout);
        } else if (overlayType.isOfType(OverlayPrefabs.Special.class)) {
            Special special = OverlayPrefabs.Special.class.cast(overlayType);
            overlayObj = generateSpecial(overlayObj, special);
        }
        overlayObj.transform.scale = overlayObj.getComponent(Overlay.class).getScale();
        if (!overlayType.isOfType(OverlayPrefabs.Special.class)) {
            Window.getScene().worldGrid().addObject(overlayObj);
        }
        return overlayObj;
    }

    private static GameObject generateInventory(GameObject inventoryObject, SpriteSheet spriteSheet,
            InventoryLayout layout) {
        inventoryObject.setName("inventoryObj");

        Sprite cornerSprite = spriteSheet.getSprite(0);
        Sprite edgeSprite = spriteSheet.getSprite(1);
        Sprite inventorySprite = spriteSheet.getSprite(2);
        Sprite fillSprite = spriteSheet.getSprite(3);
        Sprite[] sprites = { fillSprite, cornerSprite, edgeSprite };
        Map<Layout.SlotType, Sprite> layoutSprites = Map.of(Layout.SlotType.Interactable.INVENTORY, inventorySprite);
        Layout overlayLayout = AssetPool.getLayout(layout);
        Vector2i scale = overlayLayout.getScale();
        Overlay overlay = inventoryObject.addComponent(new Overlay()).init(scale.x, scale.y, 0, sprites, false);
        overlay.addLayout(overlayLayout, layoutSprites);

        Inventory inventory = inventoryObject.addComponent(new Inventory()).init(layout.getNumSlots(), 64);
        Inventory tempInventory = new Inventory().init(2, 64);
        InventorySlot slot1 = new InventorySlot(tempInventory, GroundPrefabs.Spring.DIRT_1, 1, 64);
        inventory.transferFrom(slot1, 1);
        return inventoryObject;
    }

    public static GameObject generateSpecial(GameObject specialObject, Special special) {
        if (special == Special.GRID) {
            specialObject = generateGrid(specialObject);
        } else if (special == Special.F3_INFO) {
            specialObject = generateF3_Info(specialObject);
        }
        return specialObject;
    }

    public static GameObject generateGrid(GameObject gridObject) {
        gridObject.setName("gridObj");
        Sprite gridSprite = new Sprite().setTexture(AssetPool.getTexture("overlay/gridCell.png"));
        Sprite[] gridSprites = { gridSprite };

        Vector2i gridScale = Window.getScene().worldGrid().getGridScale();
        int gridSize = Window.getScene().worldGrid().getGridSize();

        Vector3f gridPosition = new Vector3f();
        gridPosition.x = Window.getScene().camera().position.x;
        gridPosition.y = Window.getScene().camera().position.y;
        gridPosition.z = gridObject.transform.position.z;
        gridObject.transform.position.set(gridPosition);

        Overlay overlay = gridObject.addComponent(new Overlay()).init(gridScale.x * 3, gridScale.y * 2, 0, gridSprites,
                true);

        // a white grid to test
        CompositeSpriteRenderer spriteRenderer = gridObject.getComponent(CompositeSpriteRenderer.class);
        if (spriteRenderer == null) {
            spriteRenderer = new CompositeSpriteRenderer().init();
            gridObject.addComponent(spriteRenderer);
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

        gridObject.addComponent(stateMachine);

        gridObject.setSerializeOnSave(false);

        return gridObject;
    }

    public static GameObject generateF3_Info(GameObject infoObject) {
        // InteractableState readPos = new InteractableState().init("readPos");
        // readPos.addFrame(InteractableIds.Misc.READ_POSITION);

        // TextRenderer text = new TextRenderer();
        // infoObject.addComponent(text);

        // Sprite sprite = new Sprite();
        // Sprite[] sprites = { sprite };
        // Overlay overlay = new Overlay().init(infoObject, sprites, null,
        // AssetPool.getLayout(LayoutType.TextLayout.F3_INFO));
        // infoObject.addComponent(overlay);

        // InteractiveStateMachine stateMachine = new InteractiveStateMachine();
        // stateMachine.addState(readPos);

        // infoObject.addComponent(stateMachine);

        // infoObject.setSerializeOnSave(false);
        return infoObject;
    }
}
