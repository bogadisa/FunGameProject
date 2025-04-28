package secondEngine.scenes;

import org.joml.Vector2f;
import org.joml.Vector3f;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import secondEngine.Camera;
import secondEngine.Component;
import secondEngine.Window;
import secondEngine.components.Sprite;
import secondEngine.components.SpriteRenderer;
import secondEngine.components.Transform;
import secondEngine.objects.GameObject;
import secondEngine.objects.entities.Player;
import secondEngine.util.AssetPool;
import secondEngine.util.ComponentDeserializer;
import secondEngine.util.GameObjectDeserializer;
import secondEngine.util.Time;

public class GameScene extends Scene {
    public GameScene() {

    }

    public void init() {
        loadResources();

        this.camera = new Camera(new Vector2f());
        if (!Window.getScene().isLoaded()) {
            GameObject player = Player.generate();
            this.addGameObjectToScene(player);

            GameObject obj = new GameObject("Obj", new Transform(new Vector3f(256, 100, 0), new Vector3f(64, 64, 1)));
            SpriteRenderer spriteRenderer = new SpriteRenderer();
            spriteRenderer.setSprite(new Sprite());
            obj.addComponent(spriteRenderer);
            this.addGameObjectToScene(obj);
        }

        // Gson gson = new GsonBuilder()
        //     .setPrettyPrinting()
        //     .registerTypeAdapter(Component.class, new ComponentDeserializer())
        //     .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
        //     .create();

        // String serialized = gson.toJson(player);
        // System.out.println(serialized);
        // GameObject obj = gson.fromJson(serialized, GameObject.class);
        // System.out.println(obj);
    }

    private void loadResources() {
        AssetPool.getShader("/shaders/default.glsl");

        
        AssetPool.addSpriteSheet("entities/player_3_3_9.png");
    }

    public void update() {
        float dt = (float)Time.getDelta();
        for (GameObject go: this.gameObjects) {
            go.update(dt);
        }

        this.renderer.render();

    }
}
