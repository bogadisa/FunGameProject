package secondEngine.scenes;

import org.joml.Vector2f;
import org.joml.Vector3f;

import secondEngine.Camera;
import secondEngine.components.Sprite;
import secondEngine.components.SpriteRenderer;
import secondEngine.components.Transform;
import secondEngine.components.helpers.SpriteSheet;
import secondEngine.objects.GameObject;
import secondEngine.util.AssetPool;
import secondEngine.util.Time;

public class EditorScene extends Scene{

    @Override
    public void init() {
        loadResources();

        this.camera = new Camera(new Vector2f());

        // SpriteSheet spriteSheet = AssetPool.getSpriteSheet("entities/player_3_3_9.png");

        GameObject obj1 = new GameObject("Obj 1", new Transform(new Vector3f(100, 100, -2), new Vector3f(256, 256, 1)));
        obj1.addComponent(new SpriteRenderer(new Sprite(AssetPool.getTexture("entities/blendImage1.png"))));
        this.addGameObjectToScene(obj1);

        GameObject obj2 = new GameObject("Obj 2", new Transform(new Vector3f(300, 100, -1), new Vector3f(256, 256, 1)));
        obj2.addComponent(new SpriteRenderer(new Sprite(AssetPool.getTexture("entities/blendImage2.png"))));
        this.addGameObjectToScene(obj2);

    }
    
    private void loadResources() {
        AssetPool.getShader("/shaders/default.glsl");

        // AssetPool.addSpriteSheet("entities/player_3_3_9.png");
    }
    
    @Override
    public void update() {
        float dt = (float)Time.getDelta();
        for (GameObject go: this.gameObjects) {
            go.update(dt);
        }
        // camera.position.x -= 0.4 * dt * 3;
        // camera.position.y -= 0.20 * dt * 3;

        this.renderer.render();
    }

}

