package secondEngine.objects.Tiles;

import secondEngine.components.helpers.Sprite;
import secondEngine.components.helpers.SpriteSheet;
import secondEngine.util.AssetPool;
import secondEngine.util.PrefabFactory.PrefabIds;
import secondEngine.util.PrefabFactory.PrefabIds.GroundPrefabs;
import secondEngine.util.PrefabFactory.PrefabManagerBase;

public class GroundManager implements PrefabManagerBase {

    @Override
    public <GroundType extends PrefabIds> Sprite getSprite(GroundType objectId) {
        if (objectId.isOfType(GroundPrefabs.Spring.class)) {
            GroundPrefabs.Spring springId = GroundPrefabs.Spring.class.cast(objectId);
            SpriteSheet sheet = AssetPool.getSpriteSheet("background/spring_4_4_15.png");
            return sheet.getSprite(springId.ordinal());
        }
        System.err.println("Unable to find the specified ground element:" + objectId.toString());
        return null;
    }

    @Override
    public <GroundType extends PrefabIds> SpriteSheet getSpriteSheet(GroundType objectId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSpriteSheet'");
    }
}
