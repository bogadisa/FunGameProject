package secondEngine;

import org.joml.Vector2f;
import org.joml.Vector3f;

import secondEngine.components.SpriteRenderer;
import secondEngine.util.AssetPool;
import secondEngine.util.Time;

public class GameScene extends Scene {

    public GameScene() {

    }

    public void init() {
        this.camera = new Camera(new Vector2f());
        loadResources();
    }

    private void loadResources() {
        AssetPool.getShader("/shaders/default.glsl");
    }

    public void update() {
        float dt = (float)Time.getDelta();
        for (GameObject go: this.gameObjects) {
            go.update(dt);
        }
        camera.position.x -= 0.4 * dt * 3;
        camera.position.y -= 0.20 * dt * 3;

        this.renderer.render();

    }
}
