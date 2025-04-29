package secondEngine.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import secondEngine.components.helpers.SpriteSheet;
import secondEngine.renderer.Shader;
import secondEngine.renderer.Texture;

public class AssetPool {
    // Preferably these should be loaded during loading scenes
    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();
    private static Map<String, SpriteSheet> spriteSheets = new HashMap<>();

    public static Shader getShader(String resourceName) {
        File file = new File(resourceName);
        if (AssetPool.shaders.containsKey(file.getAbsolutePath())) {
            return AssetPool.shaders.get(file.getAbsolutePath());
        } else {
            Shader shader = new Shader(resourceName);
            shader.compileAndLink();
            AssetPool.shaders.put(file.getAbsolutePath(), shader);
            return shader;
        }
    }
    public static void addTexture(String resourceName, Texture texture) {
        File file = new File(resourceName);
        if (!AssetPool.textures.containsKey(file.getAbsolutePath())) {
            AssetPool.textures.put(file.getAbsolutePath(), texture);
        }
    }

    public static Texture getTexture(String resourceName) {
        File file = new File(resourceName);
        if (AssetPool.textures.containsKey(file.getAbsolutePath())) {
            return AssetPool.textures.get(file.getAbsolutePath());
        } else {
            Texture texture = new Texture();
            if (!resourceName.contains("resources/textures/")) {
                resourceName = "resources/textures/" + resourceName;
            }
            texture.init(resourceName);
            AssetPool.textures.put(file.getAbsolutePath(), texture);
            return texture;
        }
    }

    public static void addSpriteSheet(String resourceName) {
        File file = new File(resourceName);
        if (!AssetPool.spriteSheets.containsKey(file.getAbsolutePath())) {
            AssetPool.spriteSheets.put(file.getAbsolutePath(), new SpriteSheet(resourceName));
        }
    }

    public static SpriteSheet getSpriteSheet(String resourceName) {
        File file = new File(resourceName);
        if (!AssetPool.spriteSheets.containsKey(file.getAbsolutePath())) {
            assert false : "Error: Tried to access spritesheet '" + resourceName + "' and it has not been added to asset pool.";
        }
        return AssetPool.spriteSheets.getOrDefault(file.getAbsolutePath(), null);
    }
}
