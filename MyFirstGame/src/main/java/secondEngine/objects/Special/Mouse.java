package secondEngine.objects.Special;

import java.util.Optional;

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
import secondEngine.objects.Tiles.GroundManager.GroundPrefabs;
import secondEngine.util.InteractableFactory.InteractableIds;
import secondEngine.util.PrefabFactory;

public class Mouse {
    private static GameObject mouse = null;

    public static GameObject generate() {
        if (mouse != null) {
            return mouse;
        }
        Optional<Sprite> sprite = PrefabFactory.getObjectSprite(GroundPrefabs.Spring.GRASS_1);
        mouse = SpriteObject.generate(sprite, 32, 32);
        mouse.transform.position.z = 10;
        mouse.getComponent(SpriteRenderer.class).ifPresent(spriteRenderer -> spriteRenderer.refreshTexture());
        mouse.setName("Mouse");

        MouseTracker mouseTracker = new MouseTracker();
        mouse.addComponent(mouseTracker);

        // mouse.transform.gridLockX = true;
        // mouse.transform.gridLockY = true;

        Window.setCursor("resources/textures/icons/cursor.png");

        Inventory inventory = mouse.addComponent(new Inventory().init(1, 64));
        mouse.getComponent(SpriteRenderer.class)
                .ifPresent(spriteRenderer -> inventory.addSpriteRenderer(spriteRenderer));
        Inventory inventoryObj = new Inventory().init(2, 64);
        InventorySlot slot1 = new InventorySlot(inventoryObj, GroundPrefabs.Spring.GRASS_1, 1, 64);
        inventory.transferFrom(slot1, 1);

        // mouse.transform.scale = new Vector3f(64, 64, 64);

        InteractableState enlarge = new InteractableState().init("Enlarge");
        enlarge.addFrame(InteractableIds.Misc.ENLARGE);

        InteractableState highlight = new InteractableState().init("Highlight");
        highlight.addFrame(InteractableIds.Misc.HIGHLIGHT);

        InteractableState noHighlight = new InteractableState().init("NoHighlight");
        noHighlight.addFrame(InteractableIds.Misc.NO_HIGHLIGHT);

        InteractableState toggleHighlight = new InteractableState().init("ToggleHighlight");
        toggleHighlight.addFrame(InteractableIds.Misc.TOGGLE_HIGHLIGHT);

        InteractableState exchangeWith = new InteractableState().init("ExchangeWith");
        exchangeWith.addFrame(InteractableIds.MouseInteractables.EXCHANGE_WITH);

        InteractiveStateMachine stateMachine = new InteractiveStateMachine();
        stateMachine.addState(enlarge);
        stateMachine.addState(highlight);
        stateMachine.addState(noHighlight);
        stateMachine.addState(toggleHighlight);
        stateMachine.addState(exchangeWith);

        mouse.addComponent(stateMachine);

        mouse.setSerializeOnSave(false);

        return mouse;
    }
}
