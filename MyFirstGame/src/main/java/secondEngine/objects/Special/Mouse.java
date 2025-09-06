package secondEngine.objects.Special;

import org.joml.Vector3f;

import secondEngine.Window;
import secondEngine.components.InteractiveStateMachine;
import secondEngine.components.Inventory;
import secondEngine.components.MouseTracker;
import secondEngine.components.helpers.InteractableState;
import secondEngine.components.helpers.Sprite;
import secondEngine.objects.GameObject;
import secondEngine.objects.SpriteObject;
import secondEngine.util.InteractableFactory.InteractableIds;
import secondEngine.util.PrefabFactory;

public class Mouse {
    public static GameObject generate() {
        Sprite sprite = PrefabFactory.getObjectSprite(PrefabFactory.PrefabIds.GroundPrefabs.Spring.GRASS_1);
        GameObject mouse = SpriteObject.generate(sprite, 32, 32);

        MouseTracker mouseTracker = new MouseTracker();
        mouse.addComponent(mouseTracker);

        mouse.transform.gridLockX = true;
        mouse.transform.gridLockY = true;

        Window.setCursor("resources/textures/icons/cursor.png");

        Inventory inventory = new Inventory().init(1, 1);
        mouse.addComponent(inventory);

        mouse.transform.scale = new Vector3f(64, 64, 64);

        InteractableState enlarge = new InteractableState();
        enlarge.title = "enlarge";
        enlarge.addFrame(InteractableIds.Misc.TEST);

        InteractiveStateMachine stateMachine = new InteractiveStateMachine();
        stateMachine.addState(enlarge);

        mouse.addComponent(stateMachine);

        mouse.setName("Mouse");
        mouse.setSerializeOnSave(false);

        return mouse;
    }
}
