package secondEngine.objects.Tiles;

import secondEngine.components.helpers.Sprite;
import secondEngine.util.PrefabFactory.PrefabIds;

public interface TileBase {
    public abstract <TileType extends PrefabIds> Sprite getSprite(TileType objectId);
}
