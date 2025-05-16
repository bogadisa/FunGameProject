package secondEngine.objects.Special;

import secondEngine.Window;
import secondEngine.components.Inventory;
import secondEngine.components.MouseTracker;
import secondEngine.components.helpers.Sprite;
import secondEngine.objects.GameObject;
import secondEngine.objects.SpriteObject;

public class Mouse {
    public static GameObject generate() {
        GameObject mouse = SpriteObject.generate(new Sprite(), 32, 32);

        MouseTracker mouseTracker = new MouseTracker();
        mouse.addComponent(mouseTracker);

        mouse.transform.gridLockX = true;
        mouse.transform.gridLockY = true;

        Window.setCursor("resources/textures/icons/cursor.png");

        Inventory inventory = new Inventory().init(1,1);
        mouse.addComponent(inventory);


        
        mouse.setName("Mouse");
        mouse.setSerializeOnSave(false);

        return mouse;
    }
}
