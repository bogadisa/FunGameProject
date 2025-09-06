package secondEngine.objects.Tiles;

import secondEngine.components.helpers.Sprite;
import secondEngine.components.helpers.SpriteSheet;
import secondEngine.util.AssetPool;
import secondEngine.util.PrefabFactory.PrefabIds;
import secondEngine.util.PrefabFactory.PrefabIds.GroundPrefabs;

public class Ground implements TileBase{
    @Override
    public <TileType extends PrefabIds> Sprite getSprite(TileType objectId) {
        if (objectId.getClass().isAssignableFrom(GroundPrefabs.Spring.class)){
            GroundPrefabs.Spring springId = GroundPrefabs.Spring.class.cast(objectId);
            SpriteSheet sheet = AssetPool.getSpriteSheet("background/spring_4_4_15.png");
            return sheet.getSprite(springId.ordinal());
        }

        return null;
    }
}
