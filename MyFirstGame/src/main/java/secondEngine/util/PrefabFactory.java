package secondEngine.util;

import secondEngine.SpatialGrid;
import secondEngine.Window;
import secondEngine.components.helpers.Sprite;
import secondEngine.objects.GameObject;
import secondEngine.objects.SpriteObject;
import secondEngine.objects.Tiles.Ground;

/**
 * <pre>
 * Categories: 0: Misc. 1: Ground
 * 
 * <pre/>
 */
public class PrefabFactory {
    private static int maxObjectPerCategory = 1000;

    public static Sprite getObjectSprite(int objectId) {
        int categoryId = Math.floorDiv(objectId, maxObjectPerCategory);
        int subCategoryId = objectId - categoryId * maxObjectPerCategory;
        return PrefabFactory.getObjSprite(categoryId, subCategoryId);
    }

    private static Sprite getObjSprite(int categoryId, int subCategoryId) {
        switch (categoryId) {
        case 1:
            return Ground.getSprite(subCategoryId);

        default:
            break;
        }
        return null;
    }

    public static GameObject getObject(int objectId) {
        int categoryId = Math.floorDiv(objectId, maxObjectPerCategory);
        int subCategoryId = objectId - categoryId * maxObjectPerCategory;
        SpatialGrid grid = Window.getScene().worldGrid();
        switch (categoryId) {
        case 1:
            return SpriteObject.generate(PrefabFactory.getObjSprite(categoryId, subCategoryId), grid.getGridSize(),
                    grid.getGridSize());

        default:
            break;
        }
        return null;
    }
}
