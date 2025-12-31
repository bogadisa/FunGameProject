package secondEngine.scenes;

import org.joml.Vector2f;
import org.joml.Vector3f;

import secondEngine.Camera;
import secondEngine.Config;
import secondEngine.Config.UIconfig;
import secondEngine.Window;
import secondEngine.components.CompositeSpriteRenderer;
import secondEngine.components.SpriteRenderer;
import secondEngine.components.TextRenderer;
import secondEngine.components.Transform;
import secondEngine.components.helpers.Sprite;
import secondEngine.components.helpers.Text;
import secondEngine.components.helpers.TextBox;
import secondEngine.components.AnimationStateMachine;
import secondEngine.grid.SpatialGrid;
import secondEngine.objects.GameObject;
import secondEngine.objects.OverlayObject;
import secondEngine.objects.SpriteObject;
import secondEngine.objects.Special.Mouse;
import secondEngine.objects.entities.Player;
import secondEngine.objects.overlay.Layout;
import secondEngine.renderer.Font;
import secondEngine.renderer.GlyphMetrics;
import secondEngine.renderer.Texture;
import secondEngine.util.AssetPool;
import secondEngine.util.PrefabFactory;
import secondEngine.util.PrefabFactory.PrefabIds.OverlayPrefabs.InventoryLayout;

public class GameScene extends Scene {
    public void init() {
        Config.getUIconfig();
        int scale = UIconfig.getScale();
        loadResources();

        this.camera = new Camera(new Vector2f());
        this.worldGrid = new SpatialGrid("world");
        this.screenGrid = new SpatialGrid("screen");

        // GameObject grid = OverlayObject.generateGrid();
        // this.addGameObjectToScene(grid);

        GameObject mouse = Mouse.generate();
        this.addGameObjectToScene(mouse);
        // if (Window.getScene().isLoaded()) {
        // return;
        // }

        // try {
        // Font font = new Font().init("resources/fonts/Unifontexmono-lxY45.ttf");
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        if (false) {

            Sprite sprite = new Sprite().setTexture(new Texture().initFromFont());
            GameObject textObj2 = SpriteObject.generate(sprite, sprite.getTexture().getWidth(),
                    sprite.getTexture().getHeight());
            textObj2.transform.position.x = 400;
            textObj2.transform.position.y = 400;
            Font font = AssetPool.getFont("resources/fonts/Unifontexmono-lxY45.ttf");
            GlyphMetrics c = font.getMetrics("h".charAt(0));
            sprite.setTexCoords(c.texCoords);
            this.addGameObjectToScene(textObj2);
        } else {
            new Texture().initFromFont();
            GameObject textObj = this.createGameObject("text", new Transform().init(new Vector3f(1000, 400, 1)));

            TextRenderer renderer = new TextRenderer();
            TextBox textBox = renderer.getTextBox();
            Text text = new Text("I love Anna");
            textBox.addText(text);
            textObj.addComponent(renderer);
            this.addGameObjectToScene(textObj);

        }

        if (true) {
            return;
        }

        // GameObject player = Player.generate();
        // player.transform.position.add(64, 80, 0);
        // this.addGameObjectToScene(player);

        // TestManager.testComponent(Inventory.class);

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

        // GameObject inventory = PrefabFactory.getObject(InventoryLayout.DEFAULT_27);
        // this.addGameObjectToScene(inventory);

        // Window.getScene().worldGrid().addObject(inventory);

        // ui.getComponent(GridState.class).toggleHighlight();

    }

    private void loadResources() {
        // TODO move this to a seperate initializer class
        AssetPool.getShader("/shaders/sprites/default.glsl");
        AssetPool.getShader("/shaders/fonts/default.glsl");

        AssetPool.addFont("Unifontexmono-lxY45.ttf");
        // AssetPool.addFont("HerculesPixelFontRegular-ovAX0.otf");

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
        Layout.SlotType.Other n = Layout.SlotType.Other.NULL;
        Layout.SlotType.Interactable i = Layout.SlotType.Interactable.INVENTORY;
        Layout.SlotType[][] standardLayout = { new Layout.SlotType[11], { n, i, i, i, i, i, i, i, i, i, n },
                { n, i, i, i, i, i, i, i, i, i, n }, { n, i, i, i, i, i, i, i, i, i, n }, new Layout.SlotType[11] };
        Layout standard = new Layout(standardLayout);
        AssetPool.addLayout(standard, InventoryLayout.DEFAULT_27);
        Layout.SlotType[][] lineLayout = { new Layout.SlotType[11], { n, i, i, i, i, i, i, i, i, i, n },
                new Layout.SlotType[11] };
        Layout line = new Layout(lineLayout);
        AssetPool.addLayout(line, InventoryLayout.DEFAULT_9);
    }
}
