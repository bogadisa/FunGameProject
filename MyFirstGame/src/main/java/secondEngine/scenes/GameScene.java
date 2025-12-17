package secondEngine.scenes;

import org.joml.Vector2f;
import org.joml.Vector3f;

import secondEngine.Camera;
import secondEngine.Config;
import secondEngine.SpatialGrid;
import secondEngine.Config.UIconfig;
import secondEngine.Window;
import secondEngine.components.CompositeSpriteRenderer;
import secondEngine.components.GridMachine;
import secondEngine.components.InteractiveStateMachine;
import secondEngine.components.Overlay;
import secondEngine.components.SpriteRenderer;
import secondEngine.components.AnimationStateMachine;
import secondEngine.components.helpers.Sprite;
import secondEngine.components.helpers.SpriteSheet;
import secondEngine.objects.GameObject;
import secondEngine.objects.OverlayObject;
// import secondEngine.objects.OverlayObject.Layouts;
import secondEngine.objects.Special.Mouse;
import secondEngine.objects.entities.Player;
import secondEngine.util.AssetPool;
import secondEngine.util.PrefabFactory;
import secondEngine.util.Time;
import secondEngine.util.InteractableFactory.InteractableIds;
import secondEngine.util.PrefabFactory.PrefabIds.OverlayPrefabs.InventoryLayout;

public class GameScene extends Scene {
    public void init() {
        Config.getUIconfig();
        int scale = UIconfig.getScale();
        loadResources();

        this.camera = new Camera(new Vector2f());
        this.worldGrid = new SpatialGrid("base");

        GameObject grid = OverlayObject.generateGrid();
        this.addGameObjectToScene(grid);

        GameObject mouse = Mouse.generate();
        this.addGameObjectToScene(mouse);
        if (Window.getScene().isLoaded()) {
            return;
        }

        GameObject player = Player.generate();
        player.transform.position.add(64, 80, 0);
        this.addGameObjectToScene(player);

        player.getComponent(GridMachine.class).toggleHighlight();
        grid.getComponent(Overlay.class).linkObjectToGrid(player);

        // GameObject obj = new GameObject("Obj");
        // CompositeSpriteRenderer compSprite = new CompositeSpriteRenderer();
        // compSprite.init()
        // .addSpriteRenderer(new SpriteRenderer().setSprite(new Sprite()))
        // .addSpriteRenderer(new SpriteRenderer().setSprite(new Sprite()), new
        // Vector3f(64, 0, -2), 60);
        // obj.addComponent(compSprite);
        // obj.addComponent(new Transform().init(new Vector3f(256, 100, 0), new
        // Vector3f(2*scale, 2*scale, 1)));
        // this.addGameObjectToScene(obj);

        GameObject inventory = PrefabFactory.getObject(InventoryLayout.DEFAULT_27);
        // inventory.transform.init(new Vector3f(16, 16, 0), new Vector3f(scale, scale,
        // 10));
        // Window.getScene().worldGrid().addObject(inventory);

        this.addGameObjectToScene(inventory);
        // ui.getComponent(GridState.class).toggleHighlight();

    }

    private void loadResources() {
        // TODO move this to a seperate initializer class
        AssetPool.getShader("/shaders/default.glsl");

        AssetPool.addSpriteSheet("entities/player_3_3_9.png");
        AssetPool.addSpriteSheet("overlay/overlaySmallBasics_2_2_4.png");
        AssetPool.addSpriteSheet("overlay/inventory_2_2_4.png");
        AssetPool.addSpriteSheet("background/spring_4_4_15.png");

        for (GameObject go : this.gameObjects) {
            if (go.getComponent(CompositeSpriteRenderer.class) != null) {
                CompositeSpriteRenderer compSpr = go.getComponent(CompositeSpriteRenderer.class);
                compSpr.refreshTextures();
            } else if (go.getComponent(SpriteRenderer.class) != null) {
                SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
                spr.refreshTexture();
            }

            if (go.getComponent(AnimationStateMachine.class) != null) {
                AnimationStateMachine stateMachine = go.getComponent(AnimationStateMachine.class);
                stateMachine.refreshTextures();
            }
        }
        int[][] standardLayout = { new int[11], { 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 },
                { 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 }, { 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 }, new int[11],
                { 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 }, new int[11], };
        AssetPool.addLayout(standardLayout, InventoryLayout.DEFAULT_27);
        int[][] lineLayout = { new int[11], { 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 }, new int[11], };
        AssetPool.addLayout(lineLayout, InventoryLayout.DEFAULT_9);
    }
}
