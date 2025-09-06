package secondEngine.util;

import java.util.HashMap;

import secondEngine.SpatialGrid;
import secondEngine.Window;
import secondEngine.components.helpers.Sprite;
import secondEngine.objects.GameObject;
import secondEngine.objects.SpriteObject;
import secondEngine.objects.Tiles.Ground;
import secondEngine.util.PrefabFactory.PrefabIds.Categories;
import secondEngine.util.PrefabFactory.PrefabIds.MiscPrefabs;
import secondEngine.util.PrefabFactory.PrefabIds.GroundPrefabs.Spring;

/**
 * <pre>
 * Categories: 
 * <tab/> Misc.
 * <tab/> Ground
 * <pre/>
 */
public class PrefabFactory {
    public interface PrefabIds {
        public Categories id();

        enum Categories {
            MISC,
            GROUND
        }

        enum MiscPrefabs implements PrefabIds {
            MOUSE;

            @Override
            public Categories id() {
                return Categories.MISC;
            }
        }

        public interface GroundPrefabs extends PrefabIds {
            enum Spring implements GroundPrefabs {
                GRASS_1, GRASS_2, GRASS_3, GRASS_4,
                DIRT_1, DIRT_2,
                DIRT_BONE, DIRT_STONE_1, DIRT_STONE_2, DIRT_BOTTLE,
                SURFACE_ROCK, SURFACE_BUSH_1, SURFACE_BUSH_2, SURFACE_SHRUB_1, SURFACE_SHRUB_2;

                @Override
                public Categories id() {
                    return Categories.GROUND;
                }
            }
        }
    }

    private static PrefabFactory prefabFactory;
    private Ground ground;

    private HashMap<Integer, PrefabIds[]> enumValues;

    private PrefabFactory() {
        ground = new Ground();

        enumValues = new HashMap<>();
        enumValues.put(MiscPrefabs.class.hashCode(), MiscPrefabs.values());
        enumValues.put(Spring.class.hashCode(), Spring.values());
    }

    public static PrefabFactory get() {
        if (PrefabFactory.prefabFactory == null) {
            PrefabFactory.prefabFactory = new PrefabFactory();
        }
        return PrefabFactory.prefabFactory;
    }
    
    public static <EnumType extends PrefabIds> EnumType getEnum(Class<EnumType> enumClass, int idx) {
        if (Enum.class.isAssignableFrom(enumClass)) {
            PrefabIds[] enums = get().enumValues.get(enumClass.hashCode());
            PrefabIds targetEnum = enums.length > idx ? enums[idx] : null;
            if (targetEnum == null) {
                return null;
            } 
            else if (enumClass.isAssignableFrom(targetEnum.getClass()) ) {
                return enumClass.cast(targetEnum);
            } else {
                return null;
            }
        } else {
            throw new IllegalArgumentException("Argument of type '" + enumClass + "' is not assignable from Enum");
        }
    }

    public static Sprite getObjectSprite(PrefabIds objectId) {
        return PrefabFactory.getObjSprite(objectId);
    }

    private static <PrefabSubClass extends PrefabIds> Sprite getObjSprite(PrefabSubClass objectId) {
        Categories catEnum = objectId.id();
        Sprite sprite = null; 
        switch (catEnum) {
        case GROUND:
            sprite = get().ground.getSprite(objectId);
        default:
            break;
        }
        if (sprite == null) {
            throw new IllegalArgumentException("Argument of type '" + objectId + "' is not supported");
        }
        return sprite;
    }

    public static GameObject getObject(PrefabIds objectId) {
        SpatialGrid grid = Window.getScene().worldGrid();
        Categories catEnum = objectId.id();
        GameObject go = null;
        switch (catEnum) {
        case GROUND:
            go = SpriteObject.generate(PrefabFactory.getObjSprite(objectId), grid.getGridSize(),
                    grid.getGridSize());
        default:
            break;
        }
        if (go == null) {
            throw new IllegalArgumentException("Argument of type '" + objectId + "' is not supported");
        }
        return go;
    }
}
