package secondEngine.scenes;

import org.joml.Vector2f;
import org.joml.Vector3f;

import secondEngine.Camera;
import secondEngine.Window;
import secondEngine.components.Sprite;
import secondEngine.components.SpriteRenderer;
import secondEngine.components.StateMachine;
import secondEngine.components.Transform;
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
        if (Window.getScene().isLoaded()) {
            return;
        }
        
        GameObject player = Player.generate();
        this.addGameObjectToScene(player);

        GameObject obj = new GameObject("Obj");
        obj.addComponent(new SpriteRenderer().setSprite(new Sprite()));
        obj.addComponent(new Transform().init(new Vector3f(256, 100, 0), new Vector3f(64, 64, 1)));
        this.addGameObjectToScene(obj);
    }

    private void loadResources() {
        // TODO move this to a seperate initializer class
        AssetPool.getShader("/shaders/default.glsl");

        AssetPool.addSpriteSheet("entities/player_3_3_9.png");

        for (GameObject go: this.gameObjects) {
            if (go.getComponent(SpriteRenderer.class) != null) {
                SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
                if (spr.getTexture() != null) {
                    spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilepath()));
                }
            }

            if (go.getComponent(StateMachine.class) != null) {
                StateMachine stateMachine = go.getComponent(StateMachine.class);
                stateMachine.refreshTextures();
            }
        }
    }

    public void update() {
        float dt = (float)Time.getDelta();
        for (GameObject go: this.gameObjects) {
            go.update(dt);
        }

        this.renderer.render();

    }
}
