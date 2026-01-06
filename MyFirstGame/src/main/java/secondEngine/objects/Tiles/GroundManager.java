package secondEngine.objects.Tiles;

import java.util.Optional;

import secondEngine.components.helpers.Sprite;
import secondEngine.components.helpers.SpriteSheet;
import secondEngine.util.AssetPool;
import secondEngine.util.PrefabFactory.PrefabIds;
import secondEngine.util.PrefabFactory.PrefabManagerBase;

public class GroundManager implements PrefabManagerBase {

    public interface GroundPrefabs extends PrefabIds {
        enum Spring implements GroundPrefabs {
            GRASS_1, GRASS_2, GRASS_3, GRASS_4, DIRT_1, DIRT_2, DIRT_BONE, DIRT_STONE_1, DIRT_STONE_2, DIRT_BOTTLE,
            SURFACE_ROCK, SURFACE_BUSH_1, SURFACE_BUSH_2, SURFACE_SHRUB_1, SURFACE_SHRUB_2;

            @Override
            public Categories id() {
                return Categories.GROUND;
            }
        }
    }

    @Override
    public <GroundType extends PrefabIds> Optional<Sprite> getSprite(GroundType objectId) {
        if (objectId.isOfType(GroundPrefabs.Spring.class)) {
            GroundPrefabs.Spring springId = GroundPrefabs.Spring.class.cast(objectId);
            SpriteSheet sheet = AssetPool.getSpriteSheet("background/spring_4_4_15.png");
            return Optional.of(sheet.getSprite(springId.ordinal()));
        }
        System.err.println("Unable to find the specified ground element:" + objectId.toString());
        return Optional.empty();
    }

    @Override
    public <GroundType extends PrefabIds> Optional<SpriteSheet> getSpriteSheet(GroundType objectId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSpriteSheet'");
    }
}
