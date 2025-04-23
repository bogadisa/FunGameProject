package secondEngine;

import org.joml.Vector2f;
import org.joml.Vector3f;

import secondEngine.components.SpriteRenderer;
import secondEngine.components.SpriteSheet;
import secondEngine.util.AssetPool;
import secondEngine.util.Time;

public class EditorScene extends Scene{

    @Override
    public void init() {
        loadResources();

        this.camera = new Camera(new Vector2f());

        SpriteSheet spriteSheet = AssetPool.getSpriteSheet("entities/player_3_3_8.png");

        GameObject obj1 = new GameObject("Obj 1", new Transform(new Vector3f(100, 100, 0), new Vector3f(256, 256, 1)));
        obj1.addComponent(new SpriteRenderer(spriteSheet.getSprite(0)));
        this.addGameObjectToScene(obj1);

        GameObject obj2 = new GameObject("Obj 2", new Transform(new Vector3f(400, 400, 0), new Vector3f(256, 256, 1)));
        obj1.addComponent(new SpriteRenderer(spriteSheet.getSprite(1)));
        this.addGameObjectToScene(obj2);

    }
    
    private void loadResources() {
        AssetPool.getShader("/shaders/default.glsl");

        AssetPool.addSpriteSheet("entities/player_3_3_8.png");
    }
    
    @Override
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

