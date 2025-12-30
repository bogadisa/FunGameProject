package secondEngine.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import secondEngine.components.helpers.SpriteSheet;
import secondEngine.objects.overlay.Layout;
import secondEngine.renderer.GlyphMetrics;
import secondEngine.renderer.Font;
import secondEngine.renderer.Shader;
import secondEngine.renderer.Texture;
import secondEngine.util.PrefabFactory.PrefabIds.OverlayPrefabs.InventoryLayout;

public class AssetPool {
    // Preferably these should be loaded during loading scenes
    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();
    private static Map<String, Font> fonts = new HashMap<>();
    private static Map<String, SpriteSheet> spriteSheets = new HashMap<>();
    private static Map<InventoryLayout, Layout> InventoryLayout = new HashMap<>();

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
        Map<String, Texture> textures = AssetPool.textures;
        if (AssetPool.textures.containsKey(file.getAbsolutePath())) {
            return AssetPool.textures.get(file.getAbsolutePath());
        } else {
            Texture texture = new Texture();
            if (!resourceName.contains("resources/textures/") && !resourceName.contains("fonts")) {
                resourceName = "resources/textures/" + resourceName;
            }
            texture.init(resourceName);
            AssetPool.textures.put(file.getAbsolutePath(), texture);
            return texture;
        }
    }

    public static void addFont(String fontResourceName) {
        if (!fontResourceName.contains("resources/fonts/")) {
            fontResourceName = "resources/fonts/" + fontResourceName;
        }
        if (fonts.containsKey(fontResourceName)) {
            return;
        }
        Font font = new Font().init(fontResourceName);
        fonts.put(fontResourceName, font);
    }

    public static Font getFont(String fontResourceName) {
        if (!fontResourceName.contains("resources/fonts/")) {
            fontResourceName = "resources/fonts/" + fontResourceName;
        }
        if (!AssetPool.fonts.containsKey(fontResourceName)) {
            assert false
                    : "Error: Tried to access font '" + fontResourceName + "' and it has not been added to asset pool.";
        }
        return AssetPool.fonts.getOrDefault(fontResourceName, null);
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
            assert false : "Error: Tried to access spritesheet '" + resourceName
                    + "' and it has not been added to asset pool.";
        }
        return AssetPool.spriteSheets.getOrDefault(file.getAbsolutePath(), null);
    }

    public static void addLayout(Layout layout, InventoryLayout layoutType) {
        if (!AssetPool.InventoryLayout.containsKey(layoutType)) {
            AssetPool.InventoryLayout.put(layoutType, layout);
        }
    }

    public static Layout getLayout(InventoryLayout layout) {
        if (!AssetPool.InventoryLayout.containsKey(layout)) {
            assert false : "Error: Tried to use an unsupported layout '" + layout + "'.";
        }
        return AssetPool.InventoryLayout.get(layout);
    }
}
