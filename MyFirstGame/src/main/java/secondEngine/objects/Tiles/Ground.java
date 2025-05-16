package secondEngine.objects.Tiles;

import secondEngine.components.helpers.Sprite;
import secondEngine.components.helpers.SpriteSheet;
import secondEngine.util.AssetPool;

public class Ground {
    public static Sprite getSprite(int objectSubId) {
        int spriteSheetId = Math.floorDiv(objectSubId, 32);
        int spriteSheetSubId = objectSubId - spriteSheetId*32;
        switch (spriteSheetId) {
            case 0:
                SpriteSheet sheet = AssetPool.getSpriteSheet("background/spring_4_4_15.png");
                if (spriteSheetSubId < 15) {
                    return sheet.getSprite(spriteSheetSubId);
                }
                break;
        
            default:
                break;
        }

        return null;
    }
}
