package secondEngine.objects.overlay;

import secondEngine.components.helpers.Sprite;
import secondEngine.components.helpers.SpriteSheet;
import secondEngine.util.AssetPool;
import secondEngine.util.PrefabFactory.PrefabIds;
import secondEngine.util.PrefabFactory.PrefabIds.OverlayPrefabs;
import secondEngine.util.PrefabFactory.PrefabManagerBase;

public class OverlayManager implements PrefabManagerBase {

    @Override
    public <OverlayType extends PrefabIds> SpriteSheet getSpriteSheet(OverlayType objectId) {
        if (objectId.isOfType(OverlayPrefabs.InventoryLayout.class)) {
            return AssetPool.getSpriteSheet("overlay/inventory_2_2_4.png");
        }
        System.err.println("Unable to find the specified Overlay element:" + objectId.toString());
        return null;
    }

    @Override
    public <OverlayType extends PrefabIds> Sprite getSprite(OverlayType objectId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSprite'");
    }
}
