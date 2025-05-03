package secondEngine.scenes;

import org.joml.Vector2f;
import org.joml.Vector3f;

import secondEngine.Camera;
import secondEngine.Config;
import secondEngine.Config.UIconfig;
import secondEngine.Window;
import secondEngine.components.CompositeSpriteRenderer;
import secondEngine.components.Sprite;
import secondEngine.components.SpriteRenderer;
import secondEngine.components.StateMachine;
import secondEngine.components.Transform;
import secondEngine.components.helpers.SpriteSheet;
import secondEngine.objects.GameObject;
import secondEngine.objects.OverlayObject;
import secondEngine.objects.entities.Player;
import secondEngine.util.AssetPool;
import secondEngine.util.Time;

public class GameScene extends Scene {
    public GameScene() {

    }

    public void init() {
        Config.getUIconfig();
        int scale = UIconfig.getScale();
        loadResources();

        this.camera = new Camera(new Vector2f());
        if (Window.getScene().isLoaded()) {
            return;
        }
        
        GameObject player = Player.generate();
        this.addGameObjectToScene(player);

        GameObject obj = new GameObject("Obj");
        CompositeSpriteRenderer compSprite = new CompositeSpriteRenderer();
        compSprite.init()
                .addSpriteRenderer(new SpriteRenderer().setSprite(new Sprite()))
                .addSpriteRenderer(new SpriteRenderer().setSprite(new Sprite()), new Vector3f(64, 0, -2), 60);
        obj.addComponent(compSprite);
        obj.addComponent(new Transform().init(new Vector3f(256, 100, 0), new Vector3f(2*scale, 2*scale, 1)));
        this.addGameObjectToScene(obj);

        SpriteSheet spriteSheet = AssetPool.getSpriteSheet("overlay/overlayBasics_3_1_3.png");
        Sprite[] sprites = {spriteSheet.getSprite(0), spriteSheet.getSprite(1), spriteSheet.getSprite(2)};
        // Sprite[] sprites = {new Sprite(), new Sprite(), new Sprite()};
        GameObject ui = OverlayObject.generate(sprites, 10, 5);

        this.addGameObjectToScene(ui);
    }

    private void loadResources() {
        // TODO move this to a seperate initializer class
        AssetPool.getShader("/shaders/default.glsl");

        AssetPool.addSpriteSheet("entities/player_3_3_9.png");
        AssetPool.addSpriteSheet("overlay/overlayBasics_3_1_3.png");

        for (GameObject go: this.gameObjects) {
            if (go.getComponent(CompositeSpriteRenderer.class) != null) {
                CompositeSpriteRenderer compSpr = go.getComponent(CompositeSpriteRenderer.class);
                compSpr.refreshTextures();
            } else if (go.getComponent(SpriteRenderer.class) != null) {
                SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
                spr.refreshTexture();
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
