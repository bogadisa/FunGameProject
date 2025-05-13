package secondEngine.objects.Special;

import secondEngine.Window;
import secondEngine.components.MouseTracker;
import secondEngine.components.helpers.Sprite;
import secondEngine.objects.GameObject;
import secondEngine.objects.SpriteObject;

public class Mouse {
    public static GameObject generate() {
        GameObject mouse = SpriteObject.generate(new Sprite(), 32, 32);
        mouse.setName("Mouse");

        MouseTracker mouseTracker = new MouseTracker();
        mouse.addComponent(mouseTracker);

        mouse.transform.gridLockX = true;
        mouse.transform.gridLockY = true;

        Window.setCursor("resources/textures/icons/cursor.png");

        return mouse;
    }
}
