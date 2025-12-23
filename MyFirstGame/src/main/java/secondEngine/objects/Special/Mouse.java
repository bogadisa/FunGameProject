package secondEngine.objects.Special;

import secondEngine.Window;
import secondEngine.components.InteractiveStateMachine;
import secondEngine.components.Inventory;
import secondEngine.components.MouseTracker;
import secondEngine.components.SpriteRenderer;
import secondEngine.components.helpers.InteractableState;
import secondEngine.components.helpers.InventorySlot;
import secondEngine.components.helpers.Sprite;
import secondEngine.objects.GameObject;
import secondEngine.objects.SpriteObject;
import secondEngine.util.InteractableFactory.InteractableIds;
import secondEngine.util.AssetPool;
import secondEngine.util.PrefabFactory;
import secondEngine.util.PrefabFactory.PrefabIds;
import secondEngine.util.PrefabFactory.PrefabIds.OverlayPrefabs.InventoryLayout;

public class Mouse {
    public static GameObject generate() {
        Sprite sprite = PrefabFactory.getObjectSprite(PrefabFactory.PrefabIds.GroundPrefabs.Spring.GRASS_1);
        GameObject mouse = SpriteObject.generate(sprite, 32, 32);
        mouse.setName("Mouse");
        // GameObject mouse = SpriteObject.generate(new Sprite().setTexture(null), 1,
        // 1);

        MouseTracker mouseTracker = new MouseTracker();
        mouse.addComponent(mouseTracker);

        mouse.transform.gridLockX = true;
        mouse.transform.gridLockY = true;

        Window.setCursor("resources/textures/icons/cursor.png");

        Inventory inventory = new Inventory().init(1, 64);
        inventory.addSpriteRenderer(mouse.getComponent(SpriteRenderer.class));
        Inventory inventoryObj = new Inventory().init(2, 64);
        InventorySlot slot1 = new InventorySlot(inventoryObj, PrefabIds.GroundPrefabs.Spring.GRASS_1, 1, 64);
        inventory.transferFrom(slot1, 1);
        mouse.addComponent(inventory);

        // mouse.transform.scale = new Vector3f(64, 64, 64);

        InteractableState enlarge = new InteractableState().init("Enlarge");
        enlarge.addFrame(InteractableIds.Misc.ENLARGE);

        InteractableState highlight = new InteractableState().init("Highlight");
        highlight.addFrame(InteractableIds.Misc.HIGHLIGHT);

        InteractableState noHighlight = new InteractableState().init("NoHighlight");
        noHighlight.addFrame(InteractableIds.Misc.NO_HIGHLIGHT);

        InteractableState toggleHighlight = new InteractableState().init("ToggleHighlight");
        toggleHighlight.addFrame(InteractableIds.Misc.TOGGLE_HIGHLIGHT);

        InteractableState temp = new InteractableState().init("Temp");
        temp.addFrame(InteractableIds.Misc.TEMP);

        InteractiveStateMachine stateMachine = new InteractiveStateMachine();
        stateMachine.addState(enlarge);
        stateMachine.addState(highlight);
        stateMachine.addState(noHighlight);
        stateMachine.addState(toggleHighlight);
        stateMachine.addState(temp);

        mouse.addComponent(stateMachine);

        mouse.setSerializeOnSave(false);

        return mouse;
    }
}
