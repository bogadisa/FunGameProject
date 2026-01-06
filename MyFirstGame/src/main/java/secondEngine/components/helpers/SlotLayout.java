package secondEngine.components.helpers;

import java.util.Map;

import secondEngine.objects.overlay.Layout;

public class SlotLayout {
    public boolean initialized = false;

    public Layout layout;
    public Map<Layout.SlotType, Sprite> layoutSprites;

    public SlotLayout(Layout layout, Map<Layout.SlotType, Sprite> layoutSprites) {
        this.layout = layout;
        this.layoutSprites = layoutSprites;
    }
}
