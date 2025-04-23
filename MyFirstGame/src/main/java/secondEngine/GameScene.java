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

        GameObject obj1 = new GameObject("Obj 1", new Transform(new Vector3f(100, 100, 0), new Vector3f(256, 256, 1)));
        obj1.addComponent(new SpriteRenderer(AssetPool.getTexture("entities/testImage.png")));
        this.addGameObjectToScene(obj1);
        
        GameObject obj2 = new GameObject("Obj 2", new Transform(new Vector3f(400, 100, 0), new Vector3f(256, 256, 1)));
        obj2.addComponent(new SpriteRenderer(AssetPool.getTexture("icons/heart.png")));
        this.addGameObjectToScene(obj2);

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
