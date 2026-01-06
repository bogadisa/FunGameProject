package secondEngine.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import secondEngine.Window;
import secondEngine.components.helpers.Sprite;
import secondEngine.components.helpers.SpriteSheet;
import secondEngine.grid.SpatialGrid;
import secondEngine.objects.GameObject;
import secondEngine.objects.OverlayObject;
import secondEngine.objects.SpriteObject;
import secondEngine.objects.Tiles.GroundManager;
import secondEngine.objects.Tiles.GroundManager.GroundPrefabs;
import secondEngine.objects.overlay.OverlayManager;
import secondEngine.util.PrefabFactory.PrefabIds.Categories;
import secondEngine.util.PrefabFactory.PrefabIds.MiscPrefabs;

/**
 * <pre>
 * Categories: <tab/> Misc. <tab/> Ground
 * 
 * <pre/>
 */
public class PrefabFactory {
    public interface PrefabIds {
        public Categories id();

        enum Categories {
            MISC, GROUND, OVERLAY
        }

        enum MiscPrefabs implements PrefabIds {
            MOUSE;

            @Override
            public Categories id() {
                return Categories.MISC;
            }
        }

        public default <WantedPrefabType extends PrefabIds> boolean isOfType(Class<WantedPrefabType> objectClass) {
            return this.getClass().isAssignableFrom(objectClass);
        }

    }

    public interface PrefabManagerBase {
        public abstract <PrefabType extends PrefabIds> Optional<Sprite> getSprite(PrefabType objectId);

        public abstract <PrefabType extends PrefabIds> Optional<SpriteSheet> getSpriteSheet(PrefabType objectId);
    }

    private static PrefabFactory prefabFactory;
    private GroundManager groundManager;
    private OverlayManager overlayManager;

    private Map<Integer, PrefabIds[]> enumValues;

    private PrefabFactory() {
        groundManager = new GroundManager();
        overlayManager = new OverlayManager();

        enumValues = new HashMap<>();
        enumValues.put(MiscPrefabs.class.hashCode(), MiscPrefabs.values());
        enumValues.put(GroundPrefabs.Spring.class.hashCode(), GroundPrefabs.Spring.values());
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
            } else if (enumClass.isAssignableFrom(targetEnum.getClass())) {
                return enumClass.cast(targetEnum);
            } else {
                return null;
            }
        } else {
            throw new IllegalArgumentException("Argument of type '" + enumClass + "' is not assignable from Enum");
        }
    }

    public static Optional<Sprite> getObjectSprite(PrefabIds objectId) {
        return PrefabFactory.getObjSprite(objectId);
    }

    private static <PrefabSubClass extends PrefabIds> Optional<Sprite> getObjSprite(PrefabSubClass objectId) {
        if (objectId == null) {
            return Optional.of(new Sprite().setTexture(AssetPool.getTexture("default.png")));
        }
        Categories catEnum = objectId.id();
        Optional<Sprite> sprite = Optional.empty();
        switch (catEnum) {
        case GROUND:
            sprite = get().groundManager.getSprite(objectId);

        default:
            break;
        }
        if (sprite == null) {
            throw new IllegalArgumentException("Argument of type '" + objectId + "' is not supported");
        }
        return sprite;
    }

    private static <PrefabSubClass extends PrefabIds> Optional<SpriteSheet> getObjSpriteSheet(PrefabSubClass objectId) {
        Categories catEnum = objectId.id();
        Optional<SpriteSheet> spriteSheet = Optional.empty();
        switch (catEnum) {
        case OVERLAY:
            spriteSheet = get().overlayManager.getSpriteSheet(objectId);

        default:
            break;
        }
        // if (spriteSheet == null) {
        // throw new IllegalArgumentException("Argument of type '" + objectId + "' is
        // not supported");
        // }
        return spriteSheet;
    }

    public static GameObject getObject(PrefabIds objectId) {
        SpatialGrid grid = Window.getScene().worldGrid();
        Categories catEnum = objectId.id();
        GameObject go = null;
        switch (catEnum) {
        case GROUND:
            go = SpriteObject.generate(PrefabFactory.getObjSprite(objectId), grid.getGridSize(), grid.getGridSize());
        case OVERLAY:
            go = OverlayObject.generate(PrefabFactory.getObjSpriteSheet(objectId), objectId);
        default:
            break;
        }
        if (go == null) {
            throw new IllegalArgumentException("Argument of type '" + objectId + "' is not supported");
        }
        return go;
    }
}
