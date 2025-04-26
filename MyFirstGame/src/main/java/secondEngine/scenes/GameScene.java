package secondEngine.scenes;

import org.joml.Vector2f;
import secondEngine.Camera;
import secondEngine.objects.GameObject;
import secondEngine.objects.entities.Player;
import secondEngine.util.AssetPool;
import secondEngine.util.Time;

public class GameScene extends Scene {
    public GameScene() {

    }

    public void init() {
        loadResources();

        this.camera = new Camera(new Vector2f());

        GameObject player = Player.generate();
        this.addGameObjectToScene(player);
    }

    private void loadResources() {
        AssetPool.getShader("/shaders/default.glsl");

        
        AssetPool.addSpriteSheet("entities/player_3_3_8.png");
    }

    public void update() {
        float dt = (float)Time.getDelta();
        for (GameObject go: this.gameObjects) {
            go.update(dt);
        }

        this.renderer.render();

    }
}
