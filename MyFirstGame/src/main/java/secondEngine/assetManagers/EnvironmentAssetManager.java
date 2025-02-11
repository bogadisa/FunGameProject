package secondEngine.assetManagers;

import java.util.HashMap;

import secondEngine.components.Sprite;
import secondEngine.components.SpriteSheet;

public class EnvironmentAssetManager extends AssetManager {
    private static EnvironmentAssetManager instance = null;

    private static HashMap<String, SpriteSheet> envSpriteSheets;

    private EnvironmentAssetManager() {
        super("background/");
        EnvironmentAssetManager.envSpriteSheets = this.spriteSheets;
    }

    public static EnvironmentAssetManager get() {
        if (EnvironmentAssetManager.instance == null) {
            EnvironmentAssetManager.instance = new EnvironmentAssetManager();
        }

        return EnvironmentAssetManager.instance;
    }

    @Override
    public Sprite getSprite(String assetName, int index, int variation) {
        // TODO does this make sense?
        // Currently assumes 4 sprite variations
        return EnvironmentAssetManager.envSpriteSheets.get(assetName).getSprite(4 * index + variation);
    }

}
